import React, {useEffect, useState} from 'react';
import {Box, Button, Grid, TextField, Typography} from '@material-ui/core';
import {MuiPickersUtilsProvider, KeyboardDatePicker} from '@material-ui/pickers';
import {DataGrid} from '@material-ui/data-grid';
import DateFnsUtils from '@date-io/moment';

import {getResult} from '../../axios/api';

const SurveyCorrections = () => {
  const [loading, setLoading] = useState(false);
  const [searchResults, setSearchResults] = useState([]);
  const [searchParams, setSearchParams] = useState([]);

  useEffect(() => {
    if (searchParams.length < 1) return;
    setLoading(true);
    const result = getResult('surveys?=' + searchParams);
    setLoading(false);
    if (result.data) setSearchResults(result);
  }, [searchParams]);

  return (
    <Grid container justify="center">
      <Box width={900} boxShadow={1} padding={4} bgcolor="white">
        <Grid container spacing={2}>
          <Grid item xs={12}>
            <Typography variant="h4">Survey Corrections</Typography>
          </Grid>
          <Grid item xs={2}>
            <Typography variant="subtitle2">Survey ID</Typography>
            <TextField variant="outlined" />
          </Grid>
          <Grid item xs={3}>
            <Typography variant="subtitle2">Date From</Typography>
            <MuiPickersUtilsProvider utils={DateFnsUtils}>
              <KeyboardDatePicker variant="outlined" format="yyyy-mm-DD" />
            </MuiPickersUtilsProvider>
          </Grid>
          <Grid item xs={3}>
            <Typography variant="subtitle2">Date To</Typography>
            <MuiPickersUtilsProvider utils={DateFnsUtils}>
              <KeyboardDatePicker variant="outlined" format="yyyy-mm-DD" />
            </MuiPickersUtilsProvider>
          </Grid>
          <Grid item xs={2}>
            <Typography variant="subtitle2">Latitude</Typography>
            <TextField variant="outlined" />
          </Grid>
          <Grid item xs={2}>
            <Typography variant="subtitle2">Longitude</Typography>
            <TextField variant="outlined" />
          </Grid>
          <Grid item xs={2}>
            <Typography variant="subtitle2">Site Code</Typography>
            <TextField variant="outlined" />
          </Grid>
          <Grid item xs={3}>
            <Typography variant="subtitle2">Site Name</Typography>
            <TextField variant="outlined" />
          </Grid>
          <Grid item xs={3}>
            <Typography variant="subtitle2">Diver</Typography>
            <TextField variant="outlined" />
          </Grid>
          <Grid item xs={2}>
            <Typography variant="subtitle2">Depth</Typography>
            <TextField variant="outlined" />
          </Grid>
          <Grid item xs={2}>
            <Typography variant="subtitle2">Method</Typography>
            <TextField variant="outlined" />
          </Grid>
          <Grid item xs={3}>
            <Typography variant="subtitle2">Program</Typography>
            <TextField variant="outlined" />
          </Grid>
          <Grid item xs={3}>
            <Typography variant="subtitle2">State/Area</Typography>
            <TextField variant="outlined" />
          </Grid>
          <Grid item xs={3}>
            <Typography variant="subtitle2">Country/Region</Typography>
            <TextField variant="outlined" />
          </Grid>
          <Grid item xs={3}>
            <Typography variant="subtitle2">Ecoregion</Typography>
            <TextField variant="outlined" />
          </Grid>
          <Grid item xs={4}>
            <Typography variant="subtitle2">Location</Typography>
            <TextField variant="outlined" />
          </Grid>
          <Grid item xs={4}>
            <Typography variant="subtitle2">Species</Typography>
            <TextField variant="outlined" />
          </Grid>
          <Grid item xs={8} />
          <Grid item xs={4}>
            <Button
              color="secondary"
              variant="contained"
              style={{width: '100%'}}
              onClick={() => {
                setSearchParams({});
              }}
            >
              Search
            </Button>
          </Grid>
        </Grid>
      </Box>
      <Box height={640} width={900} boxShadow={1} padding={2} bgcolor="white">
        <DataGrid
          density="compact"
          style={{fontSize: 4}}
          rows={searchResults}
          columns={[
            {field: 'ID', headerName: 'Survey ID'},
            {field: 'siteName', headerName: 'Site Name', flex: 1},
            {field: 'program', headerName: 'Program', flex: 1},
            {field: 'surveyDate', headerName: 'Survey Date', flex: 1},
            {field: 'surveyTime', headerName: 'Survey Time', flex: 1},
            {field: 'depth', headerName: 'Depth', flex: 1},
            {field: 'surveyNumber', headerName: 'Survey Number', flex: 1}
          ]}
          autoPageSize={true}
          loading={loading}
        />
      </Box>
    </Grid>
  );
};

export default SurveyCorrections;
