
import {
  configureStore,
  getDefaultMiddleware
} from "@reduxjs/toolkit";

import { toggleReducer } from './layout/redux-layout';
import { importReducer } from "./import/reducers/create-import";
import createSagaMiddleware from "redux-saga";
import importMiddleware from './import/middleware/create-import';
import { listFileReducer } from "./import/reducers/list-import";
import ListFileMiddleware from './import/middleware/list-import';
import FileMiddleware from './import/middleware/file-import';

import { all } from "redux-saga/effects";

const initialiseSagaMiddleware = createSagaMiddleware();


const middleware = [
  ...getDefaultMiddleware(),
  initialiseSagaMiddleware
]; 

const store = configureStore({
  reducer: {
    toggle: toggleReducer,
    import: importReducer,
    fileList: listFileReducer
  },
  middleware,
});

 function* rootSaga() {
  yield all([
    ListFileMiddleware(),
    importMiddleware(),
    FileMiddleware()
    
  ])
};

initialiseSagaMiddleware.run(rootSaga);


export default store;