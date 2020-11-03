import {
  createSlice,
  createAction
} from "@reduxjs/toolkit";


const formState = {
  definition: {},
  entities: {},
  editItem: {},
  createdEntity: {},
  errors: {}
};


const formSlice = createSlice({
  name: "form",
  initialState: formState,
  reducers: {
    resetState: (state, action) => {
      state = formState;
    },
    entitiesLoaded: (state, action) => {
      state.entities = action.payload;
    },
    entitiesError: (state, action) => {
      console.log("Error while getting the entity data" + action);
      state.entities = [];
      state.errors = action.payload.message;
    },
    idLoaded: (state, action) => {
      state.editItem = action.payload;
    },
    entitiesCreated: (state, action) => {
      state.createdEntity = action.payload;
    }
  },
});
export const formReducer = formSlice.reducer;
export const {
  resetState,
  entitiesLoaded,
  entitiesError,
  entitiesCreated,
  idLoaded
} = formSlice.actions;


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
