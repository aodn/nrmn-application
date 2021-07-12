import {createSlice} from '@reduxjs/toolkit';

const formState = {
  entities: null,
  data: {},
  options: {},
  searchResults: null,
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
    updateFormFields: (state, action) => {
      state.data = {...state.data, ...action.payload};
    },
    selectedItemsLoaded: (state, action) => {
      if(!('_embedded' in action.payload)) {
        action.payload['_embedded'] = {'.': action.payload.map(item => { return {'.': item};} )};
      }
      const key = Object.keys(action.payload._embedded)[0];
      const newOptions = {};
      // HACK: this should not be necessary
      if (key === 'marineProtectedAreas' || key === 'protectionStatuses' || key === 'reportGroups' || key === 'habitatGroups') {
        newOptions[key] = action.payload._embedded[key].reduce((f, v) => {
          if (v.name) f.push(v.name);
          return f;
        }, []);
      } else {
        newOptions[key] = action.payload._embedded[key];
      }
      state.options = {...state.options, ...newOptions};
    },
    selectedListItemsLoaded: (state, action) => {
      // Hack to work with AutoCompleteInput
      const newOptions = {};
      const key = action.payload.key;

      newOptions[key] = action.payload.resp[key];

      state.options = {...state.options, ...newOptions};
    },
    entitiesSaved: (state, action) => {
      state.saved = action.payload;
    },
    searchRequested: (state) => {
      state.loading = true;
      state.searchResults = null;
    },
    searchFailed: (state, action) => {
      state.loading = false;
      state.searchError = action.payload.message;
    },
    searchFound: (state, action) => {
      if (action.payload?.length > 0)
        state.searchResults = action.payload.map((r, id) => {
          // remove the genus from the species to produce the species epithet
          const speciesEpithet = r.species ? r.species.replace(`${r.genus} `, '') : '';
          return {id: id, ...r, speciesEpithet: speciesEpithet};
        });
      else state.searchResults = [];
      state.loading = false;
    }
  }
});
export const formReducer = formSlice.reducer;
export const {
  searchRequested,
  searchFailed,
  searchFound,
  resetState,
  entitiesLoaded,
  entitiesError,
  entitiesSaved,
  itemLoaded,
  updateFormFields,
  selectedItemsLoaded,
  selectedListItemsLoaded,
  selectedItemEdited,
  selectedItemsEdited,
  embeddedFieldEdited
} = formSlice.actions;
