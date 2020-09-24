import React from "react";
import {  useDispatch, useSelector } from "react-redux";
import { AgGridReact } from "ag-grid-react";
import { AllModules } from "ag-grid-enterprise";
import { useEffect } from 'react';
import { ImportLoaded, FileRequested } from './reducers/create-import';

import CircularProgress from '@material-ui/core/CircularProgress';
import { makeStyles } from '@material-ui/core/styles';
import { green } from '@material-ui/core/colors';
import ColunmDef from "./ColumnDef";
import { useParams } from "react-router-dom";
import useWindowSize from "../utils/useWindowSize";



const DataSheetView = () => {


    const {fileID} = useParams();
    const dispatch = useDispatch();

    useEffect(() => {
        if (fileID) {
            dispatch(FileRequested(fileID));
        }
    },[]);
    const sheet = useSelector(state => state.import.sheet)
    const isLoading = useSelector(state => state.import.isLoading);

    const agGridReady = () => {
        dispatch(ImportLoaded());
    }
    const useStyles = makeStyles((theme) => ({
        wrapper: {
            margin: theme.spacing(1),
            position: 'relative',
        },
        buttonSuccess: {
            backgroundColor: green[500],
            '&:hover': {
                backgroundColor: green[700],
            },
        },
        buttonProgress: {
            color: green[500],
            position: 'absolute',
            top: '50%',
            left: '50%',
            marginTop: -12,
            marginLeft: -12,
        }
    }));
    const classes = useStyles();
    const size = useWindowSize();
    const themeType = useSelector(state =>  state.theme.themeType);

    return (sheet && sheet.length && !isLoading) ? (
        <div style={{ height: size.height - 200, width: '100%', marginTop: 25 }} className={ themeType ? "ag-theme-alpine-dark" : "ag-theme-alpine" } >
            <AgGridReact
                pivotMode={true}
                pivotColumnGroupTotals={"before"}
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
                rowData={sheet}
                rowSelection="multiple"
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
                }
                }
                onGridReady={agGridReady}
                modules={AllModules}
            //onGridReady={onGridReady}
            >
            </AgGridReact>
        </div>
    ) : ((isLoading) ? (<CircularProgress size={200} className={classes.buttonProgress} />)
        : (<></>))
}

export default DataSheetView;