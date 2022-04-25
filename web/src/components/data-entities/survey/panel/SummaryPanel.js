import React from 'react';
import {PropTypes} from 'prop-types';
import {Box, Button} from '@mui/material';

const SummaryPanel = () => {
  return (
    <Box m={2} mr={4}>
      <Button variant="contained">Validate</Button>
    </Box>
  );
};

SummaryPanel.propTypes = {
  api: PropTypes.any,
  agGridReact: PropTypes.any
};

export default SummaryPanel;
