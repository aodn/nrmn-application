import {Button, CircularProgress, Divider, Grid, Typography} from '@mui/material';
import Hidden from '@mui/material/Hidden';
import List from '@mui/material/List';
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
import {Link} from 'react-router-dom';
import {getEntity, originalJobFile} from '../../axios/api';
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
    if (!job) getEntity('stage/stagedJob/' + id).then((res) => setJob(res.data));
  }, [job, id]);

  return (
    <EntityContainer name="Jobs" goBackTo="/jobs">
      {job ? (
        <Grid container>
          <Grid item xs={12}>
            <Grid item lg={10} md={10} style={{padding: 15}}>
              <Typography variant="h5">{job.reference}</Typography>
              {existsOnS3 && (
                <Button variant="outlined" onClick={() => downloadZip(job.id, job.reference)}>
                  Download
                </Button>
              )}
            </Grid>
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

              {job.surveyIds && job.surveyIds.length ? (
                <>
                  <Typography variant="h6" color="primary" style={{paddingLeft: 15, fontSize: 15, fontWeight: 400}}>
                    Surveys: ({job.surveyIds.length})
                  </Typography>
                  <Grid container xs={12}>
                    {job.surveyIds &&
                      job.surveyIds.length > 0 &&
                      job.surveyIds.map((id) => (
                        <Grid key={id} item xs={1}>
                          <List dense style={{paddingTop: 0}}>
                            <ListItem>
                              <Link to={`/data/survey/${id}`} variant="a">
                                {id}
                              </Link>
                            </ListItem>
                          </List>
                        </Grid>
                      ))}
                  </Grid>
                </>
              ) : (
                <Divider />
              )}
            </Grid>
          </Grid>
          <Grid>
            {job.logs && (
              <TableContainer>
                <Table aria-label="Event Log Table">
                  <TableHead>
                    <TableRow>
                      <TableCell>Event Time</TableCell>
                      <TableCell>Event</TableCell>
                      <TableCell>Details</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {job.logs.map((log) => (
                      <TableRow key={log.id}>
                        <TableCell component="th" scope="row">
                          {new Date(log.eventTime).toUTCString()}
                        </TableCell>
                        <TableCell>{log.eventType}</TableCell>
                        <TableCell>
                          {log.details?.split('\n').map((e) => (
                            <div key={e}>
                              {e}
                              <br />
                            </div>
                          ))}
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
