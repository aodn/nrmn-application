import {Box, Button, CircularProgress, Divider, Grid, Typography} from '@mui/material';
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
import {getEntity, originalJobFile} from '../../api/api';
import EntityContainer from '../containers/EntityContainer';

const JobView = () => {
  const {id} = useParams();
  const [job, setJob] = useState();
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
    async function fetchJob() {
      await getEntity('stage/stagedJob/' + id).then((res) => setJob(res.data));
    }
    if (id) fetchJob();
  }, [id]);

  return (
    <EntityContainer name="Jobs" goBackTo="/data/jobs">
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
                    {existsOnS3 ? 'Download' : 'File not found'}
                  </Button>{' '}
                  <Button variant="outlined" component="a" href={`/data/job/${id}/edit`} clickable>
                    {['INGESTED'].includes(job.status) ? 'View' : 'Edit'} Sheet
                  </Button>
                </>
              )}
            </Box>
            <Divider style={{margin: 15, marginTop: 0}} />
            <Grid container>
              <Grid item xs={4} lg={6} style={{paddingBottom: 0}}>
                <List dense style={{paddingBottom: 0}}>
                  <ListItem>
                    <ListItemText primary="Program" secondary={job.programName} />
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
                      <ListItemText primary="Extended Size" secondary={job.isExtendedSize ? 'Yes' : 'No'} />
                    </ListItem>
                  </Hidden>
                </List>
              </Grid>
              <Hidden only={['xs', 'sm', 'md']}>
                <Grid item lg={6} style={{paddingTop: 0}}>
                  <List dense style={{paddingTop: 0}}>
                    <ListItem>
                      <ListItemText primary="Extended Size" secondary={job.isExtendedSize ? 'Yes' : 'No'} />
                    </ListItem>
                  </List>
                </Grid>
              </Hidden>
              {job.surveyIds?.length ? (
                <Grid item xs={12}>
                  <Typography variant="h6" color="primary" style={{paddingLeft: 15, fontSize: 15, fontWeight: 400}}>
                    Surveys: ({job.surveyIds.length})
                  </Typography>
                  <Grid container xs={12}>
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
        <CircularProgress size={200} color="secondary" />
      )}
    </EntityContainer>
  );
};

export default JobView;
