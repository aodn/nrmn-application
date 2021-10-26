import React, {useEffect, useState} from 'react';
import {useParams, NavLink, Redirect} from 'react-router-dom';
import config from 'react-global-configuration';
import Form from '@rjsf/material-ui';
import {Box, Button, CircularProgress, Grid, Typography} from '@material-ui/core';
import {Save} from '@material-ui/icons';
import Alert from '@material-ui/lab/Alert';
import PropTypes from 'prop-types';

import LoadingBanner from '../layout/loadingBanner';
import EntityContainer from '../containers/EntityContainer';

import CustomTextInput from '../input/CustomTextInput';
import CustomArrayInput from '../input/CustomArrayInput';
import CustomNumberInput from '../input/CustomNumberInput';
import CustomDropDownInput from '../input/CustomDropDownInput';
import CustomAutoCompleteInput from '../input/CustomAutoCompleteInput';

import SiteAddTemplate from '../templates/SiteAddTemplate';
import SiteEditTemplate from '../templates/SiteEditTemplate';
import {getResult, entityEdit, entitySave} from '../../axios/api';

const SiteEdit = ({clone}) => {
  const params = useParams();

  const schemaDefinition = config.get('api') || {};

  const [formData, setFormData] = useState({});
  const [saved, setSaved] = useState(false);
  const [errors, setErrors] = useState([]);

  const edit = !clone && typeof params.id !== 'undefined';

  useEffect(() => {
    if (params.id !== undefined) getResult(`sites/${params.id}`).then((res) => setFormData(res.data));
  }, [params.id, saved]);

  const handleSubmit = (e) => {
    if (edit) {
      entityEdit(`sites/${params.id}`, e.formData).then((res) => {
        setFormData(e.formData);
        if (res.data.siteId) {
          setSaved(res.data);
        } else {
          setErrors(res.data.errors);
        }
      });
    } else {
      delete e.formData.siteAttribute;
      entitySave(`sites`, e.formData).then((res) => {
        setFormData(e.formData);
        if (res.data.siteId) {
          setSaved(res.data);
        } else {
          setErrors(res.data.errors);
        }
      });
    }
  };

  const title = (edit === true ? 'Edit ' : clone === true ? 'Clone ' : 'New ') + 'Site';
  const entityDef = {...schemaDefinition['SiteGetDto']};
  const entitySchema = {title: title, ...entityDef};
  const JSSchema = {components: {schemas: schemaDefinition}, ...entitySchema};

  const uiSchema = {};
  for (const key in entitySchema.properties) {
    const item = entitySchema.properties[key];
    if (key === 'mpa') {
      uiSchema[key] = {'ui:field': 'autostring', route: 'marineProtectedAreas'};
    } else if (key === 'protectionStatus') {
      uiSchema[key] = {'ui:field': 'autostring', route: 'protectionStatuses'};
    } else if (key === 'locationId') {
      uiSchema[key] = {
        'ui:field': 'dropdown',
        route: 'locations?projection=selection',
        entity: 'location',
        entityList: 'locations',
        idKey: 'locationId',
        valueKey: 'locationName'
      };
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
    } else if (key === 'oldSiteCodes') {
      uiSchema[key] = {'ui:field': 'array'};
    } else {
      uiSchema[key] = {'ui:field': 'string', 'ui:readonly': item.readOnly ?? false};
    }
  }

  const fields = {
    string: CustomTextInput,
    array: CustomArrayInput,
    double: CustomNumberInput,
    dropdown: CustomDropDownInput,
    autostring: CustomAutoCompleteInput
  };

  if (saved) {
    const id = saved['siteId'];
    return <Redirect to={`/reference/site/${id}/${edit ? 'saved' : 'new'}`} />;
  }

  return params.id && Object.keys(formData).length === 0 ? (
    <Grid container direction="row" justify="flex-start" alignItems="center">
      <LoadingBanner variant="h5" msg="Loading Site.." />
    </Grid>
  ) : (
    <EntityContainer name="Sites" goBackTo="/reference/sites">
      <Grid container alignItems="flex-start" direction="row">
        <Grid item xs={10}>
          <Box fontWeight="fontWeightBold">
            <Typography variant="h4">{title}</Typography>
          </Box>
        </Grid>
      </Grid>
      <Grid container direction="column" justify="flex-start" alignItems="center">
        {params.loading ? (
          <CircularProgress size={20} />
        ) : (
          <Box pt={2} pb={6} padding={2} width="90%">
            {errors.length > 0 ? (
              <Box py={2}>
                <Alert severity="error" variant="filled">
                  Please review this form for errors and try again.
                </Alert>
              </Box>
            ) : null}
            <Form
              onError={params.onError}
              schema={JSSchema}
              uiSchema={uiSchema}
              onSubmit={handleSubmit}
              showErrorList={false}
              fields={fields}
              noValidate
              formData={formData}
              formContext={errors}
              ObjectFieldTemplate={edit ? SiteEditTemplate : SiteAddTemplate}
            >
              <Box display="flex" justifyContent="center" mt={5}>
                <Button disabled={params.loading} component={NavLink} to="/reference/sites">
                  Cancel
                </Button>
                <Button
                  style={{width: '50%', marginLeft: '5%', marginRight: '20%'}}
                  type="submit"
                  startIcon={<Save></Save>}
                  disabled={params.loading}
                >
                  Save Site
                </Button>
              </Box>
            </Form>
          </Box>
        )}
      </Grid>
    </EntityContainer>
  );
};

SiteEdit.propTypes = {
  clone: PropTypes.bool
};

export default SiteEdit;
