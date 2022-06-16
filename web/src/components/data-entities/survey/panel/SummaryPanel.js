import React, {useEffect, useState} from 'react';
import {PropTypes} from 'prop-types';
import {Box, Typography} from '@mui/material';
import ValidationSummary from '../../../import/panel/ValidationSummary';

const SummaryPanel = ({api, context}) => {
  const [blocking, setBlocking] = useState([]);

  const handleItemClick = () => {};

  useEffect(() => {
    setBlocking(context.validations?.blocking ?? []);
  });

  return (
    <Box m={2} mr={4}>
      <Box m={2} mt={1}>
        <Typography variant="button">BLOCKING</Typography>
        <ValidationSummary data={blocking} onItemClick={(item) => handleItemClick(item, true)} />
      </Box>
    </Box>
  );
};

SummaryPanel.propTypes = {
  api: PropTypes.any,
  context: PropTypes.any
};

export default SummaryPanel;
