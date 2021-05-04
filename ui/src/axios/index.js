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

axiosInstance.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (401 === error.response?.status && error.config.url !== '/api/auth/signin') {
      localStorage.clear();
    } else if (404 === error.response?.status) {
      window.location = `/notfound?resource=${window.location.pathname}`;
    }
  }
);

export default axiosInstance;
