import React from 'react';
import PropTypes from 'prop-types';
import {makeStyles} from '@material-ui/core';

const useStyles = makeStyles((theme) => ({
  fishSize: {
    color: '#c4d79b',
    borderBottom: '1px solid ' + theme.palette.divider
  },
  invertSize: {
    color: '#da9694'
  }
}));

const GridHeader = (props) => {
  const classes = useStyles();

  const onSortRequested = (order, event) => {
    props.setSort(order, event.shiftKey);
  };

  return (
    <div style={{width: '100%'}}>
      <div style={{float: 'left'}} onClick={(event) => onSortRequested(props.column.isSortAscending() ? 'desc' : 'asc', event)}>
        <div className={classes.fishSize}>{props.fishSize}</div>
        <div className={classes.invertSize}>{props.invertSize}</div>
      </div>
    </div>
  );
};

GridHeader.propTypes = {
  column: PropTypes.any,
  fishSize: PropTypes.string,
  invertSize: PropTypes.string,
  showColumnMenu: PropTypes.func,
  enableSorting: PropTypes.bool,
  menuIcon: PropTypes.string,
  enableMenu: PropTypes.bool,
  setSort: PropTypes.func
};

export default GridHeader;
