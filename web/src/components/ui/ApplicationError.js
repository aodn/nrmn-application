import React from 'react';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Alert from '@mui/material/Alert';
import {PropTypes} from 'prop-types';

const ApplicationError = ({error}) => {

  const data = error?.response?.data;
  return (
    <Box m={10}>
      <Box py={3}>
        <Alert severity="error" variant="outlined">
          {error?.message}
          {data && <><br/>{data}</>}
        </Alert>
      </Box>
      <button onClick={() => window.location.reload()}>
        Refresh Page
      </button>
    </Box>
  );
};

export default ApplicationError;

ApplicationError.propTypes = {
  error: PropTypes.object
};
