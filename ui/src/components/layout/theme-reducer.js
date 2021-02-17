import {createSlice} from '@reduxjs/toolkit';

const themeState = JSON.parse(localStorage.getItem('theme')) ||  {
  themeType: false,
  columnFit: false
};

const toggleThemeSlice = createSlice({
  name: 'themeSelection',
  initialState: themeState,
  reducers: {
    toggleTheme: (state, action) => {
      let currentTheme = JSON.parse(localStorage.getItem('theme')) || {};
      state.themeType = action.payload;
      currentTheme.themeType = action.payload;
      localStorage.setItem('theme', JSON.stringify(currentTheme));
    },
    toggleColumnFit: (state, action) => {
      let currentTheme = JSON.parse(localStorage.getItem('theme')) || {};
      state.columnFit = action.payload;
      currentTheme.columnFit = action.payload;
      localStorage.setItem('theme', JSON.stringify(currentTheme));
    }
  }
});

export const themeReducer = toggleThemeSlice.reducer;
export const { toggleTheme, toggleColumnFit } = toggleThemeSlice.actions;