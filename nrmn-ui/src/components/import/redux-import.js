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

const arrray2JSON = (sheet) => {
    const header = sheet.shift();
    return sheet.map(rowArr => {
        return array2obj(header, rowArr)
    });
}

const header2ColDef = (header) => {

    const group = {
        hide: true,
        rowGroup: true,
    }

    var coldef = header.map(key => ({ field: key + "", editable: true, width: 100 }));
    coldef[1].enablePivot = true;
    Object.assign(coldef[3], group);
    Object.assign(coldef[12], group);
    Object.assign(coldef[13], group);
    Object.assign(coldef[14], group);
    Object.assign(coldef[16], {aggFunc : 'count', headerName: ''});
    return coldef
}

const importSlice = createSlice({
    name: "import",
    initialState: importState,
    reducers: {
        loadXlxs: (state, action) => {
            state.columnDefs = header2ColDef(action.payload[0])
            state.sheet = arrray2JSON(action.payload)
        }
    },
}); { }

export const importReducer = importSlice.reducer;
export const { loadXlxs } = importSlice.actions;

