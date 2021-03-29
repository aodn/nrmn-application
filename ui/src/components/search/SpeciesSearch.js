import React from 'react';
import clsx from 'clsx';
import {AppBar, Box, Button, Divider, Grid, Tab, Tabs, TextField, Typography} from '@material-ui/core';
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
];

const SpeciesSearch = () => {
  const classes = useStyles();
  const [value, setValue] = React.useState(0);
  const [searchTerm, setSearchTerm] = React.useState('');
  const [warning, setWarning] = React.useState(null);
  const loading = useSelector((state) => state.form.loading);
  const searchResults = useSelector((state) => state.form.searchResults);

  const handleChange = (event, newValue) => {
    setSearchTerm('');
    setValue(newValue);
  };

  const dispatch = useDispatch();
  return (
    <Box ml={6} style={{background: 'white'}} boxShadow={1} margin={3} width={1000}>
      <Box pl={6}>
        <h1>Species Lookup</h1>
      </Box>
      <AppBar position="static">
        <Tabs value={value} onChange={handleChange}>
          <Tab label="WoRMS" style={{minWidth: '50%', textTransform: 'none'}} />
          <Tab label="NRMN" style={{minWidth: '50%'}} />
        </Tabs>
      </AppBar>
      <TabPanel value={value} index={0}>
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
              onClick={() => dispatch(searchRequested({searchType: 'WORMS', species: searchTerm}))}
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
      <TabPanel value={value} index={1}>
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
              onClick={() => dispatch(searchRequested({searchType: 'NRMN', species: searchTerm}))}
              color="primary"
              style={{textTransform: 'none'}}
            >
              Search NRMN
            </Button>
          </Grid>
        </Grid>
      </TabPanel>
      {loading || searchResults !== null ? (
        <div style={{height: 640, backgroundColor: 'white'}}>
          <DataGrid
            className={classes.root}
            density="compact"
            style={{fontSize: 4}}
            rows={searchResults}
            columns={columns}
            autoPageSize={true}
            loading={loading}
            onRowClick={(params) => {
              const supersededBy = params.row.supersededBy;
              if (supersededBy) {
                setWarning(`This species has been superseded by ${supersededBy}`);
              } else {
                setWarning();
                dispatch(setFields(params));
              }
            }}
          />
        </div>
      ) : (
        <Divider />
      )}
    </Box>
  );
};
export default SpeciesSearch;
// SpeciesSearch.propTypes = {
//   title: PropTypes.string
// };
