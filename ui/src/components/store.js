import {configureStore, getDefaultMiddleware} from '@reduxjs/toolkit';

import {uploadReducer} from './import/reducers/upload';
import {authReducer} from './auth/auth-reducer';
import {formReducer} from './data-entities/form-reducer';
import createSagaMiddleware from 'redux-saga';
import getEntitiesWatcher from './data-entities/middleware/entities';
import {all} from 'redux-saga/effects';
import LoginWatcher from './auth/auth-middleware';
import getSearchResult from './data-entities/middleware/search';

const initialiseSagaMiddleware = createSagaMiddleware();
const isDev = process.env.NODE_ENV == 'development';

const middleware = isDev
  ? [...getDefaultMiddleware({serializableCheck: false, immutableCheck: false}), initialiseSagaMiddleware]
  : [initialiseSagaMiddleware];

const store = configureStore({
  reducer: {
    auth: authReducer,
    form: formReducer,
    upload: uploadReducer
  },
  middleware
});

function* rootSaga() {
  yield all([LoginWatcher(), getEntitiesWatcher(), getSearchResult()]);
}

initialiseSagaMiddleware.run(rootSaga);

export default store;
