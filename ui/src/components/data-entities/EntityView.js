import React from 'react';

import {useDispatch, useSelector} from 'react-redux';
import {Link, useParams} from 'react-router-dom';
import {useEffect} from 'react';
import NestedApiFieldDetails from './customWidgetFields/NestedApiFieldDetails';
import TextInput from './customWidgetFields/TextInput';
import config from 'react-global-configuration';
import {Box, Button, Grid} from '@material-ui/core';
import Alert from '@material-ui/lab/Alert';
import {itemRequested} from './middleware/entities';
import Form from '@rjsf/material-ui';
import ObjectListViewTemplate from './ObjectListViewTemplate';
import {PropTypes} from 'prop-types';
import {Edit} from '@material-ui/icons';
import {resetState} from './form-reducer';

import EntityContainer from './EntityContainer';

const EntityView = (props) => {
  const params = useParams();
  const dispatch = useDispatch();
  const editItem = useSelector((state) => state.form.formData);
  const schemaDefinition = config.get('api') || {};

  useEffect(() => {
    dispatch(resetState());
    dispatch(itemRequested(`${props.entity.endpoint}/${params.id}`));
  }, []);

  const entityDef = schemaDefinition[props.entity.schemaKey];
  const fullTitle = 'Details for ' + props.entity.name;
  const entitySchema = {title: fullTitle, ...entityDef};
  const JSSchema = {components: {schemas: schemaDefinition}, ...entitySchema};
  const uiSchema = {'ui:widget': 'string'};

  for (const key in entitySchema.properties) {
    const item = entitySchema.properties[key];
    if (item.type === 'string' && item.format === 'uri') {
      uiSchema[key] = {'ui:field': 'relationship'};
    }
    if (item.type === 'object') {
      uiSchema[key] = {'ui:field': 'objects'};
    }
    if (item.type === 'string') {
      uiSchema[key] = {'ui:field': 'readonly'};
    }
  }

  const inputDisplay = (elem) => {
    const value = elem.formData?.toString();
    return (
      <span>
        <b>{elem.schema.title}: </b> {value ? value : ' -- '}
      </span>
    );
  };

  const objectDisplay = (elem) => {
    let items = [];
    if (elem.formData) {
      if (!Array.isArray(elem.formData)) {
        if (elem.formData.label) {
          items.push(<Grid item>{elem.formData.label}</Grid>);
        } else {
          for (let key of Object.keys(elem.formData)) {
            items.push(
              <Grid key={key} item>
                <b>{key}: </b>
                {elem.formData[key]}
              </Grid>
            );
          }
        }
      } else {
        elem.formData.map((item) => items.push(<Grid item>{item}</Grid>));
      }
    } else {
      items.push(<Grid item>--</Grid>);
    }
    return ObjectListViewTemplate({name: elem.schema.title ?? elem.name, items: items});
  };

  const fields = {
    relationship: NestedApiFieldDetails,
    objects: objectDisplay,
    ArrayField: objectDisplay,
    BooleanField: inputDisplay,
    NumberField: inputDisplay,
    StringField: TextInput
  };

  function getErrors(errors) {
    return errors.map((item, key) => {
      return <div key={key}>{item}</div>;
    });
  }

  let alert =
    params.errors && params.errors.length > 0 ? (
      <Alert severity="error" variant="filled">
        {getErrors(params.errors)}
      </Alert>
    ) : params.success ? (
      <Alert severity="info" variant="filled">
        {props.entity.name} {params.success === 'new' ? 'Created' : 'Updated'}
      </Alert>
    ) : null;

  return (
    <EntityContainer name={props.entity.name} goBackTo={props.entity.list.route}>
      <Grid item xs={9}>
        <Box pt={4} px={6} pb={6}>
          {alert}
          <Form
            disabled
            onError={params.onError}
            schema={JSSchema}
            uiSchema={uiSchema}
            showErrorList={true}
            fields={fields}
            formData={editItem}
            ObjectFieldTemplate={props.template}
          >
            <div></div>
          </Form>
        </Box>
      </Grid>
      <Grid item xs>
        <Grid container alignItems="flex-start" direction="column">
          <Button
            style={{marginTop: 25, width: '75%'}}
            component={Link}
            to={`${props.entity.route.base}/${params.id}/edit`}
            color="secondary"
            variant={'contained'}
            startIcon={<Edit>edit</Edit>}
          >
            Edit
          </Button>
        </Grid>
      </Grid>
    </EntityContainer>
  );
};

EntityView.propTypes = {
  entity: PropTypes.object,
  template: PropTypes.function
};

export default EntityView;
