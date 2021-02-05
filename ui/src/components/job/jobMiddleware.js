import { takeEvery, call, put } from 'redux-saga/effects';
import { getEntity, getFullJob } from '../../axios/api';
import { jobsError, jobsFinished, jobsRequested, jobRequested, jobFinished } from './jobReducer';

export default function* createJobsWatcher() {
    yield takeEvery(jobsRequested, getJobs);
    yield takeEvery(jobRequested, getJob);
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
        yield put(jobsError([e.message]));

    }
}