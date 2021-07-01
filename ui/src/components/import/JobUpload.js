import React, {useState} from 'react';
import {Box, Button, Checkbox, FormControlLabel, Grid, MenuItem, Select, Typography} from '@material-ui/core';
import LinearProgressWithLabel from '../ui/LinearProgressWithLabel';
import Alert from '@material-ui/lab/Alert';
import {DropzoneArea} from 'material-ui-dropzone';
import {submitJobFile} from '../../axios/api';
import {NavLink} from 'react-router-dom';

const JobUpload = () => {
  const emptyForm = {file: '', withExtendedSizes: false, programId: 1};
  const [formData, setFormData] = useState(emptyForm);
  const [uploadProgress, setUploadProgress] = useState(-1);
  const [uploadResponse, setUploadResponse] = useState(null);

  const resetForm = () => {
    setFormData(emptyForm);
    setUploadProgress(-1);
    setUploadResponse(null);
  };

  return (
    <>
      <Box m={1}>
        {uploadProgress < 0 ? (
          <NavLink to="/jobs" color="secondary">
            <Typography>{'<< Back to Jobs'}</Typography>
          </NavLink>
        ) : (
          <NavLink onClick={resetForm} to="/upload" color="secondary">
            <Typography>{'<< Back to Upload'}</Typography>
          </NavLink>
        )}
      </Box>
      <Grid container justify="center">
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
                    <DropzoneArea
                      showFileNames
                      showAlerts={false}
                      dropzoneText={!formData.file ? 'Drop an XLSX file here or click to select' : ''}
                      style={{height: '20px'}}
                      acceptedFiles={['application/vnd.openxmlformats-officedocument.spreadsheetml.sheet']}
                      filesLimit={1}
                      maxFileSize={104857600}
                      previewGridProps={{container: {spacing: 10, direction: 'column', alignContent: 'center'}}}
                      onChange={(p) => {
                        setFormData({...formData, file: p?.[0]});
                      }}
                    />
                  </Box>
                </Grid>
                <Grid item xs={6}>
                  <Box pl={3} py={1}>
                    <Select
                      variant="outlined"
                      style={{height: '40px'}}
                      defaultValue={1}
                      onChange={(e) => setFormData({...formData, programId: e.target.value})}
                    >
                      <MenuItem value={1}>RLS Program File</MenuItem>
                      <MenuItem value={2}>ATRC Program File</MenuItem>
                    </Select>
                  </Box>
                </Grid>
                <Grid item xs={6}>
                  <Box py={1}>
                    <FormControlLabel
                      control={<Checkbox onChange={(e) => setFormData({...formData, withExtendedSizes: e.target.checked})} />}
                      label="With Extended Sizes"
                    />
                  </Box>
                </Grid>
                <Grid item xs={12}>
                  <Box p={3}>
                    <Button
                      disabled={!formData.file}
                      style={{width: '100%'}}
                      onClick={() =>
                        submitJobFile(formData, setUploadProgress).then(({response}) => {
                          if (!response.data.errors)
                            setUploadResponse({errors: [{message: `Server returned with status ${response.status}`}]});
                          else setUploadResponse(response.data);
                        })
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
                    {uploadResponse?.errors.length > 0 && (
                      <>
                        <Alert my={3} severity="error">
                          Failed to add job:
                          <br />
                          {uploadResponse.errors.map((e) => (
                            <>
                              <span>{e.message}</span>
                              <br />
                            </>
                          ))}
                        </Alert>
                        <Box pt={5}>
                          <NavLink onClick={resetForm} to="/upload" color="secondary">
                            <Typography>{'<< Back to Upload'}</Typography>
                          </NavLink>
                        </Box>
                      </>
                    )}
                    {uploadResponse?.file && (
                      <>
                        <Alert severity="info" variant="filled">
                          {formData.file.name} added.
                        </Alert>
                        <Box pt={5} px={15}>
                          <Button style={{width: '100%'}} component={NavLink} to={`/validation/${uploadResponse.file.jobId}`}>
                            View {formData.file.name}
                          </Button>
                        </Box>
                        <Box py={3} px={15}>
                          <Button style={{width: '100%'}} component={NavLink} to="/jobs">
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
