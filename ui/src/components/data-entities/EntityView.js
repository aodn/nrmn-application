import React, {useEffect} from 'react';
import {Link, useParams} from 'react-router-dom';
import {useDispatch, useSelector} from 'react-redux';
import config from 'react-global-configuration';
import {PropTypes} from 'prop-types';
import {Edit} from '@material-ui/icons';
import {Box, Button, Divider, Grid, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography} from '@material-ui/core';
import Alert from '@material-ui/lab/Alert';
import Form from '@rjsf/material-ui';

import TextInput from './customWidgetFields/TextInput';
import {itemRequested} from './middleware/entities';
import {resetState} from './form-reducer';
import EntityContainer from './EntityContainer';

const EntityView = (props) => {
  const params = useParams();
  const dispatch = useDispatch();
  const formData = useSelector((state) => state.form.data);
  const schemaDefinition = config.get('api') || {};

  useEffect(() => {
    dispatch(resetState());
    dispatch(itemRequested(`${props.entity.endpoint}/${params.id}`));
  }, []);

  const entityDef = schemaDefinition[props.entity.schemaKey.view];
  const fullTitle = `${props.entity.name} Details`;
  const entitySchema = {title: fullTitle, ...entityDef};
  const JSSchema = {components: {schemas: schemaDefinition}, ...entitySchema};
  const uiSchema = {'ui:widget': 'string'};

  for (const key in entitySchema.properties) {
    const item = entitySchema.properties[key];
    uiSchema[key] = item.type === 'object' ? {'ui:field': 'objects'} : {'ui:field': 'readonly'};
  }

  const inputDisplay = (elem) => {
    const value = elem.formData?.toString();
    return (
      <span>
        <b>{elem.schema.title}: </b> {value ? value : ' -- '}
      </span>
    );
  };

  const objectTable = (elem) => {
    const keys = elem.formData ? Object.keys(elem.formData) : [];
    return keys.length > 0 ? (
      <>
        <Divider />
        <TableContainer>
          <Table size="small">
            <TableHead>
              <TableRow>
                <TableCell>{elem.schema.title}</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {keys.map((key) => (
                <TableRow key={key}>
                  <TableCell>{key}</TableCell>
                  <TableCell>{elem.formData[key]}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </>
    ) : (
      <Typography variant="subtitle2" component="i">
        No {elem.schema.title}
      </Typography>
    );
  };

  const arrayTable = (elem) => {
    const keys = elem.formData ? Object.keys(elem.formData) : [];
    return keys.length > 0 ? (
      <>
        <Typography variant="subtitle2">{elem.schema.title}</Typography>{' '}
        {keys.map((i) => `${elem.formData[i]}${i < keys.length - 1 ? ', ' : ''}`)}
      </>
    ) : (
      <Typography variant="subtitle2" component="i">
        No {elem.schema.title}
      </Typography>
    );
  };

  const fields = {
    objects: objectTable,
    ArrayField: arrayTable,
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

  const loaded = Object.keys(formData).length > 0;
  return (
    <EntityContainer name={props.entity.name} goBackTo={props.entity.list.route}>
      <Grid item xs={9}>
        <Box pt={4} px={6} pb={6}>
          {alert}
          {loaded && (
            <Form
              disabled
              onError={params.onError}
              schema={JSSchema}
              uiSchema={uiSchema}
              showErrorList={true}
              fields={fields}
              formData={formData}
              ObjectFieldTemplate={props.template}
            >
              <div></div>
            </Form>
          )}
        </Box>
      </Grid>
      <Grid item xs>
        <Grid container alignItems="flex-start" direction="column">
          {loaded && (
            <Button
              style={{marginTop: 35, width: '75%'}}
              component={Link}
              to={`${props.entity.route.base}/${params.id}/edit`}
              color="secondary"
              variant={'contained'}
              startIcon={<Edit>edit</Edit>}
            >
              Edit
            </Button>
          )}
        </Grid>
      </Grid>
    </EntityContainer>
  );
};

EntityView.propTypes = {
  entity: PropTypes.object,
  template: PropTypes.func
};

export default EntityView;
