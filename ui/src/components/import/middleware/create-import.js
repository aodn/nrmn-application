import { takeEvery, call, put } from 'redux-saga/effects';
import { ImportRequested, ImportLoaded, ImportFailed } from '../reducers/create-import';
import { submitJobFile } from '../../../axios/api';

export default function* createImportWatcher() {
    yield takeEvery(ImportRequested, createImport);
}

function* createImport(params) {
    try {
        //  const {response, err} =  yield call(submitJobFile, params.payload);
        const { response} = yield call(submitJobFile, params.payload);
        const data = response.data || {};
        var errors = data.errors || [];
        if (data.error) {
            errors = [{ message: data.error }];
        }
        console.debug('data', data);
        console.debug('errors', errors);
        if (errors.length > 0) {
            yield put(ImportFailed(errors));

        } else {
            if (data)
                yield put(ImportLoaded(data));
            else {
                yield put(ImportFailed([{ message: 'Service unavailbe.' }]));
            }

        }
    } catch (e) {
        console.log('bad', e);
        yield put(ImportFailed([e.message]));
    }
}

