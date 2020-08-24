import { takeEvery, call, put } from "redux-saga/effects";
import axios from 'axios'
import { rawSurveyReady, ImportFailed, FileRequested } from "../reducers/create-import";

export default function* FileImportWatcher() {
    yield takeEvery(FileRequested, fileImport);
}

function* fileImport(action) {
    try {
        const payload = yield call(apiData, action.payload);
        yield put(rawSurveyReady({FileID: action.payload, Rows:payload.data}));
    } catch (e) {
        yield put(ImportFailed(e));
    }
}

function apiData(fileID) {
   return  axios.get("http://localhost:8080/api/raw-survey/" + fileID).then(res => res );  
}