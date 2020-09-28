import { takeEvery, call, put } from "redux-saga/effects";
import { createEntityRequested, entitiesCreated, entitiesError } from "../redux-form";
import { entitySave } from "../../../axios/api";

export default function* getCreateEntitiesWatcher() {
    yield takeEvery(createEntityRequested, getCreateEntities);
}

function* getCreateEntities(action) {
    try {
         console.log(action.payload);

         const arrayfields = Object.keys(action.payload.data)
         .filter(field => Array.isArray(action.payload.data[field]));
         const resp = yield call(entitySave,action.payload.path,  action.payload.data);
         arrayfields.map(arrayField => {
             const relationsShirpUrl = resp.data._links[arrayField];
             console.log(relationsShirpUrl)
         })
        console.log(resp.data);
        
        yield put(entitiesCreated(resp.data));
    } catch (e) {
        yield put(entitiesError(e));
    }
}

