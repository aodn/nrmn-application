import React, {useEffect, useState} from 'react';
import {Box, Chip, CircularProgress, Divider, Grid, Paper, Typography} from '@material-ui/core';
import {useParams} from 'react-router';
import AccountBalanceOutlinedIcon from '@material-ui/icons/AccountBalanceOutlined';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableContainer from '@material-ui/core/TableContainer';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import {makeStyles} from '@material-ui/core/styles';
import {Link} from 'react-router-dom';

import {getFullJob} from '../../axios/api';

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
  const classes = useStyles();

  useEffect(() => {
    if (!job) {
      getFullJob(id).then((res) => {
        setJob(res);
      });
    }
  }, [job, id]);

  return (
    <Box>
      {job ? (
        <Grid container>
          <Grid item sm={12} md={12} lg={4}>
            <Paper style={{padding: 15}} className={classes.paper}>
              <Grid item lg={10} md={10}>
                <Typography className={classes.title} variant="h5" color="primary">
                  {job.reference}
                </Typography>
              </Grid>
              <Grid container spacing={1} justify="flex-start">
                <Grid item>
                  <Chip
                    size="small"
                    avatar={<AccountBalanceOutlinedIcon />}
                    label={job.program.programName}
                    variant="outlined"
                    mt={1}
                  ></Chip>
                </Grid>
                <Grid item>
                  <Chip size="small" label={job.source} variant="outlined"></Chip>
                </Grid>
                <Grid item>
                  <Chip size="small" label={job.status} variant="outlined"></Chip>
                </Grid>

                {job.isExtendedSize && (
                  <Grid item>
                    <Chip size="small" color="secondary" label="Extended Size" variant="outlined"></Chip>
                  </Grid>
                )}
              </Grid>
              <Divider style={{marginTop: 15, marginBottom: 15}}></Divider>
              <Grid>
                <Grid item>
                  <Typography>
                    Created by: {job.creator.email} at {job.created}{' '}
                  </Typography>
                </Grid>
                {job.surveyIds &&
                  job.surveyIds.length > 0 &&
                  job.surveyIds.map((id) => (
                    <Grid key={id} item>
                      <Link to={`/data/survey/${id}`} variant="a">
                        survey {id}
                      </Link>
                    </Grid>
                  ))}
              </Grid>
            </Paper>
          </Grid>
          <Grid item sm={12} md={12} lg={7} className={classes.paper}>
            {job.logs && (
              <TableContainer component={Paper} style={{paddingRight: 15, paddingLeft: 15}}>
                <Table className={classes.table} aria-label="Event Log Table">
                  <TableHead>
                    <TableRow>
                      <TableCell>Event Time</TableCell>
                      <TableCell>Type</TableCell>
                      <TableCell>Details</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {job.logs.map((log) => (
                      <TableRow key={log.id}>
                        <TableCell component="th" scope="row">{log.eventTime}</TableCell>
                        <TableCell>{log.eventType}</TableCell>
                        <TableCell>{log.details}</TableCell>
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
