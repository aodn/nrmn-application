import { takeEvery, call, put } from 'redux-saga/effects';
import { ImportRequested, rawSurveyReady, ImportFailed, arrray2JSON, exportRow } from '../reducers/create-import';
import { rawSurveySave } from '../../../axios/api';

export default function* createImportWatcher() {
    yield takeEvery(ImportRequested, createImport);
}

function* createImport(params) {
    const sheet = arrray2JSON(params.payload.sheet);
    const rows = sheet.map(exportRow);
    try {
        const payload = yield call(rawSurveySave, {fileID: params.payload.fileID, Rows: rows});

        yield put(rawSurveyReady(payload.data));
    } catch (e) {
        yield put(ImportFailed(e));
    }
}

