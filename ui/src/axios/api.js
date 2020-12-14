import axiosInstance from './index.js';
import store from '../components/store'; // will be useful to access to axios.all and axios.spread
import parseDataUrl from 'parse-data-url';
import { ImportProgress } from '../components/import/reducers/create-import.js';
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

const dataURLtoBlob = (dataurl) => {
  var arr = dataurl.split(','), mime = arr[0].match(/:(.*?);/)[1],
    bstr = atob(arr[1]), n = bstr.length, u8arr = new Uint8Array(n);
  while (n--) {
    u8arr[n] = bstr.charCodeAt(n);
  }
  return new Blob([u8arr], { type: mime });
};

export const userLogin = params => {
  return axiosInstance.post('/api/auth/signin', {
    username: params.username,
    password: params.password
  });
};

export const userLogout = () => {
  return axiosInstance.post('/api/auth/signout', {});
};

export const userRegistration = userDetails => {
  return axiosInstance.post('/api/auth/signup', userDetails);
};



export const user = params => {
  const config = {
    headers: { Authorization: getToken() },
    params: params
  };
  return axiosInstance.get('/api/user', config);
};



export const apiDefinition = () => axiosInstance.get('/v3/api-docs').then(res => res);

export const getReferenceEntities = (entity) => axiosInstance.get('/api/' + entity).then(res => res);

export const entitySave = (entity, params) => {
  return axiosInstance.post('/api/' + entity, params).then(res => res);
};

export const entityEdit = (path, params) => {
  console.debug('doing the put method');
  return axiosInstance.put(path, params).then(res => res);
};

export const entityRelation = (entity, urls) => {
  const config = {
    headers: {
      'Content-Type': 'text/uri-list'
    }
  };
  return axiosInstance.put(entity, urls, config).then;
};


export const submitJobFile = (params) => {

  const data = new FormData();

  const fileParsed = parseDataUrl(params.file);
  data.append('file', dataURLtoBlob(params.file), fileParsed.name);
  data.append('programId', params.programId);
  data.append('withInvertSize', params.withInvertSize);

  const config = {
    'Content-Type': 'multipart/form-data; boundary=' + data._boundary,
    Authorization: getToken(),
    onUploadProgress: (progressEvent) => {
      const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total);
      store.dispatch(ImportProgress(percentCompleted));
    }

  };
  return axiosInstance.post(

    '/api/stage/upload',
    data,
    config
  ).then(res => res);
};

