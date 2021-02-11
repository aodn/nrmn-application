import {takeEvery, call, put} from 'redux-saga/effects';
import {getWorms} from '../../../axios/api';
import { wormsSearchRequested, wormsSearchFound, wormsSearchFailed } from '../form-reducer';


export default function* getMatchingWorms() {
  yield takeEvery(wormsSearchRequested, getWormsRecord);
}

function* getWormsRecord(action) {
  try {
    const resp = yield call(getWorms, action.payload.aphia_id);
    yield put(wormsSearchFound(resp.data));
  } catch (error) {
    yield put(wormsSearchFailed(error));
  }
}
