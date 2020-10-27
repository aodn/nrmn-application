import { takeEvery, call, put } from "redux-saga/effects";
import { selectRequested, entitiesLoaded, entitiesError } from "../form-reducer";
import { getEntities } from "../../../axios/api";

export default function* getEntitiesWatcher() {
    yield takeEvery(selectRequested, entities);
}

function* entities(action) {
    try {
        const resp = yield call(getEntities, action.payload);
        debugger;
        yield put(entitiesLoaded(resp.data._embedded[action.payload]));
    } catch (e) {
        yield put(entitiesError(e));
    }
}

