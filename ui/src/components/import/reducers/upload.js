import { createSlice } from '@reduxjs/toolkit';

const importState = {
    isLoading: false,
    success: false,
    errors: [],
    jobId: '',
    percentCompleted: 0,
    insertedCount: 0,
    formData: {}
};

const uploadSlice = createSlice({
    name: 'upload',
    initialState: importState,
    reducers: {
        ImportReset: () => importState,
        ImportRequested: (state) => {
            state.isLoading = true;
        },
        ImportLoaded: (state, action) => {
            state.isLoading = false;
            state.success = true;
            state.jobId = action.payload.file.jobId;
            state.formData = {};

        },
        ImportProgress: (state, action) => {
            state.percentCompleted = action.payload.percentCompleted;
        },
        ImportFailed: (state, action) => {
            state.success = false;
            state.isLoading = false;
            state.formData = {};

            if (action.payload) {
                state.errors = action.payload;
            }
        }
    }
});

export const { ImportReset, ImportRequested, ImportLoaded, ImportProgress, ImportFailed } = uploadSlice.actions;
export const uploadReducer = uploadSlice.reducer;
