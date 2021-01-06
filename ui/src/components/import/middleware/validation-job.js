import { takeEvery, call, put } from 'redux-saga/effects';
import {JobReady, validationReady , RowUpdateRequested, JobFinished, ImportFailed, JobRequested, ValidationRequested } from '../reducers/create-import';
import {getDataJob, postJobValidation, updateRow} from '../../../axios/api';

export default function* Watcher() {
    yield takeEvery(JobRequested, loadJob);
    yield takeEvery(ValidationRequested, validateJob);
    yield takeEvery(RowUpdateRequested, update);

}

function* loadJob(action) {
    try {
        const payload = yield call(getDataJob, action.payload);
     yield put(JobReady(payload.data));
    } catch (e) {
        yield put(ImportFailed(e));
    }
}

function* validateJob(action) {
    try {
        const payload = yield call(postJobValidation, action.payload);
        console.log(payload);
     yield put(validationReady(payload.data));
    } catch (e) {
        yield put(ImportFailed(e));
    }
}

function* update(action) {
    try {
        const payload = yield call(updateRow, action.payload.id, action.payload.row);
        console.log(payload);
     yield put(JobFinished());
    } catch (e) {
        yield put(ImportFailed(e));
    }
}

