import { Badge, Box, Chip, Fab, Grid, Typography } from '@material-ui/core';
import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import DataSheetView from './DataSheetView';
import PlaylistAddCheckOutlinedIcon from '@material-ui/icons/PlaylistAddCheckOutlined';
import { makeStyles } from '@material-ui/core/styles';
import { useParams } from 'react-router';
import { JobRequested, JobStarting, validationFilter, ValidationRequested } from './reducers/create-import';
import CloudUploadIcon from '@material-ui/icons/CloudUpload';
import AccountBalanceOutlinedIcon from '@material-ui/icons/AccountBalanceOutlined';

import ValidationDrawer from './ValidationDrawer';

const useStyles = makeStyles((theme) => {
    return {
        root: {
            display: 'flex',
            '& > *': {
                margin: theme.spacing(1),
            },
        },
        hide: {
            display: 'none',
        },
        extendedIcon: {
            marginRight: theme.spacing(1),
        },

    };
});

const ValidationJob = () => {
    const { jobId } = useParams();
    const dispatch = useDispatch();
    const classes = useStyles();
    const job = useSelector(state => state.import.job);

    const isLoading = useSelector(state => state.import.isLoading);
    const editLoading = useSelector(state => state.import.editLoading);

    const handleValidate = () => {
        if (job.id) {
            dispatch(JobStarting());
            dispatch(ValidationRequested(job.id));
        }
    };

    useEffect(() => {
        if (jobId) {
            dispatch(JobRequested(jobId));
        }
    }, []);

    const jobReady = job && Object.keys(job).length > 0;
    return (jobReady) ? (
        <Box style={{ paddingRight: 60 }}>
            <ValidationDrawer></ValidationDrawer>
            <Grid container >
                <Grid item lg={10} md={10} >
                    <Typography variant="h4" color="primary">{job.reference}</Typography>
                </Grid>
                <Grid item lg={2} md={2} >

                    <Grid container justify="space-between" spacing={1} >
                        <Grid item>
                            <Fab variant="extended"
                                disabled={editLoading || isLoading}
                                onClick={() => handleValidate()}
                                size="small" label="Validate" color="secondary">
                                <PlaylistAddCheckOutlinedIcon className={classes.extendedIcon} />
                        Validate
                     </Fab>
                        </Grid>
                        <Grid item>

                            <Fab variant="extended" size="small" label="Submit" disabled={true} color="primary">
                                <CloudUploadIcon className={classes.extendedIcon} />
                         Submit
                     </Fab>
                        </Grid>
                    </Grid>
                </Grid>
            </Grid>
            <Grid container spacing={1} justify="flex-start">
                <Grid item>
                    <Chip
                        size="small"
                        avatar={<AccountBalanceOutlinedIcon></AccountBalanceOutlinedIcon>}
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
                    <Grid item>
                        <Chip
                            size="small"
                            color="secondary"
                            label={'Extended Size'}
                            variant="outlined"
                        ></Chip>
                    </Grid>}
            </Grid>
            <DataSheetView></DataSheetView>
        </Box>
    ) : (<></>);
};

export default ValidationJob;