import {
  createSlice
} from '@reduxjs/toolkit';


const toggleState = {
  leftSideMenuIsOpen: false,
  logoutMenuOpen: false
};

const toggleSlice = createSlice({
  name: 'toggle',
  initialState: toggleState,
  reducers: {
    toggleLeftSideMenu: (state, action) => {
      state.leftSideMenuIsOpen = !state.leftSideMenuIsOpen;
    },
    toggleLogoutMenuOpen: (state, action) => {
      state.logoutMenuOpen = !state.logoutMenuOpen;
    }
  },
});

export const toggleReducer = toggleSlice.reducer;
export const { toggleLeftSideMenu, toggleLogoutMenuOpen } = toggleSlice.actions;

