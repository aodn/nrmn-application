
import {
  configureStore,
  getDefaultMiddleware
} from "@reduxjs/toolkit";

import { themeReducer } from './layout/theme-reducer';
import { toggleReducer } from './layout/layout-reducer';
import { importReducer } from "./import/reducers/create-import";
import { authReducer } from "./auth/auth-reducer";
import { listFileReducer } from "./import/reducers/list-import";
import createSagaMiddleware from "redux-saga";
import importMiddleware from './import/middleware/create-import';
import ListFileMiddleware from './import/middleware/list-import';
import FileMiddleware from './import/middleware/file-import';

import { all } from "redux-saga/effects";
import LoginWatcher from "./auth/auth-middleware";

const initialiseSagaMiddleware = createSagaMiddleware();

const middleware = [
  ...getDefaultMiddleware(),
  initialiseSagaMiddleware
]; 

const store = configureStore({
  reducer: {
    theme: themeReducer,
    auth: authReducer,
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
    FileMiddleware(),
    LoginWatcher()
  ])
}

initialiseSagaMiddleware.run(rootSaga);

export default store;