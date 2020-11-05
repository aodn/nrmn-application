import {
  createSlice,
  createAction
} from "@reduxjs/toolkit";


const formState = {
  definition: {},
  entities: [],
  editItem: {},
  createdEntity: {},
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
      state.entities = action.payload;
      state.errors = [];
    },
    entitiesError: (state, action) => {
      debugger
      const error = "Error while getting the entity data"
      state.entities = [];
      state.errors = [error];
    },
    itemLoaded: (state, action) => {
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
