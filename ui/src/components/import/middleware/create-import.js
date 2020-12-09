import { takeEvery, call, put } from 'redux-saga/effects';
import { ImportRequested, rawSurveyReady, ImportFailed, arrray2JSON, exportRow } from '../reducers/create-import';
import { submitJobFile } from '../../../axios/api';

export default function* createImportWatcher() {
    yield takeEvery(ImportRequested, createImport);
}

function* createImport(params) {
    try {
        const payload = yield call(submitJobFile, params.payload);

        yield put(rawSurveyReady(payload.data));
    } catch (e) {
        yield put(ImportFailed(e));
    }
}

