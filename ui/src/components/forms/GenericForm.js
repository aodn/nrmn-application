import React from "react";

import Form from "@rjsf/material-ui"
import {useDispatch, useSelector} from "react-redux";
import {useEffect} from 'react';
import {resetState, idRequested, createEntityRequested} from "./form-reducer";
import {useParams, Redirect} from "react-router-dom";
import ArrayApiField from './customWidget/ArrayApiField';
import pluralize from 'pluralize';
import config from "react-global-configuration";
import {Box} from "@material-ui/core";
import Alert from "@material-ui/lab/Alert";
import Paper from "@material-ui/core/Paper";
import Grid from "@material-ui/core/Grid";
import {titleCase} from "title-case";
import {LoadingBanner} from "../layout/loadingBanner";

const renderError = (msg) => {
  return <Box><Alert severity="error" variant="filled">{msg}</Alert></Box>
}

const GenericForm = () => {

  const {entityName, id} = useParams();
  const schemaDefinition = config.get('api');
  const editItem = useSelector(state => state.form.editItem);
  const createdEntity = useSelector(state => state.form.createdEntity);
  const errors = useSelector(state => state.form.errors);

  const dispatch = useDispatch();
  const singular = pluralize.singular(entityName);
  const entityTitle = singular.charAt(0).toUpperCase() + singular.slice(1)

  useEffect(() => {
    if (id !== undefined) {
      dispatch(idRequested(entityName + "/" + id));
    }
  }, []);

  if (Object.keys(createdEntity).length !== 0) {
    const redirectPath = "/entity/" + entityTitle;
    console.log('redirected:', redirectPath);
    return (<Redirect to={redirectPath}></Redirect>);
  }

  if (Object.keys(schemaDefinition).length === 0 && typeof (schemaDefinition[entityTitle]) == 'undefined')
    return renderError("ERROR: API Schema not found");

  const getErrors = () => {
    if (errors) {
debugger;
      //return  renderError(errors);
    }
  }

  const fields = {ArrayField: ArrayApiField}

  const handleSubmit = (form) => {
    console.info("submited:", entityName, id, form.formData);
    dispatch(createEntityRequested({path: entityName, id: id, data: form.formData}));
  }

  const {title, ...entityDef} = schemaDefinition[entityTitle]
  const editSchema = (id) ? {title: title.replace("Add", "Edit"), ...entityDef} : schemaDefinition[entityTitle]
  const JSSchema = {components: {schemas: schemaDefinition}, ...editSchema};

  return ((id && Object.keys(editItem).length === 0) ?
      <LoadingBanner variant={"h5"} msg={"Loading edit form for " + titleCase(title)}/>  :
      <>
        <Grid
          container
          spacing={0}
          alignItems="center"
          justify="center"
          style={{minHeight: "70vh"}}
      >
        <Paper>
          <Box mx="auto" bgcolor="background.paper" pt={2} px={3} pb={3}>
            <Form
                schema={JSSchema}
                onSubmit={handleSubmit}
                fields={fields}
                formData={editItem}
            />
          </Box>
        </Paper>
      </Grid>

        {getErrors()}

        </>
)
}

export default GenericForm;