import React, {useState, useEffect} from 'react';
import clsx from 'clsx';
import {AppBar, Box, Button, Divider, Grid, Tab, Tabs, TextField, Typography} from '@material-ui/core';
import Pagination from '@material-ui/lab/Pagination';
import {DataGrid} from '@material-ui/data-grid';
import {Search} from '@material-ui/icons';
import Alert from '@material-ui/lab/Alert';
import makeStyles from '@material-ui/core/styles/makeStyles';

import TabPanel from '../containers/TabPanel';

import {useDispatch, useSelector} from 'react-redux';
import {searchRequested} from '../data-entities/form-reducer';
import {setFields} from '../data-entities/middleware/entities';

const useStyles = makeStyles({
  root: {
    '& .superseded': {
      color: '#999999'
    }
  }
});

const columns = [
  [
    {field: 'status', headerName: 'Status', flex: 1},
    {
      field: 'species',
      headerName: 'Species',
      flex: 2,
      cellClassName: (params) =>
        clsx('root', {
          superseded: params.row.supersededBy
        })
    },
    {field: 'genus', headerName: 'Genus', flex: 1},
    {field: 'family', headerName: 'Family', flex: 1},
    {field: 'order', headerName: 'Order', flex: 1},
    {field: 'class', headerName: 'Class', flex: 1},
    {field: 'phylum', headerName: 'Phylum', flex: 1}
  ],
  [
    {
      field: 'species',
      headerName: 'Species',
      flex: 2,
      cellClassName: (params) =>
        clsx('root', {
          superseded: params.row.supersededBy
        })
    },
    {field: 'genus', headerName: 'Genus', flex: 1},
    {field: 'family', headerName: 'Family', flex: 1},
    {field: 'order', headerName: 'Order', flex: 1},
    {field: 'class', headerName: 'Class', flex: 1},
    {field: 'phylum', headerName: 'Phylum', flex: 1}
  ]
];

const SpeciesSearch = () => {
  const classes = useStyles();
  const [tabIndex, setTabIndex] = useState(0);
  const [searchTerm, setSearchTerm] = useState(null);
  const [page, setPage] = useState(1);
  const [gridData, setGridData] = useState(null);
  const [warning, setWarning] = useState(null);
  const loading = useSelector((state) => state.form.loading);
  const searchResults = useSelector((state) => state.form.searchResults);
  const searchError = useSelector((state) => state.form.searchError);

  const handleChange = (event, newValue) => {
    setSearchTerm(null);
    setGridData([]);
    setPage(1);
    setTabIndex(newValue);
  };

  const pageSize = 50;
  const maxResults = 9999;

  useEffect(() => {
    if (searchResults !== null) setGridData(searchResults);
  }, [searchResults]);

  const dispatch = useDispatch();
  const paginator = ({api}) => (
    <Pagination
      page={page}
      disabled={loading}
      count={gridData !== null && gridData.length === pageSize ? page + 1 : page}
      onChange={(e, value) => api.current.setPage(value)}
    />
  );
  return (
    <Box ml={6} style={{background: 'white'}} boxShadow={1} margin={3} width="80%">
      <Box pl={6} py={2}>
        <Typography variant="h4">Species Lookup</Typography>
      </Box>
      <AppBar position="static">
        <Tabs value={tabIndex} onChange={handleChange}>
          <Tab label="WoRMS" style={{minWidth: '50%', textTransform: 'none'}} />
          <Tab label="NRMN" style={{minWidth: '50%'}} />
        </Tabs>
      </AppBar>
      {searchError ? (
        <Box pt={2} mx={2}>
          <Alert severity="error" variant="filled">
            The server may be experiencing problems. Please wait a moment and try again. (Error: {searchError})
          </Alert>
        </Box>
      ) : null}
      <TabPanel value={tabIndex} index={0}>
        <Typography variant="subtitle2">Scientific Name</Typography>
        <Grid container direction="row" alignItems="center">
          <Grid item xs={5}>
            <TextField fullWidth onChange={(e) => setSearchTerm(e.target.value)} />
          </Grid>
          <Grid item xs={1}></Grid>
          <Grid item xs={4}>
            <Button
              variant="contained"
              disabled={loading || !(searchTerm?.length > 3)}
              startIcon={<Search></Search>}
              onClick={() => {
                setPage(1);
                dispatch(searchRequested({searchType: 'WORMS', species: searchTerm, includeSuperseded: true}));
              }}
              color="primary"
              style={{textTransform: 'none'}}
            >
              Search WoRMS
            </Button>
          </Grid>
        </Grid>
      </TabPanel>
      {warning ? (
        <Box pt={2}>
          <Alert severity="warning" variant="filled">
            {warning}
          </Alert>
        </Box>
      ) : null}
      <TabPanel value={tabIndex} index={1}>
        <Typography variant="subtitle2">Scientific Name</Typography>
        <Grid container direction="row" alignItems="center">
          <Grid item xs={5}>
            <TextField fullWidth onChange={(e) => setSearchTerm(e.target.value)} />
          </Grid>
          <Grid item xs={1}></Grid>
          <Grid item xs={3}>
            <Button
              variant="contained"
              disabled={loading || !(searchTerm?.length > 3)}
              startIcon={<Search></Search>}
              onClick={() => dispatch(searchRequested({searchType: 'NRMN', species: searchTerm, includeSuperseded: true}))}
              color="primary"
              style={{textTransform: 'none'}}
            >
              Search NRMN
            </Button>
          </Grid>
        </Grid>
      </TabPanel>
      {loading || gridData !== null ? (
        <div style={{height: '640px', backgroundColor: 'white'}}>
          <DataGrid
            className={classes.root}
            page={page}
            pageSize={gridData === null ? 0 : Math.min(gridData.length, pageSize)}
            rowCount={gridData === null ? 0 : gridData.length < pageSize ? pageSize * (page + 1) + gridData.length : maxResults}
            paginationMode="server"
            disabled={loading}
            disableSelectionOnClick
            density="compact"
            style={{fontSize: 4}}
            rows={gridData === null ? [] : gridData}
            columns={columns[tabIndex]}
            components={{
              Pagination: paginator
            }}
            loading={loading}
            onPageChange={(params) => {
              dispatch(
                searchRequested({
                  searchType: tabIndex === 0 ? 'WORMS' : 'NRMN',
                  species: searchTerm,
                  includeSuperseded: true,
                  page: params.page - 1
                })
              );
              setPage(params.page);
            }}
            onRowClick={(params) => {
              const supersededBy = params.row.supersededBy;
              if (supersededBy) {
                setWarning(`This species has been superseded by ${supersededBy}`);
              } else {
                setWarning();
                dispatch(setFields(params));
              }
            }}
          ></DataGrid>
        </div>
      ) : (
        <Divider />
      )}
    </Box>
  );
};
export default SpeciesSearch;
