import {
  createAction,
  createSlice
} from '@reduxjs/toolkit';

const initialState = JSON.parse(localStorage.getItem('auth')) ||
  { errors: [], succes: false, loading: false, redirect: '/' };

const authSlice = createSlice({
  name: 'auth',
  initialState: initialState,
  reducers: {
    loginAttempted: (state) => {
      localStorage.clear();
      state.loading = true;
    },
    login: (state, action) => {
      state.errors = [];
      state.username = action.payload.username;
      state.redirect = action.payload.redirect;
      state.accessToken = action.payload.accessToken;
      state.tokenType = action.payload.tokenType;
      state.success = true;
      state.loading = false;
      localStorage.setItem('auth', JSON.stringify(state));
      state.redirect = (action.payload.redirect) ? action.payload.redirect : '/';
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
      localStorage.clear();
      window.location = '/login';
      return initialState;
    }
  },
});

export const authReducer = authSlice.reducer;
export const { login, loginFailed, loginAttempted, logout, authError } = authSlice.actions;
export const loginSubmitted = createAction('LOGIN_SUBMITTED', function (formData) { return { payload: formData }; });
export const logoutSubmitted = createAction('LOGOUT_SUBMITTED', function (formData) { return { payload: formData }; });


