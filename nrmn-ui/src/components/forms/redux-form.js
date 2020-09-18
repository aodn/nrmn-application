import {
    createSlice,
    createAction
  } from "@reduxjs/toolkit";
  
  
  const formState = {
      definition : {},
      entities: []
  }
  
  
  const formSlice = createSlice({
    name: "form",
    initialState: formState,
    reducers: {
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
      }
    },
  });
  export const formReducer = formSlice.reducer;
  export const { definitionLoaded, definitionError, entitiesLoaded, entitiesError } = formSlice.actions;
  export const  definitionRequested = createAction('DEFINITION_REQUESTED');    
  export const  selectRequested = createAction('select_REQUESTED', 
    function(entity){
       return { payload : entity}
  });    
