import {
    createSlice,
    createAction
} from '@reduxjs/toolkit';


const importState = {
    success: false,
    isLoading: false,
    percentCompleted: 0,
    errors: [],
    rows: [],
    errorsByMsg: [],
    filterIds: [],
    jobId: '',
    job: {}
};

export const exportRow = (row) => {
    const { measureJson } = { ...row };
    Object.getOwnPropertyNames(measureJson)
        .forEach(numKey => {
            row[numKey] = measureJson[numKey];
        });
    delete row.measureJson;
    return row;
};

export const flatten = (row) => {
    const measures = row.MeasureJson;
    if (measures) {
        delete row.MeasureJson;
        return Object.assign(row, measures);
    }
    return row;
};

const mergeErrors = (rows) => {
    if (rows && rows.length > 0)
        return rows.map(row => row.errors).reduce((acc, err) => acc.concat(err));

    return [];
};

const groupBy = (list, keyGetter) => {
    const map = new Map();
    list.forEach((item) => {
        const key = keyGetter(item);
        const collection = map.get(key);
        if (!collection) {
            map.set(key, [item]);
        } else {
            collection.push(item);
        }
    });
    return map;
};


const importSlice = createSlice({
    name: 'import',
    initialState: importState,
    reducers: {
        ImportStarted: (state, action) => {
            console.debug('started');
            state.isLoading = true;
        },
        ImportLoaded: (state, action) => {
            console.debug('loaded');
            state.success = action.payload.errors.length == 0;
            state.isLoading = false;
            state.errors = action.payload.errors;
            state.jobId = action.payload.file.jobId;
        },
        ImportProgress: (state, action) => {
            state.percentCompleted = action.payload.percentCompleted;
        },
        ImportFailed: (state, action) => {
            state.success = false;
            state.isLoading = false;
            state.errors = action.payload.errors;
        },
        JobReady: (state, action) => {
            state.rows = action.payload.rows.map(row => exportRow(row));
            state.job = action.payload.job;
        },
        validationFilter: (state, action) => {
            console.log(action);
            state.filterIds = action.payload;
        },
        validationReady: (state, action) => {
            if (action.payload.rows.length > 0) {
                state.rows = action.payload.rows.map(row => exportRow(row));
                const validationErrors = mergeErrors(state.rows);
                const errorsGrouped = groupBy(validationErrors, (row) => row.message);
                console.log(errorsGrouped);
                state.errorsByMsg = [...errorsGrouped.keys()].map(key => {
                    const length = errorsGrouped.get(key).length;
                    const ids = errorsGrouped.get(key).map(err => err.rowId);
                    return { msg: key, count: errorsGrouped.get(key).length, ids: ids };
                });
                console.log(state.errorsByMsg);
            }
            state.isLoading = false;

        },
        JobStarting: (state) => {
            state.isLoading = true;
        },
        JobFinished: (state) => {
            state.isLoading = false;
        }
    }
});

export const importReducer = importSlice.reducer;
export const { ImportProgress, JobStarting, validationFilter, validationReady, JobFinished, JobReady, ImportStarted, ImportLoaded, ImportFailed } = importSlice.actions;
export const ImportRequested = createAction('IMPORT_REQUESTED', function (xlsFile) { return { payload: xlsFile }; });
export const JobRequested = createAction('JOB_REQUESTED', function (jobId) { return { payload: jobId }; });
export const ValidationRequested = createAction('VALIDATION_REQUESTED', function (jobId) { return { payload: jobId }; });
export const RowUpdateRequested = createAction('ROW_UDPDATE_REQUESTED', function (id, row) { return { payload: { id: id, row: row } }; });

