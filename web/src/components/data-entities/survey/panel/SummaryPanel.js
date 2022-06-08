import React, {useEffect, useState} from 'react';
import {PropTypes} from 'prop-types';
import {Box, Typography} from '@mui/material';
import ValidationSummary from '../../../import/panel/ValidationSummary';

const SummaryPanel = ({api, context}) => {
  const [blocking, setBlocking] = useState([]);

 // const handleItemClick = (item, noFilter) => {
    // const rowId = item.row || item.rowIds[0];
    // context.focusedRows = noFilter ? item.rowIds || [item.row] : [];
    // const row = context.rowData.find((r) => r.id === rowId);
    // let visible = false;
    // props.api.forEachNodeAfterFilter((n) => (visible = n.data.id === row.id || visible));
    // if (visible) props.api.ensureNodeVisible(row, 'middle');
    // if (item.columnName) props.api.ensureColumnVisible(item.columnName);
    // if (item.columnNames) for (const column of item.columnNames) props.api.ensureColumnVisible(column);
    // props.api.setFilterModel(noFilter ? null : {id: {type: 'set', values: item.rowIds.map((id) => id.toString())}});
    // props.api.redrawRows();
  //  };

  const handleItemClick = () => {};

  const updateValidations = () => {
    setBlocking(context.validations?.blocking ?? []);
  };

  useEffect(() => {
    api.addEventListener('toolPanelVisibleChanged', updateValidations);
    return () => api.removeEventListener('toolPanelVisibleChanged', updateValidations);
  });

  return (
    <Box m={2} mr={4}>
      <Box m={2} mt={1}>
        <Typography variant="button">BLOCKING</Typography>
        <ValidationSummary data={blocking} onItemClick={(item) => handleItemClick(item, true)} />
      </Box>
    </Box>
  );
};

SummaryPanel.propTypes = {
  api: PropTypes.any,
  context: PropTypes.any
};

export default SummaryPanel;
