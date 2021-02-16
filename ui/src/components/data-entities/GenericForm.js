import React from 'react';

import {useDispatch, useSelector} from 'react-redux';
import {useEffect} from 'react';
import {useParams} from 'react-router-dom';
import NestedApiField from './customWidgetFields/NestedApiField';
import pluralize from 'pluralize';
import config from 'react-global-configuration';
import {Box} from '@material-ui/core';
import Alert from '@material-ui/lab/Alert';
import Grid from '@material-ui/core/Grid';
import {titleCase} from 'title-case';
import LoadingBanner from '../layout/loadingBanner';
import {createEntityRequested, itemRequested, updateEntityRequested} from './middleware/entities';
import Typography from '@material-ui/core/Typography';
import BaseForm from '../BaseForm';
import LinkButton from './LinkButton';


const renderError = (msgArray) => {
  return (msgArray.length > 0) ? <><Box><Alert severity='error' variant='filled'>{msgArray}</Alert></Box></> : <></>;
};

const GenericForm = () => {

  const {entityName, id} = useParams();
  const schemaDefinition = config.get('api') || {};

  const editItem = useSelector(state => state.form.editItem);
  const entitySaved = useSelector(state => state.form.entitySaved);
  const errors = useSelector(state => state.form.errors);

  const dispatch = useDispatch();

  const singular = pluralize.singular(entityName);
  const entityTitle = singular.charAt(0).toUpperCase() + singular.slice(1);
  const submitButtonLabel = 'List ' + entityName;


  useEffect(() => {
    if (id !== undefined) {
      dispatch(itemRequested(entityName + '/' + id));
    }
  }, [entitySaved]);

  if (Object.keys(schemaDefinition).length === 0) {
    return renderError('ERROR: API Schema not found');
  }
  if ( typeof (schemaDefinition[entityTitle]) == 'undefined') {
    return renderError(`ERROR: Entity '` + entityTitle + `' missing from API Schema`);
  }

  const handleSubmit = (form) => {
    const data = {path: entityName, id: id, data: form.formData};
    (id) ?
      dispatch(updateEntityRequested(data)) :
      dispatch(createEntityRequested(data)) ;
  };

  const entityDef = schemaDefinition[entityTitle];

  let fullTitle = (id) ?  'Edit ' + entityTitle  : `Add '` + entityTitle + `'` ;

  const entitySchema = {title: fullTitle, ...entityDef};
  const JSSchema = {components: {schemas: schemaDefinition}, ...entitySchema};

  const uiSchemaHacks = Object.keys(entitySchema.properties).filter( key => {
    return entitySchema.properties[key].type === 'string' && entitySchema.properties[key].format === 'uri';
  } );

  const uiSchema = {};

  uiSchemaHacks.map( key => {
    uiSchema[key] = {'ui:field': 'relationship'};
  });

  const fields = {
    relationship: NestedApiField
  };

  const formContent = ()=>{
    if (entitySaved) {
      return <>
        <Typography variant={'h4'} >Entity saved successfully!</Typography>
      </>;
    }
    else {
      return <BaseForm
          schema={JSSchema}
          uiSchema={uiSchema}
          onSubmit={handleSubmit}
          fields={fields}
          formData={editItem}
          onCancel={'/list/' + entityTitle}
      />;
    }
  };

  if (errors.length > 0) {
    return renderError(errors);
  }
  else {
    if (schemaDefinition[entityTitle] === undefined) {
      return renderError(['Entity '+ entityName + ' cannot be found']);
    }
    else {
      return (id && Object.keys(editItem).length === 0) ?
          <Grid
              container
              direction='row'
              justify='flex-start'
              alignItems='center'
          >
          <LoadingBanner variant={'h5'} msg={`Loading '` + titleCase(entityName) + `' form`} />
          </Grid> :
          <Grid
              container
              direction='row'
              justify='center'
              alignItems='center'
              style={{minHeight: '70vh'}}
          >
            <Grid item >
              <Grid
                  container
                  alignItems='flex-end'
                  justify='space-around'
                  direction='column'
              >
                <LinkButton
                    key={submitButtonLabel}
                    title={submitButtonLabel}
                    label={submitButtonLabel}
                    to={'/list/' + entityTitle}
                />
                <Grid item >
                  {formContent()}
                </Grid>
              </Grid>
            </Grid>
          </Grid>;
    }
  }
};

export default GenericForm;
