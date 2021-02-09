import React from 'react';

import { useDispatch, useSelector } from 'react-redux';
import { Link } from 'react-router-dom';
import { useEffect } from 'react';
import { useParams } from 'react-router-dom';
import NestedApiFieldDetails from './customWidgetFields/NestedApiFieldDetails';
import pluralize from 'pluralize';
import config from 'react-global-configuration';
import { Box } from '@material-ui/core';
import Alert from '@material-ui/lab/Alert';
import Grid from '@material-ui/core/Grid';
import { itemRequested } from './middleware/entities';
import Button from '@material-ui/core/Button';
import { makeStyles } from '@material-ui/core/styles';
import BaseForm from '../BaseForm';
import ObjectListViewTemplate from './ObjectListViewTemplate';
import LinkButton from './LinkButton';


const useStyles = makeStyles(() => ({
  buttons: {
    '& > *': {
      marginTop: 20
    }
  }
}));

const renderError = (msgArray) => {
  return (msgArray.length > 0) ? <><Box><Alert severity="error" variant="filled">{msgArray}</Alert></Box></> : <></>;
};

const GenericDetailsView = () => {

  const nonGenericEntities = {
    'stagedJobs': {
      linkLabel: 'Edit Job  ',
      linkPath: '/validation/{}'
    }
  };

  const classes = useStyles();

  const { entityName, id } = useParams();
  const schemaDefinition = config.get('api') || {};

  const editItem = useSelector(state => state.form.editItem);

  const dispatch = useDispatch();
  const singular = pluralize.singular(entityName);
  const entityTitle = singular.charAt(0).toUpperCase() + singular.slice(1);


  useEffect(() => {
    if (id !== undefined) {
      // Todo: make this request the specific list view api
      dispatch(itemRequested(entityName + '/' + id));
    }
  }, []);

  if (Object.keys(schemaDefinition).length === 0) {
    return renderError('ERROR: API Schema not found');
  }
  if (typeof (schemaDefinition[entityTitle]) == 'undefined') {
    return renderError("ERROR: Entity '" + entityTitle + "' missing from API Schema");
  }

  const entityDef = schemaDefinition[entityTitle];

  let fullTitle = 'Details for ' + entityTitle;
  const entitySchema = { title: fullTitle, ...entityDef };
  const JSSchema = { components: { schemas: schemaDefinition }, ...entitySchema };

  const uiSchemaRelationships = Object.keys(entitySchema.properties).filter(key => {
    return entitySchema.properties[key].type === 'string' && entitySchema.properties[key].format === 'uri';
  });
  const uiSchemaObjects = Object.keys(entitySchema.properties).filter(key => {
    return entitySchema.properties[key].type === 'object';
  });

  const inputDisplay = (elem) => {
    const value = elem.formData?.toString();
    return (<span><b>{elem.name}: </b> {(value) ? value : ' -- '}</span>);
  };

  const objectDisplay = (elem) => {
    let items = [];
    if (elem.formData) {

      if (!Array.isArray(elem.formData)) {
        if (elem.formData.label) {
          items.push(<Grid item>{elem.formData.label}</Grid>);
        } else {
          for (let key of Object.keys(elem.formData)) {
            items.push(<Grid item><b>{key}: </b>{elem.formData[key]}</Grid>);
          }
        }
      } else {
        elem.formData.map(item => items.push(<Grid item>{item}</Grid>));
      }
    }
    else {
      items.push(<Grid item>--</Grid>);
    }

    return ObjectListViewTemplate({ name: elem.name, items: items });

  };



  const uiSchema = {
    'ui:widget': 'string'
  };

  uiSchemaRelationships.map(key => {
    uiSchema[key] = { 'ui:field': 'relationship' };
  });

  uiSchemaObjects.map(key => {
    uiSchema[key] = { 'ui:field': 'objects' };
  });

  const fields = {
    relationship: NestedApiFieldDetails,
    objects: objectDisplay,
    ArrayField: objectDisplay,
    BooleanField: inputDisplay,
    NumberField: inputDisplay,
    StringField: inputDisplay
  };

  const submitButton = () => {

    const linkPath = nonGenericEntities[entityName]?.linkPath;
    let linkLabel = `Edit ` + entityTitle;
    let link = '/';
    if ((entityName in nonGenericEntities) && linkPath) {
      link =  '/' + linkPath.replace(/{(.*?)}/, id);
      linkLabel = (nonGenericEntities[entityName]?.linkLabel) ? nonGenericEntities[entityName]?.linkLabel : linkLabel;
    }
    else {
      link = '/form/' + entityName + '/' + id;
    }
    return <div className={classes.buttons}>
      <Button
        type={'submit'}
        component={Link}
        to={link}
        color="secondary"
        aria-label={linkLabel}
        variant={'contained'}
      >
        {linkLabel}
      </Button>
    </div>;
  };

  const formContent = () => {
    return <BaseForm
      schema={JSSchema}
      uiSchema={uiSchema}
      fields={fields}
      formData={editItem}
      submitButton={submitButton()}
    />;
  };

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
        <LinkButton
            to={'/list/' + entityTitle}
            title={'List ' + entityName}
            size={'small'}
        />
        <Grid item >
          {formContent()}
        </Grid>
      </Grid>
    </Grid>
  </Grid>;
};



export default GenericDetailsView;
