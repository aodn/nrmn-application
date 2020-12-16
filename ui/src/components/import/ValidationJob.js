import { Box, Chip, Fab, Grid, Typography } from '@material-ui/core';
import React, { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import DataSheetView from './DataSheetView';
import PlaylistAddCheckOutlinedIcon from '@material-ui/icons/PlaylistAddCheckOutlined';
import { makeStyles } from '@material-ui/core/styles';
import { useParams } from 'react-router';
import { JobRequested } from './reducers/create-import';
import CloudUploadIcon from '@material-ui/icons/CloudUpload';
import AccountBalanceOutlinedIcon from '@material-ui/icons/AccountBalanceOutlined';
import PostAddOutlinedIcon from '@material-ui/icons/PostAddOutlined';
const useStyles = makeStyles((theme) => ({

    root: {
        '& > *': {
            margin: theme.spacing(1),
        },
    },
    extendedIcon: {
        marginRight: theme.spacing(1),
    },
}));

const ValidationJob = () => {
    const { jobId } = useParams();
    const dispatch = useDispatch();
    const classes = useStyles();
    const job = useSelector(state => state.import.job);

    useEffect(() => {
        if (jobId) {
            dispatch(JobRequested(jobId));
        }
    }, []);
    const jobReady = job && Object.keys(job).length > 0;
    console.log(job.program);
    return (jobReady) ? (
        <Grid container justify="space-between"  >
            <Grid item>
                <Typography variant="h4" color="primary">{job.reference}</Typography>
            </Grid>
            <Grid item mt={1} pt={2}>
                <Fab variant="extended"
                    size="small" label="Validate" color="secondary">
                    <PlaylistAddCheckOutlinedIcon className={classes.extendedIcon} />
                    Validate
                </Fab>
                <Fab variant="extended" size="small" label="Submit" disabled={true} color="primary">
                    <CloudUploadIcon className={classes.extendedIcon} />
                    Submit
                </Fab>
            </Grid>

            <Grid item sm={12} lg={12} md={12} mt={1} pt={2}>
                <Chip
                    avatar={<AccountBalanceOutlinedIcon></AccountBalanceOutlinedIcon>}
                    label={job.program.programName}
                    variant="outlined"
                    mt={1}
                ></Chip>
                <Chip
                    avatar={<AccountBalanceOutlinedIcon></AccountBalanceOutlinedIcon>}
                    label={job.program.programName}
                    variant="outlined"
                ></Chip>
            </Grid>

            <Grid item sm={12} lg={12} md={12}>
                <DataSheetView></DataSheetView>
            </Grid>
        </Grid>
    ) : (<></>);
};

export default ValidationJob;