import {
    createSlice,
    createAction
} from '@reduxjs/toolkit';


const importState = {
    success: false,
    isLoading: false,
    percentCompleted: 0,
    jobId: '',
    sheet: [],
    fileID: ''
};

export const exportRow = (row) => {
    const jsonRow = { ...row, MeasureJson: {} };
    Object.getOwnPropertyNames(jsonRow).filter(key => !isNaN(key)).forEach(numKey => {
        jsonRow.MeasureJson[numKey] = jsonRow[numKey];
        delete jsonRow[numKey];
    });
    return jsonRow;
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
            state.success = true;
            state.isLoading = false;
            state.jobId = action.payload.file.jobId;
        },
        ImportProgress:(state, action) => {
            state.percentCompleted = action.payload.percentCompleted;
        },
        ImportFailed: (state, action) => {
            state.success = false;
            state.isLoading = false;
            console.debug('data import falied:', action);
        }
    }
});

export const importReducer = importSlice.reducer;
export const { ImportProgress, ImportStarted, ImportLoaded, ImportFailed } = importSlice.actions;
export const ImportRequested = createAction('IMPORT_REQUESTED', function (xlsFile) { return { payload: xlsFile }; });
export const FileRequested = createAction('FILE_REQUESTED', function (fileID) { return { payload: fileID }; });


