import {takeEvery, call, put} from "redux-saga/effects";

import {entityEdit, entitySave, getEntity, getSelectedEntityItems} from "../../../axios/api";
import {createAction} from "@reduxjs/toolkit";
import {
  entitiesCreated,
  entitiesError,
  entitiesLoaded,
  itemLoaded,
  selectedItemsLoaded,
  setSelectedFormData
} from "../form-reducer";

export default function* getEntitiesWatcher() {
  yield takeEvery(selectRequested, entities);
  yield takeEvery(itemRequested, getEntityData);
  yield takeEvery(selectedItemsRequested, getSelectedItemsData);
  yield takeEvery(createEntityRequested, saveEditEntities);
  yield takeEvery(updateEntityRequested, saveEditEntities);
  yield takeEvery(setNestedField, setNestedFormData);
}

function* entities(action) {
  try {
    const resp = yield call(getEntity, action.payload);
    yield put(entitiesLoaded(resp.data));
  } catch (e) {
    yield put(entitiesError({e}));
  }
}

function* setNestedFormData(action) {
  try {
    const resp = {}
    resp[action.payload.entity] = action.payload.newValues;
    yield put(setSelectedFormData(resp));
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

function* saveEditEntities(action) {
  try {
    const href = (action.payload.data?._links?.self?.href) ?
        action.payload.data._links.self.href :
        action.payload.path;
    delete action.payload.data._links;

    const entity = (action.type === 'CREATE_ENTITY_REQUESTED') ? entitySave: entityEdit;
    const resp = yield call(entity, href, action.payload.data);
    yield put(entitiesCreated(resp));

  } catch (e) {
    yield put(entitiesError({e}));
  }
}

export const selectRequested = createAction('SELECT_REQUESTED',
    function (entity) {
      return {payload: entity};
    });

export const itemRequested = createAction('ID_REQUESTED',
    function (entity) {
      return {payload: entity};
    });

export const setNestedField = createAction('SELECTED_NESTED_ENTITY',
    function (entity) {
      return {payload: entity};
    });

export const selectedItemsRequested = createAction('SELECTED_ENTITY_ITEMS_REQUESTED',
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
