import { Box, Chip, Drawer, Fab, Grid, Typography } from '@material-ui/core';
import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import DataSheetView from './DataSheetView';
import PlaylistAddCheckOutlinedIcon from '@material-ui/icons/PlaylistAddCheckOutlined';
import { makeStyles, useTheme } from '@material-ui/core/styles';
import { useParams } from 'react-router';
import { JobRequested } from './reducers/create-import';
import CloudUploadIcon from '@material-ui/icons/CloudUpload';
import AccountBalanceOutlinedIcon from '@material-ui/icons/AccountBalanceOutlined';
import Divider from '@material-ui/core/Divider';
import IconButton from '@material-ui/core/IconButton';
import MenuIcon from '@material-ui/icons/Menu';
import ChevronLeftIcon from '@material-ui/icons/ChevronLeft';
import ChevronRightIcon from '@material-ui/icons/ChevronRight';
import clsx from 'clsx';
import InboxIcon from '@material-ui/icons/MoveToInbox';
import MailIcon from '@material-ui/icons/Mail';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import List from '@material-ui/core/List';
import ReportProblemOutlinedIcon from '@material-ui/icons/ReportProblemOutlined';
import ErrorOutlineOutlinedIcon from '@material-ui/icons/ErrorOutlineOutlined';
const drawerWidth = 240;

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
        width: theme.spacing(5) + 1,
        [theme.breakpoints.up('sm')]: {
            width: theme.spacing(6) + 1,
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
    const [open, setOpen] = useState(false);

    const handleDrawerOpen = () => {
        setOpen(true);
    };

    const handleDrawerClose = () => {
        setOpen(false);
    };

    useEffect(() => {
        if (jobId) {
            dispatch(JobRequested(jobId));
        }
    }, []);
    const jobReady = job && Object.keys(job).length > 0;
    console.log(job);
    return (jobReady) ? (
        <Box style={{ paddingRight: 40 }}>
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
                    {['buddy not found', 'Site Not found', 'Species missing', 'Drafts'].map((text, index) => (
                        <ListItem className={classes.errorItem} button key={text}>
                            <ListItemIcon >{index % 2 === 0 ? <ReportProblemOutlinedIcon color="warning" onClick={() => setOpen(!open)} /> : <ErrorOutlineOutlinedIcon color="danger" />}</ListItemIcon>
                            <ListItemText color="warning" primary={text} />
                        </ListItem>
                    ))}
                </List>

            </Drawer>
            <Grid container >
                <Grid item lg={10} md={10} >
                    <Typography variant="h4" color="primary">{job.reference}</Typography>
                </Grid>
                <Grid item lg={2} md={2} >

                    <Grid container justify="space-between" spacing={1} >
                        <Grid item>
                            <Fab variant="extended"
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