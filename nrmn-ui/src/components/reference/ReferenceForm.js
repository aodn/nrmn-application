import React from 'react';
import Container from "@material-ui/core/Container";
import Box from "@material-ui/core/Box";
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
        <Box mx="auto" bgcolor="background.paper"  pt={2} px={3} pb={3}>
          <Typography variant="h4" >
            {params.formTitle}
          </Typography>
          <Form
              schema={params.schema}
              uiSchema={params.uiSchema}
              //onSubmit={params.submitAction}
          />
        </Box>
      </Paper>
    </Grid>
  </>;
}



export default ReferenceForm;


