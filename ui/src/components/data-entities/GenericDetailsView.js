import React from "react";

import {useDispatch, useSelector} from "react-redux";
import { Link } from 'react-router-dom'
import {useEffect} from 'react';
import {useParams} from "react-router-dom";
import NestedApiFieldDetails from './customWidgetFields/NestedApiFieldDetails';
import pluralize from 'pluralize';
import config from "react-global-configuration";
import {Box} from "@material-ui/core";
import Alert from "@material-ui/lab/Alert";
import Grid from "@material-ui/core/Grid";
import {itemRequested} from "./middleware/entities";
import Button from "@material-ui/core/Button";
import {makeStyles} from "@material-ui/core/styles";
import BaseForm from "../BaseForm";
import ObjectListViewTemplate from "./ObjectListViewTemplate";


const useStyles = makeStyles(theme => ({
  buttons: {
    "& > *": {
      marginTop: 20
    }
  }
}));

const renderError = (msgArray) => {
  return (msgArray.length > 0) ? <><Box><Alert severity="error" variant="filled">{msgArray}</Alert></Box></> : <></>;
}

const GenericDetailsView = () => {

  const classes = useStyles();

  const {entityName, id} = useParams();
  const schemaDefinition = config.get('api') || {};

  const editItem = useSelector(state => state.form.editItem);

  const dispatch = useDispatch();
  const singular = pluralize.singular(entityName);
  const entityTitle = singular.charAt(0).toUpperCase() + singular.slice(1)


  useEffect(() => {
    if (id !== undefined) {
      dispatch(itemRequested(entityName + "/" + id));
    }
  }, []);

  if (Object.keys(schemaDefinition).length === 0) {
    return renderError("ERROR: API Schema not found");
  }
  if ( typeof (schemaDefinition[entityTitle]) == 'undefined') {
    return renderError("ERROR: Entity '" + entityTitle + "' missing from API Schema");
  }

  const entityDef = schemaDefinition[entityTitle];

  let fullTitle = "Details for " + entityTitle + " '" + id + "'"  ;
  const entitySchema = {title: fullTitle, ...entityDef}
  const JSSchema = {components: {schemas: schemaDefinition}, ...entitySchema};

  const uiSchemaRelationships = Object.keys(entitySchema.properties).filter( key => {
    return entitySchema.properties[key].type === "string" && entitySchema.properties[key].format === "uri"
  });
  const uiSchemaObjects = Object.keys(entitySchema.properties).filter( key => {
    return entitySchema.properties[key].type === "object";
  });

  const inputDisplay = (props) => {

    const value = (typeof props.formData === "boolean") ? props.formData.toString() : props.formData
    return (<span><b>{props.name}: </b> {(value) ? value: " -- "}</span>);
  };

  const objectDisplay = (props) => {
    let items = [];
    if (props.formData) {

      if (!Array.isArray(props.formData)) {
        if (props.formData.label) {
          items.push(<Grid item>{props.formData.label}</Grid>);
        } else {
          for (let key of Object.keys(props.formData)) {
            items.push(<Grid item><b>{key}: </b>{props.formData[key]}</Grid>);
          }
        }
      } else {
        props.formData.map(item => items.push(<Grid item>{item}</Grid>))
      }
    }
    else {
      items.push(<Grid item>--</Grid>)
    }

    return ObjectListViewTemplate({name: props.name, items:items});

  };

  const uiSchema = {
    "ui:widget": "string"
  };

  uiSchemaRelationships.map( key => {
    uiSchema[key] = {'ui:field': "relationship"}
  });

  uiSchemaObjects.map( key => {
    uiSchema[key] = {'ui:field': "objects"}
  });

  const fields = {
    relationship: NestedApiFieldDetails,
    objects: objectDisplay,
    ArrayField: objectDisplay,
    BooleanField: inputDisplay,
    NumberField: inputDisplay,
    StringField: inputDisplay
  }

  const submitButton = () => {
    return  <div className={classes.buttons}>
      <Button
          type={"submit"}
          component={Link}
          to={"/form/" + entityName + "/" + id}
          color="secondary"
          aria-label={"Edit " + entityTitle + " " + id }
          variant={"contained"}
      >
        Edit {entityTitle} '{id}'
      </Button>
    </div>
  }

  const formContent = () => {
      return <BaseForm
            schema={JSSchema}
            uiSchema={uiSchema}
            fields={fields}
            formData={editItem}
            submitButton={submitButton()}
        />
  }

  return <Grid
      container
      direction="row"
      justify="center"
      alignItems="center"
    >
      <Grid item >
        <Grid
            container
            alignItems="flex-end"
            justify="space-around"
            direction="column"
        >
          <Grid item >
            <div className={classes.buttons}>
            <Button
                size="small"
                component={Link}
                to={"/list/" + entityTitle}
                color="secondary"
                aria-label={"List " + entityTitle}
                variant={"contained"}
            >
              List {entityName}
            </Button>
            </div>
          </Grid>
          <Grid item >
            {formContent()}
          </Grid>
        </Grid>
      </Grid>
  </Grid>
}

export default GenericDetailsView;
