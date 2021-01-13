import Grid from '@material-ui/core/Grid';
import Typography from '@material-ui/core/Typography';
import {Box} from '@material-ui/core';
import React from 'react';

const ObjectListViewTemplate = (props) => {

  return (
      <Grid m={0}>
        <Grid item m={0}><Typography variant={'h6'}>{props.name}:</Typography></Grid>
        <Grid item>
          <Box component="div" ml={2}>
            <Grid >
              {props.items}
            </Grid>
          </Box>
        </Grid>
      </Grid>
  );
};

export default ObjectListViewTemplate;