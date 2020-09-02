import axios from "axios";

export const properties = {
  APIBaseUrl: process.env.NODE_ENV === 'development' ? process.env.REACT_APP_LOCALDEV_API_HOST : ""
};

export default axios.create({
  baseURL: properties.APIBaseUrl,
  crap: process.env,
  headers: { "Content-Type": "application/json" }
});

