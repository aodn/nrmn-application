import { takeEvery, call, put } from "redux-saga/effects";
import axios from 'axios'
import { fileListRequested, fileListReady, fileListFailed } from "../reducers/list-import";

export default function* ListImportWatcher() {
    yield takeEvery(fileListRequested, listImport);
}

function* listImport() {
    try {
        const payload = yield call(apiData);
        yield put(fileListReady(payload.data));
    } catch (e) {
        yield put(fileListFailed(e));
    }
}

function apiData() {
   return  axios.get("http://localhost:8080/api/raw-survey").then(res => res );  
}