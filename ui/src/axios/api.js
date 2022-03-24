import {LicenseManager} from 'ag-grid-enterprise';
import jwtDecode from 'jwt-decode';
import axiosInstance from './index.js';

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
    window.setApplicationError(error?.message || JSON.stringify(error), error);
    console.error({error});
  }
);

export const userLogin = (params, onResult) => {
  return axiosInstance
    .post(
      '/api/auth/signin',
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
        localStorage.setItem('auth', JSON.stringify(state));
        localStorage.setItem('gridLicense', JSON.stringify(res.data.gridLicense));
        LicenseManager.setLicenseKey(res.data.gridLicense);
        onResult(state, null);
      } else {
        onResult({expires: 0}, res.data.error);
      }
    });
};

export const userLogout = () => axiosInstance.post('/api/auth/signout', {});

export const getResult = (entity) => axiosInstance.get('/api/' + entity);

export const getEntity = (entity) => getResult(entity).then((res) => res);

export const deleteJob = (jobId) => axiosInstance.delete('/api/stage/delete/' + jobId);

export const entitySave = (entity, params) => {
  return axiosInstance
    .post('/api/' + entity, params, {
      validateStatus: () => true
    })
    .then((res) => res)
    .catch((err) => err);
};

export const entityEdit = (entity, params) => {
  return axiosInstance
    .put('/api/' + entity, params, {
      validateStatus: () => true
    })
    .then((res) => res)
    .catch((err) => err);
};

export const entityDelete = (url, id) => {
  return axiosInstance
    .delete(`/api/${url}/${id}`, {
      validateStatus: () => true
    })
    .then((res) => res)
    .catch((err) => err);
};

export const getDataJob = (jobId) =>
  axiosInstance
    .get('/api/stage/job/' + jobId, {
      validateStatus: () => true
    })
    .then((res) => res)
    .catch((err) => err);

export const validateJob = (jobId, completion) => {
  return axiosInstance.post(`/api/stage/validate/${jobId}`).then(completion);
};

export const updateRows = (jobId, rows, completion) => {
  return axiosInstance.put(`/api/stage/job/${jobId}`, rows).then((res) => completion(res));
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
    .post('/api/stage/upload', data, config)
    .then((response) => ({response}))
    .catch((err) => ({err}));
};

export const submitIngest = (jobId, success, error) => {
  return axiosInstance
    .post(
      '/api/ingestion/ingest/' + jobId,
      {},
      {
        validateStatus: () => true
      }
    )
    .then((res) => success(res))
    .catch((err) => error(err));
};

export const search = (params) => {
  const url = `/api/species?searchType=${escape(params.searchType)}&species=${escape(params.species)}&includeSuperseded=${
    params.includeSuperseded
  }${params.page ? '&page=' + params.page : ''}`;

  return axiosInstance
    .get(url, {
      validateStatus: () => true
    })
    .then((response) => response);
};

export const templateZip = (params) => {
  return axiosInstance.get(`/api/template/template.zip?${params}`, {responseType: 'blob'});
};

export const originalJobFile = (jobId) => {
  return axiosInstance.get(`/api/stage/job/download/${jobId}`, {responseType: 'blob'});
};
