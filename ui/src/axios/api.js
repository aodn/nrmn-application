import axiosInstance from './index.js';
import axios from 'axios';
import store from '../components/store'; // will be useful to access to axios.all and axios.spread
import {ImportProgress} from '../components/import/reducers/upload';
import {importRow} from '../components/import/reducers/create-import.js';

function getToken() {
  const {accessToken, tokenType} = store.getState().auth;
  return `${tokenType} ${accessToken}`;
}

axiosInstance.interceptors.request.use(
  (config) => {
    config.headers.authorization = getToken();
    return config;
  },
  (error) => Promise.reject(error)
);

const dataURLtoBlob = (dataurl) => {
  var arr = dataurl.split(','),
    mime = arr[0].match(/:(.*?);/)[1],
    bstr = atob(arr[1]),
    n = bstr.length,
    u8arr = new Uint8Array(n);
  while (n--) {
    u8arr[n] = bstr.charCodeAt(n);
  }
  return new Blob([u8arr], {type: mime});
};

export const tokenExpired = () => {
  const {expires} = store.getState().auth;
  return expires && expires < Date.now();
};

export const userLogin = (params) => {
  return axiosInstance.post(
    '/api/auth/signin',
    {
      username: params.username,
      password: params.password
    },
    {
      validateStatus: () => true
    }
  );
};

export const userLogout = () => {
  return axiosInstance.post('/api/auth/signout', {});
};

export const userRegistration = (userDetails) => {
  return axiosInstance.post('/api/auth/signup', userDetails);
};

export const user = (params) => {
  const config = {
    headers: {Authorization: getToken()},
    params: params
  };
  return axiosInstance.get('/api/user', config);
};

export const apiDefinition = () => axiosInstance.get('/v3/api-docs').then((res) => res);

export const getEntity = (entity) => axiosInstance.get('/api/' + entity).then((res) => res);

export const getFullJob = (id) => {
  const jobReq = axiosInstance.get('/api/stagedJobs/' + id);
  const logsReq = axiosInstance.get('/api/stagedJobs/' + id + '/logs');
  const programReq = axiosInstance.get('/api/stagedJobs/' + id + '/program');
  return axios
    .all([jobReq, logsReq, programReq])
    .then(
      axios.spread((...responses) => {
        const job = responses[0].data || {};
        const logs = responses[1]?.data._embedded.stagedJobLogs || [];
        const program = responses[2].data || {};
        const fullJob = {...job, program: program, logs: logs};
        return fullJob;
      })
    )
    .catch((err) => {
      console.error(err);
      return {error: err};
    });
};

export const deleteJobAPI = (jobId) => {
  axiosInstance.delete('/api/stage/delete/' + jobId);
};

export const getSelectedEntityItems = (paths) =>
  axiosInstance.all([axiosInstance.get('/api/' + paths[0]), paths[1] ? axiosInstance.get(paths[1]) : null]).then((resp) => {
    let response = resp[0].data;
    response.selected = resp[1] ? resp[1].data : null;
    return response;
  });

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

export const entityRelation = (entity, urls) => {
  const config = {
    headers: {
      'Content-Type': 'text/uri-list'
    }
  };
  return axiosInstance.put(entity, urls, config).then;
};

export const getDataJob = (jobId) =>
  axiosInstance
    .get('/api/stage/job/' + jobId, {
      validateStatus: () => true
    })
    .then((res) => res)
    .catch((err) => err);

export const postJobValidation = (jobId) => axiosInstance.post('/api/stage/validate/' + jobId).then((res) => res);
export const updateRow = (jobId, rows) => {
  return axiosInstance.put('/api/stage/updates/' + jobId, rows.map(importRow)).then((res) => res);
};
export const deleteRow = (jobId, rows) => {
  return axiosInstance.put('/api/stage/delete/rows/' + jobId, rows).then((res) => res);
};

export const submitJobFile = (params) => {
  const data = new FormData();
  const splited = params.file.split(';');
  const filename = splited[1].split('=')[1];
  data.append('file', dataURLtoBlob(params.file), filename);
  data.append('programId', params.programId);
  data.append('withInvertSize', params.withInvertSize);

  const config = {
    validateStatus: () => true,
    'Content-Type': 'multipart/form-data; boundary=' + data._boundary,
    onUploadProgress: (progressEvent) => {
      const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total);
      store.dispatch(ImportProgress(percentCompleted));
    }
  };
  return axiosInstance
    .post('/api/stage/upload', data, config)
    .then((response) => ({response}))
    .catch((err) => ({err}));
};

export const submitingest = (jobId) => {
  return axiosInstance
    .get('/api/ingestion/ingest/' + jobId)
    .then((res) => res)
    .catch((err) => ({err}));
};

export const search = (params) => {
  const url = `/api/species?searchType=${escape(params.searchType)}&species=${escape(params.species)}`;
  return axiosInstance
    .get(url, {
      validateStatus: () => true
    })
    .then((res) => res)
    .catch((err) => err);
};
