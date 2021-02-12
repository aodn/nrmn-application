import { takeEvery, call, put } from 'redux-saga/effects';
import { login, loginAttempted, logout, loginFailed, authError, loginSubmitted, logoutSubmitted } from './auth-reducer';
import { userLogin, userLogout } from '../../axios/api';

export default function* LoginWatcher() {
  yield takeEvery(loginSubmitted, apiLogin);
  yield takeEvery(logoutSubmitted, apiLogout);
}

function* apiLogin(loginSubmitted) {
  try {
    yield put(loginAttempted());
    const payload = yield call(userLogin, loginSubmitted.payload);
    if (payload.status == 401 || payload.status == 400){
      yield put (loginFailed());
      return;
    }
    if (payload.status > 401) {
      yield put (authError());
    } else {
      const username = JSON.parse(payload.config.data).username;
      var data = {...payload.data};
       data.username = username;
       data.redirect = loginSubmitted.payload.redirect;
      yield put(login(data));

    }
  } catch (e) {
    yield put(authError(e.message));
  }
}

function* apiLogout(logoutSubmitted) {
  try {
    const payload = yield call(userLogout, logoutSubmitted.payload);
    yield put(logout(payload));
  } catch (e) {
    console.error('ERROR: Logout failed', e);
  }
}
