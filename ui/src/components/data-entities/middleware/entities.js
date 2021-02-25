import {takeEvery, call, put} from 'redux-saga/effects';

import {entityEdit, entitySave, getEntity, getSelectedEntityItems} from '../../../axios/api';
import {createAction} from '@reduxjs/toolkit';
import {entitiesSaved, entitiesError, entitiesLoaded, itemLoaded, selectedItemsLoaded, selectedItemsEdited} from '../form-reducer';
import {isSuccessful200Response} from '../../utils/helpers';

export default function* getEntitiesWatcher() {
  yield takeEvery(selectRequested, entities);
  yield takeEvery(itemRequested, getEntityData);
  yield takeEvery(selectedItemsRequested, getSelectedItemsData);
  yield takeEvery(createEntityRequested, saveEntities);
  yield takeEvery(updateEntityRequested, updateEntities);
  yield takeEvery(setNestedField, setNestedFormData);
}

function* entities(action) {
  try {
    const resp = yield call(getEntity, action.payload);
    if (resp?.data) {
      yield put(entitiesLoaded(resp.data));
    }
  } catch (e) {
    yield put(entitiesError({e}));
  }
}

function* setNestedFormData(action) {
  try {
    const resp = {};
    resp[action.payload.entity] = action.payload.newValues;
    yield put(selectedItemsEdited(resp));
  } catch (e) {
    yield put(entitiesError({e}));
  }
}

function* getEntityData(action) {
  try {
    const resp = yield call(getEntity, action.payload);
    yield put(itemLoaded(resp.data));
  } catch (e) {
    yield put(entitiesError({e}));
  }
}

function* getSelectedItemsData(action) {
  try {
    const resp = yield call(getSelectedEntityItems, action.payload);
    yield put(selectedItemsLoaded(resp));
  } catch (e) {
    yield put(entitiesError({e}));
  }
}

function* saveEntities(action) {
  try {
    const href = action.payload.data?._links?.self?.href ? action.payload.data._links.self.href : action.payload.path;
    delete action.payload.data._links;

    const response = yield call(entitySave, href, action.payload.data);
    if (response?.data?.errors) {
      yield put(entitiesError({e: {response: response}}));
    } else {
      yield put(entitiesSaved(isSuccessful200Response(response.status)));
    }
  } catch (e) {
    yield put(entitiesError({e}));
  }
}

function* updateEntities(action) {
  try {
    const resp = yield call(entityEdit, action.payload.data._links.self.href, action.payload.data);
    const checkStatus = (response) => isSuccessful200Response(response.status);
    yield put(entitiesSaved(resp.every(checkStatus)));
  } catch (e) {
    yield put(entitiesError({e}));
  }
}

export const selectRequested = createAction('SELECT_REQUESTED', function (entity) {
  return {payload: entity};
});

export const itemRequested = createAction('ID_REQUESTED', function (entity) {
  return {payload: entity};
});

export const setNestedField = createAction('SELECTED_NESTED_ENTITY', function (entity) {
  return {payload: entity};
});

export const selectedItemsRequested = createAction('SELECTED_ENTITY_ITEMS_REQUESTED', function (entity) {
  return {payload: entity};
});

export const createEntityRequested = createAction('CREATE_ENTITY_REQUESTED', function (entity) {
  return {payload: entity};
});

export const updateEntityRequested = createAction('UPDATE_ENTITY_REQUESTED', function (entity) {
  return {payload: entity};
});
