import { takeEvery, call, put } from "redux-saga/effects";
import {login, logout, authError, loginSubmitted, logoutSubmitted} from "./auth-reducer";
import { userLogin, userLogout } from "../../axios/api";

export default function* LoginWatcher() {
  yield takeEvery(loginSubmitted, apiLogin);
  yield takeEvery(logoutSubmitted, apiLogout);
}

function* apiLogin(loginSubmitted) {
  try {
    const payload = yield call(userLogin, loginSubmitted.payload);
    yield put(login(payload));
  } catch (e) {
    yield put(authError(e));
  }
}

function* apiLogout(logoutSubmitted) {
  try {
    const payload = yield call(userLogout, logoutSubmitted.payload);
    yield put(logout(payload));
  } catch (e) {
    yield put(authError(e));
  }
}
