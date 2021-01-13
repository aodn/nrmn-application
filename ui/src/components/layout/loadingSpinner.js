import React from 'react';
import CircularProgress from "@material-ui/core/CircularProgress";


export const LoadingSpinner = (props) => {
  const size = (props.size) ? props.size : 21;
  const thickness = (props.thickness) ? props.thickness : size/3 + 3  ;


  return <CircularProgress
      {...props}
      color={props.color} size={size} thickness={thickness} />
}