import {
  createSlice,
  createAction
} from "@reduxjs/toolkit";


const formState = {
  definition: {},
  entities: [],
  editItem: {},
  newlyCreatedEntity: {},
  errors: []
};


const formSlice = createSlice({
  name: "form",
  initialState: formState,
  reducers: {
    resetState: (state, action) => {
      state = formState;
    },
    entitiesLoaded: (state, action) => {
      state.newlyCreatedEntity = {};
      state.editItem = {};
      state.entities = action.payload;
      state.errors = [];
    },
    entitiesError: (state, action) => {
      const error = "Error while getting the entity data"
      state.entities = [];
      state.errors = [error];
    },
    itemLoaded: (state, action) => {
      state.editItem = action.payload;
    },
    entitiesCreated: (state, action) => {
      state.newlyCreatedEntity = action.payload;
    }
  },
});
export const formReducer = formSlice.reducer;
export const {
  resetState,
  entitiesLoaded,
  entitiesError,
  entitiesCreated,
  itemLoaded
} = formSlice.actions;


export const selectRequested = createAction('SELECT_REQUESTED',
    function (entity) {
      return {payload: entity};
    });

export const itemRequested = createAction('ID_REQUESTED',
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
