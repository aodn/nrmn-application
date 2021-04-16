import React from 'react';
import {PropTypes} from 'prop-types';
import {Box, Divider, Grid} from '@material-ui/core';

const ObservableItemEditTemplate = ({properties}) => {
  const el = {};
  properties.map((e) => {
    el[e.name] = e.content;
  });

  return (
    <>
      <Box component="div">
        <Box pt={2} pb={2}>
          <Grid container spacing={2}>
            <Grid item xs={6}>
              {el['observableItemName']}
            </Grid>
            <Grid item xs={6}>
              {el['commonName']}
            </Grid>
          </Grid>
          <Grid container spacing={2}>
            <Grid item xs={6}>
              {el['speciesEpithet']}
            </Grid>
            <Grid item xs={6}>
              {el['supersededBy']}
            </Grid>
          </Grid>
          <Grid container spacing={2}>
            <Grid item xs={6}>
              {el['letterCode']}
            </Grid>
            <Grid item xs={6}>
              {el['reportGroup']}
            </Grid>
          </Grid>
          <Grid container spacing={2}>
            <Grid item xs={6}>
              {el['habitatGroups']}
            </Grid>
          </Grid>
        </Box>
        <Divider />
        <Box pt={2} pb={2}>
          <Grid container spacing={2}>
            <Grid item xs={6}>
              {el['phylum']}
            </Grid>
            <Grid item xs={6}>
              {el['class']}
            </Grid>
          </Grid>
          <Grid container spacing={2}>
            <Grid item xs={6}>
              {el['order']}
            </Grid>
            <Grid item xs={6}>
              {el['family']}
            </Grid>
          </Grid>
          <Grid container spacing={2}>
            <Grid item xs={6}>
              {el['genus']}
            </Grid>
          </Grid>
        </Box>
        <Divider />
        <Box pt={2} pb={2}>
          <Grid container spacing={2}>
            <Grid item xs={6}>
              {el['lengthWeightA']}
            </Grid>
            <Grid item xs={6}>
              {el['lengthWeightB']}
            </Grid>
          </Grid>
          <Grid container spacing={2}>
            <Grid item xs={6}>
              {el['lengthWeightCf']}
            </Grid>
          </Grid>
        </Box>
      </Box>
    </>
  );
};

ObservableItemEditTemplate.propTypes = {
  properties: PropTypes.any,
  title: PropTypes.string
};

export default ObservableItemEditTemplate;
