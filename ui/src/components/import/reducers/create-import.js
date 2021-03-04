import {createSlice} from '@reduxjs/toolkit';

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
  validationErrors: {},
  errorsByMsg: [],
  errSelected: [],
  jobId: '',
  job: {}
};
const measureKey = [
  'Inverts',
  '2-5',
  '5',
  '7-5',
  '10',
  '12-5',
  '15',
  '20',
  '25',
  '30',
  '35',
  '40',
  '50',
  '62-5',
  '75',
  '87-5',
  '100',
  '112-5',
  '125',
  '137-5',
  '150',
  '162-5',
  '175',
  '187-5',
  '200',
  '250',
  '300',
  '350',
  '400',
  '450',
  '500',
  '550',
  '600',
  '650',
  '700',
  '750',
  '800',
  '850',
  '900',
  '950',
  '1000'
];

export const exportRow = (row) => {
  const {measureJson} = {...row};
  Object.getOwnPropertyNames(measureJson || {}).forEach((numKey) => {
    row[measureKey[numKey]] = measureJson[numKey];
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
      state.job = action.payload.job;
      state.isLoading = false;
    },
    validationFilter: (state, action) => {
      state.errSelected = action.payload;
    },
    validationReady: (state, action) => {
      if (action.payload.errors.length > 0) {
        state.validationErrors = action.payload.errors.reduce((acc, err) => {
          acc[err.id] = err.errors;
          return acc;
        }, {});

        const validationErrors = mergeErrors(action.payload.errors);
        state.EnableSubmit = validationErrors.filter((err) => err.level === 'BLOCKING').length === 0;
        state.errorsByMsg = action.payload.summaries.sort((sum1, sum2) => sum2.count - sum1.count);
      } else {
        state.EnableSubmit = true;
      }
    },
    AddRowIndex: (state, action) => {
        state.indexChanged[action.payload.id] = action.payload.row;
        state.EnableSubmit = false;
    },
    RowUpdateRequested: (state) => {
      state.editLoading = true;
    },
    EditRowFinished: (state) => {
      state.editLoading = false;
      state.indexChanged = {};
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
    ValidationFinished: (state) => {
      state.validationErrors = {};
      state.validationLoading = false;
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
  validationFilter,
  validationReady,
  ValidationFinished,
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
