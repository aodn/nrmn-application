
import {
    configureStore,
    getDefaultMiddleware
  } from "@reduxjs/toolkit";

import {toggleReducer} from './layout/redux-layout';
import { importReducer } from "./import/redux-import";

 const middleware = [
  ...getDefaultMiddleware(),
];

const store = configureStore({
    reducer: {
      toggle: toggleReducer,
      import: importReducer
    },
    middleware,
  });
  
  export default store;