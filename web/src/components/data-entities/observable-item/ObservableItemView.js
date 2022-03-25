import React, {useEffect, useState} from 'react';
import {Box, Divider, Grid, Typography, Paper, Button} from '@mui/material';
import {getEntity} from '../../../api/api';
import {useParams} from 'react-router';
import {Edit} from '@mui/icons-material';
import {NavLink} from 'react-router-dom';

const ObservableItemView = () => {
  const id = useParams()?.id;
  const [data, setData] = useState({});

  useEffect(() => getEntity(`reference/observableItem/${id}`).then((res) => setData(res.data)), [id]);

  return (
    <Grid container alignItems="center" justifyContent="center" style={{minHeight: '70vh'}}>
      <Paper>
        <Box m={2} display="flex" flexDirection="row">
          <Box p={1} flexGrow={1}>
            <Typography variant="h4">Observable Item Details</Typography>
          </Box>
          <Box>
            <Button style={{width: '100%'}} component={NavLink} to={`/reference/observableItem/${id}/edit`} startIcon={<Edit>edit</Edit>}>
              Edit
            </Button>
          </Box>
        </Box>
        <Box p={2}>
          <Grid container spacing={2}>
            <Grid item xs={6}>
              <b>ID:</b> {data.observableItemId}
            </Grid>
            <Grid item xs={6}>
              <b>Species Name:</b> {data.observableItemName}
            </Grid>
            <Grid item xs={6}>
              <b>Observable Item Type:</b> {data.obsItemTypeName}
            </Grid>
            <Grid item xs={6}>
              <b>Common Name</b> {data.commonName}
            </Grid>
            <Grid item xs={6}>
              <b>Aphia ID:</b> {data.aphiaId ?? '---'}
            </Grid>
            <Grid item xs={6}>
              <b>Aphia Relation:</b> {data.aphiaRelTypeName ?? '---'}
            </Grid>
            <Grid item xs={6}>
              <b>Superseded By:</b> {data.supersededBy ?? '---'}
            </Grid>
            <Grid item xs={6}>
              <b>Superseded Names:</b> {data.supersededNames ?? '---'}
            </Grid>
            <Grid item xs={6}>
              <b>Superseded IDs:</b> {data.supersededIds ?? '---'}
            </Grid>
            <Grid item xs={6}>
              <b>Letter Code:</b> {data.letterCode ?? '---'}
            </Grid>
          </Grid>
        </Box>
        <Divider />
        <Box p={2}>
          <Grid container spacing={2}>
            <Grid item xs={6}>
              <b>Phylum:</b> {data.phylum}
            </Grid>
            <Grid item xs={6}>
              <b>Class:</b> {data.class}
            </Grid>
            <Grid item xs={6}>
              <b>Order:</b> {data.order}
            </Grid>
            <Grid item xs={6}>
              <b>Family:</b> {data.family}
            </Grid>
            <Grid item xs={6}>
              <b>Genus:</b> {data.genus}
            </Grid>
            <Grid item xs={6}>
              <b>Report Group:</b> {data.reportGroup}
            </Grid>
            <Grid item xs={6}>
              <b>Habitat Group:</b> {data.habitatGroups}
            </Grid>
            <Grid item xs={6}>
              <b>Species Epithet:</b> {data.speciesEpithet}
            </Grid>
          </Grid>
        </Box>
        <Divider />
        <Box p={2}>
          <Grid container spacing={2}>
            <Grid item xs={6}>
              <b>Length-Weight a:</b> {data.lengthWeightA}
            </Grid>
            <Grid item xs={6}>
              <b>Length-Weight b:</b> {data.lengthWeightB}
            </Grid>
            <Grid item xs={6}>
              <b>Length-Weight cf:</b> {data.lengthWeightCf}
            </Grid>
          </Grid>
        </Box>
        <Box p={2}>
          {data.obsItemAttribute ? (
            data.obsItemAttribute
          ) : (
            <Typography variant="subtitle2" component="i">
              No Other Attributes
            </Typography>
          )}
        </Box>
      </Paper>
    </Grid>
  );
};

export default ObservableItemView;
