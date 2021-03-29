import React from 'react';
import {Box, Grid} from '@material-ui/core';
import {NavLink} from 'react-router-dom';
import {PropTypes} from 'prop-types';

const EntityContainer = (props) => (
  <>
    <NavLink to={props.goBackTo} color="secondary">
      {'<< Back to ' + props.name}
    </NavLink>
    <Grid container justify="center">
      {props.header}
      <Box style={{background: 'white', width: 700}} boxShadow={1} margin={3}>
        <Grid container alignItems="flex-start" direction="row">
          {props.children}
        </Grid>
      </Box>
    </Grid>
  </>
);

EntityContainer.propTypes = {
  name: PropTypes.string.isRequired,
  goBackTo: PropTypes.string.isRequired,
  children: PropTypes.any,
  header: PropTypes.any
};

export default EntityContainer;
