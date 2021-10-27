import axiosInstance from './index.js';
import axios from 'axios';
import store from '../components/store'; // will be useful to access to axios.all and axios.spread

function getToken() {
  const {accessToken, tokenType} = store.getState().auth;
  return `${tokenType} ${accessToken}`;
}

axiosInstance.interceptors.request.use(
  (config) => {
    window.setApplicationError(null);
    config.headers.authorization = getToken();
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

export const getResult = (entity) => axiosInstance.get('/api/' + entity);

export const getEntity = (entity) => getResult(entity).then((res) => res);

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

export const getSiteEdit = () => {
  const locations = axiosInstance.get('/api/locations?projection=selection');
  const marineProtectedAreas = axiosInstance.get('/api/marineProtectedAreas');
  const protectionStatuses = axiosInstance.get('/api/protectionStatuses');
  const states = axiosInstance.get('/api/siteStates');
  const countries = axiosInstance.get('/api/siteCountries');
  return axios.all([locations, marineProtectedAreas, protectionStatuses, states, countries]).then(
    axios.spread((...responses) => {
      const locations =
        responses[0]?.data._embedded.locations.map((l) => {
          return {id: l.locationId, label: l.locationName};
        }) || [];
      const marineProtectedAreas = responses[1]?.data._embedded.marineProtectedAreas.map((m) => m.name) || [];
      const protectionStatuses = responses[2]?.data._embedded.protectionStatuses.map((m) => m.name) || [];
      const states = responses[3]?.data || [];
      const countries = responses[4]?.data || [];
      return {locations, marineProtectedAreas, protectionStatuses, states, countries};
    })
  );
};

export const deleteJob = (jobId) => {
  return axiosInstance.delete('/api/stage/delete/' + jobId);
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
