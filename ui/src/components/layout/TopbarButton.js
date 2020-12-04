import React from 'react';
import PropTypes from 'prop-types';

import { makeStyles } from '@material-ui/styles';
import Button from '@material-ui/core/Button';

const useStyles = makeStyles({
  root: {
    padding: '0 20px',
  },
});

// prop types too generic to be specify

 const TopbarButton = (props) => {
   console.debug(props);
  const classes = useStyles();

  return <Button
    {...props}
    color="inherit"
    size="small"
    className={classes.root}>{props.children}</Button>;
};


export default TopbarButton;