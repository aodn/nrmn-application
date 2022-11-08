import {Box, Button, Grid, LinearProgress, Typography} from '@mui/material';
import {Edit} from '@mui/icons-material';
import React, {useEffect, useState} from 'react';
import {useParams} from 'react-router';
import {NavLink} from 'react-router-dom';
import {getEntity} from '../../../api/api';
import EntityContainer from '../../containers/EntityContainer';
import CustomTextInput from '../../input/CustomTextInput';

const SurveyView = () => {
  const id = useParams()?.id;
  const [data, setData] = useState({});

  useEffect(() => {
    async function getSurvey() {
      await getEntity(`data/survey/${id}`).then((res) => setData(res.data));
    }
    if (id) getSurvey();
  }, [id]);

  return (
    <EntityContainer name="Surveys" goBackTo="/data/surveys">
      <Box m={2} display="flex" flexDirection="row" width="100%">
        <Box flexGrow={1}>
          <Typography variant="h4">Survey Details</Typography>
        </Box>
        <Box>
          <Button variant="contained" disabled={!data.surveyId} component={NavLink} to={`/data/survey/${id}/edit`} startIcon={<Edit>edit</Edit>}>
            Edit
          </Button>
        </Box>
      </Box>
      {data.surveyId ? <Box p={2}>
        <Grid container spacing={2}>
        <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="Created" formData={data.created} asDate />
          </Grid>
          <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="Updated" formData={data.updated} asDate />
          </Grid>
          <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="Survey ID" formData={data.surveyId} />
          </Grid>
          <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="Site" formData={`${data.siteCode} ${data.siteName}`} />
          </Grid>
          <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="Program" formData={data.program} />
          </Grid>
          <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="Survey Date" formData={data.surveyDate} />
          </Grid>
          <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="Survey Time" formData={data.surveyTime} />
          </Grid>
          <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="Project Title" formData={data.projectTitle} />
          </Grid>
          <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="Depth" formData={data.depth} />
          </Grid>
          <Grid item xs={6}></Grid>
          <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="Visibility" formData={data.visibility} />
          </Grid>
          <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="Direction" formData={data.direction} />
          </Grid>
          <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="Block" formData={data.block} />
          </Grid>
          <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="Method(s)" formData={data.method} />
          </Grid>
          <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="Survey Latitude" formData={data.latitude} />
          </Grid>
          <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="Survey Longitude" formData={data.longitude} />
          </Grid>
          <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="Site Latitude" formData={data.siteLatitude} />
          </Grid>
          <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="Site Longitude" formData={data.siteLongitude} />
          </Grid>
          <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="Location Name" formData={data.locationName} />
          </Grid>
          <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="Area" formData={data.area} />
          </Grid>
          <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="Country" formData={data.country} />
          </Grid>
          <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="Survey Protection Status" formData={data.protectionStatus} />
          </Grid>
          <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="Inside Marine Park" formData={data.insideMarinePark} />
          </Grid>
          <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="PQ Catalogued" formData={data.pqCatalogued} />
          </Grid>
          <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="PQ diver" formData={data.pqDiver} />
          </Grid>
          <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="PQ Zip Url" formData={data.pqZipUrl} />
          </Grid>
          <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="Block Abundance Simulated" formData={data.blockAbundanceSimulated} />
          </Grid>
          <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="Survey(s) Not Done" formData={data.surveyNotDone} />
          </Grid>
          <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="Divers" formData={data.divers} />
          </Grid>
          <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="Notes" formData={data.notes} />
          </Grid>
        </Grid>
      </Box> :    <Box sx={{ width: '100%' }}>
      <LinearProgress />
    </Box>}
    </EntityContainer>
  );
};

export default SurveyView;
