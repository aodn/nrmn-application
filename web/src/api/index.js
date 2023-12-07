const axios = require('axios/dist/axios');

export const properties = {
  APIVersion: 1,
  APIBaseUrl: process.env.NODE_ENV === 'development' ? 'http://localhost:8080' : ''
};

const axiosInstance = axios.create({
  baseURL: `${properties.APIBaseUrl}/api/v${properties.APIVersion}/`,
  headers: {'Content-Type': 'application/json'}
});

export default axiosInstance;
