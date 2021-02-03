import {
    createSlice,
    createAction
} from '@reduxjs/toolkit';


const importState = {
    success: false,
    isLoading: false,
    editLoading: false,
    ingestLoading: false,
    submitReady: false,
    ingestSuccess: false,
    percentCompleted: 0,
    errors: [],
    rows: [],
    errorsByMsg: [],
    errSelected: [],
    jobId: '',
    job: {}
};

export const exportRow = (row) => {
    const { measureJson } = { ...row };
    Object.getOwnPropertyNames(measureJson || {})
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
        ImportStarted: (state) => {
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
            if (action.payload) {
                state.errors = action.payload;
            }
        },
        JobReady: (state, action) => {
            state.rows = action.payload.rows.map(row => exportRow(row));
            state.job = action.payload.job;
        },
        validationFilter: (state, action) => {
            state.errSelected = action.payload;
        },
        validationReady: (state, action) => {
            if (action.payload.rows.length > 0) {
                state.rows = action.payload.rows.map(row => exportRow(row));
                const validationErrors = mergeErrors(state.rows);
                state.EnableSubmit = validationErrors.filter(err => err.level === 'BLOCKING').length === 0;
                const errorsGrouped = groupBy(validationErrors, (row) => row.message);
                state.errorsByMsg = [...errorsGrouped.keys()].map(key => {
                    const elems = errorsGrouped.get(key);
                    const ids = errorsGrouped.get(key).map(err => err.rowId);
                    return {
                        msg: key,
                        count: elems.length,
                        ids: ids,
                        columnTarget: elems[0].columnTarget,
                        level: elems[0].errorLevel
                    };
                }).sort((err1, err2) => {
                    if (err1.level == err2.level) {
                        return err2.count - err1.count;
                    }
                    return err1.level === 'BLOCKING' ? -1 : 1;
                });
            } else {
                state.EnableSubmit = true;
            }
            state.isLoading = false;

        },
        EditRowStarting: (state) => {
            state.editLoading = true;
        },
        EditRowFinished: (state) => {
            state.editLoading = false;
        },
        JobStarting: (state) => {
            state.isLoading = true;
        },
        JobFinished: (state) => {
            state.isLoading = false;
        },
        ingestStarting: (state) => {
            state.ingestLoading = true;
        },
        ingestFinished: (state) => {
            state.ingestLoading = false;
            state.ingestSuccess = true;
        },
        EnableSubmit: (state, action) => {
            state.submitReady = action.payload;
        }
    }
});

export const importReducer = importSlice.reducer;
export const {
    ImportProgress,
    JobStarting,
    validationFilter,
    validationReady,
    EditRowStarting,
    EditRowFinished,
    JobFinished,
    JobReady,
    ImportStarted,
    ImportLoaded,
    ingestStarting,
    ingestFinished,
    EnableSubmit,
    ImportFailed } = importSlice.actions;
export const ImportRequested = createAction('IMPORT_REQUESTED', function (xlsFile) { return { payload: xlsFile }; });
export const JobRequested = createAction('JOB_REQUESTED', function (jobId) { return { payload: jobId }; });
export const ValidationRequested = createAction('VALIDATION_REQUESTED', function (jobId) { return { payload: jobId }; });
export const RowUpdateRequested = createAction('ROW_UDPDATE_REQUESTED', function (id, row) { return { payload: { id: id, row: row } }; });
export const SubmitingestRequested = createAction('SUBMIT_ingest_REQUESTED', function (jobId) { return { payload: jobId }; });
