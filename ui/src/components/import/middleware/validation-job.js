import { takeEvery, call, put } from 'redux-saga/effects';
import { JobReady, validationReady, RowUpdateRequested, jobFailed, JobRequested, ValidationRequested, EditRowFinished, SubmitingestRequested, ingestFinished } from '../reducers/create-import';
import {updateRow, getDataJob, postJobValidation, submitingest } from '../../../axios/api';

export default function* Watcher() {
    yield takeEvery(JobRequested, loadJob);
    yield takeEvery(ValidationRequested, validateJob);
    yield takeEvery(RowUpdateRequested, update);
    yield takeEvery(SubmitingestRequested, submit);

}

function* loadJob(action) {
    try {
        const payload = yield call(getDataJob, action.payload);
        yield put(JobReady(payload.data));
    } catch (e) {
        yield put(jobFailed(e));
    }
}

function* validateJob(action) {
    try {
        const payload = yield call(postJobValidation, action.payload);
        yield put(validationReady(payload.data));
    } catch (e) {
        yield put(jobFailed(e));
    }
}

function* update(action) {
    try {
        const rows = Object.values(action.payload.rows);
        yield call(updateRow,action.payload.jobId, rows);
        yield put(EditRowFinished());
    } catch (e) {
        yield put(jobFailed(e));
    }
}


function* submit(action) {
    try {
        const payload = yield call(submitingest, action.payload);
        yield put(ingestFinished(payload.data));
    } catch (error) {
        yield put(jobFailed(error));

    }
}
