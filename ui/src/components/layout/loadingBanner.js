import React from 'react';
import Typography from "@material-ui/core/Typography";
import {LoadingSpinner} from "./loadingSpinner";


export const LoadingBanner = (props) => {

  return <>
      <Typography variant={props.variant}>{props.msg} &nbsp; </Typography>
      <LoadingSpinner color={"primary"} />
    </>

}