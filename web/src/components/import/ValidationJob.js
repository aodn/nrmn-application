import React, {useState} from 'react';
import {Navigate, useParams} from 'react-router';
import {Box} from '@mui/material';
import DataSheetView from './DataSheetView';
import Alert from '@mui/material/Alert';

const ValidationJob = () => {
  const {jobId: id} = useParams();
  const [ingestState, setIngestState] = useState({});

  if (ingestState.success) {
    return <Navigate to={`/data/jobs/${id}/view`} />;
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
  return <DataSheetView jobId={id} onIngest={setIngestState} />;
};

export default ValidationJob;
