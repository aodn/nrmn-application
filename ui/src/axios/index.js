import axios from 'axios';

export const properties = {
  APIBaseUrl: process.env.NODE_ENV === 'development' ? process.env.REACT_APP_LOCALDEV_API_HOST : ''
};

const axiosInstance = axios.create({
  baseURL: properties.APIBaseUrl,
  headers: {'Content-Type': 'application/json'}
});

axiosInstance.all = function all(promises) {
  return Promise.all(promises);
};

export default axiosInstance;
