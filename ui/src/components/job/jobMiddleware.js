import { takeEvery, call, put } from 'redux-saga/effects';
import { getEntity, getFullJob, deleteJobAPI } from '../../axios/api';
import { jobsError, DeleteJobRequested, DeleteFinished, jobsFinished, jobsRequested, jobRequested, jobFinished } from './jobReducer';

export default function* createJobsWatcher() {
    yield takeEvery(jobsRequested, getJobs);
    yield takeEvery(jobRequested, getJob);
    yield takeEvery(DeleteJobRequested, deleteJob);
}

function* getJobs() {
    try {
        const response = yield call(getEntity, 'stagedJobs');
        const data = response.data?._embedded.stagedJobs || [];
        if (data)
            yield put(jobsFinished(data));
        else {
            yield put(jobsError([{ message: 'Service unavailable.' }]));
        }


    } catch (e) {
        console.error('bad', e);
        yield put(jobsError([e.message]));
    }
}

function* getJob(param) {
    try {
        const job = yield call(getFullJob, param.payload.id);

        if (job)
            yield put(jobFinished(job));
        else
            yield put(jobsError([{ message: 'Service unavailbe.' }]));
    } catch (e) {
        console.error(e);
        yield put(jobsError([e]));

    }
}

function* deleteJob(param) {
    try {
        yield call(deleteJobAPI, param.payload);
        yield put(DeleteFinished());
    } catch (e) {
        console.error(e);
        yield put(jobsError([e]));
    }


}