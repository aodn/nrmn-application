import React from 'react';
// import {PropTypes} from 'prop-types';
import {AppBar, Box, Button, Divider, Grid, Tab, Tabs, TextField, Typography} from '@material-ui/core';
import {DataGrid} from '@material-ui/data-grid';
import {Search} from '@material-ui/icons';

import TabPanel from '../containers/TabPanel';

import {useDispatch, useSelector} from 'react-redux';
import {searchRequested} from '../data-entities/form-reducer';
import {setFields} from '../data-entities/middleware/entities';

const columns = [
  {field: 'species', headerName: 'Species', width: 180},
  {field: 'genus', headerName: 'Genus', width: 180},
  {field: 'family', headerName: 'Family', width: 180},
  {field: 'order', headerName: 'Order', width: 180},
  {field: 'class', headerName: 'Class', width: 180},
  {field: 'phylum', headerName: 'Phylum', width: 180}
];

const SpeciesSearch = () => {
  // const classes = useStyles();
  const [value, setValue] = React.useState(0);
  const [searchTerm, setSearchTerm] = React.useState('');
  const loading = useSelector((state) => state.form.loading);
  const searchResults = useSelector((state) => state.form.searchResults);

  const handleChange = (event, newValue) => {
    setValue(newValue);
  };

  const dispatch = useDispatch();
  return (
    <>
      <Box ml={6}>
        <h1>Species Lookup</h1>
      </Box>
      <AppBar position="static">
        <Tabs value={value} onChange={handleChange}>
          <Tab label="NRMN" style={{minWidth: '50%'}} />
          <Tab label="WoRMS" style={{minWidth: '50%', textTransform: 'none'}} />
        </Tabs>
      </AppBar>
      <TabPanel value={value} index={0}>
        <Typography variant="subtitle2">Scientific Name</Typography>
        <Grid container direction="row" alignItems="center">
          <Grid item xs={5}>
            <TextField fullWidth onBlur={(e) => setSearchTerm(e.target.value)} />
          </Grid>
          <Grid item xs={1}></Grid>
          <Grid item xs={3}>
            <Button
              variant="contained"
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
      <TabPanel value={value} index={1}>
        <Typography variant="subtitle2">Scientific Name</Typography>
        <Grid container direction="row" alignItems="center">
          <Grid item xs={5}>
            <TextField fullWidth />
          </Grid>
          <Grid item xs={1}></Grid>
          <Grid item xs={4}>
            <Button
              variant="contained"
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
      {loading || searchResults !== null ? (
        <div style={{height: 320, width: '100%'}}>
          <DataGrid
            density="compact"
            rows={searchResults}
            columns={columns}
            autoPageSize={true}
            loading={loading}
            onRowClick={(params) => dispatch(setFields(params))}
          />
        </div>
      ) : (
        <Divider />
      )}
    </>
  );
};
export default SpeciesSearch;
// SpeciesSearch.propTypes = {
//   title: PropTypes.string
// };
