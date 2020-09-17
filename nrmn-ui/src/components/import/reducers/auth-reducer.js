import {
  createSlice
} from "@reduxjs/toolkit";


const initialState = {
  loggedIn: false,
  username: 'unknown'
}

const authSlice = createSlice({
  name: "auth",
  initialState: initialState,
  reducers: {
    login: (state, action) => {
      state.loggedIn = !state.loggedIn;
      state.username = 'trusted-person';
    },
    logout: (state, action) => {
      state.loggedIn = !state.loggedIn;
      state.username = 'unknown';
    },
    register: (state, action) => {
      console.log("registered")
    }
  },
});

export const authReducer = authSlice.reducer;
export const { login, logout, register } = authSlice.actions;

