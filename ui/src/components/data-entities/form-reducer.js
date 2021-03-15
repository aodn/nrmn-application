import {createSlice} from '@reduxjs/toolkit';

const formState = {
  entities: undefined,
  formData: {},
  formOptions: {},
  entitySaved: false,
  errors: [],
  isLoading: false
};

const formSlice = createSlice({
  name: 'form',
  initialState: formState,
  reducers: {
    resetState: () => formState,
    entitiesLoaded: (state, action) => {
      state.entityEdited = {};
      state.formData = {};
      state.entitySaved = false;
      state.entities = action.payload;
      state.errors = [];
    },
    entitiesError: (state, action) => {
      state.entities = [];
      state.errors = action.payload.e.response?.data?.errors ?? [];
    },
    itemLoaded: (state, action) => {
      state.formData = action.payload;
    },
    selectedItemEdited: (state, action) => {
      const key = Object.keys(action.payload)[0];
      let fieldData = {};
      fieldData[key] = action.payload[key];
      state.formData = {...state.formData, ...fieldData};
    },
    selectedItemsEdited: (state, action) => {
      const key = Object.keys(action.payload)[0];

      let optionsData = {};
      optionsData[key] = action.payload[key];
      state.formOptions = {...state.formOptions, ...optionsData};

      let fieldData = {};
      fieldData[key] = action.payload[key]._links.self.href;
      state.formData = {...state.formData, ...fieldData};
    },
    embeddedFieldEdited: (state, action) => {
      const key = Object.keys(action.payload)[0];
      let fieldData = {};
      fieldData[key] = action.payload[key];
      state.formData = {...state.formData, ...fieldData};
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
      state.formOptions = {...state.formOptions, ...newOptions};
    },
    entitiesSaved: (state, action) => {
      state.entitySaved = action.payload;
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
