import React from 'react';
import {styled} from '@mui/material/styles';
import PropTypes from 'prop-types';


const Root = styled('div')(({theme}
) => ({
  [`& .GridHeader-fishSize`]: {
    color: '#c4d79b',
    borderBottom: '1px solid ' + theme.palette.divider
  },

  [`& .GridHeader-invertSize`]: {
    color: '#da9694'
  }
}));

const GridHeader = (props) => {


  const onSortRequested = (order, event) => {
    props.setSort(order, event.shiftKey);
  };

  return (
      <Root style={{width: '100%'}}>
        <div style={{float: 'left'}} onClick={(event) => onSortRequested(props.column.isSortAscending() ? 'desc' : 'asc', event)}>
          <div className={`GridHeader-fishSize`}>{props.fishSize}</div>
          <div className={`GridHeader-invertSize`}>{props.invertSize}</div>
        </div>
      </Root>
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