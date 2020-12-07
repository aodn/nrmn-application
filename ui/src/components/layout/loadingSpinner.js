import React from 'react';
import CircularProgress from '@material-ui/core/CircularProgress';
import PropTypes from 'prop-types';


const LoadingSpinner = (props) => {
  const size = (props.size) ? props.size : 21;
  const thickness = (props.thickness) ? props.thickness : size / 3 + 3;


  return <CircularProgress {...props} color = { 'secondary'} size = { size } thickness = { thickness } />;
};

LoadingSpinner.propTypes = {
  size: PropTypes.number,
  thickness: PropTypes.number
};

export default LoadingSpinner;