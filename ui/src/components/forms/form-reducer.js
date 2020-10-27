import {
  createSlice,
  createAction
} from "@reduxjs/toolkit";


const formState = {
  definition: {},
  entities: [],
  editItem: {},
  createdEntity: {}
};


const formSlice = createSlice({
  name: "form",
  initialState: formState,
  reducers: {
    resetState: (state, action) => {
      state = formState;
      console.log("state reset", state);
    },
    definitionLoaded: (state, action) => {
      state.definition = action.payload.components.schemas;
    },
    definitionError: (state, action) => {
      console.error("error while getting the definition");
    },
    entitiesLoaded: (state, action) => {
      state.entities = action.payload;
    },
    entitiesError: (state, action) => {
      console.error("error while getting the entities");
    },
    idLoaded: (state, action) => {
      state.editItem = action.payload;
    },
    idError: (state, action) => {
      console.error("error while requesting id");
    },
    entitiesCreated: (state, action) => {
      state.createdEntity = action.payload;
    }
  },
});
export const formReducer = formSlice.reducer;
export const {
  resetState,
  definitionLoaded,
  definitionError,
  entitiesLoaded,
  entitiesError,
  entitiesCreated,
  idLoaded,
  idError
} = formSlice.actions;
export const definitionRequested = createAction('DEFINITION_REQUESTED');
export const selectRequested = createAction('SELECT_REQUESTED',
    function (entity) {
      return {payload: entity};
    });

export const idRequested = createAction('ID_REQUESTED',
    function (entity) {
      return {payload: entity};
    });

export const createEntityRequested = createAction('CREATE_ENTITY_REQUESTED',
    function (entity) {
      return {payload: entity};
    });

export const updateEntityRequested = createAction('UPDATE_ENTITY_REQUESTED',
    function (entity) {
      return {payload: entity};
    });
