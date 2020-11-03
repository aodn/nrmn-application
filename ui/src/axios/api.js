import axiosInstance from "./index.js";
import axios from "axios";
import store from "../components/store"; // will be useful to access to axios.all and axios.spread

function getToken() {
   const token = store.getState().auth.accessToken;
   const tokenType = store.getState().auth.tokenType;
   return `${tokenType} ${token}`;
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
export const userLogout = () => {
  const headers = { Authorization: getToken()};
  return axiosInstance.post("/api/auth/signout", {} , { headers });
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

// Ingest
export const rawSurvey = fileId => {
  return  axiosInstance.get(`/api/raw-survey/${fileId ? fileId: ""}`).then(res => res );
}

export const rawSurveySave = params => {
  return  axiosInstance.post("/api/raw-survey", params).then(res => res );
}

export const apiDefinition = () =>  axiosInstance.get("/v3/api-docs").then(res => res)

export const getReferenceEntities = (entity) => axiosInstance.get("/api/" + entity).then(res=>res);

export const entitySave = (entity, params) => {
  return  axiosInstance.post("/api/" + entity, params).then(res => res );
}
export const entityEdit = (path, params) => {
  return  axiosInstance.put( path , params).then(res => res );
}

export const entityRelation = (entity, urls) => {
  const config = {
    headers: {
      "Content-Type": "text/uri-list"
    }
  }
  return axiosInstance.put(entity, urls,config).then
}