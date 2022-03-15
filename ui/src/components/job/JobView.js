import React, {useEffect, useState} from 'react';
import {Box, Button, CircularProgress, Divider, Grid, Paper, Typography} from '@material-ui/core';
import {useParams} from 'react-router';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableContainer from '@material-ui/core/TableContainer';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import {makeStyles} from '@material-ui/core/styles';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import Hidden from '@material-ui/core/Hidden';
import {Link} from 'react-router-dom';

import {getEntity, originalJobFile} from '../../axios/api';
import FileDownload from 'js-file-download';

const useStyles = makeStyles(() => ({
  paper: {
    marginLeft: 15,
    marginTop: 15
  },
  title: {
    overflowWrap: 'break-word'
  }
}));

const JobView = () => {
  const {id} = useParams();
  const [job, setJob] = useState();
  const [existsOnS3, setExistsOnS3] = useState(true);
  const classes = useStyles();

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
    <Box>
      {job ? (
        <Grid container>
          <Grid item sm={12} md={12} lg={4}>
            <Paper className={classes.paper}>
              <Grid item lg={10} md={10} style={{padding: 15}}>
                <Typography className={classes.title} variant="h5" color="primary">
                  {job.reference}
                </Typography>
                {existsOnS3 && <Button onClick={() => downloadZip(job.id, job.reference)}>Download</Button>}
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
                  <Grid item xs={12}>
                    <Typography
                      className={classes.title}
                      variant="h6"
                      color="primary"
                      style={{paddingLeft: 15, fontSize: 15, fontWeight: 400}}
                    >
                      Surveys: ({job.surveyIds.length})
                    </Typography>
                    <Grid container>
                      {job.surveyIds &&
                        job.surveyIds.length > 0 &&
                        job.surveyIds.map((id) => (
                          <Grid key={id} item xs={3} lg={6}>
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
                  </Grid>
                ) : (
                  <Divider />
                )}
              </Grid>
            </Paper>
          </Grid>
          <Grid item sm={12} md={12} lg={7} className={classes.paper}>
            {job.logs && (
              <TableContainer component={Paper} style={{paddingRight: 15, paddingLeft: 15}}>
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
    </Box>
  );
};

export default JobView;
