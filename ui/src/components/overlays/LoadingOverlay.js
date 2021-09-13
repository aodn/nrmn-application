import React from 'react';
import {Box, Paper} from '@material-ui/core';
import LinearProgressWithLabel from '../ui/LinearProgressWithLabel';

const LoadingOverlay = (e) => {
  const ctx = e.api.gridOptionsWrapper.gridOptions.context;
  const labelText = ctx?.useOverlay || 'Loading';
  return (
    <Box component={Paper} width={500} p={3}>
      <LinearProgressWithLabel determinate={false} label={`${labelText}...`} />
    </Box>
  );
};

export default LoadingOverlay;
