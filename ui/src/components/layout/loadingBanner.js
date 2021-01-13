import React from 'react';
import Typography from "@material-ui/core/Typography";
import {LoadingSpinner} from "./loadingSpinner";
import makeStyles from "@material-ui/core/styles/makeStyles";

const useStyles = makeStyles((theme) => ({
  white: {
    color: "#FFFFFF"
  }
}));

export const LoadingBanner = (props) => {

  const classes = useStyles();
  let style = "";
  let spinnerColor = "primary";

  if (props.color === "white") {
    style =  classes.white;
    spinnerColor = "inherit";
  }

  return <>
      <Typography className={style} variant={props.variant}>{props.msg} &nbsp; </Typography>
      <LoadingSpinner color={spinnerColor} />
    </>

}