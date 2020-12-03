import axiosInstance from './index.js';
import store from '../components/store'; // will be useful to access to axios.all and axios.spread

function getToken() {
   const token = store.getState().auth.accessToken;
   const tokenType = store.getState().auth.tokenType;
   return `${tokenType} ${token}`;
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
  return axiosInstance.get('/api/user', config);
};


export const rawSurveySave = params => {
  return  axiosInstance.post('/api/raw-survey', params).then(res => res );
};

export const apiDefinition = () =>  axiosInstance.get('/v3/api-docs').then(res => res);

export const getReferenceEntities = (entity) => axiosInstance.get('/api/' + entity).then(res=>res);

export const entitySave = (entity, params) => {
  return  axiosInstance.post('/api/' + entity, params ).then(res => res );
};

export const entityEdit = (path, params) => {
  console.debug('doing the put method');
  return  axiosInstance.put( path , params ).then(res => res );
};

export const entityRelation = (entity, urls) => {
  const config = {
    headers: {
      'Content-Type': 'text/uri-list'
    }
  };
  return axiosInstance.put(entity, urls,config).then;
};