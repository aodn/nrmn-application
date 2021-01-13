import { takeEvery, call, put } from 'redux-saga/effects';
import {JobReady, validationReady , RowUpdateRequested, JobFinished, ImportFailed, JobRequested, ValidationRequested, EditRowFinished, SubmitingestRequested } from '../reducers/create-import';
import {getDataJob, postJobValidation, updateRow, submitingest} from '../../../axios/api';

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
     yield put(EditRowFinished());
    } catch (e) {
        yield put(ImportFailed(e));
    }
}


function* submit(action) {
    try {
        const payload = yield  call(submitingest, action.payload);
        yield put();
    } catch (error) {
        yield put(ImportFailed(error));

    }
}
