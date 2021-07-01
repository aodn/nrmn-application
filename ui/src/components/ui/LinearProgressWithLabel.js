import React from 'react';
import {Box, LinearProgress, Typography} from '@material-ui/core';
import {PropTypes} from 'prop-types';

const LinearProgressWithLabel = (props) => {
  return (
    <Box>
      <Typography>{props.label}</Typography>
      <Box display="flex" alignItems="center">
        <Box width="100%" mr={1}>
          <LinearProgress variant={props.determinate ? 'determinate' : 'indeterminate'} value={props.value} />
        </Box>

        {props.value && (
          <Box minWidth={35}>
            <Typography variant="body2" color="textSecondary">
              {props.done ? 'Done' : props.value >= 0 ? `${Math.round(props.value)}%` : ''}
            </Typography>
          </Box>
        )}
      </Box>
    </Box>
  );
};

LinearProgressWithLabel.propTypes = {
  label: PropTypes.string.isRequired,
  determinate: PropTypes.bool,
  done: PropTypes.bool,
  value: PropTypes.number
};

export default LinearProgressWithLabel;
