import {createSlice} from '@reduxjs/toolkit';
import produce from 'immer';

const importState = {
  success: false,
  isLoading: false,
  validationLoading: false,
  editLoading: false,
  ingestLoading: false,
  submitReady: false,
  ingestSuccess: false,
  percentCompleted: 0,
  indexChanged: {},
  errors: [],
  rows: [],
  errorsByMsg: [],
  errSelected: [],
  jobId: '',
  job: {}
};

export const exportRow = (row) => {
  const {measureJson} = {...row};
  Object.getOwnPropertyNames(measureJson || {}).forEach((numKey) => {
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

const mergeErrors = (errors) => {
  if (errors && errors.length > 0)
    return errors
      .map((err) => err.errors | [])
      .reduce((acc, err) => {
        return acc.concat(err);
      }, []);
  return [];
};

const importSlice = createSlice({
  name: 'import',
  initialState: importState,
  reducers: {
    ResetState: () => importState,
    JobReady: (state, action) => {
      if (action.payload.rows && action.payload.rows.length > 0) {
        state.rows = action.payload.rows.map((row) => exportRow(row));
      }
      state.job = action.payload.job;
      state.isLoading = false;
    },
    validationFilter: (state, action) => {
      state.errSelected = action.payload;
    },
    validationReady: (state, action) => {
      return produce(state, (draft) => {
        if (action.payload.errors.length > 0) {
          const errors = action.payload.errors.reduce((acc, err) => {
            acc[err.id] = err.errors;
            return acc;
          }, {});

          draft.rows = draft.rows.map((row) => {
            const err = errors[row.id] || [];
            return {
              ...row,
              errors: err
            };
          });

          const validationErrors = mergeErrors(action.payload.errors);
          draft.EnableSubmit = validationErrors.filter((err) => err.level === 'BLOCKING').length === 0;
          draft.errorsByMsg = action.payload.summaries.sort((sum1, sum2) => sum2.count - sum1.count);
          draft.validationLoading = false;
        }
      });
    },
    AddRowIndex: (state, action) => {
      return produce(state, (draft) => {
        draft.rows[action.payload.id][action.payload.field] = action.payload.value;
        const row = draft.rows[action.payload.id];
        draft.indexChanged[row.id] = row;

        draft.EnableSubmit = false;
      });
    },
    RowUpdateRequested: (state) => {
      state.editLoading = true;
    },
    EditRowFinished: (state) => {
      state.editLoading = false;
      state.indexChanged = {};
    },
    JobStarting: (state) => {
      state.isLoading = true;
    },
    JobRequested: (state) => {
      state.isLoading = true;
    },
    SubmitingestRequested: (state) => {
      state.ingestLoading = true;
    },
    ValidationRequested: (state) => {
      state.validationLoading = true;
    },
    JobFinished: (state) => {
      state.isLoading = false;
    },
    ingestFinished: (state) => {
      state.ingestLoading = false;
      state.ingestSuccess = true;
    },
    EnableSubmit: (state, action) => {
      state.submitReady = action.payload;
    },
    jobFailed: (state, action) => {
      state.success = false;
      state.isLoading = false;
      state.validationLoading = false;
      if (action.payload) {
        state.errors = action.payload;
      }
    }
  }
});

export const importReducer = importSlice.reducer;
export const {
  ResetState,
  jobFailed,
  JobStarting,
  validationFilter,
  validationReady,
  EditRowStarting,
  EditRowFinished,
  JobFinished,
  JobReady,
  ingestFinished,
  EnableSubmit,
  RowUpdateRequested,
  ResetRowIndex,
  ValidationRequested,
  SubmitingestRequested,
  JobRequested,
  AddRowIndex
} = importSlice.actions;
