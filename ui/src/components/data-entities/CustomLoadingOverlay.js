import React from 'react';
import LoadingBanner from '../layout/loadingBanner';
import {Box} from '@material-ui/core';
import {makeStyles} from '@material-ui/core/styles';

const useStyles = makeStyles(() => ({
  main: {
    '& > *': {
      margin: 10
    }
  }
}));

const CustomLoadingOverlay = () => {
  const classes = useStyles();

  return (
    <div className="ag-custom-loading-cell">
      <Box component="div" ml={2} className={classes.main}>
        <LoadingBanner variant={'h5'} msg={'Loading data..  '} />
      </Box>
    </div>
  );
};

export default CustomLoadingOverlay;
