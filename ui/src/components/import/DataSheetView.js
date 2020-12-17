import React from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { AgGridReact } from 'ag-grid-react';
import { AllModules } from 'ag-grid-enterprise';
import { useEffect } from 'react';
import { JobFinished, JobRequested } from './reducers/create-import';

import { makeStyles } from '@material-ui/core/styles';
import { green } from '@material-ui/core/colors';
import ColunmDef from './ColumnDef';
import { useParams } from 'react-router-dom';
import 'ag-grid-community/dist/styles/ag-grid.css';
import 'ag-grid-community/dist/styles/ag-theme-material.css';
import { Box } from '@material-ui/core';
import useWindowSize from '../utils/useWindowSize';


const DataSheetView = () => {
    const dispatch = useDispatch();
    const rows = useSelector(state => state.import.rows);
    const isLoading = useSelector(state => state.import.isLoading);

    const agGridReady = () => {
        dispatch(JobFinished());
    };

    const size = useWindowSize();
    const themeType = useSelector(state => state.theme.themeType);
    const condition = rows && rows.length && !isLoading;
    return (<Box>
        {condition &&
            <div style={{ height: size.height - 165, width: '100%', marginTop: 25 }} className={themeType ? 'ag-theme-material-dark' : 'ag-theme-material'} >
                <AgGridReact
                    pivotMode={true}
                    pivotColumnGroupTotals={'before'}
                    sideBar={true}
                    autoGroupColumnDef={{
                        width: 100,
                        cellRendererParams: {
                            suppressCount: true,
                            innerRenderer: 'nameCellRenderer'
                        }
                    }}

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
                //onGridReady={onGridReady}
                >
                </AgGridReact>
            </div>}
    </Box>);
};

export default DataSheetView;