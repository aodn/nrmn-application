import {
    createSlice,
} from '@reduxjs/toolkit';

const jobSlice = createSlice({
    name: 'job',
    initialState: {
        jobs : [],
        errors: [],
        isLoading: false
    },
    reducers: {
        jobsRequested : (state) => {
            state.isLoading = true;
        },
        jobsFinished : (state, action) => {
            state.jobs = action.payload;
        },
        jobsError : (state, action) => {
            state.isLoading = false;
            state.errors = action.paylaod;

        },
    }
});


export const {jobsRequested , jobsFinished,jobsError} = jobSlice.actions;
export const jobReducer = jobSlice.reducer;
