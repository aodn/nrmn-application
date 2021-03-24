import {createSlice} from '@reduxjs/toolkit';

const formState = {
  entities: null,
  data: {},
  options: {},
  loading: false,
  saved: false,
  errors: []
};

const formSlice = createSlice({
  name: 'form',
  initialState: formState,
  reducers: {
    resetState: () => formState,
    entitiesLoaded: (state, action) => {
      state.entityEdited = {};
      state.data = {};
      state.saved = false;
      state.entities = action.payload;
      state.errors = [];
    },
    entitiesError: (state, action) => {
      state.entities = [];
      state.errors = action.payload.e.response?.data?.errors ?? [];
    },
    itemLoaded: (state, action) => {
      state.data = action.payload;
    },
    selectedItemEdited: (state, action) => {
      const key = Object.keys(action.payload)[0];
      let fieldData = {};
      fieldData[key] = action.payload[key];
      state.data = {...state.data, ...fieldData};
    },
    selectedItemsEdited: (state, action) => {
      const key = Object.keys(action.payload)[0];

      let optionsData = {};
      optionsData[key] = action.payload[key];
      state.options = {...state.options, ...optionsData};

      let fieldData = {};
      fieldData[key] = action.payload[key]._links.self.href;
      state.data = {...state.data, ...fieldData};
    },
    embeddedFieldEdited: (state, action) => {
      const key = Object.keys(action.payload)[0];
      let fieldData = {};
      fieldData[key] = action.payload[key];
      state.data = {...state.data, ...fieldData};
    },
    selectedItemsLoaded: (state, action) => {
      const key = Object.keys(action.payload._embedded)[0];
      const newOptions = {};
      // FIXME: this should not be necessary
      if (key === 'marineProtectedAreas' || key === 'protectionStatuses') {
        newOptions[key] = action.payload._embedded[key].reduce((f, v) => {
          if (v.name) f.push(v.name);
          return f;
        }, []);
      } else {
        newOptions[key] = action.payload._embedded[key];
      }
      state.options = {...state.options, ...newOptions};
    },
    entitiesSaved: (state, action) => {
      state.saved = action.payload;
    }
  }
});
export const formReducer = formSlice.reducer;
export const {
  resetState,
  entitiesLoaded,
  entitiesError,
  entitiesSaved,
  itemLoaded,
  selectedItemsLoaded,
  selectedItemEdited,
  selectedItemsEdited,
  embeddedFieldEdited
} = formSlice.actions;
