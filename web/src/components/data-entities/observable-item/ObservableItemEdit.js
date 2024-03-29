import React, {useEffect, useReducer, useState, useCallback} from 'react';
import {useParams, NavLink, Navigate} from 'react-router-dom';
import {Box, Button, CircularProgress, Divider, Grid, Typography} from '@mui/material';
import {Save, Edit, Delete} from '@mui/icons-material';
import Alert from '@mui/material/Alert';
import PropTypes from 'prop-types';
import { StatusCodes } from 'http-status-codes';

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

const STEP_EDIT_SCREEN = 1;
const STEP_EDIT_SUMMARY_SCREEN = 2;
const STEP_EDIT_WEIGHT_LENGTH_SCREEN = 3;
const STEP_EDIT_WEIGHT_LENGTH_SUMMARY_SCREEN = 4;

const WEIGHT_LENGTH_A = 'lengthWeightA';
const WEIGHT_LENGTH_B = 'lengthWeightB';
const WEIGHT_LENGTH_CF = 'lengthWeightCf';

const ObservableItemEdit = () => {
  const observableItemId = useParams()?.id;

  const [saved, setSaved] = useState(false);
  const [steps, setSteps] = useState(1);
  const [deleted, setDeleted] = useState(false);
  const [errors, setErrors] = useState([]);
  const [options, setOptions] = useState({});
  const [nodes, setNodes] = useState(null);
  const [savedSpecies, setSavedSpecies] = useState(null);
  const [saveSpeciesError, setSaveSpeciesError] = useState(null);

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

  const dispatchAndCheckLengthWeight = useCallback((field, value) => {
    dispatch({ field: field , value: value });

    // We setup error message if user enter some length weight values but not all
    // three length weight fields
    setErrors(prevState => {
      // Remove any previous error
      const v = [...prevState]
        .filter((f) => f.property !== WEIGHT_LENGTH_A && f.property !== WEIGHT_LENGTH_B && f.property !== WEIGHT_LENGTH_CF);

      // Copy value from item and then union the value from event, at this moment item isn't updated fully
      let temp = {
        lengthWeightA: item.lengthWeightA,
        lengthWeightB: item.lengthWeightB,
        lengthWeightCf: item.lengthWeightCf
      };

      temp[field] = value;

      // Skip if all null or all have values for the length weight
      if(!((!temp.lengthWeightA && !temp.lengthWeightB && !temp.lengthWeightCf) || (temp.lengthWeightA && temp.lengthWeightB && temp.lengthWeightCf))) {
        // Do nothing
        if(!temp.lengthWeightA) {
          v.push({property: WEIGHT_LENGTH_A, message: 'Missing value'});
        }
        if(!temp.lengthWeightB) {
          v.push({property: WEIGHT_LENGTH_B, message: 'Missing value'});
        }
        if(!temp.lengthWeightCf) {
          v.push({property: WEIGHT_LENGTH_CF, message: 'Missing value'});
        }
      }

      return v;
    });
  }, [item]);

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

  const handleSaveLengthWeightChange = async(items) => {
    await entityEdit('reference/observableItems', items)
      .then((s) => {
        if(s.status === StatusCodes.OK) {
          setSavedSpecies(s.data);
        }
        else {
          setSaveSpeciesError('Server error, fail to update species length weight');
        }
      })
      .finally(() => setSteps(STEP_EDIT_WEIGHT_LENGTH_SUMMARY_SCREEN));
  };

  const handleSubmit = () => {
    entityEdit(`reference/observableItem/${observableItemId}`, item).then((res) => {
      if (res.status === StatusCodes.OK) {
        setSaved(res.data);
        setSteps(STEP_EDIT_SUMMARY_SCREEN);
        setErrors([]);
      } else {
        // Must use res.data
        setErrors(res.data);
      }});
  };

  const handleEditWeigthLength = () => {
    getFamilyForReactFlow(observableItemId)
      .then(value => {
        setNodes(value.data);
        setSteps(STEP_EDIT_WEIGHT_LENGTH_SCREEN);
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

  const createFamilyTreeGrid = useCallback(() => {
    return(
          <FamilyTree items={nodes}
                      focusNodeId={Number(observableItemId)}
                      onExitEdit={() => setSteps(STEP_EDIT_SCREEN)}
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
                dataTestId="observable-item-name-text"
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
                onChange={(t) => dispatchAndCheckLengthWeight('lengthWeightA', t )}
              />
            </Grid>
            <Grid item xs={6}>
              <CustomTextInput
                type="number"
                label="Length-Weight b"
                formData={item.lengthWeightB}
                field="lengthWeightB"
                errors={errors}
                onChange={(t) => dispatchAndCheckLengthWeight('lengthWeightB', t)}
              />
            </Grid>
            <Grid item xs={6}>
              <CustomTextInput
                type="number"
                label="Length-Weight cf"
                formData={item.lengthWeightCf}
                field="lengthWeightCf"
                errors={errors}
                onChange={(t) => dispatchAndCheckLengthWeight('lengthWeightCf', t )}
              />
            </Grid>
          </Grid>
        </Grid>
      </Grid>
      <Box display="flex" justifyContent="space-between" m={5}>
        <Button variant="outlined" component={NavLink} to="/reference/observableItems">
          Cancel
        </Button>
        <Button
          variant="outlined"
          onClick={handleEditWeigthLength}
          startIcon={<Edit></Edit>}
        >
          Edit Superseded(by) Length-Weight
        </Button>
        <Button
          variant="contained"
          data-testid="observable-item-save-btn"
          onClick={handleSubmit}
          startIcon={<Save></Save>}
        >
          Save Observable Item
        </Button>
      </Box>
    </>);
  };

  if (steps === STEP_EDIT_SUMMARY_SCREEN) {
    const id = saved['observableItemId'];
    return <Navigate to={`/reference/observableItem/${id}`}
                     state={{
                       message: 'Observable Item Updated',
                       error: saveSpeciesError
                     }} />;
  }

  if (steps === STEP_EDIT_WEIGHT_LENGTH_SUMMARY_SCREEN) {
    return <Navigate to={`/reference/observableItem/${observableItemId}`}
                     state={{
                       species: savedSpecies,
                       error: saveSpeciesError
                     }} />;
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
                  <Typography variant="h4">{steps === STEP_EDIT_SCREEN ? 'Edit Observable Item' : 'Edit Superseded(by) Weight Length'}</Typography>
                </Box>
              </Grid>
              { steps === STEP_EDIT_SCREEN &&
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
                      <Alert severity="error" variant="filled" data-testid="alert-field-error">
                        {errors[0]?.banner ? errors[0].banner : 'Please review this form for errors and try again.'}
                      </Alert>
                    </Box>
                  ) : null}
                  {steps === STEP_EDIT_SCREEN ? createItemEditGrid() : createFamilyTreeGrid()}
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
