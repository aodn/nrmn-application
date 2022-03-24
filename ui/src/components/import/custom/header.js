import React from 'react';
import PropTypes from 'prop-types';
import {makeStyles} from '@mui/styles';

const useStyles = makeStyles((theme) => ({
  fishSize: {
    color: '#c4d79b',
    borderBottom: '1px solid ' + theme.palette.divider
  },
  invertSize: {
    color: '#da9694'
  }
}));

const AgGridHeader = (props) => {
  const classes = useStyles();

  const onSortRequested = (order, event) => {
    props.setSort(order, event.shiftKey);
  };

  return (
    <div style={{width: '100%'}}>
      <div style={{float: 'left'}} onClick={(event) => onSortRequested(props.column.isSortAscending() ? 'desc' : 'asc', event)}>
        <div className={classes.fishSize}>{props.displayName}</div>
        <div className={classes.invertSize}>{props.column.colDef.invertSize}</div>
      </div>
    </div>
  );
};

AgGridHeader.propTypes = {
  column: PropTypes.any,
  displayName: PropTypes.string,
  showColumnMenu: PropTypes.func,
  enableSorting: PropTypes.bool,
  menuIcon: PropTypes.string,
  enableMenu: PropTypes.bool,
  setSort: PropTypes.func
};

export default AgGridHeader;
