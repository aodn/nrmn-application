import React from 'react';
import Box from "@material-ui/core/Box";
import Alert from '@material-ui/lab/Alert';
import Grid from "@material-ui/core/Grid";
import Paper from "@material-ui/core/Paper";
import Form from "@rjsf/material-ui"
import makeStyles from "@material-ui/core/styles/makeStyles";


const useStyles = makeStyles((theme) => ({
  root: {
    '& > * + *': {
      marginTop: theme.spacing(2),
    },
  },
}));

const BaseForm = (params) => {

  const classes = useStyles();

  let errorAlert = params.errors ? <Alert  severity="error" variant="outlined" >{params.errors}</Alert> : "";

  return <>
    <Grid
        container
        spacing={0}
        alignItems="center"
        justify="center"
        style={{ minHeight: "70vh" }}
    >
      <Paper elevation={0}>
        <Box pt={4} px={6} pb={6} className={classes.root} >
          <Form
              schema={params.schema}
              uiSchema={params.uiSchema}
              onSubmit={params.onSubmit}
              gutterBottom={true}
              showErrorList={true}
          />
          {errorAlert}
        </Box>
      </Paper>
    </Grid>
  </>;
}

export default BaseForm;

