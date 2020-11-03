import React from 'react';
import { makeStyles } from '@material-ui/styles';
import CircularProgress from "@material-ui/core/CircularProgress";


export const LoadingSpinner = (props) => {
  //const classes = useStyles();
  const size = (props.size) ? props.size : 21;
  const thickness = (props.thickness) ? props.thickness : size/3 + 3  ;


  return <CircularProgress
      {...props}
      color={"secondary"} size={size} thickness={thickness} />
}