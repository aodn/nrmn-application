import { Badge, Box, Chip, Drawer, Fab, Grid, Typography } from '@material-ui/core';
import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import DataSheetView from './DataSheetView';
import PlaylistAddCheckOutlinedIcon from '@material-ui/icons/PlaylistAddCheckOutlined';
import { makeStyles } from '@material-ui/core/styles';
import { useParams } from 'react-router';
import { JobRequested, JobStarting, validationFilter, ValidationRequested } from './reducers/create-import';
import CloudUploadIcon from '@material-ui/icons/CloudUpload';
import AccountBalanceOutlinedIcon from '@material-ui/icons/AccountBalanceOutlined';
import Divider from '@material-ui/core/Divider';
import clsx from 'clsx';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import List from '@material-ui/core/List';
import ReportProblemOutlinedIcon from '@material-ui/icons/ReportProblemOutlined';
import ErrorOutlineOutlinedIcon from '@material-ui/icons/ErrorOutlineOutlined';
const drawerWidth = 500;

const useStyles = makeStyles((theme) => ({

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
    drawer: {
        width: drawerWidth,
        flexShrink: 0,
        whiteSpace: 'nowrap',
    },
    drawerOpen: {
        width: drawerWidth,
        transition: theme.transitions.create('width', {
            easing: theme.transitions.easing.sharp,
            duration: theme.transitions.duration.enteringScreen,
        }),
    },
    drawerClose: {
        transition: theme.transitions.create('width', {
            easing: theme.transitions.easing.sharp,
            duration: theme.transitions.duration.leavingScreen,
        }),
        overflowX: 'hidden',
        width: theme.spacing(8) + 1,
        [theme.breakpoints.up('sm')]: {
            width: theme.spacing(9) + 1,
        },
    },
    toolbar: {
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'flex-end',
        padding: theme.spacing(0, 1),
        // necessary for content to be below app bar
        ...theme.mixins.toolbar,
    },
    content: {
        flexGrow: 1,
        padding: theme.spacing(3),
    },
    errorItem: {
        color: theme.palette.error
    },
}));



const ValidationJob = () => {
    const { jobId } = useParams();
    const dispatch = useDispatch();
    const classes = useStyles();
    const job = useSelector(state => state.import.job);
    const errorsByMsg = useSelector(state => state.import.errorsByMsg);

    const isLoading = useSelector(state => state.import.isLoading);

    const [open, setOpen] = useState(false);

    const handleDrawerOpen = () => {
        setOpen(true);
    };

    const handleFilter = (err) => {

        dispatch(validationFilter(err.ids));
    };

    const handleDrawerClose = (err) => {
        setOpen(false);
    };

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
        <Box style={{ paddingRight: 40 }}>
            {errorsByMsg && (
                <Drawer
                    anchor="right"
                    variant="permanent"
                    className={clsx(classes.drawer, {
                        [classes.drawerOpen]: open,
                        [classes.drawerClose]: !open,
                    })}
                    classes={{
                        paper: clsx({
                            [classes.drawerOpen]: open,
                            [classes.drawerClose]: !open,
                        }),
                    }}
                >
                    <div className={classes.toolbar}>
                    </div>
                    <Divider />
                    <List>
                        {errorsByMsg.map((err, index) => (
                            <ListItem className={classes.errorItem} button key={err.msg}>
                                <ListItemIcon >
                                    <Badge badgeContent={err.count} color="primary">
                                        <ReportProblemOutlinedIcon color="error" onClick={() => setOpen(!open)} />
                                    </Badge>
                                </ListItemIcon>
                                <ListItemText onClick={() => handleFilter(err)} color="secondary" primary={err.msg} />
                            </ListItem>
                        ))}
                    </List>
                </Drawer>)}
            <Grid container >
                <Grid item lg={10} md={10} >
                    <Typography variant="h4" color="primary">{job.reference}</Typography>
                </Grid>
                <Grid item lg={2} md={2} >

                    <Grid container justify="space-between" spacing={1} >
                        <Grid item>
                            <Fab variant="extended"
                                disabled={isLoading}
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