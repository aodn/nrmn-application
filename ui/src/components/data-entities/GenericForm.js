import React from 'react';

import {useDispatch, useSelector} from 'react-redux';
import {useEffect} from 'react';
import {useParams} from 'react-router-dom';
import NestedApiField from './customWidgetFields/NestedApiField';
import config from 'react-global-configuration';
import {Box} from '@material-ui/core';
import Alert from '@material-ui/lab/Alert';
import Grid from '@material-ui/core/Grid';
import LoadingBanner from '../layout/loadingBanner';
import {createEntityRequested, itemRequested, updateEntityRequested} from './middleware/entities';
import Typography from '@material-ui/core/Typography';
import BaseForm from '../BaseForm';
import LinkButton from './LinkButton';
import ObjectListViewTemplate from './ObjectListViewTemplate';
import PropTypes from 'prop-types';
import _ from 'lodash';

const GenericForm = (props) => {
  const {id} = useParams();
  const schemaDefinition = config.get('api') || {};

  const editItem = useSelector((state) => state.form.editItem);
  const entitySaved = useSelector((state) => state.form.entitySaved);
  const errors = useSelector((state) => state.form.errors);

  const dispatch = useDispatch();

  const submitButtonLabel = 'List ' + props.entity.entityName;

  useEffect(() => {
    if (id !== undefined) {
      dispatch(itemRequested(props.entity.entityListName + '/' + id));
    }
  }, [entitySaved]);

  const handleSubmit = (form) => {
    const data = {path: props.entity.entityListName, id: id, data: form.formData};
    id ? dispatch(updateEntityRequested(data)) : dispatch(createEntityRequested(data));
  };

  const entityDef = schemaDefinition[props.entity.entityName];

  let fullTitle = (id ? 'Edit ' : 'New ') + props.entity.entityName;

  const entitySchema = {title: fullTitle, ...entityDef};
  const JSSchema = {components: {schemas: schemaDefinition}, ...entitySchema};
  const uiSchema = {};

  for (const key in entitySchema.properties) {
    const item = entitySchema.properties[key];
    if (item.type === 'string' && item.format === 'uri') {
      uiSchema[key] = {'ui:field': 'relationship'};
    }
    if (item.type === 'object' && item.readOnly === true) {
      uiSchema[key] = {'ui:field': 'readonlyObject'};
    }
  }

  const objectDisplay = (elem) => {
    let items = [];
    if (elem.formData) {
      if (!Array.isArray(elem.formData)) {
        for (let key of Object.keys(elem.formData)) {
          items.push(
            <Grid key={_.uniqueId('dataObject-')} item>
              <b>{key}: </b>
              {elem.formData[key]}
            </Grid>
          );
        }
      } else {
        elem.formData.map((item) => items.push(<Grid item>{item}</Grid>));
      }
    } else {
      items.push(
        <Grid key={_.uniqueId('dataObject-')} item>
          --
        </Grid>
      );
    }
    return ObjectListViewTemplate({name: elem.name + ' (Readonly)', items: items});
  };

  const fields = {
    relationship: NestedApiField,
    readonlyObject: objectDisplay
  };

  const formContent = () => {
    if (entitySaved) {
      return (
        <>
          <Typography variant={'h4'}>Entity saved successfully!</Typography>
        </>
      );
    } else {
      return (
        <>
          {errors.length > 0 ? (
            <Box>
              <Alert severity="error" variant="filled">
                {errors[0].message}
              </Alert>
            </Box>
          ) : null}
          <BaseForm
            schema={JSSchema}
            uiSchema={uiSchema}
            onSubmit={handleSubmit}
            fields={fields}
            formData={editItem}
            onCancel={'/list/' + props.entity.entityListName}
          />
        </>
      );
    }
  };

  return id && Object.keys(editItem).length === 0 ? (
    <Grid container direction="row" justify="flex-start" alignItems="center">
      <LoadingBanner variant={'h5'} msg={`Loading ` + props.entity.entityName} />
    </Grid>
  ) : (
    <Grid container direction="row" justify="center" alignItems="center" style={{minHeight: '70vh'}}>
      <Grid item>
        <Grid container alignItems="flex-end" justify="space-around" direction="column">
          <LinkButton
            key={submitButtonLabel}
            title={submitButtonLabel}
            label={submitButtonLabel}
            to={'/list/' + props.entity.entityListName}
          />
          <Grid item>{formContent()}</Grid>
        </Grid>
      </Grid>
    </Grid>
  );
};

GenericForm.propTypes = {
  entity: PropTypes.object
};

export default GenericForm;
