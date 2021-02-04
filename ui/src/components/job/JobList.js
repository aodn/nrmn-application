import { Box, Grid, Fab, Button, Typography, makeStyles } from '@material-ui/core';
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
        },
        filter: 'agNumberColumnFilter'
    },
    {
        field: 'reference',
        filter: 'agTextColumnFilter'

    },
    {
        field: 'isExtendedSize',
        headerName: 'Extended'
    },
    {
        field: 'status',
        cellRendererFramework: function stagedRender(params) {

            return (<Button
                disabled={params.data.status != 'STAGED'}
                component={NavLink}
                to={'/validation/' + params.data.id}>
                {params.data.status}
            </Button>);
        },
        filter: 'agTextColumnFilter'


    },
    {
        field: 'Program',
        cellRenderer: (params) => {
            return params.data.program.programName;
        },
        filter: 'agTextColumnFilter'


    },
    {
        field: 'source',
        headerName: 'type'
    }, {
        field: 'Initiator',
        cellRenderer: (params) => { return params.data.creator.email; },
        filter: 'agTextColumnFilter'

    },
    {
        field: 'last updated',
        cellRenderer: (params) => {
            return new Date(params.data.lastUpdated).toLocaleString();
        },
        filter: 'agDateColumnFilter'

    },
    {
        field: 'created date',
        cellRenderer: (params) => {
            return new Date(params.data.created).toLocaleString();
        },
        filter: 'agDateColumnFilter'

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
    useEffect(() => {
        dispatch(jobsRequested());
    }, []);

    const onReady = (params) => {
        params.api.setRowData(jobs);
        var allColumnIds = [];
        params.columnApi.getAllColumns().forEach(function (column) {
            allColumnIds.push(column.colId);
        });
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
                sideBar={'filters'}
                columnDefs={colunmDef}
                rowSelection="single"
                animateRows={true}
                // rowData={jobs}
                onGridReady={onReady}
                defaultColDef={{
                    sortable: true,
                    resizable: true,
                    filter: true,
                    headerComponentParams: {
                        menuIcon: 'fa-bars'
                    }
                }} />
        </div>)}
    </Box>);
};

export default JobList;