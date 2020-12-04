import {takeEvery, call, put} from 'redux-saga/effects';
import {
  selectRequested, entitiesLoaded, entitiesError, itemRequested, itemLoaded,
  createEntityRequested, entitiesCreated, updateEntityRequested
} from '../form-reducer';
import {entityEdit, entitySave, getReferenceEntities} from '../../../axios/api';

export default function* getEntitiesWatcher() {
  yield takeEvery(selectRequested, entities);
  yield takeEvery(itemRequested, getEntityData);
  yield takeEvery(createEntityRequested, saveEditEntities);
  yield takeEvery(updateEntityRequested, saveEditEntities);
}

function* entities(action) {
  try {
    const resp = yield call(getReferenceEntities, action.payload);
    yield put(entitiesLoaded(resp.data));
  } catch (e) {
    yield put(entitiesError({e}));
  }
}

function* getEntityData(action) {
  try {
    const resp = yield call(getReferenceEntities, action.payload);
    yield put(itemLoaded(resp.data));
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

