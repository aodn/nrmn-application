import { takeEvery, call, put } from "redux-saga/effects";
import { definitionRequested, definitionLoaded, definitionError } from "../redux-form";
import { rawSurveySave, definition } from "../../../axios/api";

export default function* getDefinitionWatcher() {
    yield takeEvery(definitionRequested, getDefinition);
}

function* getDefinition() {
    try {
        console.log("call def")
        const payload = yield call(definition);
        console.log(payload)
        yield put(definitionLoaded(payload.data));
    } catch (e) {
        yield put(definitionError(e));
    }
}

