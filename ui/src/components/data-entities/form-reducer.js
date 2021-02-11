import {createSlice} from '@reduxjs/toolkit';
import pluralize from 'pluralize';

const formState = {
  entities: undefined,
  editItem: {},
  entitySaved: false,
  errors: [],
  wormsForm: {},
  isLoading: false
};

const formSlice = createSlice({
  name: 'form',
  initialState: formState,
  reducers: {
    resetState: () => formState,
    entitiesLoaded: (state, action) => {
      state.entityEdited = {};
      state.editItem = {};
      state.entitySaved = false;
      state.entities = action.payload;
      state.errors = [];
    },
    entitiesError: (state, action) => {
      const error = action.payload.e.response?.data?.error ? action.payload.e.response.data.error : 'Error while getting the entity data';
      state.entities = [];
      state.errors = [error];
    },
    itemLoaded: (state, action) => {
      state.editItem = action.payload;
    },
    wormsSearchRequested: (state) => {
      state.isLoading = true;
    },
    wormsSearchFailed: (state, action) => {
      state.isLoading = false;
      state.errors = action.payload;
    },
    wormsSearchFound: (state, action) => {
      state.isLoading = false;
      state.wormsForm = {
        genus: action.payload.rankGenus,
        phylum: action.payload.rankPhylum,
        order: action.payload.rankOrder,
        clazz: action.payload.rankClass,
        family: action.payload.rankFamily
      };
    },
    selectedItemsEdited: (state, action) => {
      let resp = {};
      const key = Object.keys(action.payload)[0];
      resp[key + 'Selected'] = action.payload[key];
      resp[key] = action.payload[key]._links.self.href;
      state.editItem = {...state.editItem, ...resp};
    },
    selectedItemsLoaded: (state, action) => {
      let resp = {};
      const key = Object.keys(action.payload._embedded)[0];
      const singularKey = pluralize.singular(key);
      resp[key] = action.payload._embedded;
      resp[singularKey + 'Selected'] = action.payload.selected;
      resp[singularKey] = action.payload.selected ? action.payload.selected._links.self.href : undefined;
      state.editItem = {...state.editItem, ...resp};
    },
    entitiesSaved: (state, action) => {
      state.entitySaved = action.payload;
    }
  }
});
export const formReducer = formSlice.reducer;
export const {
  resetState,
  wormsSearchRequested,
  wormsSearchFailed,
  wormsSearchFound,
  entitiesLoaded,
  entitiesError,
  entitiesSaved,
  itemLoaded,
  selectedItemsLoaded,
  selectedItemsEdited
} = formSlice.actions;
