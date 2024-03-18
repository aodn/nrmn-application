import React from 'react';
import { styled } from '@mui/material/styles';
import PropTypes from 'prop-types';
const PREFIX = 'AgGridHeader';

const classes = {
  fishSize: `${PREFIX}-fishSize`,
  invertSize: `${PREFIX}-invertSize`
};

const Root = styled('div')((
    {
      theme
    }
) => ({
  [`& .${classes.fishSize}`]: {
    color: '#c4d79b',
    borderBottom: '1px solid ' + theme.palette.divider
  },

  [`& .${classes.invertSize}`]: {
    color: '#da9694'
  }
}));

const AgGridHeader = (props) => {


  const onSortRequested = (order, event) => {
    props.setSort(order, event.shiftKey);
  };

  return (
      <Root style={{width: '100%'}}>
        <div style={{float: 'left'}} onClick={(event) => onSortRequested(props.column.isSortAscending() ? 'desc' : 'asc', event)}>
          <div className={classes.fishSize}>{props.displayName}</div>
          <div className={classes.invertSize}>{props.column.colDef.invertSize}</div>
        </div>
      </Root>
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