import { takeEvery, call, put } from "redux-saga/effects";
import {login, loginAttempted, logout, authError, loginSubmitted, logoutSubmitted} from "./auth-reducer";
import { userLogin, userLogout } from "../../axios/api";

export default function* LoginWatcher() {
  yield takeEvery(loginSubmitted, apiLogin);
  yield takeEvery(logoutSubmitted, apiLogout);
}

function* apiLogin(loginSubmitted) {
  try {
    yield put(loginAttempted());
    const payload = yield call(userLogin, loginSubmitted.payload);
    payload.redirect = loginSubmitted.payload.redirect;
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
    console.log("ERROR: Logout failed", e)
  }
}
