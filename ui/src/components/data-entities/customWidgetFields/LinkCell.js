import React from 'react';

import {Button} from '@material-ui/core';
import {NavLink} from 'react-router-dom';
import {PropTypes} from 'prop-types';

const LinkCell = (props) => (
  <Button color="primary" component={NavLink} to={props.link}>
    {props.label}
  </Button>
);

LinkCell.propTypes = {
  link: PropTypes.string,
  label: PropTypes.string
};

export default LinkCell;
