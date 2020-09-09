import React from 'react';
import { makeStyles } from '@material-ui/styles';
import Button from '@material-ui/core/Button';

const useStyles = makeStyles({
  root: {
    padding: '0 20px',
  },
});

export const TopbarButton = (props) => {
  const classes = useStyles();
  return <Button
      {...props}
      color="inherit"
      size="small"
      className={classes.root}>{props.children}</Button>;
}