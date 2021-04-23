import {createSlice} from '@reduxjs/toolkit';

const importState = {
  success: false,
  isLoading: false,
  validationLoading: false,
  editLoading: false,
  deleteLoading: false,
  ingestLoading: false,
  enableSubmit: false,
  submitReady: false,
  ingestSuccess: false,
  percentCompleted: 0,
  indexChanged: {},
  errors: [],
  validationErrors: {},
  errorsByMsg: [],
  errSelected: [],
  globalErrors: [],
  globalWarnings: [],
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

export const importRow = (row) => {
  var measure = {};
  Object.getOwnPropertyNames(row || {})
    .filter((key) => !isNaN(parseFloat(key)))
    .forEach((numKey) => {
      var pos = measureKey.indexOf(numKey);
      measure[pos] = row[numKey];
      delete row[numKey];
    });
  row.measureJson = measure;
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
    ResetState: () => importState,
    JobReady: (state, action) => {
      state.job = action.payload.job;
      state.isLoading = false;
    },
    validationFilter: (state, action) => {
      state.errSelected = action.payload;
    },
    validationReady: (state, action) => {
      
      state.globalWarnings = state.globalErrors = [];
      
      if (action.payload.errors.length > 0) {
        state.validationErrors = action.payload.errors.reduce((acc, err) => {
          acc[err.id] = err.errors;
          return acc;
        }, {});
       const errorsList =  action.payload.errors.map(err =>err.errors);
        const validationErrors = errorsList.reduce((acc,err) => [...acc, ...err], []);
        state.enableSubmit = validationErrors.some((err) => err.errorLevel === 'BLOCKING') === 0;
        state.errorsByMsg = action.payload.summaries;
      } else if (action.payload.errorGlobal) {
        state.globalWarnings = action.payload.errorGlobal.filter((e) => e.errorLevel === 'WARNING');
        state.globalErrors = action.payload.errorGlobal.filter((e) => e.errorLevel === 'BLOCKING');
        state.enableSubmit = state.globalErrors.length === 0;
      } else {
        state.enableSubmit = true;
        state.errorsByMsg = [];
      }
      state.validationLoading = false;
    },
    AddRowIndex: (state, action) => {
      state.indexChanged[action.payload.id] = action.payload.row;
      state.enableSubmit = false;
    },
    RowDeleteRequested: (state) => {
      state.deleteLoading = true;
    },
    RowDeleteFinished: (state) => {
      state.deleteLoading = false;
    },
    RowUpdateRequested: (state) => {
      state.editLoading = true;
    },
    EditRowFinished: (state) => {
      state.editLoading = false;
      state.indexChanged = {};
      state.errors = [];
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
      state.deleteLoading = false;
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
  RowDeleteRequested,
  RowDeleteFinished,
  ResetRowIndex,
  ValidationRequested,
  SubmitingestRequested,
  JobRequested,
  AddRowIndex
} = importSlice.actions;
