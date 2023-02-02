import React, {useEffect, useState} from 'react';
import {PropTypes} from 'prop-types';
import {Box, Typography} from '@mui/material';
import ArrowDropDownIcon from '@mui/icons-material/ArrowDropDown';
import ArrowRightIcon from '@mui/icons-material/ArrowRight';
import TreeItem from '@mui/lab/TreeItem';
import TreeView from '@mui/lab/TreeView';
import {allMeasurements} from '../../../../common/constants';

const groupArrayByKey = (xs, key) =>
  xs.reduce((rv, x) => {
    (rv[x[key]] = rv[x[key]] || []).push(x);
    return rv;
  }, {});

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

  useEffect(() => {
    var formatted = [];
    const validations = context.validations;
    validations.forEach((validation, idx) => {
      if (validation.rowIds?.length == 1 && validation.columnNames) {
        const [rowId] = validation.rowIds;
        const rowNode = api.getRowNode(rowId);
        const columnPath = validation.columnNames[0].split('.')[1] || validation.columnNames[0];
        const isInvertSize = rowNode?.data.isInvertSizing.toUpperCase() === 'YES';
        validation.id = `${validation.levelId}-${idx}`;
        validation.description = [
          {
            ...(validation.columnNames.length > 1 ? {columnNames: validation.columnNames} : {columnName: columnPath}),
            messageId: `${validation.id}-0`,
            rowIds: validation.rowIds,
            rowNumbers: validation.rowIds
              .map((r) => context.rowData.find((d) => d.id === r)?.pos)
              .map((r) => context.rowPos.indexOf(r) + 1)
              .sort((a, b) => a - b),
            isInvertSize,
            value: rowNode ? rowNode.data[columnPath] : ''
          }
        ];
      }
      if (validation.columnNames?.length === 1) {
        const columnName = validation.columnNames[0].split('.')[1] || validation.columnNames[0];
        const rowPos = validation.rowIds.map((r) => context.rowData.find((d) => d.id === r)?.pos);
        const rowNum = rowPos.map((r) => context.rowPos.indexOf(r) + 1);
        rowNum.sort((a, b) => a - b);
        validation.id = validation.message + rowNum.join('.');
        const rowData = context.rowData.find((d) => d.id === validation.rowIds[0]);
        const value = rowData?.[columnName];
        const isInvertSize = rowData?.isInvertSizing.toUpperCase() === 'YES';
        validation.description = [{columnName, isInvertSize, rowIds: validation.rowIds, rowNumbers: rowNum, value}];
      } else {
        const rowPos = validation.rowIds.map((r) => context.rowData.find((d) => d.id === r)?.pos);
        const rowNum = rowPos.map((r) => context.rowPos.indexOf(r) + 1);
        rowNum.sort((a, b) => a - b);
        validation.id = validation.message + rowNum.join('.');
        validation.description = [{columnName: 'id', rowIds: validation.rowIds, rowNumbers: rowNum, value: ''}];
      }

      formatted.push(validation);
    });
    const groupedMessages = groupArrayByKey(formatted, 'levelId');
    setMessages(groupedMessages);
  }, [context, api]);

  const summary = (
    <Box m={2}>
      <TreeView defaultCollapseIcon={<ArrowDropDownIcon />} defaultExpandIcon={<ArrowRightIcon />}>
        {['BLOCKING', 'WARNING', 'DUPLICATE', 'INFO'].map((level) => (
          <div key={level}>
            <Typography variant="button">{messages[level] ? level : 'No ' + level + '✔'}</Typography>
            {messages[level]?.map((m, i) => (
              <Box key={i}>
                <TreeItem
                  nodeId={m.id}
                  key={m.id}
                  label={
                    <Typography variant="body2">
                      {m.message} {m.description?.length > 1 ? '(' + m.description.length + ')' : ''}
                    </Typography>
                  }
                >
                  {m.description?.map((d) => {
                    if (d.columnNames) d.columnName = d.columnNames[0];
                    const mmHeader = allMeasurements.find((m) => d.columnName && m.field === d.columnName.replace('measurements.', ''));
                    const label = mmHeader ? `${d.isInvertSize ? mmHeader.invertSize : mmHeader.fishSize}cm` : d.columnName;
                    return (
                      <TreeItem
                        nodeId={m.messageId}
                        key={m.messageId}
                        onClick={() => handleItemClick(d)}
                        label={
                          <Typography variant="body2">
                            <b>{label}</b> {d.value ? d.value : d.label ? 'is empty' : ''}
                            {d.rowNumbers ? ` (${d.rowNumbers.join(',')})` : ''}
                          </Typography>
                        }
                      />
                    );
                  })}
                </TreeItem>
              </Box>
            ))}
          </div>
        ))}
      </TreeView>
    </Box>
  );

  return summary;
};

SurveyCorrectPanel.propTypes = {
  api: PropTypes.any,
  context: PropTypes.any
};

export default SurveyCorrectPanel;
