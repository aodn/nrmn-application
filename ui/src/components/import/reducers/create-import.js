import {
    createSlice,
    createAction
} from '@reduxjs/toolkit';
import { FilledInput } from '@material-ui/core';


const importState = {
    isLoading: false,
    sheet: [],
    fileID: ''
};

const array2obj = (header, rowArr) => {
    const obj = {};
    header.forEach((key, i) => {
        obj[key] = rowArr[i];
    });
    return obj;
};

export const arrray2JSON = (sheet) => {
    const header = sheet.shift();
    sheet.shift();
    return sheet.map(rowArr => {
        return array2obj(header, rowArr);
    });
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
        rawSurveyReady: (state, action) => {
            state.fileID = action.payload.fileID;
            state.sheet = action.payload.Rows;
        },
        ImportStarted: (state, action) =>  {
            state.isLoading = true;
        },
        ImportLoaded: (state, action) => {

            state.isLoading  = false;
        },
        ImportFailed: (state, action) => {
            state.isLoading  = false;
        }
    }
});

export const importReducer = importSlice.reducer;
export const { rawSurveyReady,ImportStarted, ImportLoaded, ImportFailed } = importSlice.actions;
export const ImportRequested = createAction('IMPORT_REQUESTED', function (xlsFile) { return { payload: xlsFile }; });
export const FileRequested = createAction('FILE_REQUESTED', function(fileID) { return {payload: fileID};});


