import { takeEvery, call, put } from "redux-saga/effects";
import { selectRequested, entitiesLoaded, entitiesError } from "../redux-form";
import { entities } from "../../../axios/api";

export default function* getEntitiesWatcher() {
    yield takeEvery(selectRequested, getEntities);
}

function* getEntities(action) {
    try {
        console.log("call entities")
        const resp = yield call(entities, action.payload);
        console.log("entites data", resp)

        yield put(entitiesLoaded(resp.data._embedded[action.payload]));
    } catch (e) {
        yield put(entitiesError(e));
    }
}

