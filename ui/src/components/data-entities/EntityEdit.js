import React, {useEffect} from 'react';
import {useDispatch, useSelector} from 'react-redux';
import {useLocation, useParams, NavLink, Redirect} from 'react-router-dom';
import config from 'react-global-configuration';
import Form from '@rjsf/material-ui';
import {Box, Button, CircularProgress, Grid} from '@material-ui/core';
import Alert from '@material-ui/lab/Alert';
import PropTypes from 'prop-types';

import {createEntityRequested, itemRequested, updateEntityRequested} from './middleware/entities';

import LoadingBanner from '../layout/loadingBanner';
import ObjectListViewTemplate from './ObjectListViewTemplate';
import EntityContainer from './EntityContainer';

import NestedApiField from './customWidgetFields/NestedApiField';
import TextInput from './customWidgetFields/TextInput';
import NumberInput from './customWidgetFields/NumberInput';
import CheckboxInput from './customWidgetFields/CheckboxInput';
import AutocompleteField from './customWidgetFields/AutocompleteField';

const EntityEdit = (props) => {
  const params = useParams();
  const location = useLocation();
  const schemaDefinition = config.get('api') || {};

  const editItem = useSelector((state) => state.form.formData);
  const entitySaved = useSelector((state) => state.form.entitySaved);
  const errors = useSelector((state) => state.form.errors);
  const dispatch = useDispatch();

  useEffect(() => {
    if (params.id !== undefined) {
      dispatch(itemRequested(`${props.entity.endpoint}/${params.id}`));
    }
  }, [entitySaved]);

  const handleSubmit = (form) => {
    const data = {path: props.entity.route.base, id: params.id, data: form.formData};
    params.id ? dispatch(updateEntityRequested(data)) : dispatch(createEntityRequested(data));
  };

  const entityDef = schemaDefinition[props.entity.schemaKey];
  const isEdit = typeof params.id !== 'undefined';

  let fullTitle = (isEdit ? 'Edit ' : 'New ') + props.entity.name;

  const entitySchema = {title: fullTitle, ...entityDef};
  const JSSchema = {components: {schemas: schemaDefinition}, ...entitySchema};
  const uiSchema = {};

  for (const key in entitySchema.properties) {
    const item = entitySchema.properties[key];
    // HACK: to just get this working
    if (key === 'mpa') {
      uiSchema[key] = {'ui:field': 'autostring', 'ui:route': 'marineProtectedAreas'};
    } else if (key === 'protectionStatus') {
      uiSchema[key] = {'ui:field': 'autostring', 'ui:route': 'protectionStatuses'};
    } else if (item.type === 'string' && item.format === 'uri') {
      uiSchema[key] = {'ui:field': 'relationship'};
    } else if (item.type === 'object' && item.readOnly === true) {
      uiSchema[key] = {'ui:field': 'readonlyObject'};
    } else if (item.format === 'double') {
      uiSchema[key] = {'ui:field': 'double'};
    } else if (item.type === 'boolean') {
      uiSchema[key] = {'ui:field': 'boolean'};
    } else {
      uiSchema[key] = {'ui:field': 'string'};
    }
  }

  const objectDisplay = (elem) => {
    let items = [];
    if (elem.formData) {
      if (!Array.isArray(elem.formData)) {
        for (let key of Object.keys(elem.formData)) {
          items.push(
            <Grid key={key} item>
              <b>{key}: </b>
              {elem.formData[key]}
            </Grid>
          );
        }
      } else {
        elem.formData.map((item) => items.push(<Grid item>{item}</Grid>));
      }
    } else {
      items.push(<Grid item>--</Grid>);
    }
    return ObjectListViewTemplate({name: elem.name + ' (Readonly)', items: items});
  };

  const fields = {
    relationship: NestedApiField,
    readonlyObject: objectDisplay,
    string: TextInput,
    double: NumberInput,
    boolean: CheckboxInput,
    autostring: AutocompleteField
  };

  function getErrors(errors) {
    return errors.map((item, key) => {
      return <div key={key}>{item}</div>;
    });
  }

  let errorAlert =
    params.errors && params.errors.length > 0 ? (
      <Alert severity="error" variant="filled">
        {getErrors(params.errors)}
      </Alert>
    ) : (
      ''
    );

  if (entitySaved) {
    return <Redirect to={`${location.pathname.replace('/edit', '/saved')}`} />;
  }

  return params.id && Object.keys(editItem).length === 0 ? (
    <Grid container direction="row" justify="flex-start" alignItems="center">
      <LoadingBanner variant={'h5'} msg={`Loading ${props.entity.name}`} />
    </Grid>
  ) : (
    <EntityContainer name={props.entity.name} goBackTo={props.entity.list.route}>
      <Grid item>
        {errors.length > 0 ? (
          <Box>
            <Alert severity="error" variant="filled">
              {errors[0].message}
            </Alert>
          </Box>
        ) : null}
        {params.loading ? (
          <CircularProgress size={20} />
        ) : (
          <Box pt={2} px={6} pb={6}>
            {errorAlert}
            <Form
              onError={params.onError}
              schema={JSSchema}
              uiSchema={uiSchema}
              onSubmit={handleSubmit}
              showErrorList={false}
              fields={fields}
              formData={editItem}
              ObjectFieldTemplate={props.template}
            >
              <Box display="flex" justifyContent="center" mt={5}>
                <Button variant="contained" disabled={params.loading} component={NavLink} to={props.entity.list.route}>
                  Cancel
                </Button>
                <Button
                  style={{width: '50%', marginLeft: '5%', marginRight: '20%'}}
                  type="submit"
                  variant="contained"
                  color="secondary"
                  disabled={params.loading}
                >
                  Save {props.entity.name}
                </Button>
              </Box>
            </Form>
          </Box>
        )}
      </Grid>
    </EntityContainer>
  );
};

EntityEdit.propTypes = {
  entity: PropTypes.object,
  template: PropTypes.function
};

export default EntityEdit;
