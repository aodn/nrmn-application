import {
    createSlice,
    createAction
} from '@reduxjs/toolkit';

const ListState = {
    isLoading: false,
    fileIDs: [],
};

const listSlice = createSlice({
    name: 'fileList',
    initialState: ListState,
    reducers: {
        fileListReady: (state, action) => {
            state.fileIDs = action.payload;
        },
        fileListStarted: (state, action) => {
            state.isLoading = true;
        },
        fileListLoaded: (state, action) => {
            state.isLoading = false;
        },
        fileListFailed: (state, action) => {
            state.isLoading = false;
            console.error(action);
        }
    }

});

export const listFileReducer = listSlice.reducer;
export const {
    fileListReady,
    fileListStarted,
    fileListLoaded,
    fileListFailed } = listSlice.actions;
export const fileListRequested = createAction('FILE_LIST_REQURESTED');