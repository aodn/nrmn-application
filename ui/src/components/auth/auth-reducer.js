import {createAction, createSlice} from '@reduxjs/toolkit';
import {LicenseManager} from 'ag-grid-enterprise';
import jwtDecode from 'jwt-decode';

const initialState = JSON.parse(localStorage.getItem('auth')) || {errors: [], success: false, loading: false, redirect: '/'};

const authSlice = createSlice({
  name: 'auth',
  initialState: initialState,
  reducers: {
    loginAttempted: (state) => {
      localStorage.clear();
      state.loading = true;
    },
    login: (state, action) => {
      const jwt = jwtDecode(action.payload.accessToken);
      state.expires = jwt.exp * 1000;
      state.errors = [];
      state.username = action.payload.username;
      state.accessToken = action.payload.accessToken;
      state.tokenType = action.payload.tokenType;
      state.roles = jwt.roles;
      state.success = true;
      state.loading = false;
      localStorage.setItem('auth', JSON.stringify(state));
      localStorage.setItem('gridLicense', JSON.stringify(action.payload.gridLicense));
      LicenseManager.setLicenseKey(action.payload.gridLicense);
    },
    loginFailed: (state) => {
      state.errors = ['Login failed: invalid username or password.'];
      state.success = false;
      state.loading = false;
    },
    authError: (state) => {
      state.errors = ['Service unavailable'];
      state.success = false;
      state.loading = false;
    },
    logout: () => {
      return initialState;
    }
  }
});

export const authReducer = authSlice.reducer;
export const {login, loginFailed, loginAttempted, logout, authError} = authSlice.actions;
export const loginSubmitted = createAction('LOGIN_SUBMITTED', function (formData) {
  return {payload: formData};
});
export const logoutSubmitted = createAction('LOGOUT_SUBMITTED', function (formData) {
  return {payload: formData};
});
