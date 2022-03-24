import {Box, Button, CircularProgress, Grid, Typography} from '@mui/material';
import {Save} from '@mui/icons-material';
import Alert from '@mui/material/Alert';
import React, {useEffect, useReducer, useState} from 'react';
import {Navigate, NavLink, useParams} from 'react-router-dom';
import {entityEdit, getResult} from '../../../axios/api';
import EntityContainer from '../../containers/EntityContainer';
import CustomCheckboxInput from '../../input/CustomCheckboxInput';
import CustomDropDownInput from '../../input/CustomDropDownInput';
import CustomTextInput from '../../input/CustomTextInput';

const SurveyEdit = () => {
  const surveyId = useParams()?.id;

  const [saved, setSaved] = useState(false);
  const [errors, setErrors] = useState([]);
  const [divers, setDivers] = useState([]);
  const [siteListItems, setSiteListItems] = useState([]);
  const [programs, setPrograms] = useState([]);

  const formReducer = (state, action) => {
    if (action.form) return {...state, ...action.form};
    switch (action.field) {
      default:
        return {...state, [action.field]: action.value};
    }
  };

  const [item, dispatch] = useReducer(formReducer, {
    surveyId: '',
    visibility: '',
    direction: '',
    latitude: '',
    longitude: '',
    siteCode: '',
    programId: '',
    blockAbundanceSimulated: false,
    surveyDate: '',
    surveyTime: '',
    depth: '',
    surveyNum: '',
    pqDiverInitials: '',
    projectTitle: '',
    protectionStatus: '',
    insideMarinePark: '',
    notes: ''
  });

  useEffect(() => {
    if (surveyId) getResult(`data/survey/${surveyId}`).then((res) => dispatch({form: res.data}));
  }, [surveyId]);

  useEffect(
    () =>
      getResult('data/programs').then((res) =>
        setPrograms(
          res.data?.map((p) => {
            return {id: p.programId, label: p.programName};
          })
        )
      ),
    []
  );

  useEffect(
    () =>
      getResult('siteListItems').then((res) => {
        setSiteListItems(res.data);
      }),
    []
  );

  useEffect(
    () =>
      getResult('divers').then((res) => {
        setDivers(
          res.data?.map((d) => {
            return {id: d.initials, label: d.fullName};
          })
        );
      }),
    []
  );

  const handleSubmit = () => {
    entityEdit(`data/survey/${surveyId}`, item).then((res) => {
      if (res.data.surveyId) {
        setSaved(res.data);
      } else {
        setErrors(res.data.errors);
      }
    });
  };

  if (saved) {
    const id = saved['surveyId'];
    return <Navigate to={`/data/survey/${id}`} state={{message: 'Survey Updated'}} />;
  }

  return (
    <EntityContainer name="Surveys" goBackTo="/data/surveys">
      <Grid container alignItems="flex-start" direction="row">
        <Grid item xs={10}>
          <Box fontWeight="fontWeightBold">
            <Typography variant="h4">Edit Survey</Typography>
          </Box>
        </Grid>
      </Grid>
      <Grid container direction="column" justifyContent="flex-start" alignItems="center">
        {surveyId && Object.keys(item).length === 0 ? (
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
              <Grid item xs={12}>
                <CustomTextInput type="number" readOnlyModify label="Survey ID" formData={item.surveyId} field="surveyId" errors={errors} />
              </Grid>
              <Grid item xs={6}>
                <CustomTextInput
                  label="Visibility"
                  formData={item.visibility}
                  field="visibility"
                  errors={errors}
                  onChange={(t) => dispatch({field: 'visibility', value: t})}
                />
              </Grid>
              <Grid item xs={6}>
                <CustomTextInput
                  label="Direction"
                  formData={item.direction}
                  field="direction"
                  errors={errors}
                  onChange={(t) => dispatch({field: 'direction', value: t})}
                />
              </Grid>
              <Grid item xs={6}>
                <CustomTextInput
                  label="Survey Latitude"
                  formData={item.latitude}
                  field="latitude"
                  errors={errors}
                  onChange={(t) => dispatch({field: 'latitude', value: t})}
                />
              </Grid>
              <Grid item xs={6}>
                <CustomTextInput
                  label="Survey Longitude"
                  formData={item.longitude}
                  field="longitude"
                  errors={errors}
                  onChange={(t) => dispatch({field: 'longitude', value: t})}
                />
              </Grid>
              <Grid item xs={6}>
                <CustomTextInput
                  readOnlyModify
                  label="Site Name"
                  formData={siteListItems?.find((i) => i.siteCode === item.siteCode)?.siteName}
                  field="siteName"
                />
              </Grid>
              <Grid item xs={6}>
                <CustomDropDownInput
                  label="Site Code"
                  formData={item.siteCode}
                  options={siteListItems?.map((l) => {
                    return {id: l.siteCode, label: l.siteCode};
                  })}
                  field="siteCode"
                  errors={errors}
                  onChange={(t) => dispatch({field: 'siteCode', value: t})}
                />
              </Grid>
              <Grid item xs={6}>
                <CustomDropDownInput
                  label="Program"
                  formData={item.programId}
                  options={programs}
                  field="program"
                  errors={errors}
                  onChange={(t) => dispatch({field: 'program', value: t})}
                />
              </Grid>
              <Grid item xs={6}>
                <CustomCheckboxInput
                  label="Block Abundance Simulated"
                  formData={item.blockAbundanceSimulated ?? false}
                  field="blockAbundanceSimulated"
                  onChange={(t) => dispatch({field: 'blockAbundanceSimulated', value: t})}
                />
              </Grid>
              <Grid item xs={6}>
                <CustomTextInput
                  label="Survey Date"
                  formData={item.surveyDate}
                  field="surveyDate"
                  errors={errors}
                  onChange={(t) => dispatch({field: 'surveyDate', value: t})}
                />
              </Grid>
              <Grid item xs={6}>
                <CustomTextInput
                  label="Survey Time"
                  formData={item.surveyTime}
                  field="surveyTime"
                  errors={errors}
                  onChange={(t) => dispatch({field: 'surveyTime', value: t})}
                />
              </Grid>
              <Grid item xs={6}>
                <CustomTextInput
                  label="Depth"
                  type="number"
                  formData={item.depth}
                  field="depth"
                  errors={errors}
                  onChange={(t) => dispatch({field: 'depth', value: t})}
                />
              </Grid>
              <Grid item xs={6}>
                <CustomTextInput
                  label="Survey Number"
                  type="number"
                  formData={item.surveyNum}
                  field="surveyNum"
                  errors={errors}
                  onChange={(t) => dispatch({field: 'surveyNum', value: t})}
                />
              </Grid>
              <Grid item xs={6}>
                <CustomDropDownInput
                  label="PQ Diver"
                  optional={true}
                  formData={item.pqDiverInitials}
                  options={divers}
                  field="pqDiverInitials"
                  errors={errors}
                  onChange={(t) => dispatch({field: 'pqDiverInitials', value: t})}
                />
              </Grid>
              <Grid item xs={12}>
                <CustomTextInput
                  label="Project Title"
                  formData={item.projectTitle}
                  field="projectTitle"
                  errors={errors}
                  onChange={(t) => dispatch({field: 'projectTitle', value: t})}
                />
              </Grid>
              <Grid item xs={6}>
                <CustomTextInput
                  label="Survey Protection Status"
                  formData={item.protectionStatus}
                  field="protectionStatus"
                  errors={errors}
                  onChange={(t) => dispatch({field: 'protectionStatus', value: t})}
                />
              </Grid>
              <Grid item xs={6}>
                <CustomDropDownInput
                  optional
                  options={[
                    {id: 'Yes', label: 'Yes'},
                    {id: 'No', label: 'No'},
                    {id: 'Unsure', label: 'Unsure'}
                  ]}
                  label="Inside Marine Park"
                  formData={item.insideMarinePark}
                  field="insideMarinePark"
                  errors={errors}
                  onChange={(t) => dispatch({field: 'insideMarinePark', value: t})}
                />
              </Grid>
              <Grid item xs={12}>
                <CustomTextInput
                  label="Notes"
                  formData={item.notes}
                  field="notes"
                  errors={errors}
                  onChange={(t) => dispatch({field: 'notes', value: t})}
                />
              </Grid>
            </Grid>
            <Box display="flex" justifyContent="center" mt={5}>
              <Button component={NavLink} to="/data/surveys">
                Cancel
              </Button>
              <Button variant="contained" onClick={handleSubmit} startIcon={<Save></Save>}>
                Save Survey
              </Button>
            </Box>
          </Box>
        )}
      </Grid>
    </EntityContainer>
  );
};

export default SurveyEdit;
