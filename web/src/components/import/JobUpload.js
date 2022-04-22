import React, {useEffect, useState} from 'react';
import {Box, Button, Checkbox, FormControlLabel, Grid, MenuItem, Select, Typography} from '@mui/material';
import LinearProgressWithLabel from '../ui/LinearProgressWithLabel';
import Alert from '@mui/material/Alert';
import {submitJobFile} from '../../api/api';
import {NavLink} from 'react-router-dom';
import axiosInstance from '../../api';

const JobUpload = () => {
  const emptyForm = {file: null, withExtendedSizes: false, programId: null};
  const [formData, setFormData] = useState(emptyForm);
  const [programs, setPrograms] = useState();
  const [uploadProgress, setUploadProgress] = useState(-1);
  const [uploadResponse, setUploadResponse] = useState();

  useEffect(() => {
    async function fetchPrograms() {
      await axiosInstance.get('/api/v1/data/programs').then((p) => {
        setPrograms(p.data);
      });
    }
    fetchPrograms();
  }, []);

  const resetForm = () => {
    setFormData(emptyForm);
    setUploadProgress(-1);
    setUploadResponse(null);
  };

  return (
    <>
      <Box m={1}>
        {uploadProgress < 0 ? (
          <NavLink to="/data/jobs" color="secondary">
            <Typography>{'<< Back to Jobs'}</Typography>
          </NavLink>
        ) : (
          <NavLink onClick={resetForm} to="/data/upload" color="secondary">
            <Typography>{'<< Back to Upload'}</Typography>
          </NavLink>
        )}
      </Box>
      <Grid container justifyContent="center">
        <Box style={{background: 'white', width: 800}} boxShadow={1} margin={3} padding={3}>
          <Grid container alignItems="flex-start" direction="row">
            <Grid item xs={12}>
              <Box fontWeight="fontWeightBold">
                <Typography variant="h4">Add Job</Typography>
              </Box>
            </Grid>
            {uploadProgress < 0 ? (
              <>
                <Grid item xs={12}>
                  <Box padding={3}>
                    <input
                      type="file"
                      onChange={(p) => {
                        setFormData({...formData, file: p.target.files[0]});
                      }}
                    />
                  </Box>
                </Grid>
                <Grid item xs={6}>
                  <Box px={3}>
                    <Typography variant="subtitle2">Program Name</Typography>
                    <Select
                      fullWidth
                      labelId="program-name-label"
                      size="small"
                      disabled={!programs}
                      displayEmpty={false}
                      onChange={(e) => setFormData({...formData, programId: e.target.value})}
                    >
                      {programs?.map((p) => (
                        <MenuItem key={p.programId} value={p.programId}>
                          {p.programName}
                        </MenuItem>
                      ))}
                    </Select>
                  </Box>
                </Grid>
                <Grid item xs={6}>
                  <Box py={2}>
                    <FormControlLabel
                      control={<Checkbox onChange={(e) => setFormData({...formData, withExtendedSizes: e.target.checked})} />}
                      label="With Extended Sizes"
                    />
                  </Box>
                </Grid>
                <Grid item xs={12}>
                  <Box p={3}>
                    <Button
                      variant="contained"
                      disabled={!formData.file || !formData.programId}
                      style={{width: '100%'}}
                      onClick={() =>
                        submitJobFile(formData, setUploadProgress).then(({response}) =>
                          setUploadResponse(
                            !response.data.error && !response.data.id
                              ? {error: `Server returned with status ${response.status}`}
                              : response.data
                          )
                        )
                      }
                    >
                      Upload
                    </Button>
                  </Box>
                </Grid>
              </>
            ) : (
              <>
                <Grid item xs={12}>
                  <Box p={3}>
                    <LinearProgressWithLabel
                      determinate={true}
                      value={uploadProgress}
                      done={false}
                      label={`Uploading ${formData.file.name}...`}
                    />
                    {uploadProgress === 100 && (
                      <LinearProgressWithLabel determinate={uploadResponse} done={uploadResponse} label="Verifying..." />
                    )}
                  </Box>
                  <Box mt={3} ml={3} mr={3} mb={5}>
                    {uploadResponse?.error && (
                      <>
                        <Alert my={3} severity="error">
                          Failed to add job:
                          <br />
                          <span>{uploadResponse.error}</span>
                        </Alert>
                        <Box pt={5}>
                          <NavLink onClick={resetForm} to="/data/upload" color="secondary">
                            <Typography>{'<< Back to Upload'}</Typography>
                          </NavLink>
                        </Box>
                      </>
                    )}
                    {uploadResponse?.id && (
                      <>
                        <Alert severity="info" variant="filled">
                          Staged {formData.file.name}
                          <br />
                          {uploadResponse.message}
                        </Alert>
                        <Box pt={5} px={15}>
                          <Button
                            variant="contained"
                            style={{width: '100%'}}
                            component={NavLink}
                            to={`/data/job/${uploadResponse.id}/edit`}
                          >
                            View {formData.file.name}
                          </Button>
                        </Box>
                        <Box py={3} px={15}>
                          <Button variant="outlined" style={{width: '100%'}} component={NavLink} to="/data/jobs">
                            View All Jobs
                          </Button>
                        </Box>
                      </>
                    )}
                  </Box>
                </Grid>
              </>
            )}
          </Grid>
        </Box>
      </Grid>
    </>
  );
};

export default JobUpload;
