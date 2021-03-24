import {Box, Button, Grid, Typography} from '@material-ui/core';
// import {useDispatch} from 'react-redux';
// import {wormsSearchRequested} from './form-reducer';

import React from 'react';
import {PropTypes} from 'prop-types';

const ObservableItemTemplate = ({properties, title}) => {
  const el = {};
  properties.map((e) => {
    el[e.name] = e.content;
  });

  // const dispatch = useDispatch();
  return (
    <>
      <Box component="div" width={600}>
        <h1>{title}</h1>
        <Grid container spacing={2}>
          <Grid item xs={6}>
            {el['observableItemName']}
          </Grid>
        </Grid>
        <Grid container spacing={2}>
          <Grid item xs={6}>
            {el['commonName']}
          </Grid>
          <Grid item xs={6}>
            {el['obsItemType']}
          </Grid>
        </Grid>
        <Grid container spacing={2}>
          <Grid item xs={6}>
            {el['aphiaRef']}
          </Grid>
          <Grid item xs={6}>
            {el['aphiaRelType']}
          </Grid>
          <Button
            variant="contained"
            style={{minWidth: '100%', textTransform: 'none'}}
            // onClick={() => dispatch(wormsSearchRequested({aphia_id: el['aphiaRef'].props.formData.id}))}
            color="primary"
          >
            Search WoRMS
          </Button>
          <Grid container direction="row" justify="center" alignItems="center" alignContent="center" m={20}>
            <Typography>Overrides</Typography>
          </Grid>
          <Grid container spacing={2}>
            <Grid item xs={6}>
              {el['phylum']}
            </Grid>
            <Grid item xs={6}>
              {el['clazz']}
            </Grid>
            <Grid item xs={6}>
              {el['order']}
            </Grid>
            <Grid item xs={6}>
              {el['family']}
            </Grid>
            <Grid item xs={6}>
              {el['genus']}
            </Grid>
            <Grid item xs={6}>
              {el['supersededBy']}
            </Grid>
          </Grid>
          <Grid container spacing={2}>
            <Grid item xs={6}>
              {el['reportGroup']}
            </Grid>
            <Grid item xs={6}>
              {el['habitatGroups']}
            </Grid>
            <Grid item xs={6}>
              {el['templatecode']}
            </Grid>
            <Grid item xs={6}>
              {el['letterCode']}
            </Grid>
          </Grid>
          {el['lengthWeight']}
        </Grid>
      </Box>
    </>
  );
};

ObservableItemTemplate.propTypes = {
  properties: PropTypes.any,
  title: PropTypes.string
};

export default ObservableItemTemplate;
