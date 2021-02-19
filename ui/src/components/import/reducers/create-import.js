import {createSlice} from '@reduxjs/toolkit';

const importState = {
  success: false,
  isLoading: false,
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
    console.log(measureKey[numKey] + '- ' + measureJson[numKey]);
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
  if (rows && rows.length > 0) return rows.map((row) => row.errors).reduce((acc, err) => acc.concat(err));

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
      if (action.payload.rows.length > 0) {
        state.rows = action.payload.rows.map((row) => exportRow(row));
        const validationErrors = mergeErrors(state.rows);
        state.EnableSubmit = validationErrors.filter((err) => err.level === 'BLOCKING').length === 0;
        const errorsGrouped = groupBy(validationErrors, (row) => row.message);
        state.errorsByMsg = [...errorsGrouped.keys()]
          .map((key) => {
            const elems = errorsGrouped.get(key);
            const ids = errorsGrouped.get(key).map((err) => err.rowId);
            return {
              msg: key,
              count: elems.length,
              ids: ids,
              columnTarget: elems[0].columnTarget,
              level: elems[0].errorLevel
            };
          })
          .sort((err1, err2) => {
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
    AddRowIndex: (state, action) => {
      state.indexChanged[action.payload.id] = action.payload.row;
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
      state.isLoading = true;
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
