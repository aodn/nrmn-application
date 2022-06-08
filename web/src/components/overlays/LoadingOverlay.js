import React from 'react';
import {Box, Paper} from '@mui/material';
import {PropTypes} from 'prop-types';
import LinearProgressWithLabel from '../ui/LinearProgressWithLabel';

const LoadingOverlay = ({context}) => {
  const labelText = context.useOverlay || 'Loading';
  return (
    <Box component={Paper} width={500} p={3}>
      <LinearProgressWithLabel determinate={false} label={`${labelText}...`} />
    </Box>
  );
};

LoadingOverlay.propTypes = {
  context: PropTypes.any
};

export default LoadingOverlay;
