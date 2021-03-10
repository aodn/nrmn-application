import {Box, Grid} from '@material-ui/core';

import React from 'react';
import {PropTypes} from 'prop-types';

const SiteAddTemplate = (props) => {
  const {properties, title} = props;
  const el = {};
  properties.map((e) => {
    el[e.name] = e.content;
  });

  return (
    <Box width={600}>
      <h1>{title}</h1>
      <Grid container spacing={2}>
        <Grid item xs={6}>
          {el['siteCode']}
        </Grid>
        <Grid item xs={6}>
          {el['siteName']}
        </Grid>
        <Grid item xs={6}>
          {el['locationId']}
        </Grid>
      </Grid>
      <Grid container spacing={2}>
        <Grid item xs={6}>
          {el['state']}
        </Grid>
        <Grid item xs={6}>
          {el['country']}
        </Grid>
        <Grid item xs={6}>
          {el['latitude']}
        </Grid>
        <Grid item xs={6}>
          {el['longitude']}
        </Grid>
        <Grid item xs={6}>
          {el['mpa']}
        </Grid>
        <Grid item xs={6}>
          {el['protectionStatus']}
        </Grid>
      </Grid>
    </Box>
  );
};

SiteAddTemplate.propTypes = {
  properties: PropTypes.any,
  title: PropTypes.string
};

export default SiteAddTemplate;
