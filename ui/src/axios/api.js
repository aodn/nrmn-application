import axiosInstance from './index.js';
import axios from 'axios';
import store from '../components/store'; // will be useful to access to axios.all and axios.spread

function getToken() {
   const token = store.getState().auth.accessToken;
   const tokenType = store.getState().auth.tokenType;
   return `${tokenType} ${token}`;
}

function getAxiosPromise(method, path, params, contentType) {
  return axiosInstance({
    headers: { 'Content-Type': (contentType) ? contentType: 'application/json' },
    method: method,
    url: path,
    data: params
  });
}

axiosInstance.interceptors.request.use(
    config => {
      config.headers.authorization = getToken();
      return config;
    },
    error => Promise.reject(error)
);

// siteConfig loaded into react-global-configuration
export const siteConfig = () => {
  return axiosInstance.get('/api/utils/siteconfig');
};

// Authentication
export const activate = params => {
  return axiosInstance.post('/api/auth/activate', params);
};
export const resetPassword = data => {
  return axiosInstance.post('/api/auth/resetPassword', data);
};
export const changePassword = data => {
  return axiosInstance.post('/api/auth/changePassword', data);
};

export const userLogin = params => {
  return axiosInstance.post('/api/auth/signin', {
    username: params.username,
    password: params.password
  });
};
export const userLogout = () => {
  return axiosInstance.post('/api/auth/signout', {} );
};

// User
export const me = () => {
  return axiosInstance.get('/api/user/me');
};
export const meUpdate = userDetails => {
  delete userDetails.password;
  return axiosInstance.put('/api/user/me', userDetails );
};
export const userRegistration = userDetails => {
  return axiosInstance.post('/api/auth/signup', userDetails);
};


// Admin users only
export const userById = id => {
  return axiosInstance.get(`/api/user/${id}`);
};
export const userUpdate = userDetails => {
  delete userDetails.password;
  return axiosInstance.put(`/api/user/${userDetails.id}`, userDetails);
};

export const user = params => {
  const config = {
    headers: { Authorization: getToken() },
    params: params
  };
  return axiosInstance.get(`/api/user`, config);
};

// Ingest
export const rawSurvey = fileId => {
  return  axiosInstance.get(`/api/raw-survey/${fileId ? fileId: ''}`).then(res => res );
};

export const rawSurveySave = params => {
  return  axiosInstance.post('/api/raw-survey', params).then(res => res );
};

export const apiDefinition = () =>  axiosInstance.get('/v3/api-docs').then(res => res);

export const getEntity = (entity) => axiosInstance.get('/api/' + entity).then(res=>res);

export const getResource = (url) => axiosInstance.get(url).then(res=>res);


export const getSelectedEntityItems = (paths) => axios.all([
    axiosInstance.get('/api/' + paths[0]),
    (paths[1]) ? axiosInstance.get(paths[1]) : null,
  ]).then(resp => {
    let response = resp[0].data;
    response.selected = (resp[1]) ? resp[1].data : null;
    return response;
  });


export const entitySave = (entity, params) => {
  return  axiosInstance.post('/api/' + entity, params ).then(res => res );
};

export const entityEdit = (path, params) => {

  let axiosPromises = [getAxiosPromise('put', path ,params)];

  Object.keys(params).filter( key => {
    if (key.endsWith('Selected')) {
      const thisnestedEntity = key.replace('Selected', '');
      axiosPromises.push(getAxiosPromise('put', path + '/' + thisnestedEntity, params[key]._links.self.href, 'text/uri-list'));
    }
  });
  return axiosInstance.all(axiosPromises).then(res => res );
};

export const entityRelation = (entity, urls) => {
  const config = {
    headers: {
      'Content-Type': 'text/uri-list'
    }
  };
  return axiosInstance.put(entity, urls,config).then;
};