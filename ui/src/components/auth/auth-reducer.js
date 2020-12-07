import {
  createAction,
  createSlice
} from '@reduxjs/toolkit';

const initialState = JSON.parse(localStorage.getItem('auth')) || {
  loading: false,
  success: false,
  username: ''
};

const authSlice = createSlice({
  name: 'auth',
  initialState: initialState,
  reducers: {
    loginAttempted: (state) => {
      state.loading = true;
    },
    login: (state, action) => {
        state = action.payload.data;
        // TODO get username/role from API payload
        state.username = JSON.parse(action.payload.config.data).username;
        localStorage.setItem('auth', JSON.stringify(state));
        state.errors = undefined;
        state.success = true;
        state.loading = false;
        window.location = (action.payload.redirect) ? action.payload.redirect : '/';
    },
    authError: (state, action) => {
      state.errors = ['ERROR: ' + action.payload.response.data.error];
      if (typeof action.payload.response.data.errors !== 'undefined') {
        state.errors = state.errors.concat(action.payload.response.data.errors.map(error =>
            `${error.field.toUpperCase()}: ${error.defaultMessage}`
        ));
      }
      state.success = false;
      state.loading = false;
    },
    logout: (state) => {
      localStorage.clear();
      state = initialState;
      window.location = '/login';
    }
  },
});

export const authReducer = authSlice.reducer;
export const { login, loginAttempted, logout, authError } = authSlice.actions;
export const loginSubmitted = createAction('LOGIN_SUBMITTED', function (formData) { return { payload: formData }; });
export const logoutSubmitted = createAction('LOGOUT_SUBMITTED', function (formData) { return { payload: formData }; });


