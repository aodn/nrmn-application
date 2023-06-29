import {LicenseManager} from 'ag-grid-enterprise';
import jwtDecode from 'jwt-decode';
import axiosInstance from './index.js';

// define setApplicationError if this method is not present
// eg. in unit tests
if (typeof window.setApplicationError === 'undefined') {
  window.setApplicationError = () => {};
}

const sleep = ms => new Promise(r => setTimeout(r, ms));

axiosInstance.interceptors.request.use(
  (config) => {
    window.setApplicationError(null);
    const {accessToken, tokenType} = JSON.parse(localStorage.getItem('auth')) || {};
    if (accessToken && tokenType) config.headers.authorization = `${tokenType} ${accessToken}`;
    return config;
  },
  (error) => Promise.reject(error)
);

axiosInstance.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error?.response?.status === 401) {
      localStorage.removeItem('auth');
      window.location.reload();
    }
    else {
      if(!error?.response?.config?.useCustomErrorHandler) {
        window.setApplicationError(error);
        console.error({ error });
      }
      else {
        return Promise.reject(error);
      }
    }
  }
);

export const changePassword = (params, onResult) => {
  return axiosInstance
    .post(
      'auth/update',
      {
        username: params.username,
        password: params.password,
        newPassword: params.newPassword
      },
      {
        validateStatus: () => true
      }
    )
    .then((res) => onResult(res));
};

export const userLogin = (params, onResult) => {
  return axiosInstance
    .post(
      'auth/signin',
      {
        username: params.username,
        password: params.password
      },
      {
        validateStatus: () => true
      }
    )
    .then((res) => {
      if (res.data.changePassword) {
        onResult({changePassword: true});
      } else if (res.data.accessToken) {
        const jwt = jwtDecode(res.data.accessToken);
        let state = {};
        state.expires = jwt.exp * 1000;
        state.username = params.username;
        state.accessToken = res.data.accessToken;
        state.tokenType = res.data.tokenType;
        state.roles = jwt.roles;
        state.features = res.data.features;
        try {
          localStorage.setItem('auth', JSON.stringify(state));
          localStorage.setItem('gridLicense', JSON.stringify(res.data.gridLicense));
        } catch (_) {
          console.error('Browser has no local storage');
        }
        LicenseManager.setLicenseKey(res.data.gridLicense);
        onResult(state, null);
      } else {
        const retryHeader = res.headers['x-rate-limit-retry-after-seconds'];
        if (retryHeader) {
          const seconds = parseInt(retryHeader);
          const delay = seconds < 60 ? `${seconds} seconds.` : `${Math.ceil(seconds / 60)} minutes.`;
          onResult({expires: 0}, `Too many authentication requests from your IP. Try again in ${delay}`);
        } else {
          onResult({expires: 0}, res.data.error);
        }
      }
    });
};

export const userLogout = () => axiosInstance.post('auth/signout', {});

export const getResult = (entity) => axiosInstance.get(entity);

export const getEntity = (entity) => getResult(entity).then((res) => res);

export const deleteJob = (jobId) => axiosInstance.delete('stage/delete/' + jobId);

export const entitySave = (entity, params) => {
  return axiosInstance
    .post(entity, params, {
      validateStatus: () => true
    })
    .then((res) => res)
    .catch((err) => err);
};

export const entityEdit = (entity, params) => {
  return axiosInstance
    .put(entity, params, {
      validateStatus: () => true
    })
    .then((res) => res)
    .catch((err) => err);
};

export const entityDelete = (url, id) => {
  return axiosInstance
    .delete(`${url}/${id}`, {
      validateStatus: () => true
    })
    .then((res) => res)
    .catch((err) => err);
};

export const getDataJob = (jobId) =>
  axiosInstance
    .get('stage/job/' + jobId, {
      validateStatus: () => true
    })
    .then((res) => res)
    .catch((err) => err);

export const searchSpecies = (payload) => {
  return axiosInstance
    .post(`correction/searchSpecies`, payload, {
      validateStatus: () => true
    })
    .then((res) => res)
    .catch((err) => err);
};

export const searchSpeciesSummary = (payload) => {
  return axiosInstance
    .post(`correction/searchSpeciesSummary`, payload, {
      validateStatus: () => true
    })
    .then((res) => res)
    .catch((err) => err);
};

export const postSpeciesCorrection = (payload) => {
  return axiosInstance
    .post(`correction/correctSpecies`, payload, {useCustomErrorHandler: true})
    .then((res) => res);
};

export const getCorrections = (surveyIds) =>
  axiosInstance
    .get('correction/correct?surveyIds=' + surveyIds, {
      validateStatus: () => true
    })
    .then((res) => res)
    .catch((err) => err);

export const validateSurveyCorrection = (surveyIds, bodyDto) => {
  return axiosInstance.post('correction/validate?surveyIds=' + surveyIds, bodyDto);
};

export const submitSurveyCorrection = (surveyIds, bodyDto) => {
  return axiosInstance.post('correction/correct?surveyIds=' + surveyIds, bodyDto);
};

export const validateJob = (jobId, completion) => {
  return axiosInstance.post(`stage/validate/${jobId}`).then(completion);
};

export const updateRows = (jobId, rows, completion) => {
  return axiosInstance.put(`stage/job/${jobId}`, rows).then((res) => completion(res));
};

export const runDailyTasks = () => axiosInstance.post('admin/runDailyTasks').then((res) => res);

export const runStartupTasks = () => axiosInstance.post('admin/runStartupTasks').then((res) => res);

export const getSharedLinks = () =>
  axiosInstance
    .get('sharedLinks', {
      validateStatus: () => true
    })
    .then((res) => res)
    .catch((err) => err);

export const createSharedLink = (sharedLinkDto) => axiosInstance.put('sharedLink', sharedLinkDto).then((res) => res);

export const deleteSharedLink = (linkId) => axiosInstance.delete(`sharedLink/${linkId}`).then((res) => res);

export const submitJobFile = (params, onProgress) => {
  const data = new FormData();
  data.append('file', params.file);
  data.append('programId', params.programId);
  data.append('withExtendedSizes', params.withExtendedSizes);

  const config = {
    validateStatus: () => true,
    'Content-Type': 'multipart/form-data',
    onUploadProgress: (progressEvent) => {
      const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total);
      onProgress(percentCompleted);
    }
  };
  return axiosInstance
    .post('stage/upload', data, config)
    .then((response) => ({response}))
    .catch((err) => ({err}));
};
// inst - is use in test to return canned data
export const submitIngest = async (jobId, onLocked, onResult, inst = axiosInstance, timeout = 5000) => {

  const postResponse = await inst
    .post(
      'ingestion/ingest/' + jobId,
      {},
      {
        validateStatus: () => true
      }
    )
    .then(res => res.data)
    .catch(res => res.data);

  if (postResponse.jobStatus === 'FAILED' && postResponse.reason === 'locked') {
    onLocked(postResponse);
    return;
  };

  let notDone = true;
  let getResponse;

  while(notDone) {
    getResponse = await inst.get('ingestion/ingest/' + postResponse.jobLogId, { validateStatus: () => true }).then(res => res.data);
    notDone = getResponse.jobStatus === 'INGESTING';
    if(notDone) await sleep(timeout);
  }

  onResult({
      status: getResponse.jobStatus === 'INGESTED' ? 200 : 400,
      data: getResponse
    });
};

export const search = (params) => {
  const url = `species?searchType=${escape(params.searchType)}&species=${escape(params.species)}&includeSuperseded=${
    params.includeSuperseded
  }${params.page ? '&page=' + params.page : ''}${params.pageSize ? '&pageSize=' + params.pageSize : ''}`;

  return axiosInstance
    .get(url, {
      validateStatus: () => true
    })
    .then((response) => response);
};

export const templateZip = (params) => {
  return axiosInstance.get(`template/template.zip?${params}`, {responseType: 'blob'});
};

export const originalJobFile = (jobId) => {
  return axiosInstance.get(`stage/job/download/${jobId}`, {responseType: 'blob'});
};

export const getSupersedTreeForReactFlow = (observableItemId) => {
  return axiosInstance.get(`reference/observableItem/${observableItemId}/findRoot`);
};