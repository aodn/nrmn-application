import { takeEvery, call, put } from 'redux-saga/effects';
import {JobReady , ImportFailed, JobRequested } from '../reducers/create-import';
import {getDataJob} from '../../../axios/api';
export default function* Watcher() {
    yield takeEvery(JobRequested, loadDataJob);
}

function* loadDataJob(action) {
    try {
        const payload = yield call(getDataJob, action.payload);
     yield put(JobReady(payload.data));
    } catch (e) {
        yield put(ImportFailed(e));
    }
}
