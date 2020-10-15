import {
  createSlice
} from "@reduxjs/toolkit";

const initialState = JSON.parse(localStorage.getItem('auth')) || {}

const authSlice = createSlice({
  name: "auth",
  initialState: initialState,
  reducers: {
    login: (state, action) => {

      if (action.payload.status === 200) {
        state = action.payload.data;
        // TODO get username/role from API payload
        state.username = JSON.parse(action.payload.config.data).username;
        localStorage.setItem('auth', JSON.stringify(state));
        state.errors = undefined;
        window.location = "/"
      }
      else {
        state.errors = "ERROR: " + action.payload.data.message;
      }
    },
    logout: (state, action) => {
      state = {};
      localStorage.clear();
      window.location = "/"
    }
  },
});

export const authReducer = authSlice.reducer;
export const { login, logout } = authSlice.actions;

