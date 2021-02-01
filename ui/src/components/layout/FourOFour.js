import React from 'react';
import { Box, Grid, makeStyles, Typography } from '@material-ui/core';
import Link from '@material-ui/core/Link';
import { NavLink } from 'react-router-dom';

const FourOFour = () => {


    const useStyles = makeStyles(() => ({
        link: {
            cursor: 'pointer'
        }
    }));
    const classes = useStyles();

    return (
        <Grid container>
            <Grid item md={12} lg={12} sm={12}>
                <Box>
                    <Typography align="center" style={{ fontSize: 125 }} variant='h1'>404</Typography>
                    <Typography align="center" style={{ fontSize: 125 }} variant='h3'>Page not found.</Typography>
                    <Typography align="center" style={{ fontSize: 20 }} variant='h5'>
                        <Link  component={NavLink} className={classes.link} to="/jobs" color='secondary'> Jobs </Link> or
                        <Link component={NavLink} className={classes.link} color='secondary' to="/home"> Home </Link>
                    </Typography>

                </Box>
            </Grid>
        </Grid>
    );
};


export default FourOFour;