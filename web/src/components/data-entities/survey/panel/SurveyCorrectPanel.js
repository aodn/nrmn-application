import React, {useEffect, useState} from 'react';
import {PropTypes} from 'prop-types';
import {Box} from '@mui/material';
import ValidationSummary from '../../../import/panel/ValidationSummary';
import eh from '../../../../components/import/DataSheetEventHandlers';

const SurveyCorrectPanel = ({api, context}) => {
  const [messages, setMessages] = useState({});

  const handleItemClick = (item, noFilter) => {
    const rowId = item.row || item.rowIds[0];
    context.focusedRows = noFilter ? item.rowIds || [item.row] : [];
    const row = context.rowData.find((r) => r.id === rowId);
    let visible = false;
    api.forEachNodeAfterFilter((n) => (visible = n.data.id === row.id || visible));
    if (visible) api.ensureNodeVisible(row, 'middle');
    if (item.columnName) api.ensureColumnVisible(item.columnName);
    if (item.columnNames) for (const column of item.columnNames) api.ensureColumnVisible(column);
    api.setFilterModel(noFilter ? null : {id: {type: 'set', values: item.rowIds.map((id) => id.toString())}});
    api.redrawRows();
  };

  useEffect(() => setMessages(eh.generateErrorTree(context.rowData, context.validations)), [context, api]);

  const summary = (
    <Box m={2}>
      <ValidationSummary data={messages} onItemClick={handleItemClick} />
    </Box>
  );

  return summary;
};

SurveyCorrectPanel.propTypes = {
  api: PropTypes.any,
  context: PropTypes.any
};

export default SurveyCorrectPanel;
