import {
    createSlice
} from "@reduxjs/toolkit";


const importState = {
    sheet: [],
    columnDefs: []
}

const array2obj = (header, rowArr) => {
    const obj = {};
    header.forEach((key, i) => {
        obj[key] = rowArr[i]
    })
    return obj;
}

const sheet2JSON = (sheet) => {
    const header = sheet.shift();
    return sheet.map(rowArr => {
        return array2obj(header, rowArr)
    });
}

const header2ColDef = (header) => {
    console.log(header)
    var coldef = header.map(key => ({field: key + "", editable : true}));
    coldef[1].enablePivot = true;
    coldef[3].enableRowGroup = true;
    coldef[12].enableRowGroup = true;
    coldef[13].enableRowGroup = true;
    coldef[14].enableRowGroup = true;
    coldef[15].enableRowGroup = true;
    coldef[16].aggFunc = 'count';
    return coldef
}


const importSlice = createSlice({
    name: "import",
    initialState: importState,
    reducers: {
        loadXlxs: (state, action) => {
            state.columnDefs = header2ColDef(action.payload[0])
            state.sheet = sheet2JSON(action.payload)
        }
    },
});{}

export const importReducer = importSlice.reducer;
export const { loadXlxs } = importSlice.actions;

