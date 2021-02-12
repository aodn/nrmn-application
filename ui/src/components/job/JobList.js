import {Grid, Fab, Button, Typography, makeStyles, CircularProgress} from '@material-ui/core';
import {AgGridReact} from 'ag-grid-react';
import React, {useEffect} from 'react';
import {useSelector, useDispatch} from 'react-redux';
import {jobsRequested} from './jobReducer';
import LinkCell from '../data-entities/customWidgetFields/LinkCell';
import {NavLink} from 'react-router-dom';
import CloudUploadOutlinedIcon from '@material-ui/icons/CloudUploadOutlined';
import {Backdrop} from '@material-ui/core';

import 'ag-grid-community/dist/styles/ag-grid.css';
import 'ag-grid-community/dist/styles/ag-theme-material.css';

const columnDef = [
  {
    field: 'id',
    cellRendererFramework: function stagedRender(params) {
      return <LinkCell link={'/jobs/' + params.data.id + '/view'} label={params.data.id}></LinkCell>;
    },
    filter: false
  },
  {
    field: 'reference',
    filter: 'agTextColumnFilter'
  },
  {
    field: 'isExtendedSize',
    headerName: 'Extended',
    filter: false
  },
  {
    field: 'status',
    cellRendererFramework: function stagedRender(params) {
      return (
        <Button disabled={params.data.status != 'STAGED'} component={NavLink} to={'/validation/' + params.data.id}>
          {params.data.status}
        </Button>
      );
    },
    filter: 'agTextColumnFilter'
  },
  {
    field: 'Program',
    cellRenderer: (params) => {
      return params.data.program.programName;
    },
    filterValueGetter: (params) => {
      return params.data.program.programName;
    },
    valueGetter: (params) => {
      return params.data.program.programName;
    }
  },
  {
    field: 'source',
    headerName: 'Type'
  },
  {
    field: 'Initiator',

    cellRenderer: (params) => {
      return params.data.creator.email;
    },
    filter: 'agTextColumnFilter',
    filterValueGetter: (params) => {
      return params.data.creator.email;
    },
    valueGetter: (params) => {
      return params.data.creator.email;
    }
  },

  {
    field: 'last updated',
    cellRenderer: (params) => {
      return new Date(params.data.lastUpdated).toLocaleString();
    },
    valueGetter: (params) => {
      return new Date(params.data.lastUpdated);
    },
    filter: 'agDateColumnFilter'
  },
  {
    field: 'created date',
    cellRenderer: (params) => {
      return new Date(params.data.created).toLocaleString();
    },
    valueGetter: (params) => {
      return new Date(params.data.created);
    },
    filter: 'agDateColumnFilter'
  }
];

const useStyles = makeStyles((theme) => ({
  extendedIcon: {
    marginRight: theme.spacing(1)
  }
}));

const JobList = () => {
  const dispatch = useDispatch();
  const jobs = useSelector((state) => state.job.jobs);
  const themeType = useSelector((state) => state.theme.themeType);
  const classes = useStyles();
  const isLoading = useSelector((state) => state.job.isLoading);
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

  return (
    <>
      <Grid container direction="row" justify="space-between" alignItems="center" style={{padding: '23px'}}>
        <Grid item>
          <Typography variant="h4">Jobs</Typography>
        </Grid>
        <Grid item>
          <Grid container spacing={2}>
            <Fab variant="extended" size="small" color="secondary" component={NavLink} to="/upload">
              <CloudUploadOutlinedIcon className={classes.extendedIcon} />
              Upload File
            </Fab>
          </Grid>
        </Grid>
      </Grid>
      {isLoading && (
        <Backdrop open={isLoading}>
          <CircularProgress size={200} style={{color: '#ccc'}}></CircularProgress>
        </Backdrop>
      )}
      {jobs && jobs.length > 0 && (
        <div style={{height: '100%'}} className={themeType ? 'ag-theme-material-dark' : 'ag-theme-material'}>
          <AgGridReact
            sideBar={'filters'}
            columnDefs={columnDef}
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
            }}
          />
        </div>
      )}
    </>
  );
};

export default JobList;
