import { takeEvery, call, put } from "redux-saga/effects";
import { ImportRequested, rawSurveyReady, ImportFailed, arrray2JSON, exportRow } from "../reducers/create-import";
import axios from 'axios'

export default function* createImportWatcher() {
    yield takeEvery(ImportRequested, createImport);
}

function* createImport(params) {
    const sheet = arrray2JSON(params.payload.sheet);
    const rows = sheet.map(exportRow)
    try {
        const payload = yield call(apiData, {fileID: params.payload.fileID, Rows: rows});
        
        yield put(rawSurveyReady(payload.data));
    } catch (e) {
        yield put(ImportFailed(e));
    }
}

function apiData(params) {
   return  axios.post("http://localhost:8080/api/raw-survey", params).then(res => res );  
}