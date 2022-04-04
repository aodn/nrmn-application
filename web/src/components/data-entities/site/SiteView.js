import {
  Alert,
  Box,
  Button,
  Divider,
  Grid,
  Table,
  TableCell,
  TableRow,
  TableBody,
  TableContainer,
  TableHead,
  Typography
} from '@mui/material';
import {Edit} from '@mui/icons-material';
import React, {useEffect, useState} from 'react';
import {useLocation, useParams} from 'react-router';
import {NavLink} from 'react-router-dom';
import {getEntity} from '../../../api/api';
import EntityContainer from '../../containers/EntityContainer';
import CustomTextInput from '../../input/CustomTextInput';

const SiteView = () => {
  const id = useParams()?.id;
  const {state} = useLocation();
  const [data, setData] = useState({});

  useEffect(() => {
    async function fetchSite() {
      await getEntity(`site/${id}`).then((res) => setData(res.data));
    }
    if (id) fetchSite();
  }, [id]);

  return (
    <EntityContainer name="Sites" goBackTo="/reference/sites">
      <Box m={2} display="flex" flexDirection="row" width="100%">
        <Box flexGrow={1}>
          <Typography variant="h4">Site Details</Typography>
        </Box>
        <Box>
          <Button variant="outlined" component={NavLink} to={`/reference/site/${id}/edit`} startIcon={<Edit>edit</Edit>}>
            Edit
          </Button>
        </Box>
      </Box>
      {state?.message && (
        <Box mx={5} flexGrow={1}>
          <Alert severity="info" variant="filled">
            {state.message}
          </Alert>
        </Box>
      )}
      <Box p={2}>
        <Grid container spacing={2}>
          <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="Site Code" formData={data.siteCode} />
          </Grid>
          <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="Site Name" formData={data.siteName} />
          </Grid>
          <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="Location" formData={data.locationName} />
          </Grid>
          <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="Is Active" formData={data.isActive ? 'True' : 'False'} />
          </Grid>
          <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="State" formData={data.state} />
          </Grid>
          <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="Country" formData={data.country} />
          </Grid>
          <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="Marine Protected Area" formData={data.mpa} />
          </Grid>
          <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="Protection Status" formData={data.protectionStatus} />
          </Grid>
          <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="Latitude" formData={data.latitude} />
          </Grid>
          <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="Longitude" formData={data.longitude} />
          </Grid>
          <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="Relief" formData={data.relief} />
          </Grid>
          <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="Slope" formData={data.slope} />
          </Grid>
          <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="Wave Exposures" formData={data.waveExposure} />
          </Grid>
          <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="Currents" formData={data.currents} />
          </Grid>
          <Grid item xs={6}>
            <CustomTextInput readOnlyInput label="Old Site Codes" formData={data.oldSiteCodes?.join(',')} />
          </Grid>
        </Grid>
        <Box pt={2} pb={2}>
          <Divider />
          {data.siteAttribute ? (
            <TableContainer style={{width: '50%'}}>
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell>Other Attributes</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {Object.keys(data.siteAttribute).map((v, i) => (
                    <TableRow key={i}>
                      <TableCell>{v}</TableCell>
                      <TableCell>{data.siteAttribute[v]}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          ) : (
            <Typography variant="subtitle2" component="i">
              No Other Attributes
            </Typography>
          )}
        </Box>
      </Box>
    </EntityContainer>
  );
};

export default SiteView;
