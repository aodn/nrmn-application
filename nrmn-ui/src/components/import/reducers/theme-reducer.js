import {createSlice} from "@reduxjs/toolkit";

const themeState = {
  themeType: false
};

const toggleThemeSlice = createSlice({
  name: "themeSelection",
  initialState: themeState,
  reducers: {
    toggleTheme: (state, action) => {
      state.themeType = action.payload;
    }
  }
});

export const themeReducer = toggleThemeSlice.reducer;
export const { toggleTheme } = toggleThemeSlice.actions;