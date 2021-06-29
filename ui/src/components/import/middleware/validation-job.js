import {call, put, takeEvery} from 'redux-saga/effects';
import {getDataJob, postJobValidation, submitingest} from '../../../axios/api';
import {
  ingestFailed,
  ingestFinished,
  jobFailed,
  JobReady,
  JobRequested,
  SubmitingestRequested,
  validationReady,
  ValidationRequested
} from '../reducers/create-import';

export default function* Watcher() {
  yield takeEvery(JobRequested, loadJob);
  yield takeEvery(ValidationRequested, validateJob);
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

function* submit(action) {
  try {
    const {response} = yield call(submitingest, action.payload);
    if (response.status != 200) {
      yield put(ingestFailed(response.data));
    } else {
      yield put(ingestFinished(response.data));
    }
  } catch (error) {
    yield put(ingestFailed([error]));
  }
}
