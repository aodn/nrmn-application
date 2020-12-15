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
    jobId: '',
    sheet: [],
    fileID: ''
};

export const exportRow = (row) => {
    const {measureJson} = { ...row};
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
            state.rows = action.payload.Rows.map(row => exportRow(row));
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
export const { ImportProgress, JobStarting, JobFinished, JobReady, ImportStarted, ImportLoaded, ImportFailed } = importSlice.actions;
export const ImportRequested = createAction('IMPORT_REQUESTED', function (xlsFile) { return { payload: xlsFile }; });
export const JobRequested = createAction('JOB_REQUESTED', function (fileID) { return { payload: fileID }; });


