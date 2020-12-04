import React from 'react';
import PropTypes, { checkPropTypes } from 'prop-types';
import Typography from '@material-ui/core/Typography';
import Grid from '@material-ui/core/Grid';
import LoadingSpinner from './loadingSpinner';


const LoadingBanner = (props) => {

  return <Grid
    container
    direction="row"
    justify="flex-start"
    alignItems="center"
  >
    <Typography variant={props.variant}>{props.msg} &nbsp; </Typography>
    <LoadingSpinner color={'primary'} />
  </Grid>;
};

LoadingBanner.propTypes = {
  msg: PropTypes.string,
  variant: PropTypes.string
};

export default LoadingBanner;