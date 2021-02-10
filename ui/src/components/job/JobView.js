import React, { useEffect } from 'react';
import { Backdrop, Box, Chip, CircularProgress, Divider, Grid, Paper, Typography } from '@material-ui/core';
import { useDispatch, useSelector } from 'react-redux';
import { jobRequested } from './jobReducer';
import { useParams } from 'react-router';
import AccountBalanceOutlinedIcon from '@material-ui/icons/AccountBalanceOutlined';
import Timeline from '@material-ui/lab/Timeline';
import TimelineItem from '@material-ui/lab/TimelineItem';
import TimelineSeparator from '@material-ui/lab/TimelineSeparator';
import TimelineConnector from '@material-ui/lab/TimelineConnector';
import TimelineContent from '@material-ui/lab/TimelineContent';
import TimelineOppositeContent from '@material-ui/lab/TimelineOppositeContent';
import TimelineDot from '@material-ui/lab/TimelineDot';
import { makeStyles } from '@material-ui/core/styles';
import PublishIcon from '@material-ui/icons/Publish';
import SaveIcon from '@material-ui/icons/Save';
import CheckCircleOutlineIcon from '@material-ui/icons/CheckCircleOutline';
import EditIcon from '@material-ui/icons/Edit';
import BackupIcon from '@material-ui/icons/Backup';
import HighlightOffIcon from '@material-ui/icons/HighlightOff';
import ErrorOutlineIcon from '@material-ui/icons/ErrorOutline';
const useStyles = makeStyles((theme) => ({
    paper: {
        padding: '6px 16px',
    },
    secondaryTail: {
        backgroundColor: theme.palette.secondary.main,
    },
}));

const event2icon = {
    'UPLOADED': function display() { return (<PublishIcon />); },
    'VALIDATING': function display() { return (<CheckCircleOutlineIcon />); },
    'STAGING': function display() { return (<CheckCircleOutlineIcon />); },
    'STAGED': function display() { return (<SaveIcon />); },
    'EDITING': function display() { return (<EditIcon />); },
    'INGESTING': function display() { return (<BackupIcon />); },
    'CORRECTING': function display() { return (<CheckCircleOutlineIcon />); },
    'INGESTED': function display() { return (<CheckCircleOutlineIcon />); },
    'CORRECTED': function display() { return (<CheckCircleOutlineIcon />); },
    'DELETED': function display() { return (<HighlightOffIcon />); },
    'ABANDONED': function display() { return (<CheckCircleOutlineIcon />); },
    'ERROR': function display() { return (<ErrorOutlineIcon />); }
};

const JobView = () => {
    const dispatch = useDispatch();
    const { id } = useParams();
    const job = useSelector(state => state.job.currentJob);
    const isLoading = useSelector(state => state.job.isLoading);
    const classes = useStyles();
    useEffect(() => {
        dispatch(jobRequested({ id }));
    }, []);

    return (
        <Box>
            {!isLoading && job && (
                <Grid container >
                    <Grid item sm={12} md={12} lg={4}>
                        <Paper elevation={3} style={{ padding: 15 }}>
                            <Grid item lg={10} md={10} >
                                <Typography variant="h5" color="primary">{job.reference}</Typography>
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
                                    <Chip
                                        size="small"
                                        color="secondary"
                                        label={job.source}
                                        variant="outlined"
                                    ></Chip>
                                </Grid>

                                {job.isExtendedSize &&
                                    (<Grid item>
                                        <Chip
                                            size="small"
                                            color="secondary"
                                            label={'Extended Size'}
                                            variant="outlined"
                                        ></Chip>
                                    </Grid>)}
                            </Grid>
                            <Divider style={{ marginTop: 15, marginBottom: 15 }}></Divider>
                            <Grid>
                                <Grid item>
                                    <Typography>created by:  {job.creator.email}  at  {job.created} </Typography>
                                </Grid>
                            </Grid>
                        </Paper>
                    </Grid>
                    <Grid item sm={12} md={12} lg={8}>
                        {job.logs && (<Timeline align="alternate">
                            { job.logs.map((log) => (
                                <TimelineItem key={log.id}>
                                    <TimelineOppositeContent>
                                        <Typography variant="body2" color="textSecondary">
                                            {log.eventTime}
                                        </Typography>
                                    </TimelineOppositeContent>
                                    <TimelineSeparator>
                                        <TimelineDot>
                                            {event2icon[log.eventType]()}
                                        </TimelineDot>
                                        <TimelineConnector />
                                    </TimelineSeparator>
                                    <TimelineContent>
                                        <Paper elevation={3} className={classes.paper}>
                                            <Typography variant="h6" component="h1">
                                                {log.eventType}
                                            </Typography>
                                            <Typography>
                                                {log.details}
                                            </Typography>
                                        </Paper>
                                    </TimelineContent>
                                </TimelineItem>))}
                        </Timeline>)}
                    </Grid>
                </Grid>)}
            <Backdrop open={isLoading}  >  <CircularProgress size={200} color="secondary" /></Backdrop>

        </Box>);
};

export default JobView;