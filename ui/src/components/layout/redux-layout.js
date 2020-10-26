import {
  createSlice
} from "@reduxjs/toolkit";


const toggleState = {
  menuIsOpen: false,
}


const toggleSlice = createSlice({
  name: "toggle",
  initialState: toggleState,
  reducers: {
    toggleMenu: (state, action) => {
      state.menuIsOpen = !state.menuIsOpen;
    }
  },
});

export const toggleReducer = toggleSlice.reducer;
export const { toggleMenu } = toggleSlice.actions;

