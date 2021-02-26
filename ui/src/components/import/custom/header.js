import React, { useRef } from 'react';
import PropTypes from 'prop-types';
import {makeStyles} from '@material-ui/core';
import MenuIcon from '@material-ui/icons/Menu';
import {Box} from '@material-ui/core';

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
  const refButton = useRef(null);

  const onMenuClicked = () => {
    props.showColumnMenu(refButton.current);
  };



  const onSortRequested = (order, event) => {
    props.setSort(order, event.shiftKey);
  };

  let menu = null;
  if (props.enableMenu) {
    menu = (
      <Box ref={refButton} style={{float: 'right'}} variant="div" onClick={() => onMenuClicked()}>
        <MenuIcon className={'menu-icon'} style={{fontSize: '1rem', opacity: 0}} />
      </Box>
    );
  }
  return (
    <div style={{width: '100%'}}>
      <div style={{float: 'left'}} onClick={(event) => onSortRequested(props.column.isSortAscending() ? 'desc' : 'asc', event)}>
        <div className={classes.fishSize}>{props.displayName}</div>
        <div className={classes.invertSize}>{props.column.colDef.invertSize}</div>
      </div>
      {menu}
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
