import React, {useEffect, useReducer, useState} from 'react';
import {Navigate, NavLink} from 'react-router-dom';
import {Box, Button, Grid, Typography} from '@mui/material';
import {Save} from '@mui/icons-material';
import Alert from '@mui/material/Alert';
import PropTypes from 'prop-types';
import SpeciesSearch from '../search/SpeciesSearch';

import EntityContainer from '../containers/EntityContainer';
import CustomDropDownInput from '../input/CustomDropDownInput';
import CustomAutoCompleteInput from '../input/CustomAutoCompleteInput';
import CustomTextInput from '../input/CustomTextInput';
import CustomSearchInput from '../input/CustomSearchInput';

import {getResult, entitySave} from '../../axios/api';

const ObservableItemAdd = () => {
  const [savedId, setSavedId] = useState(false);
  const [errors, setErrors] = useState([]);
  const [options, setOptions] = useState({});

  const formReducer = (state, action) => {
    if (action.form) return {...state, ...action.form};
    switch (action.field) {
      default:
        return {...state, [action.field]: action.value};
    }
  };

  const [item, dispatch] = useReducer(formReducer, {
    aphiaId: '',
    observableItemName: '',
    commonName: '',
    obsItemTypeId: 1,
    speciesEpithet: '',
    supersededBy: null,
    letterCode: '',
    reportGroup: '',
    habitatGroups: '',
    phylum: '',
    class: '',
    order: '',
    family: '',
    genus: '',
    lengthWeightA: null,
    lengthWeightB: null,
    lengthWeightCf: null
  });

  useEffect(
    () =>
      getResult('species/taxonomyDetail').then((options) => {
        options.data.obsItemTypes = options.data.obsItemTypes.map((i) => {
          return {id: i.obsItemTypeId, label: i.obsItemTypeName};
        });
        setOptions(options.data);
      }, []),
    []
  );

  const handleSubmit = () => {
    entitySave(`reference/observableItem`, item).then((res) => {
      if (res.data.observableItemId) {
        setSavedId(res.data.observableItemId);
      } else {
        setErrors(res.data.errors);
      }
    });
  };

  if (savedId) return <Navigate to={`/reference/observableItem/${savedId}`} state={{message: 'Observable Item Saved'}} />;

  return (
    <EntityContainer
      name="Observable Items"
      goBackTo="/reference/observableItems"
      header={<SpeciesSearch onRowClick={(i) => dispatch({form: {...i}})} />}
    >
      <Grid container alignItems="flex-start" direction="row">
        <Grid item xs={10}>
          <Typography variant="h5">New Observable Item</Typography>
        </Grid>
      </Grid>
      <Grid container direction="column" justifyContent="flex-start" alignItems="center">
        <Box pt={2} pb={6} padding={2} width="90%">
          {errors.length > 0 ? (
            <Box py={2}>
              <Alert severity="error" variant="filled">
                Please review this form for errors and try again.
              </Alert>
            </Box>
          ) : null}
          <Grid container spacing={2}>
            <Grid item xs={6}>
              <CustomTextInput
                label="Aphia ID"
                formData={item.aphiaId}
                field="aphiaId"
                errors={errors}
                onChange={(t) => dispatch({field: 'aphiaId', value: isNaN(parseInt(t)) ? '' : parseInt(t)})}
              />
            </Grid>
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
              <CustomDropDownInput
                label="Observable Item Type"
                options={options.obsItemTypes}
                formData={item.obsItemTypeId}
                field="obsItemTypeId"
                errors={errors}
                onChange={(t) => dispatch({field: 'obsItemTypeId', value: t})}
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
                field="className"
                errors={errors}
                onChange={(t) => dispatch({field: 'genus', value: t})}
              />
            </Grid>
            <Grid item xs={6}>
              <CustomAutoCompleteInput
                label="Species Epithet"
                formData={item.speciesEpithet}
                options={options.taxonomy?.speciesEpithet}
                field="speciesEpithet"
                errors={errors}
                onChange={(t) => dispatch({field: 'speciesEpithet', value: t})}
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
              />
            </Grid>
            <Grid item xs={6}>
              <CustomSearchInput
                clearOnBlur
                label="Superseded By"
                formData={item.supersededBy}
                options={options.observableItemName}
                exclude={item.observableItemName}
                field="supersededBy"
                errors={errors}
                onChange={(t) => dispatch({field: 'supersededBy', value: t})}
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
              Save observable Item
            </Button>
          </Box>
        </Box>
      </Grid>
    </EntityContainer>
  );
};

ObservableItemAdd.propTypes = {
  clone: PropTypes.bool
};

export default ObservableItemAdd;
