import {takeEvery, call, put} from "redux-saga/effects";
import {
  selectRequested,
  entitiesLoaded,
  entitiesError,
  idRequested,
  idLoaded,
  createEntityRequested, entitiesCreated
} from "../form-reducer";
import {entityEdit, entitySave, getReferenceEntities} from "../../../axios/api";

export default function* getEntitiesWatcher() {
  yield takeEvery(selectRequested, entities);
  yield takeEvery(idRequested, entityData);
  yield takeEvery(createEntityRequested, getCreateEntities);
}

function* entities(action) {
  try {
    const resp = yield call(getReferenceEntities, action.payload);
    yield put(entitiesLoaded(resp.data));
  } catch (e) {
    yield put(entitiesError({e, action}));
  }
}

function* entityData(action) {
  try {
    const resp = yield call(getReferenceEntities, action.payload);
    yield put(idLoaded(resp.data));
  } catch (e) {
    yield put(entitiesError({e, action}));
  }
}

function* getCreateEntities(action) {
  try {
    let resp;
    console.log(action.payload);
    if (action.payload.data?._links?.self?.href) {
      resp = yield call(entityEdit, action.payload.data._links.self.href, action.payload.data);
    } else {
      resp = yield call(entitySave, action.payload.path, action.payload.data);
    }

    const arrayfields = Object.keys(action.payload.data).filter(field => Array.isArray(action.payload.data[field]));
    arrayfields.map(arrayField => {
      const relationsShirpUrl = resp.data._links[arrayField];
      console.log(relationsShirpUrl)
    })

    yield put(entitiesCreated(resp));

  } catch (e) {
    console.log(e)
    yield put(entitiesError(e));
  }
}

