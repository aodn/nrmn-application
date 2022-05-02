import {LicenseManager} from 'ag-grid-enterprise';
import jwtDecode from 'jwt-decode';
import axiosInstance from './index.js';

// define setApplicationError if this method is not present
// eg. in unit tests
if (typeof window.setApplicationError === 'undefined') {
  window.setApplicationError = () => {};
}

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
    } else {
      window.setApplicationError(error?.message || JSON.stringify(error), error);
      console.error({error});
    }
  }
);

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
      if (res.data.accessToken) {
        const jwt = jwtDecode(res.data.accessToken);
        let state = {};
        state.expires = jwt.exp * 1000;
        state.username = params.username;
        state.accessToken = res.data.accessToken;
        state.tokenType = res.data.tokenType;
        state.roles = jwt.roles;
        try {
          localStorage.setItem('auth', JSON.stringify(state));
          localStorage.setItem('gridLicense', JSON.stringify(res.data.gridLicense));
        } catch (_) {
          console.error('Browser has no local storage');
        }
        LicenseManager.setLicenseKey(res.data.gridLicense);
        onResult(state, null);
      } else {
        onResult({expires: 0}, res.data.error);
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

export const getCorrections = (surveyId) =>
  axiosInstance
    .get('correction/correct/' + surveyId, {
      validateStatus: () => true
    })
    .then((res) => res)
    .catch((err) => err);

export const validateJob = (jobId, completion) => {
  return axiosInstance.post(`stage/validate/${jobId}`).then(completion);
};

export const updateRows = (jobId, rows, completion) => {
  return axiosInstance.put(`stage/job/${jobId}`, rows).then((res) => completion(res));
};

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

export const submitIngest = (jobId, onResult) => {
  return axiosInstance
    .post(
      'ingestion/ingest/' + jobId,
      {},
      {
        validateStatus: () => true
      }
    )
    .then((res) => onResult(res));
};

export const search = (params) => {
  const url = `species?searchType=${escape(params.searchType)}&species=${escape(params.species)}&includeSuperseded=${
    params.includeSuperseded
  }${params.page ? '&page=' + params.page : ''}`;

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
