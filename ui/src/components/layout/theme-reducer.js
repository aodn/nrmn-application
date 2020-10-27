import {createSlice} from "@reduxjs/toolkit";

const themeState =  JSON.parse(localStorage.getItem('theme')) || {
  themeType: false
};

const toggleThemeSlice = createSlice({
  name: "themeSelection",
  initialState: themeState,
  reducers: {
    toggleTheme: (state, action) => {
      state.themeType = action.payload;
      localStorage.setItem('theme', JSON.stringify({themeType : state.themeType}));
    }
  }
});

export const themeReducer = toggleThemeSlice.reducer;
export const { toggleTheme } = toggleThemeSlice.actions;