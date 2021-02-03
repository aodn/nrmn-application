import React from 'react';

import {NavLink} from 'react-router-dom';
import Grid from '@material-ui/core/Grid';
import Button from '@material-ui/core/Button';
import {makeStyles} from '@material-ui/core/styles';
import {PropTypes} from 'prop-types';


const useStyles = makeStyles(() => ({
  buttons: {
    '& > *': {
      marginTop: 10
    }
  }
}));


const LinkButton = (props) => {

  const classes = useStyles();
  return <>
    <Grid item key={props.title}>
      <div className={classes.buttons}>
        <Button
            {...props}
            to={props.to}
            component={NavLink}
            color='secondary'
            aria-label={props.title}
            variant={'contained'}
          >{props.title}
          </Button>
      </div>
    </Grid>
    </>;
};

LinkButton.propTypes = {
  title: PropTypes.string,
  to: PropTypes.string
};

export default LinkButton;