import {
  createAction,
  createSlice
} from "@reduxjs/toolkit";

const initialState = JSON.parse(localStorage.getItem('auth')) || {}

const authSlice = createSlice({
  name: "auth",
  initialState: initialState,
  reducers: {
    login: (state, action) => {
        state = action.payload.data;
        // TODO get username/role from API payload
        state.username = JSON.parse(action.payload.config.data).username;
        localStorage.setItem('auth', JSON.stringify(state));
        state.errors = undefined;
        window.location = "/"
    },
    authError: (state, action) => {
      state.errors = ["ERROR: " + action.payload.response.data.error];
      if (typeof action.payload.response.data.errors !== "undefined") {
        state.errors = state.errors.concat(action.payload.response.data.errors.map(error =>
            `${error.field.toUpperCase()}: ${error.defaultMessage}`
        ));
      }
    },
    logout: (state, action) => {
      localStorage.clear();
      window.location = "/login"
    }
  },
});

export const authReducer = authSlice.reducer;
export const { login, logout, authError } = authSlice.actions;
export const loginSubmitted = createAction('LOGIN_SUBMITTED', function (formData) { return { payload: formData } });
export const logoutSubmitted = createAction('LOGOUT_SUBMITTED', function (formData) { return { payload: formData } });


