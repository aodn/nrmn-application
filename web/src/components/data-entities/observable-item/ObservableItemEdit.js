import React, {useEffect, useReducer, useState} from 'react';
import {useParams, NavLink, Navigate} from 'react-router-dom';
import {Box, Button, CircularProgress, Divider, Grid, Typography} from '@mui/material';
import {Save, Delete} from '@mui/icons-material';
import Alert from '@mui/material/Alert';
import PropTypes from 'prop-types';

import EntityContainer from '../../containers/EntityContainer';

import CustomAutoCompleteInput, {ERROR_TYPE} from '../../input/CustomAutoCompleteInput';
import CustomTextInput from '../../input/CustomTextInput';
import CustomSearchInput from '../../input/CustomSearchInput';

import {getResult, entityEdit, entityDelete} from '../../../api/api';

const ObservableItemEdit = () => {
  const observableItemId = useParams()?.id;

  const [saved, setSaved] = useState(false);
  const [deleted, setDeleted] = useState(false);
  const [errors, setErrors] = useState([]);
  const [options, setOptions] = useState({});

  const formReducer = (state, action) => {
    if (action.form) return {...state, ...action.form};
    switch (action.field) {
      case 'supersededBy': {
        const supersedingCleared = action.value === '' && state.hasSupersededBy;
        return {...state, [action.field]: action.value, supersedingCleared};
      }
      default:
        return {...state, [action.field]: action.value};
    }
  };

  const [item, dispatch] = useReducer(formReducer, {
    observableItemName: '',
    commonName: '',
    speciesEpithet: '',
    supersededBy: '',
    letterCode: '',
    reportGroup: '',
    habitatGroups: '',
    phylum: '',
    class: '',
    order: '',
    family: '',
    genus: '',
    lengthWeightA: '',
    lengthWeightB: '',
    lengthWeightCf: ''
  });

  useEffect(() => {
    document.title = 'Edit Observable Item';
    async function fetchTaxonomyDetail() {
      await getResult('species/taxonomyDetail').then((options) => setOptions(options.data));
    }
    fetchTaxonomyDetail();
  }, []);

  useEffect(() => {
    async function fetchObservableItem() {
      await getResult(`reference/observableItem/${observableItemId}`).then((res) => dispatch({form: {...res.data, hasSupersededBy: res.data.supersededBy}}));
    }
    if (observableItemId) fetchObservableItem();
  }, [observableItemId]);

  const handleSubmit = () => {
    entityEdit(`reference/observableItem/${observableItemId}`, item).then((res) => {
      if (res.data.observableItemId) {
        setSaved(res.data);
      } else {
        setErrors(res.data.errors);
      }
    });
  };

  const handleDelete = () => {
    entityDelete(`reference/observableItem`, observableItemId).then((res) => {
      if (res.data.error) {
        setErrors([{banner: 'Unable to delete. Observable Item has linked observations.'}]);
      } else {
        setDeleted(true);
      }
    });
  };

  if (saved) {
    const id = saved['observableItemId'];
    return <Navigate to={`/reference/observableItem/${id}`} state={{message: 'Observable Item Updated'}} />;
  }

  if (deleted) {
    return <Navigate to={`/reference/observableItem/-1`} state={{message: 'Observable Item Deleted'}} />;
  }

  return (
    <EntityContainer name="Observable Items" goBackTo="/reference/observableItems">
      <Grid container alignItems="flex-start" direction="row">
        <Grid item xs={10}>
          <Box fontWeight="fontWeightBold">
            <Typography variant="h4">Edit Observable Item</Typography>
          </Box>
        </Grid>
        <Button variant="outlined" style={{float: 'right'}} onClick={handleDelete} startIcon={<Delete></Delete>}>
          Delete
        </Button>
      </Grid>
      <Grid container direction="column" justifyContent="flex-start" alignItems="center">
        {observableItemId && Object.keys(item).length === 0 ? (
          <CircularProgress size={20} />
        ) : (
          <Box pt={2} pb={6} padding={2} width="90%">
            {errors.length > 0 ? (
              <Box py={2}>
                <Alert severity="error" variant="filled">
                  {errors[0]?.banner ? errors[0].banner : 'Please review this form for errors and try again.'}
                </Alert>
              </Box>
            ) : null}
            <Grid container spacing={2}>
              <Grid item xs={6}>
                <CustomTextInput
                  label="Species Name"
                  formData={item.observableItemName}
                  field="observableItemName"
                  errors={errors}
                  onChange={(t) => dispatch({field: 'observableItemName', value: t})}
                />
              </Grid>
              <Grid item xs={6}>
                <CustomTextInput
                  label="Common Name"
                  formData={item.commonName}
                  field="commonName"
                  errors={errors}
                  onChange={(t) => dispatch({field: 'commonName', value: t})}
                />
              </Grid>
              <Grid item xs={6}>
                <CustomAutoCompleteInput
                  label="Species Epithet"
                  options={options.taxonomy?.speciesEpithet}
                  formData={item.speciesEpithet}
                  field="speciesEpithet"
                  errors={errors}
                  onChange={(t) => dispatch({field: 'speciesEpithet', value: t})}
                />
              </Grid>
              <Grid item xs={6}>
                {item.supersedingCleared && (
                  <span style={{display: 'inline-block', color: 'red', marginLeft: '150px', position: 'absolute'}}>
                    * Superseding will be removed
                  </span>
                )}
                <CustomSearchInput
                  label="Superseded By"
                  formData={item.supersededBy}
                  field="supersededBy"
                  errors={errors}
                  onChange={(t) => dispatch({field: 'supersededBy', value: t})}
                />
              </Grid>
              <Grid item xs={6}>
                <CustomTextInput
                  label="Letter Code"
                  formData={item.letterCode}
                  field="letterCode"
                  errors={errors}
                  onChange={(t) => dispatch({field: 'letterCode', value: t})}
                />
              </Grid>
              <Grid item xs={6}>
                <CustomAutoCompleteInput
                  label="Report Group"
                  formData={item.reportGroup}
                  options={options.reportGroups}
                  field="reportGroup"
                  errors={errors}
                  onChange={(t) => dispatch({field: 'reportGroup', value: t})}
                  warnLevelOnNewValue={ERROR_TYPE.WARNING}
                />
              </Grid>
              <Grid item xs={6}>
                <CustomAutoCompleteInput
                  label="Habitat Groups"
                  formData={item.habitatGroups}
                  options={options.habitatGroups}
                  field="habitatGroups"
                  errors={errors}
                  onChange={(t) => dispatch({field: 'habitatGroups', value: t})}
                  warnLevelOnNewValue={ERROR_TYPE.WARNING}
                />
              </Grid>
              <Grid item xs={12}>
                <Divider />
              </Grid>
              <Grid item xs={6}>
                <CustomAutoCompleteInput
                  label="Phylum"
                  formData={item.phylum}
                  options={options.taxonomy?.phylum}
                  field="phylum"
                  errors={errors}
                  onChange={(t) => dispatch({field: 'phylum', value: t})}
                />
              </Grid>
              <Grid item xs={6}>
                <CustomAutoCompleteInput
                  label="Class"
                  formData={item.class}
                  options={options.taxonomy?.className}
                  field="class"
                  errors={errors}
                  onChange={(t) => dispatch({field: 'class', value: t})}
                />
              </Grid>
              <Grid item xs={6}>
                <CustomAutoCompleteInput
                  label="Order"
                  formData={item.order}
                  options={options.taxonomy?.order}
                  field="order"
                  errors={errors}
                  onChange={(t) => dispatch({field: 'order', value: t})}
                />
              </Grid>
              <Grid item xs={6}>
                <CustomAutoCompleteInput
                  label="Family"
                  formData={item.family}
                  options={options.taxonomy?.family}
                  field="family"
                  errors={errors}
                  onChange={(t) => dispatch({field: 'family', value: t})}
                />
              </Grid>
              <Grid item xs={6}>
                <CustomAutoCompleteInput
                  label="Genus"
                  formData={item.genus}
                  options={options.taxonomy?.genus}
                  field="genus"
                  errors={errors}
                  onChange={(t) => dispatch({field: 'genus', value: t})}
                />
              </Grid>
              <Grid item xs={12}>
                <Divider />
              </Grid>
              <Grid item xs={6}>
                <CustomTextInput
                  type="number"
                  label="Length-Weight a"
                  formData={item.lengthWeightA}
                  field="lengthWeightA"
                  errors={errors}
                  onChange={(t) => dispatch({field: 'lengthWeightA', value: t})}
                />
              </Grid>
              <Grid item xs={6}>
                <CustomTextInput
                  type="number"
                  label="Length-Weight b"
                  formData={item.lengthWeightB}
                  field="lengthWeightB"
                  errors={errors}
                  onChange={(t) => dispatch({field: 'lengthWeightB', value: t})}
                />
              </Grid>
              <Grid item xs={6}>
                <CustomTextInput
                  type="number"
                  label="Length-Weight cf"
                  formData={item.lengthWeightCf}
                  field="lengthWeightCf"
                  errors={errors}
                  onChange={(t) => dispatch({field: 'lengthWeightCf', value: t})}
                />
              </Grid>
            </Grid>
            <Box display="flex" justifyContent="center" mt={5}>
              <Button variant="outlined" component={NavLink} to="/reference/observableItems">
                Cancel
              </Button>
              <Button
                variant="contained"
                style={{width: '50%', marginLeft: '5%', marginRight: '20%'}}
                onClick={handleSubmit}
                startIcon={<Save></Save>}
              >
                Save Observable Item
              </Button>
            </Box>
          </Box>
        )}
      </Grid>
    </EntityContainer>
  );
};

ObservableItemEdit.propTypes = {
  clone: PropTypes.bool
};

export default ObservableItemEdit;
