import {takeEvery, call, put} from "redux-saga/effects";
import {
  selectRequested, entitiesLoaded, entitiesError, itemRequested, itemLoaded,
  createEntityRequested, entitiesCreated, updateEntityRequested
} from "../form-reducer";
import {entityEdit, entitySave, getReferenceEntities} from "../../../axios/api";

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

    let resp;
    const href = (action.payload.data?._links?.self?.href) ?
        action.payload.data._links.self.href :
        action.payload.path;
    delete action.payload.data._links;

    if (action.type == 'CREATE_ENTITY_REQUESTED') {
      resp = yield call(entitySave, href, action.payload.data);
    } else {
      resp = yield call(entityEdit, href, action.payload.data);
    }

    // const arrayfields = Object.keys(action.payload.data).filter(field => Array.isArray(action.payload.data[field]));
    // arrayfields.map(arrayField => {
    //   const relationsShirpUrl = resp.data._links[arrayField];
    //   console.log(relationsShirpUrl)
    // })

    yield put(entitiesCreated(resp));

  } catch (e) {
    yield put(entitiesError({e}));
  }
}

