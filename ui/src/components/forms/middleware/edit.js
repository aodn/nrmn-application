import { takeEvery, call, put } from "redux-saga/effects";
import { idRequested, idLoaded, idError } from "../form-reducer";
import { getEntities } from "../../../axios/api";

export default function* getEditWatcher() {
    yield takeEvery(idRequested, getEditItem);
}

function* getEditItem(action) {
    try {
        const resp = yield call(getEntities, action.payload);
        console.log("id:", resp.data);
        yield put(idLoaded(resp.data._embedded[action.payload]));
    } catch (e) {
        yield put(idError(e));
    }
}

