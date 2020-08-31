import isPlainObject from "react-redux/lib/utils/isPlainObject";

const errorHeaders = {
  errorHeader500: "Something went wrong.",
  errorHeader400: "You have errors in your submission.",
  errorHeader401: "Unauthorised.",
  errorHeader404: "The data was not found.",
  errorHeader409: "Conflict."
};

let responseErrors = {
  messages: [],
  header: ""
};

export const getAPIErrorMessages = (responseData, errorCode) => {

  responseErrors.messages = [];

  if (Array.isArray(responseData)) {
    responseErrors.messages = [...responseErrors.messages, ...responseData];
  } else if (isPlainObject(responseData)) {
    if (typeof responseData.message !== "undefined") {
      responseErrors.messages.push(responseData.message);
    }
    if (typeof responseData.errors !== "undefined") {
      responseErrors.messages = [...responseErrors.messages, ...responseData.errors];
    }
  } else {
    responseErrors.messages.push(responseData);
  }

  responseErrors.header = errorHeaders["errorHeader" + errorCode];

  return responseErrors;

};