import {Box, Chip, CircularProgress, Grid, Typography} from '@material-ui/core';
import React, {useEffect} from 'react';
import {useDispatch, useSelector} from 'react-redux';
import DataSheetView from './DataSheetView';
import {Redirect, useParams} from 'react-router';
import AccountBalanceOutlinedIcon from '@material-ui/icons/AccountBalanceOutlined';
import {Backdrop} from '@material-ui/core';
import ValidationDrawer from './ValidationDrawer';
import { JobRequested, ResetState } from './reducers/create-import';

const ValidationJob = () => {
  const {jobId} = useParams();
  const dispatch = useDispatch();
  const job = useSelector((state) => state.import.job);
  const isLoading = useSelector((state) => state.import.isLoading);
  const validationLoading = useSelector((state) => state.import.validationLoading);
  const submitLoading = useSelector((state) => state.import.submitLoading);
  const ingestSuccess = useSelector((state) => state.import.ingestSuccess);


  useEffect(() => {
    if (jobId) {
     dispatch(JobRequested(jobId));
    }
    return function clean() {
      dispatch(ResetState());
    };
  }, []);

  if (ingestSuccess) {
    return <Redirect to="/"></Redirect>;
  }
  const jobReady = job && Object.keys(job).length > 0;
  return jobReady ? (
    <Box style={{paddingRight: 60}}>
      <ValidationDrawer></ValidationDrawer>
      <Grid container>
        <Grid item lg={8} md={8}>
          <Typography variant="h4" color="primary">
            {job.reference}
          </Typography>
        </Grid>
      </Grid>
      <Grid container spacing={1} justify="flex-start">
        <Grid item>
          <Chip
            size="small"
            avatar={<AccountBalanceOutlinedIcon></AccountBalanceOutlinedIcon>}
            label={job.program.programName}
            variant="outlined"
            mt={1}
          ></Chip>
        </Grid>
        <Grid item>
          <Chip size="small" color="secondary" label={job.source} variant="outlined"></Chip>
        </Grid>
        {job.isExtendedSize && (
          <Grid item>
            <Chip size="small" color="secondary" label={'Extended Size'} variant="outlined"></Chip>
          </Grid>
        )}
      </Grid>
      <DataSheetView></DataSheetView>
      {submitLoading && (
        <Backdrop open={submitLoading}>
          <Typography variant="h2">Ingesting...</Typography>
          <CircularProgress size={200} style={{color: '#ccc'}}></CircularProgress>
        </Backdrop>
      )}
      {isLoading && (
        <Backdrop open={isLoading}>
          <CircularProgress size={200} style={{color: '#ccc'}}></CircularProgress>
        </Backdrop>
      )}
      {validationLoading && (
        <Backdrop open={validationLoading}>
          <CircularProgress size={200} style={{color: '#ccc'}}></CircularProgress>
        </Backdrop>
      )}
    </Box>
  ) : (
    <Box>
      <Typography>No Data</Typography>
    </Box>
  );
};

export default ValidationJob;
