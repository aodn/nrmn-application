import React from 'react';
import {styled} from '@mui/material/styles';
import PropTypes from 'prop-types';



const Root = styled('div')(
  ({theme}) => ({
  [`& .AgGridHeader-fishSize`]: {
    color: '#c4d79b',
    borderBottom: '1px solid ' + theme.palette.divider
  },

  [`& .AgGridHeader-invertSize`]: {
    color: '#da9694'
  }
  })
);

const AgGridHeader = (props) => {


  const onSortRequested = (order, event) => {
    props.setSort(order, event.shiftKey);
  };

  return (
      <Root style={{width: '100%'}}>
        <div style={{float: 'left'}} onClick={(event) => onSortRequested(props.column.isSortAscending() ? 'desc' : 'asc', event)}>
          <div className={`AgGridHeader-fishSize`}>{props.displayName}</div>
          <div className={`AgGridHeader-invertSize`}>{props.column.colDef.invertSize}</div>
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