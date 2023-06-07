import React, {useState} from 'react';
import {Navigate, useParams} from 'react-router';
import {Box} from '@mui/material';
import DataSheetView from './DataSheetView';
import Alert from '@mui/material/Alert';
import {AuthContext} from '../../contexts/auth-context';

const ValidationJob = () => {
  const {id} = useParams();
  const [ingestState, setIngestState] = useState({});

  if (ingestState.data && ingestState.status === 200) {
    return <Navigate to={`/data/job/${id}/view`} />;
  }

  return (
    <AuthContext.Consumer>
      {({auth}) => (
        <>
          {ingestState.data && (
            <Box mx={2}>
              <Alert severity="error" variant="outlined">
                <p>{ingestState.data.message}</p>
              </Alert>
            </Box>
          )}
          <DataSheetView
            roles={auth.roles}
            jobId={id}
            onIngest={setIngestState}
          />
        </>
      )}
    </AuthContext.Consumer>
  );
};

export default ValidationJob;
