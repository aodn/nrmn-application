import axios from 'axios';

export const properties = {
  APIBaseUrl: process.env.NODE_ENV === 'development' ? 'http://localhost:8080' : ''
};

const axiosInstance = axios.create({
  baseURL: properties.APIBaseUrl,
  headers: {'Content-Type': 'application/json'}
});

axiosInstance.all = function all(promises) {
  return Promise.all(promises);
};

export default axiosInstance;
