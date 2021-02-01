import { takeEvery, call, put } from 'redux-saga/effects';
import { getEntity } from '../../axios/api';
import { jobsError, jobsFinished, jobsRequested } from './jobReducer';

export default function* createJobsWatcher() {
    yield takeEvery(jobsRequested, getJobs);
}

function* getJobs() {
    try {
        const response = yield call(getEntity, 'stagedJobs');
        const data = response.data?._embedded.stagedJobs || [];
        if (data)
            yield put(jobsFinished(data));
        else {
            yield put(jobsError([{ message: 'Service unavailbe.' }]));
        }


    } catch (e) {
        console.log('bad', e);
        yield put(jobsError([e.message]));
    }
}

