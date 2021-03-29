import React from 'react';
import {PropTypes} from 'prop-types';
import {Box, Divider, Grid} from '@material-ui/core';

const ObservableItemTemplate = ({properties, title}) => {
  const el = {};
  properties.map((e) => {
    el[e.name] = e.content;
  });

  return (
    <>
      <Box component="div" width={600}>
        <h1>{title}</h1>
        <Box pt={2} pb={2}>
          <Grid container spacing={2}>
            <Grid item xs={6}>
              {el['observableItemId']}
            </Grid>
            <Grid item xs={6}>
              {el['observableItemName']}
            </Grid>
          </Grid>
          <Grid container spacing={2}>
            <Grid item xs={6}>
              {el['obsItemTypeName']}
            </Grid>
            <Grid item xs={6}>
              {el['commonName']}
            </Grid>
          </Grid>
          <Grid container spacing={2}>
            <Grid item xs={6}>
              {el['aphiaId']}
            </Grid>
            <Grid item xs={6}>
              {el['aphiaRelTypeName']}
            </Grid>
          </Grid>
          <Grid container spacing={2}>
            <Grid item xs={6}>
              {el['supersededBy']}
            </Grid>
            <Grid item xs={6}>
              {el['supersededNames']}
            </Grid>
          </Grid>
          <Grid container spacing={2}>
            <Grid item xs={6}>
              {el['supersededIds']}
            </Grid>
            <Grid item xs={6}>
              {el['letterCode']}
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
          <Grid container spacing={2}>
            <Grid item xs={6}>
              {el['reportGroup']}
            </Grid>
            <Grid item xs={6}>
              {el['habitatGroups']}
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
        <Box pt={2} pb={2}>
          {el['obsItemAttribute']}
        </Box>
      </Box>
    </>
  );
};

ObservableItemTemplate.propTypes = {
  properties: PropTypes.any,
  title: PropTypes.string
};

export default ObservableItemTemplate;
