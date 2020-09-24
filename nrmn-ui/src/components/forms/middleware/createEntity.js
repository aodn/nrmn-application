import { takeEvery, call, put } from "redux-saga/effects";
import { createEntityRequested, entitiesCreated, entitiesError } from "../redux-form";
import { entitySave } from "../../../axios/api";

export default function* getCreateEntitiesWatcher() {
    yield takeEvery(createEntityRequested, getCreateEntities);
}

function* getCreateEntities(action) {
    try {
        const resp = yield call(entitySave,action.payload.path,  action.payload.data);
        console.log(resp.data);
        yield put(entitiesCreated(resp.data));
    } catch (e) {
        yield put(entitiesError(e));
    }
}

