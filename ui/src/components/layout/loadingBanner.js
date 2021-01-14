import React from 'react';
import Typography from '@material-ui/core/Typography';
import Grid from '@material-ui/core/Grid';
import {LoadingSpinner} from './loadingSpinner';
import {PropTypes} from 'prop-types';


export const LoadingBanner = (props) => {

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