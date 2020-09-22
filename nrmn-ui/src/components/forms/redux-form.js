import {
    createSlice,
    createAction
  } from "@reduxjs/toolkit";
  
  
  const formState = {
      definition : {},
      entities: [],
      editItem : {}
  };
  
  
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
      },
      idLoaded: (state, action) => {
        state.editItem = action.payload;
      },
      idError : (state, action) => {
        console.error("error while requesting id");
      }
      
    },
  });
  export const formReducer = formSlice.reducer;
  export const { definitionLoaded, definitionError,entitiesLoaded,entitiesError, idLoaded, idError } = formSlice.actions;
  export const  definitionRequested = createAction('DEFINITION_REQUESTED');    
  export const  selectRequested = createAction('select_REQUESTED', 
    function(entity){
       return { payload : entity};
  });    

  export const  idRequested = createAction('select_REQUESTED', 
    function(entity){
       return { payload : entity};
  });  
