import React from "react";

import Form from "@rjsf/material-ui"
import {useDispatch, useSelector} from "react-redux";
import {useEffect} from 'react';
import {itemRequested, createEntityRequested, updateEntityRequested} from "./form-reducer";
import {useParams, Redirect} from "react-router-dom";
import ArrayApiField from './customWidgetFields/ArrayApiField';
import pluralize from 'pluralize';
import config from "react-global-configuration";
import {Box} from "@material-ui/core";
import Alert from "@material-ui/lab/Alert";
import Paper from "@material-ui/core/Paper";
import Grid from "@material-ui/core/Grid";
import {titleCase} from "title-case";
import {LoadingBanner} from "../layout/loadingBanner";

const renderError = (msgArray) => {
  return (msgArray.length > 0) ? <><Box><Alert severity="error" variant="filled">{msgArray}</Alert></Box></> : <></>;
}

const GenericForm = () => {

  const {entityName, id} = useParams();
  const schemaDefinition = config.get('api') || {};

  const editItem = useSelector(state => state.form.editItem);
  const newlyCreatedEntity = useSelector(state => state.form.newlyCreatedEntity);
  const errors = useSelector(state => state.form.errors);

  const dispatch = useDispatch();
  const singular = pluralize.singular(entityName);
  const entityTitle = singular.charAt(0).toUpperCase() + singular.slice(1)


  useEffect(() => {
    if (id !== undefined) {
      dispatch(itemRequested(entityName + "/" + id));
    }
  }, [newlyCreatedEntity]);

  if (Object.keys(newlyCreatedEntity).length !== 0) {
    const redirectPath = "/list/" + entityTitle;
    return (<Redirect to={redirectPath}></Redirect>);
  }
  if (Object.keys(schemaDefinition).length === 0) {
    return renderError("ERROR: API Schema not found");
  }
  if ( typeof (schemaDefinition[entityTitle]) == 'undefined') {
    return renderError("ERROR: Entity '" + entityTitle + "' missing from API Schema");
  }

  const fields = {ArrayField: ArrayApiField}

  const handleSubmit = (form) => {

    const data = {path: entityName, id: id, data: form.formData}
    console.info("submited:", data);
    (Object.keys(editItem).length === 0) ?
      dispatch(createEntityRequested(data)) :
      dispatch(updateEntityRequested(data));
  }

  const entityDef = schemaDefinition[entityTitle];

  let fullTitle = (id) ?  "Edit " + entityTitle + " '" + id + "'" : "Add '" + entityTitle + "'" ;
  const entitySchema = {title: fullTitle, ...entityDef}
  const JSSchema = {components: {schemas: schemaDefinition}, ...entitySchema};

  if (errors.length > 0) {
    return renderError(errors)
  }
  else {
    if (schemaDefinition[entityTitle] === undefined) {
      return renderError(["Entity '" + entityName + "' cannot be found"]);
    }
    else {
      return (id && Object.keys(editItem).length === 0) ?
          <LoadingBanner variant={"h5"} msg={"Loading '" + titleCase(entityName) + "' form"  } /> :

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
    }
  }

}

export default GenericForm;