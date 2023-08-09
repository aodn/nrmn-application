import React, {useEffect, useReducer, useState, useCallback} from 'react';
import {useParams, NavLink, Navigate} from 'react-router-dom';
import {Box, Button, CircularProgress, Divider, Grid, Typography} from '@mui/material';
import {Save, Delete} from '@mui/icons-material';
import Alert from '@mui/material/Alert';
import PropTypes from 'prop-types';
import {AuthContext} from '../../../contexts/auth-context';
import {AppConstants} from '../../../common/constants';
import EntityContainer from '../../containers/EntityContainer';
import FamilyTree from '../../../common/relation-graph/FamilyTree';

import CustomAutoCompleteInput, {ERROR_TYPE} from '../../input/CustomAutoCompleteInput';
import CustomTextInput from '../../input/CustomTextInput';
import CustomSearchInput from '../../input/CustomSearchInput';

import {
  getResult,
  entityEdit,
  entityDelete,
  getFamilyForReactFlow
} from '../../../api/api';

const ObservableItemEdit = () => {
  const observableItemId = useParams()?.id;

  const [saved, setSaved] = useState(false);
  const [steps, setSteps] = useState(1);
  const [deleted, setDeleted] = useState(false);
  const [errors, setErrors] = useState([]);
  const [options, setOptions] = useState({});
  const [nodes, setNodes] = useState(null);
  const [savedSpecies, setSavedSpecies] = useState(null);

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
    if (observableItemId) {
      fetchObservableItem();
    }
  }, [observableItemId]);
  // Just skip to the next screen if nothing change on species weight length.
  const handleSkipLengthWeightChange = () => {
    setSteps(3);
  };

  const handleSaveLengthWeightChange = async(items) => {
    await entityEdit('reference/observableItems', items)
      .then((s) => {
        setSavedSpecies(s.data);
        setSteps(3);
      });
  };

  const handleSubmit = () => {
    entityEdit(`reference/observableItem/${observableItemId}`, item).then((res) => {
      if (res.data.observableItemId) {
        setSaved(res.data);
        setSteps(2);
        getFamilyForReactFlow(observableItemId)
          .then(value => {
            setNodes(value.data);
          });
      } else {
        setErrors(res.data.errors);
      }});
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

  const createFamilyTreeGrid = useCallback(() => {
    return(
          <FamilyTree items={nodes}
                      focusNodeId={Number(observableItemId)}
                      onSkipLengthWeightChange={handleSkipLengthWeightChange}
                      onSaveLengthWeightChange={handleSaveLengthWeightChange}
          />);
  },[observableItemId, nodes]);

  const createItemEditGrid = () => {
    return(
      <>
      <Grid container spacing={2}>
        <Grid item xs={16}>
          <Grid container spacing={2}>
            <Grid item xs={6}>
              <CustomTextInput
                label="Species Name"
                formData={item.observableItemName}
                field="observableItemName"
                errors={errors}
                onChange={(t) => dispatch({ field: 'observableItemName', value: t })}
              />
            </Grid>
            <Grid item xs={6}>
              <CustomTextInput
                label="Common Name"
                formData={item.commonName}
                field="commonName"
                errors={errors}
                onChange={(t) => dispatch({ field: 'commonName', value: t })}
              />
            </Grid>
            <Grid item xs={6}>
              <CustomAutoCompleteInput
                label="Species Epithet"
                options={options.taxonomy?.speciesEpithet}
                formData={item.speciesEpithet}
                field="speciesEpithet"
                errors={errors}
                onChange={(t) => dispatch({ field: 'speciesEpithet', value: t })}
              />
            </Grid>
            <Grid item xs={6}>
              {item.supersedingCleared && (
                <span
                  style={{ display: 'inline-block', color: 'red', marginLeft: '150px', position: 'absolute' }}>* Superseding will be removed</span>
              )}
              <CustomSearchInput
                label="Superseded By"
                formData={item.supersededBy}
                exclude={item.observableItemName}
                field="supersededBy"
                errors={errors}
                onChange={(t) => dispatch({ field: 'supersededBy', value: t })}
              />
            </Grid>
            <Grid item xs={6}>
              <CustomTextInput
                label="Letter Code"
                formData={item.letterCode}
                field="letterCode"
                errors={errors}
                onChange={(t) => dispatch({ field: 'letterCode', value: t })}
              />
            </Grid>
            <Grid item xs={6}>
              <CustomAutoCompleteInput
                label="Report Group"
                formData={item.reportGroup}
                options={options.reportGroups}
                field="reportGroup"
                errors={errors}
                onChange={(t) => dispatch({ field: 'reportGroup', value: t })}
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
                onChange={(t) => dispatch({ field: 'habitatGroups', value: t })}
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
                onChange={(t) => dispatch({ field: 'phylum', value: t })}
              />
            </Grid>
            <Grid item xs={6}>
              <CustomAutoCompleteInput
                label="Class"
                formData={item.class}
                options={options.taxonomy?.className}
                field="class"
                errors={errors}
                onChange={(t) => dispatch({ field: 'class', value: t })}
              />
            </Grid>
            <Grid item xs={6}>
              <CustomAutoCompleteInput
                label="Order"
                formData={item.order}
                options={options.taxonomy?.order}
                field="order"
                errors={errors}
                onChange={(t) => dispatch({ field: 'order', value: t })}
              />
            </Grid>
            <Grid item xs={6}>
              <CustomAutoCompleteInput
                label="Family"
                formData={item.family}
                options={options.taxonomy?.family}
                field="family"
                errors={errors}
                onChange={(t) => dispatch({ field: 'family', value: t })}
              />
            </Grid>
            <Grid item xs={6}>
              <CustomAutoCompleteInput
                label="Genus"
                formData={item.genus}
                options={options.taxonomy?.genus}
                field="genus"
                errors={errors}
                onChange={(t) => dispatch({ field: 'genus', value: t })}
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
                onChange={(t) => dispatch({ field: 'lengthWeightA', value: t })}
              />
            </Grid>
            <Grid item xs={6}>
              <CustomTextInput
                type="number"
                label="Length-Weight b"
                formData={item.lengthWeightB}
                field="lengthWeightB"
                errors={errors}
                onChange={(t) => dispatch({ field: 'lengthWeightB', value: t })}
              />
            </Grid>
            <Grid item xs={6}>
              <CustomTextInput
                type="number"
                label="Length-Weight cf"
                formData={item.lengthWeightCf}
                field="lengthWeightCf"
                errors={errors}
                onChange={(t) => dispatch({ field: 'lengthWeightCf', value: t })}
              />
            </Grid>
          </Grid>
        </Grid>
      </Grid>
      <Box display="flex" justifyContent="center" mt={5}>
        <Button variant="outlined" component={NavLink} to="/reference/observableItems">
          Cancel
        </Button>
        <Button
          variant="contained"
          style={{ width: '50%', marginLeft: '5%'}}
          onClick={handleSubmit}
          startIcon={<Save></Save>}
        >
          Save Observable Item
        </Button>
      </Box>
    </>);
  };

  if (steps === 3) {
    const id = saved['observableItemId'];
    return <Navigate to={`/reference/observableItem/${id}`} state={{message: 'Observable Item Updated', species: savedSpecies}} />;
  }

  if (deleted) {
    return <Navigate to={`/reference/observableItem/-1`} state={{message: 'Observable Item Deleted'}} />;
  }

  return (
    <AuthContext.Consumer>
      {({auth}) => {
        if(auth.roles.includes(AppConstants.ROLES.DATA_OFFICER) || auth.roles.includes(AppConstants.ROLES.ADMIN)) {
          return (<EntityContainer name="Observable Items" goBackTo="/reference/observableItems">
            <Grid container alignItems="flex-start" direction="row">
              <Grid item xs={10}>
                <Box fontWeight="fontWeightBold">
                  <Typography variant="h4">{steps === 1 ? 'Edit Observable Item' : 'Edit Length Weight'}</Typography>
                </Box>
              </Grid>
              { steps === 1 &&
                (<Button variant="outlined" style={{ float: 'right' }} onClick={handleDelete}
                        startIcon={<Delete></Delete>}>
                  Delete
                </Button>)
              }
            </Grid>
            <Grid container direction="column" justifyContent="flex-start" alignItems="center">
              {observableItemId && Object.keys(item).length === 0 ? (
                <CircularProgress size={20} />
              ) : (
                <Box pt={2} pb={6} padding={2}>
                  {errors.length > 0 ? (
                    <Box py={2}>
                      <Alert severity="error" variant="filled">
                        {errors[0]?.banner ? errors[0].banner : 'Please review this form for errors and try again.'}
                      </Alert>
                    </Box>
                  ) : null}
                  {steps === 1 ? createItemEditGrid() : createFamilyTreeGrid()}
                </Box>
              )}
            </Grid>
          </EntityContainer>);
        }
        else {
          return(
            <Alert severity="error" variant="outlined">
              <p>Permission Denied</p>
            </Alert>
          );
        }
      }}
    </AuthContext.Consumer>
  );
};

ObservableItemEdit.propTypes = {
  clone: PropTypes.bool
};

export default ObservableItemEdit;
