import { takeEvery, call, put } from 'redux-saga/effects';
import { rawSurveyReady, ImportFailed, FileRequested } from '../reducers/create-import';

export default function* FileImportWatcher() {
    yield takeEvery(FileRequested, fileImport);
}

function* fileImport(action) {
    try {
        //const payload = yield call(rawSurvey, action.payload);
       // yield put(rawSurveyReady({FileID: action.payload, Rows:payload.data}));
    } catch (e) {
        yield put(ImportFailed(e));
    }
}
