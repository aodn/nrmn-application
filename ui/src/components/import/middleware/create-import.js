import { takeEvery, call, put } from 'redux-saga/effects';
import { ImportRequested, ImportLoaded, ImportFailed } from '../reducers/create-import';
import { submitJobFile } from '../../../axios/api';

export default function* createImportWatcher() {
    yield takeEvery(ImportRequested, createImport);
}

function* createImport(params) {
    try {
        const payload = yield call(submitJobFile, params.payload);
        console.log(payload);
        yield put(ImportLoaded(payload.data));
    } catch (e) {
        console.log(e);
        yield put(ImportFailed(e.response.data));
    }
}

