import React, { useEffect } from 'react';
import { Backdrop, Box, Chip, CircularProgress, Divider, Grid, Paper, Typography } from '@material-ui/core';
import { useDispatch, useSelector } from 'react-redux';
import { jobRequested } from './jobReducer';
import { useParams } from 'react-router';
import AccountBalanceOutlinedIcon from '@material-ui/icons/AccountBalanceOutlined';

const JobView = () => {
    const dispatch = useDispatch();
    const { id } = useParams();
    const job = useSelector(state => state.job.currentJob);
    //const errors = useSelector(state => state.job.errors);
    const isLoading = useSelector(state => state.job.isLoading);

    useEffect(() => {
        dispatch(jobRequested({ id }));
    }, []);

    return (
        <Box>
            {!isLoading && job && (
                <Grid container >
                    <Paper elevation={3} style={{padding: 15}}>
                        <Grid item lg={10} md={10} >
                            <Typography variant="h4" color="primary">{job.reference}</Typography>
                        </Grid>
                        <Grid container spacing={1} justify="flex-start">
                            <Grid item>
                                <Chip
                                    size="small"
                                    avatar={<AccountBalanceOutlinedIcon/>}
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
                        <Divider style={{marginTop: 15, marginBottom: 15}}></Divider>
                        <Grid></Grid>
                    </Paper>
                </Grid>)}
                <Backdrop open={isLoading}  >  <CircularProgress size={200} color="secondary" /></Backdrop>

        </Box>);
};

export default JobView;