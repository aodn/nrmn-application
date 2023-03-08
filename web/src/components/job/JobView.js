import {Box, Button, Divider, Grid, LinearProgress, Typography} from '@mui/material';
import Hidden from '@mui/material/Hidden';
import List from '@mui/material/List';
import Chip from '@mui/material/Chip';
import ListItem from '@mui/material/ListItem';
import ListItemText from '@mui/material/ListItemText';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import FileDownload from 'js-file-download';
import React, {useEffect, useState} from 'react';
import {useParams} from 'react-router';
import {Navigate} from 'react-router-dom';
import {getEntity, originalJobFile} from '../../api/api';
import PropTypes from 'prop-types';
import SurveyDiff from '../data-entities/survey/SurveyDiff';

const JobView = ({jobId}) => {
  const {id} = useParams();
  const [job, setJob] = useState();
  const [redirect, setRedirect] = useState(false);
  const [existsOnS3, setExistsOnS3] = useState(true);

  const downloadZip = (jobId, fileName) => {
    originalJobFile(jobId).then((result) => {
      if (result.data.size > 0) {
        FileDownload(result.data, fileName);
      } else {
        setExistsOnS3(false);
      }
    });
  };

  useEffect(() => {
    document.title = 'View Job';
    const key = id || jobId;
    async function fetchJob() {
      await getEntity('stage/stagedJob/' + key).then((res) => setJob(res.data));
    }
    if (key) fetchJob();
  }, [id, jobId]);

  if (redirect) {
    const url = `/data/survey/${job.surveyIds.join(',')}/correct`;
    return <Navigate to={url} />;
  }

  return (
    <>
      {job ? (
        <Grid container>
          <Grid item xs={12}>
            <Box m={2}>
              <Box my={2}>
                <Typography variant="h5">{job.reference}</Typography>
              </Box>
              {['STAGED', 'INGESTED'].includes(job.status) && (
                <>
                  <Button disabled={!existsOnS3} variant="outlined" onClick={() => downloadZip(job.id, job.reference)}>
                    {existsOnS3 ? 'View uploaded XLSX' : 'File not found'}
                  </Button>{' '}
                  <Button variant="outlined" component="a" href={`/data/job/${id}/edit`} clickable>
                    {['INGESTED'].includes(job.status) ? 'View ingested ' : 'Edit'} Sheet
                  </Button>{' '}
                  {job.surveyIds?.length > 0 && (
                    <Button variant="outlined" component="a" onClick={() => setRedirect(true)} clickable>
                      Correct Job Surveys
                    </Button>
                  )}
                </>
              )}
            </Box>
            <Divider style={{margin: 15, marginTop: 0}} />
            <Grid container>
              <Grid item xs={4} lg={6} style={{paddingBottom: 0}}>
                <List dense style={{paddingBottom: 0}}>
                  <ListItem>
                    {job.programName && <ListItemText primary="Program" secondary={job.programName} />}
                  </ListItem>
                  <ListItem>
                    <ListItemText primary="Source" secondary={job.source} />
                  </ListItem>
                </List>
              </Grid>
              <Grid item xs={4} lg={6} style={{paddingBottom: 0}}>
                <List dense style={{paddingBottom: 0}}>
                  <ListItem>
                    <ListItemText primary="Status" secondary={job.status} />
                  </ListItem>
                  <ListItem>
                    <ListItemText primary="Creator" secondary={job.creatorEmail} />
                  </ListItem>
                </List>
              </Grid>
              <Grid display={{lg: 'none'}} item xs={4} lg={6} style={{paddingTop: 0}}>
                <List dense style={{paddingTop: 0}}>
                  <ListItem>
                    <ListItemText primary="Date Created" secondary={new Date(job.created).toUTCString()} />
                  </ListItem>
                  <Hidden only="lg">
                    <ListItem display={{lg: 'none'}}>
                    {job.programName && <ListItemText primary="Extended Size" secondary={job.isExtendedSize ? 'Yes' : 'No'} />}
                    </ListItem>
                  </Hidden>
                </List>
              </Grid>
              <Hidden only={['xs', 'sm', 'md']}>
                <Grid item lg={6} style={{paddingTop: 0}}>
                  <List dense style={{paddingTop: 0}}>
                    <ListItem>
                      {job.programName && <ListItemText primary="Extended Size" secondary={job.isExtendedSize ? 'Yes' : 'No'} />}
                    </ListItem>
                  </List>
                </Grid>
              </Hidden>
              {job.surveyIds?.length ? (
                <Grid item xs={12}>
                  <Typography variant="h6" color="primary" style={{paddingLeft: 15, fontSize: 15, fontWeight: 400}}>
                    Surveys: ({job.surveyIds.length})
                  </Typography>
                  <Grid container>
                    {job.surveyIds.map((id) => (
                      <Chip key={id} style={{margin: 5}} label={id} component="a" href={`/data/survey/${id}`} clickable />
                    ))}
                  </Grid>
                </Grid>
              ) : (
                <Divider />
              )}
            </Grid>
          </Grid>
          <Grid width="100%">
            {job.logs && (
              <TableContainer>
                <Table aria-label="Event Log Table">
                  <TableHead>
                    <TableRow>
                      <TableCell width="150px">Event Time</TableCell>
                      <TableCell width="100px">Event</TableCell>
                      <TableCell>Details</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {job.logs.map((log) => (
                      <TableRow key={log.id}>
                        <TableCell component="th" scope="row" style={{verticalAlign: 'top'}}>
                          <Typography variant="caption">
                            {new Date(log.eventTime).toLocaleDateString('en-AU') +
                              ' ' +
                              new Date(log.eventTime).toLocaleTimeString('en-AU', {hour: 'numeric', minute: '2-digit'})}
                          </Typography>
                        </TableCell>
                        <TableCell style={{verticalAlign: 'top'}}>
                          <Typography variant="caption" fontWeight="bold">
                            {log.eventType}
                          </Typography>
                        </TableCell>
                        <TableCell>
                          <Typography variant="caption">
                            {log.details?.split('\n').map((e) => (
                              <div key={e}>
                                {e}
                                <br />
                              </div>
                            ))}
                            {log.summary && <SurveyDiff surveyDiff={log.summary} />}
                          </Typography>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            )}
          </Grid>
        </Grid>
      ) : (
        <LinearProgress />
      )}
    </>
  );
};

export default JobView;

JobView.propTypes = {
  jobId: PropTypes.string
};