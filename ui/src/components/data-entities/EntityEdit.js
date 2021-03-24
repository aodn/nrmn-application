import React, {useEffect} from 'react';
import {useDispatch, useSelector} from 'react-redux';
import {useParams, NavLink, Redirect} from 'react-router-dom';
import config from 'react-global-configuration';
import Form from '@rjsf/material-ui';
import {Box, Button, CircularProgress, Grid} from '@material-ui/core';
import {Save} from '@material-ui/icons';
import Alert from '@material-ui/lab/Alert';
import PropTypes from 'prop-types';

import {createEntityRequested, itemRequested, updateEntityRequested} from './middleware/entities';

import LoadingBanner from '../layout/loadingBanner';
import ObjectListViewTemplate from './ObjectListViewTemplate';
import EntityContainer from './EntityContainer';

import DropDownInput from './customWidgetFields/DropDownInput';
import TextInput from './customWidgetFields/TextInput';
import NumberInput from './customWidgetFields/NumberInput';
import CheckboxInput from './customWidgetFields/CheckboxInput';
import AutoCompleteInput from './customWidgetFields/AutoCompleteInput';

import SpeciesSearch from '../search/SpeciesSearch';

const EntityEdit = ({entity, template, clone}) => {
  const params = useParams();

  const schemaDefinition = config.get('api') || {};
  const formData = useSelector((state) => state.form.data);
  const saved = useSelector((state) => state.form.saved);
  const errors = useSelector((state) => state.form.errors);
  const dispatch = useDispatch();

  const edit = !clone && typeof params.id !== 'undefined';

  useEffect(() => {
    if (params.id !== undefined) {
      dispatch(itemRequested(`${entity.endpoint}/${params.id}`));
    }
  }, [saved]);

  const handleSubmit = (e) => {
    if (edit) {
      const data = {path: `${entity.endpoint}/${params.id}`, data: e.formData};
      dispatch(updateEntityRequested(data));
    } else {
      const data = {path: entity.endpoint, data: e.formData};
      dispatch(createEntityRequested(data));
    }
  };

  const title = (edit ? 'Edit ' : clone ? 'Clone ' : 'New ') + entity.name;
  const entityDef = {...schemaDefinition[entity.schemaKey]};
  const entitySchema = {title: title, ...entityDef};
  const JSSchema = {components: {schemas: schemaDefinition}, ...entitySchema};

  const uiSchema = {};
  for (const key in entitySchema.properties) {
    const item = entitySchema.properties[key];
    // HACK: to just get this working
    if (key === 'mpa') {
      uiSchema[key] = {'ui:field': 'autostring', route: 'marineProtectedAreas'};
    } else if (key === 'protectionStatus') {
      uiSchema[key] = {'ui:field': 'autostring', route: 'protectionStatuses'};
    } else if (key === 'obsItemTypeId') {
      uiSchema[key] = {
        'ui:field': 'dropdown',
        default: 'Species',
        route: 'obsItemTypes',
        entity: 'obsItemTypeId',
        entityList: 'obsItemTypes',
        idKey: 'obsItemTypeId',
        valueKey: 'obsItemTypeName'
      };
    } else if (key === 'locationId') {
      uiSchema[key] = {
        'ui:field': 'dropdown',
        route: 'locations?projection=selection',
        entity: 'location',
        entityList: 'locations',
        idKey: 'locationId',
        valueKey: 'locationName'
      };
    } else if (key === 'reportGroup') {
      uiSchema[key] = {
        'ui:field': 'autostring',
        route: 'reportGroups'
      };
    } else if (key === 'habitatGroups') {
      uiSchema[key] = {
        'ui:field': 'autostring',
        route: 'habitatGroups'
      };
      // HACK: just to get these fields working on the Edit page
    } else if (key === 'relief' || key === 'slope' || key === 'waveExposure' || key === 'currents') {
      uiSchema[key] = {
        'ui:field': 'dropdown',
        entity: key,
        optional: true,
        values: [
          {id: 1, label: '1'},
          {id: 2, label: '2'},
          {id: 3, label: '3'},
          {id: 4, label: '4'}
        ]
      };
    } else if (item.type === 'object' && item.readOnly === true) {
      uiSchema[key] = {'ui:field': 'readonlyObject'};
    } else if (item.format === 'double') {
      uiSchema[key] = {'ui:field': 'double'};
    } else if (item.type === 'boolean') {
      uiSchema[key] = {'ui:field': 'boolean'};
    } else if (key === 'oldSiteCodes') {
      uiSchema[key] = {'ui:field': 'array'};
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
    dropdown: DropDownInput,
    readonlyObject: objectDisplay,
    string: TextInput,
    double: NumberInput,
    boolean: CheckboxInput,
    autostring: AutoCompleteInput
  };

  if (template === false) return <Redirect to="/wip" />;

  if (saved) {
    if (entity.name === 'Observable Item') window.location = '/reference/observableItems';
    const id = saved[entity.idKey];
    return <Redirect to={`${entity.route.base}/${id}/${edit ? 'saved' : 'new'}`} />;
  }

  return params.id && Object.keys(formData).length === 0 ? (
    <Grid container direction="row" justify="flex-start" alignItems="center">
      <LoadingBanner variant={'h5'} msg={`Loading ${entity.name}`} />
    </Grid>
  ) : (
    <EntityContainer name={entity.name} goBackTo={entity.list.route}>
      <Grid item>
        {params.loading ? (
          <CircularProgress size={20} />
        ) : (
          <>
            {entity.showSpeciesSeach && <SpeciesSearch />}
            <Box pt={2} px={6.25} pb={6}>
              {errors.length > 0 ? (
                <Box pt={2}>
                  <Alert severity="error" variant="filled">
                    Please review this form for errors and try again.
                  </Alert>
                </Box>
              ) : null}
              <Form
                onError={params.onError}
                errors={errors}
                schema={JSSchema}
                uiSchema={uiSchema}
                onSubmit={handleSubmit}
                showErrorList={false}
                fields={fields}
                formData={formData}
                ObjectFieldTemplate={template}
              >
                <Box display="flex" justifyContent="center" mt={5}>
                  <Button variant="contained" disabled={params.loading} component={NavLink} to={entity.list.route}>
                    Cancel
                  </Button>
                  <Button
                    style={{width: '50%', marginLeft: '5%', marginRight: '20%'}}
                    type="submit"
                    variant="contained"
                    color="secondary"
                    startIcon={<Save></Save>}
                    disabled={params.loading}
                  >
                    Save {entity.name}
                  </Button>
                </Box>
              </Form>
            </Box>
          </>
        )}
      </Grid>
    </EntityContainer>
  );
};

EntityEdit.propTypes = {
  entity: PropTypes.object,
  template: PropTypes.func,
  clone: PropTypes.bool
};

export default EntityEdit;
