import React from 'react';
import Container from "@material-ui/core/Container";
import Grid from "@material-ui/core/Grid";
import Typography from "@material-ui/core/Typography";
import Paper from "@material-ui/core/Paper";
import {connect} from "react-redux";
import Form from "@rjsf/material-ui"

const validatorMapper = {}


const submitAction = (formValues) => {
  // get response and show errors, or move to next page
}

const ReferenceForm = (params) => {

  return <>
    <Grid
        container
        spacing={0}
        alignItems="center"
        justify="center"
        style={{ minHeight: "70vh" }}
    >
      <Paper >
        <Container  maxWidth="sm">
          <Typography variant="h4" >
            {params.formTitle}
          </Typography>
          <Form
              schema={params.schema}
              onSubmit={params.submitAction}
          />
        </Container>
      </Paper>
    </Grid>
  </>;
}



export default ReferenceForm;


