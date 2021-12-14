import React, {useEffect, useState} from 'react';
import clsx from 'clsx';
import {AppBar, Box, Button, Divider, Grid, Tab, Tabs, TextField, Typography} from '@material-ui/core';
import {DataGrid} from '@material-ui/data-grid';
import {Search} from '@material-ui/icons';
import Alert from '@material-ui/lab/Alert';
import makeStyles from '@material-ui/core/styles/makeStyles';
import TabPanel from '../containers/TabPanel';
import {search} from '../../axios/api';
import PropTypes from 'prop-types';

const useStyles = makeStyles({
  root: {
    '& .superseded': {
      color: '#999999',
      fontStyle: 'italic'
    },
    '& .present': {
      fontStyle: 'bold'
    },
    '& .MuiTablePagination-caption': {
      display: 'none'
    }
  }
});

const columns = [
  [
    {field: 'isPresent', headerName: 'NRMN', flex: 0.75},
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
    {field: 'phylum', headerName: 'Phylum', flex: 1},
    {field: 'supersededBy', headerName: 'Superseded By', flex: 1}
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
    {field: 'phylum', headerName: 'Phylum', flex: 1},
    {field: 'supersededBy', headerName: 'Superseded By', flex: 1}
  ]
];

const SpeciesSearch = ({onRowClick}) => {
  const classes = useStyles();
  const [tabIndex, setTabIndex] = useState(0);
  const [searchTerm, setSearchTerm] = useState(null);
  const [page, setPage] = useState(0);
  const [gridData, setGridData] = useState(null);
  const [currentSearch, setCurrentSearch] = useState({});
  const [info, setInfo] = useState(null);

  const [searchRequested, setSearchRequested] = useState(null);
  const [searchError, setSearchError] = useState(null);

  const loading = searchRequested && !gridData && !searchError;

  const handleChange = (_, newValue) => {
    setSearchTerm(null);
    setGridData(null);
    setSearchRequested(null);
    setCurrentSearch(null);
    setPage(0);
    setTabIndex(newValue);
  };

  const pageSize = 50;

  useEffect(() => {
    if (!searchRequested) return;
    setGridData(null);
    setSearchError(null);
    if (currentSearch?.species !== searchRequested.species) setPage(0);
    search(searchRequested)
      .then((res) => {
        if (res.data.error) {
          setSearchError(res.data.error);
          return;
        }
        setCurrentSearch(searchRequested);
        setGridData(
          res?.data
            ? res.data.map((r, id) => {
                // if not a generic name then remove the genus from the species to produce the species epithet
                let speciesEpithet = '';
                if (r.species) {
                  const isGenericName =
                    r.species.toUpperCase().includes('SP.') ||
                    r.species.toUpperCase().includes('SPP.') ||
                    r.species.includes('(') ||
                    r.species.includes('[') ||
                    !r.species.includes(' ');
                  if (!isGenericName) speciesEpithet = r.species.replace(`${r.genus} `, '');
                }
                return {id: id, ...r, speciesEpithet};
              })
            : null
        );
      })
      .catch((err) => setSearchError(err.message));
  }, [searchRequested, currentSearch]);

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
            <TextField
              fullWidth
              onChange={(e) => setSearchTerm(e.target.value.trim())}
              onKeyDown={(e) => {
                if (e.key === 'Enter') setSearchRequested({searchType: 'WORMS', species: searchTerm, includeSuperseded: true});
              }}
            />
          </Grid>
          <Grid item xs={1}></Grid>
          <Grid item xs={4}>
            <Button
              disabled={loading || !(searchTerm?.length > 3)}
              startIcon={<Search></Search>}
              onClick={() => {
                setPage(1);
                setSearchRequested({searchType: 'WORMS', species: searchTerm, includeSuperseded: true});
              }}
              style={{textTransform: 'none'}}
            >
              Search WoRMS
            </Button>
          </Grid>
        </Grid>
      </TabPanel>
      {info ? (
        <Box pt={2}>
          <Alert severity="info" variant="filled">
            {info}
          </Alert>
        </Box>
      ) : null}
      <TabPanel value={tabIndex} index={1}>
        <Typography variant="subtitle2">Scientific Name</Typography>
        <Grid container direction="row" alignItems="center">
          <Grid item xs={5}>
            <TextField
              fullWidth
              onKeyDown={(e) => {
                if (e.key === 'Enter') setSearchRequested({searchType: 'NRMN', species: searchTerm, includeSuperseded: true});
              }}
              onChange={(e) => setSearchTerm(e.target.value.trim())}
            />
          </Grid>
          <Grid item xs={1}></Grid>
          <Grid item xs={3}>
            <Button
              disabled={loading || !(searchTerm?.length > 3)}
              startIcon={<Search></Search>}
              onClick={() => setSearchRequested({searchType: 'NRMN', species: searchTerm, includeSuperseded: true})}
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
            pageSize={pageSize}
            rowCount={
              gridData === null ? 0 : gridData.length < pageSize ? pageSize * page + gridData.length : pageSize * page + gridData.length + 1
            }
            hide
            paginationMode="server"
            disabled={loading}
            disableSelectionOnClick
            hideFooterRowCount
            rowsPerPageOptions={[50]}
            density="compact"
            style={{fontSize: 4}}
            rows={gridData === null ? [] : gridData}
            columns={columns[tabIndex]}
            loading={loading}
            onPageChange={(params) => {
              setSearchRequested({
                searchType: tabIndex === 0 ? 'WORMS' : 'NRMN',
                species: searchTerm,
                includeSuperseded: true,
                page: params.page
              });
              setPage(params.page);
            }}
            onRowClick={(params) => {
              const supersededBy = params.row.supersededBy;
              const unacceptReason = params.row.unacceptReason;
              const isPresent = params.row.isPresent;
              if (supersededBy) {
                setInfo(
                  `This species has been superseded by ${supersededBy}` + (unacceptReason != null ? ` (Reason: ${unacceptReason})` : '')
                );
              } else if (isPresent) {
                setInfo('This species name exists in the NRMN database');
              } else {
                setInfo();
                onRowClick({
                  aphiaId: params.row.aphiaId,
                  phylum: params.row.phylum,
                  family: params.row.family,
                  class: params.row.class,
                  order: params.row.order,
                  genus: params.row.genus,
                  speciesEpithet: params.row.speciesEpithet
                });
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

SpeciesSearch.propTypes = {
  onRowClick: PropTypes.func
};

export default SpeciesSearch;
