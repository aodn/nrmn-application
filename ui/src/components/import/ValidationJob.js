import React, {useState} from 'react';
import {Redirect, useParams} from 'react-router';
import {Box} from '@material-ui/core';
import DataSheetView from './DataSheetView';
import Alert from '@material-ui/lab/Alert';

const ValidationJob = () => {
  const {jobId} = useParams();
  const [ingestState, setIngestState] = useState({});

  if (ingestState.success) {
    return <Redirect to={`/jobs/${jobId}/view`}></Redirect>;
  }

  if (ingestState.error) {
    return (
      <Box mb={2}>
        <Alert severity="error" variant="filled">
          <p>
            Sheet failed to ingest. No survey data has been inserted.
            <br />
            If this problem persists, please contact info@aodn.org.au.
          </p>
          <p>Error: {ingestState.error}</p>
        </Alert>
      </Box>
    );
  }
  return <DataSheetView jobId={jobId} onIngest={setIngestState} />;
};

export default ValidationJob;
