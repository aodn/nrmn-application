import axios from "axios";

export const properties = {
  APIBaseUrl: process.env.NODE_ENV === 'development' ? process.env.REACT_APP_LOCALDEV_API_HOST : ""
};

const axiosInstance = axios.create({
  baseURL: properties.APIBaseUrl,
  headers: { "Content-Type": "application/json" }
});

axiosInstance.interceptors.response.use((response) => {
  return response;
}, (error) => {
  if (401 === error.response?.status && error.config.url !== "/api/auth/signin") {
    const persist = JSON.parse(localStorage.getItem("persist:root"));
    const user = JSON.parse(persist.user);
    if (user.accessToken) {
      if (error.config.url === "/api/user/me") {  // If 401 from /api/user/me then the token is invalid
        localStorage.clear();
        window.location.reload(); // try again (the resource will try to load as an unauthenticated user)
      } else {
        window.location = "/notpermitted"
      }
    } else {
      window.location = `/login?redirect=${window.location.pathname}`;
    }
  } else if (404 === error.response?.status && error.config.url !== "/api/auth/resetPassword") {
    window.location = `/notfound?resource=${window.location.pathname}`;
  } else {
    return Promise.reject(error);
  }
});

export default axiosInstance;

