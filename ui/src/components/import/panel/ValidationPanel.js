import React, {useEffect, useState} from 'react';
import {PropTypes} from 'prop-types';
import {Box} from '@material-ui/core';

const ValidationPanel = (props) => {
  const [title, setTitle] = useState('');

  useEffect(() => {
    const results = props.api.gridOptionsWrapper.gridOptions.context.validationResults;
    if (results?.job) {
      setTitle(`${results.errors.length} errors 😞`);
    }
  }, [setTitle, props.api.gridOptionsWrapper.gridOptions.context.validationResults]);

  return (
    <Box m={2} mr={4}>
      <h1>{title}</h1>
    </Box>
  );
};

ValidationPanel.propTypes = {
  api: PropTypes.any,
  agGridReact: PropTypes.any
};

export default ValidationPanel;
