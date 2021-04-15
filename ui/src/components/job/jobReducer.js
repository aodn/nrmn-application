import {createSlice} from '@reduxjs/toolkit';
const jobState = {
  jobs: [],
  errors: [],
  isLoading: false,
  currentJob: null
};

const jobSlice = createSlice({
  name: 'job',
  initialState: jobState,
  reducers: {
    ResetState: () => jobState,
    jobsRequested: (state) => {
      state.isLoading = true;
    },
    jobRequested: (state) => {
      state.isLoading = true;
    },
    jobFinished: (state, action) => {
      state.isLoading = false;
      state.currentJob = action.payload;
    },
    DeleteJobRequested: (state) => {
      state.isLoading = true;
    },
    DeleteFinished: (state) => {
      state.isLoading = false;
    },
    jobsFinished: (state, action) => {
      state.isLoading = false;

      state.jobs = action.payload.reverse();
    },
    jobsError: (state, action) => {
      state.isLoading = false;
      state.errors = action.paylaod;
    }
  }
});

export const {
  ResetState,
  DeleteJobRequested,
  DeleteFinished,
  jobsRequested,
  jobFinished,
  jobsFinished,
  jobRequested,
  jobsError
} = jobSlice.actions;
export const jobReducer = jobSlice.reducer;
