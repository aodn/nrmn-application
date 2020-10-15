import { takeEvery, call, put } from "redux-saga/effects";
import { fileListRequested, fileListReady, fileListFailed } from "../reducers/list-import";
import { rawSurvey } from "../../../axios/api";

export default function* ListImportWatcher() {
    yield takeEvery(fileListRequested, listImport);
}

function* listImport() {
    try {
        const payload = yield call(rawSurvey);
        yield put(fileListReady(payload.data));
    } catch (e) {
        yield put(fileListFailed(e));
    }
}