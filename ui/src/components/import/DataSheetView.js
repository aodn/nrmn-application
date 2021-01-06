import React, { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { AgGridReact } from 'ag-grid-react';
import { AllModules } from 'ag-grid-enterprise';
import { useEffect } from 'react';
import { JobFinished, JobRequested, RowUpdateRequested } from './reducers/create-import';

import { makeStyles } from '@material-ui/core/styles';
import { green } from '@material-ui/core/colors';
import ColunmDef from './ColumnDef';
import { useParams } from 'react-router-dom';
import 'ag-grid-community/dist/styles/ag-grid.css';
import 'ag-grid-community/dist/styles/ag-theme-material.css';
import { Box } from '@material-ui/core';
import useWindowSize from '../utils/useWindowSize';

Object.unfreeze = function (o) {
    var oo = undefined;
    if (o instanceof Array) {
        oo = []; var clone = function (v) { oo.push(v); };
        o.forEach(clone);
    } else if (o instanceof String) {
        oo = new String(o).toString();
    } else if (typeof o == 'object') {
        oo = {};
        for (var property in o) { oo[property] = o[property]; }
    }
    return oo;
};

const DataSheetView = () => {
    const dispatch = useDispatch();
    const immutableRows = useSelector(state => state.import.rows);
    const isLoading = useSelector(state => state.import.isLoading);
    const [gridColumnApi, setGridColumnApi] = useState(null);
    const [gridApi, setGridApi] = useState(null);
    var rows = immutableRows.map(Object.unfreeze);



    const agGridReady = () => {
        const onGridReady = (params) => {
            setGridApi(params.api);
            setGridColumnApi(params.columnApi);
        };

        dispatch(JobFinished());
    };

    const onCellChanged = (input) => {
        const row = rows[input.rowIndex];
        dispatch(RowUpdateRequested(row.id, row));
        //dispatch  updateBackend
    };

    const size = useWindowSize();
    const themeType = useSelector(state => state.theme.themeType);
    const condition = rows && rows.length && !isLoading;
    return (<Box>
        {condition &&
            <div style={{ height: size.height - 165, width: '100%', marginTop: 25 }} className={themeType ? 'ag-theme-material-dark' : 'ag-theme-material'} >
                <AgGridReact
                    pivotMode={false}
                    pivotColumnGroupTotals={'before'}
                    sideBar={true}
                    autoGroupColumnDef={{
                        width: 100,
                        cellRendererParams: {
                            suppressCount: true,
                            innerRenderer: 'nameCellRenderer'
                        }
                    }}
                    onCellValueChanged={onCellChanged}
                    columnDefs={ColunmDef}
                    groupDefaultExpanded={4}
                    rowData={rows}
                    animateRows={true}
                    groupMultiAutoColumn={true}
                    groupHideOpenParents={true}
                    rowSelection={'multiple'}
                    defaultColDef={{
                        filter: true,
                        sortable: true,
                        resizable: true,
                        headerComponentParams: {
                            menuIcon: 'fa-bars'
                        }
                    }}
                    onGridReady={agGridReady}
                    modules={AllModules}
                >
                </AgGridReact>
            </div>}
    </Box>);
};

export default DataSheetView;