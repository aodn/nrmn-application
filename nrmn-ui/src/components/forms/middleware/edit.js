import { takeEvery, call, put } from "redux-saga/effects";
import { idRequested, idLoaded, idError } from "../redux-form";
import { entities } from "../../../axios/api";

export default function* getEditWatcher() {
    yield takeEvery(idRequested, getEditItem);
}

function* getEditItem(action) {
    try {
        console.log("id req:", action.payload);
        const resp = yield call(entities, action.payload);
        console.log("id:", resp.data);
        yield put(idLoaded(resp.data));
    } catch (e) {
        yield put(idError(e));
    }
}

