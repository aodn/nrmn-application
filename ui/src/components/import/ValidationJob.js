import React, {useEffect} from 'react';
import {useDispatch, useSelector} from 'react-redux';
import {Redirect, useParams} from 'react-router';
import {Box} from '@material-ui/core';
import DataSheetView from './DataSheetView';
import {ResetState} from './reducers/create-import';
import Alert from '@material-ui/lab/Alert';

const ValidationJob = () => {
  const dispatch = useDispatch();
  const {jobId} = useParams();
  const ingestSuccess = useSelector((state) => state.import.ingestSuccess);
  const ingestError = useSelector((state) => state.import.ingestError);
  const errors = useSelector((state) => state.import.errors);

  useEffect(() => {
    dispatch(ResetState());
  });

  if (ingestSuccess) {
    return <Redirect to={`/jobs/${jobId}/view`}></Redirect>;
  } else {
    {
      errors && errors.length > 0 && (
        <Box mb={2}>
          <Alert severity="error" variant="filled">
            {errors.map((item, key) => {
              return <div key={key}>{item}</div>;
            })}
          </Alert>
        </Box>
      );
    }
    {
      ingestError && (
        <Box mb={2}>
          <Alert severity="error" variant="filled">
            <p>
              Sheet failed to ingest. No survey data has been inserted.
              <br />
              If this problem persists, please contact info@aodn.org.au.
            </p>
            <p>Error: {ingestError}</p>
          </Alert>
        </Box>
      );
    }
    return <DataSheetView jobId={jobId} />;
  }
};

export default ValidationJob;
