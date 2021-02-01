import { Box, Grid, Fab, Typography, makeStyles } from '@material-ui/core';
import { AgGridReact } from 'ag-grid-react';
import React, { useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import useWindowSize from '../utils/useWindowSize';
import { jobsRequested } from './jobReducer';
import LinkCell from '../data-entities/customWidgetFields/LinkCell';
import { NavLink } from 'react-router-dom';
import CloudDownloadOutlinedIcon from '@material-ui/icons/CloudDownloadOutlined';
import CloudUploadOutlinedIcon from '@material-ui/icons/CloudUploadOutlined';
import 'ag-grid-community/dist/styles/ag-grid.css';
import 'ag-grid-community/dist/styles/ag-theme-material.css';
const colunmDef = [
    {
        field: 'id',
        cellRendererFramework: function stagedRender(params) {
            return (<LinkCell link={'/view/stagedJobs/' + params.data.id} label={params.data.id}></LinkCell>);
        }
    },
    {
        field: 'reference',

    },
    {
        field: 'isExtendedSize',
        headerName: 'Extended'
    },
    {
        field: 'status',
        cellRendererFramework: function stagedRender(params) {
            return (params.data.status == 'STAGED') ?
                (<LinkCell link={'/validation/' + params.data.id} label={params.data.status}></LinkCell>) :
                (<Typography color="secondary">{params.data.status}</Typography>);

        }

    },
    {
        field: 'Program',
        cellRenderer: (params) => {
            return params.data.program.programName;
        }

    },
    {
        field: 'source'
    }, {
        field: 'Initiator',
        cellRenderer: (params) => { return params.data.creator.email; }
    },
    {
        field: 'last updated',
        cellRenderer: (params) => {
            return new Date(params.data.lastUpdated).toLocaleString();
        }
    },
    {
        field: 'created date',
        cellRenderer: (params) => {
            return new Date(params.data.created).toLocaleString();
        }
    }

];

const useStyles = makeStyles((theme) => ({
    extendedIcon: {
        marginRight: theme.spacing(1),
    }
}));


const JobList = () => {
    const dispatch = useDispatch();;
    const jobs = useSelector(state => state.job.jobs);
    const size = useWindowSize();
    const classes = useStyles();
    console.log('jobs', jobs);
    useEffect(() => {
        dispatch(jobsRequested());
    }, []);

    const onReady = (params) => {
        params.api.setRowData(jobs);
        var allColumnIds = [];
        params.columnApi.getAllColumns().forEach(function (column) {
            allColumnIds.push(column.colId);
        });
        console.log(allColumnIds);
        params.columnApi.autoSizeColumns(allColumnIds, false);
    };

    return (<Box>

        <Grid
            container
            direction="row"
            justify="space-between"
            alignItems="center"
        >
            <Typography variant="h4">Jobs</Typography>
            <Grid item  >
                <Grid
                    container
                    justify="space-between"
                    spacing={2}
                >
                    <Grid item>
                        <Fab variant="extended" size="small" color="secondary" component={NavLink} to='/upload'>
                            <CloudUploadOutlinedIcon className={classes.extendedIcon} />Upload File</Fab>
                    </Grid>
                    <Grid item>
                        <Fab variant="extended" size="small" color="secondary" component={NavLink} to="/jobs" disabled={true}>
                            <CloudDownloadOutlinedIcon className={classes.extendedIcon} />Pull Correction</Fab>
                    </Grid>
                </Grid>
            </Grid>
        </Grid>
        {jobs && jobs.length > 0 && (<div style={{ width: '100%', height: size.height - 170, marginTop: 25 }}
            className={'ag-theme-material'}>
            <AgGridReact
                columnDefs={colunmDef}
                rowSelection="single"
                animateRows={true}
                // rowData={jobs}
                onGridReady={onReady}
                defaultColDef={{
                    sortable: true,
                    resizable: true,
                    headerComponentParams: {
                        menuIcon: 'fa-bars'
                    }
                }} />
        </div>)}
    </Box>);
};

export default JobList;