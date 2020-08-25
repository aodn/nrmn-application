import axiosInstance from "./index.js";
import axios from "axios"; // will be useful to access to axios.all and axios.spread

function getToken() {
  // const token = store.getState().user.accessToken;
  // const tokenType = store.getState().user.tokenType;
  // return `${tokenType} ${token}`;
}

// siteConfig loaded into react-global-configuration
export const siteConfig = () => {
  return axiosInstance.get("/api/utils/siteconfig");
};

// Authentication
export const activate = params => {
  return axiosInstance.post("/api/auth/activate", params);
};
export const resetPassword = data => {
  return axiosInstance.post("/api/auth/resetPassword", data);
};
export const changePassword = data => {
  return axiosInstance.post("/api/auth/changePassword", data);
};
export const userLogin = params => {
  return axiosInstance.post("/api/auth/signin", {
    username: params.username,
    password: params.password
  });
};

// User
export const me = () => {
  const headers = { Authorization: getToken() };
  return axiosInstance.get("/api/user/me", { headers });
};
export const meUpdate = userDetails => {
  const headers = { Authorization: getToken() };
  delete userDetails.password;
  return axiosInstance.put("/api/user/me", userDetails, { headers });
};
export const userRegistration = userDetails => {
  return axiosInstance.post("/api/auth/signup", userDetails);
};


// Admin users only
export const userById = id => {
  const headers = { Authorization: getToken() };
  return axiosInstance.get(`/api/user/${id}`, { headers });
};
export const userUpdate = userDetails => {
  const headers = { Authorization: getToken() };
  delete userDetails.password;
  return axiosInstance.put(`/api/user/${userDetails.id}`, userDetails, {
    headers
  });
};

export const user = params => {
  const config = {
    headers: { Authorization: getToken() },
    params: params
  };
  return axiosInstance.get(`/api/user`, config);
};

// Injest
export const rawSurvey = params => {
  return  axiosInstance.get("/api/raw-survey", params).then(res => res );
}
