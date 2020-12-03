import { takeEvery, put } from 'redux-saga/effects';

import { fileListRequested, fileListReady, fileListFailed } from '../reducers/list-import';

export default function* ListImportWatcher() {
    yield takeEvery(fileListRequested, listImport);
}

function* listImport() {
    try {
        //TODO update callBack
        const payload = { data: {}};
        yield put(fileListReady(payload.data));
    } catch (e) {
        yield put(fileListFailed(e));
    }
}