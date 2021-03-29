import {takeEvery, call, put} from 'redux-saga/effects';
import {search} from '../../../axios/api';
import {searchRequested, searchFound, searchFailed} from '../form-reducer';

export default function* getMatchingWorms() {
  yield takeEvery(searchRequested, getSearchResult);
}

function* getSearchResult(action) {
  try {
    const resp = yield call(search, action.payload);
    yield put(searchFound(resp.data));
  } catch (error) {
    yield put(searchFailed(error));
  }
}
