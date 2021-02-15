import {Box, Grid, Fab, Button, Typography, makeStyles, CircularProgress, IconButton, useMediaQuery, useTheme} from '@material-ui/core';
import {AgGridReact} from 'ag-grid-react';
import React, {useEffect, useState} from 'react';
import {useSelector, useDispatch} from 'react-redux';
import useWindowSize from '../utils/useWindowSize';
import {jobsRequested, DeleteJobRequested} from './jobReducer';
import LinkCell from '../data-entities/customWidgetFields/LinkCell';
import {NavLink} from 'react-router-dom';
import CloudUploadOutlinedIcon from '@material-ui/icons/CloudUploadOutlined';
import {Backdrop} from '@material-ui/core';
import DeleteForeverOutlinedIcon from '@material-ui/icons/DeleteForeverOutlined';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';

import 'ag-grid-community/dist/styles/ag-grid.css';
import 'ag-grid-community/dist/styles/ag-theme-material.css';

const useStyles = makeStyles((theme) => ({
  extendedIcon: {
    marginRight: theme.spacing(1)
  }
}));

const JobList = () => {
  const dispatch = useDispatch();
  const jobs = useSelector((state) => state.job.jobs);
  const size = useWindowSize();
  const classes = useStyles();
  const isLoading = useSelector((state) => state.job.isLoading);
  const theme = useTheme();
  const fullScreen = useMediaQuery(theme.breakpoints.down('sm'));

  useEffect(() => {
    dispatch(jobsRequested());
  }, []);
  const defaultPopup = {isOpen: false, jobId: 0, index: 0};
  const [deletePopup, setDeletePopup] = useState(defaultPopup);
  const [gridApi, setGridApi] = useState(null);

  const setPopup = (id, rowIndex) => {
    setDeletePopup({isOpen: true, jobId: id, index: rowIndex});
  };

  const colunmDef = [
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
    },
    {
      field: 'Delete',
      cellRendererFramework: function detelete(params) {
        return (
          <IconButton color="error" onClick={() => setPopup(params.data.id, params.rowIndex)}>
            <DeleteForeverOutlinedIcon color="error" />
          </IconButton>
        );
      }
    }
  ];

  const onReady = (params) => {
    params.api.setRowData(jobs);
    var allColumnIds = [];
    params.columnApi.getAllColumns().forEach(function (column) {
      allColumnIds.push(column.colId);
    });
    params.columnApi.autoSizeColumns(allColumnIds, false);
    setGridApi(params.api);
  };

  const HandleDelete = () => {
    dispatch(DeleteJobRequested(deletePopup.jobId));
    gridApi.applyTransaction({remove: [jobs[deletePopup.index]]});
    setDeletePopup(defaultPopup);
  };

  return (
    <Box>
      <Grid container direction="row" justify="space-between" alignItems="center">
        <Typography variant="h4">Jobs</Typography>
        <Grid item>
          <Grid container justify="space-between" spacing={2}>
            <Grid item>
              <Fab variant="extended" size="small" color="secondary" component={NavLink} to="/upload">
                <CloudUploadOutlinedIcon className={classes.extendedIcon} />
                Upload File
              </Fab>
            </Grid>
          </Grid>
        </Grid>
      </Grid>
      {isLoading && (
        <Backdrop open={isLoading}>
          <CircularProgress size={200} style={{color: '#ccc'}}></CircularProgress>
        </Backdrop>
      )}
      {jobs && jobs.length > 0 && (
        <div style={{width: '100%', height: size.height - 170, marginTop: 25}} className={'ag-theme-material'}>
          <AgGridReact
            sideBar={'filters'}
            columnDefs={colunmDef}
            rowSelection="single"
            animateRows={true}
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
      <Dialog
        fullScreen={fullScreen}
        open={deletePopup.isOpen}
        onClose={() => setDeletePopup(defaultPopup)}
        aria-labelledby="Confirmation-upload"
      >
        <DialogTitle color="primary" id="Confirmation-upload">
          {'Confirmation'}
        </DialogTitle>
        <DialogContent>
          <DialogContentText>Are you sure you want to delete the job {deletePopup.jobId}</DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button autoFocus onClick={() => setDeletePopup(defaultPopup)} color="secondary">
            Cancel
          </Button>
          <Button color="secondary" onClick={() => HandleDelete()} autoFocus>
            Delete
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default JobList;
