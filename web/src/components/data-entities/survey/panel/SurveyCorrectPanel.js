import React, {useEffect, useState} from 'react';
import {PropTypes} from 'prop-types';
import {Box, Typography} from '@mui/material';
import ArrowDropDownIcon from '@mui/icons-material/ArrowDropDown';
import ArrowRightIcon from '@mui/icons-material/ArrowRight';
import TreeItem from '@mui/lab/TreeItem';
import TreeView from '@mui/lab/TreeView';
import {measurements, extendedMeasurements} from '../../../../common/constants';

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
    for (const validation of context.validations) {
      if (validation.rowIds?.length == 1 && validation.columnNames) {
        const rowId = validation.rowIds[0];
        const rowData = api.getRowNode(rowId).data;
        const columnPath = validation.columnNames[0];
        const value = rowData[columnPath[0]];
        const col = validation.columnNames.length > 1 ? {columnNames: validation.columnNames} : {columnName: columnPath};
        const rowPos = validation.rowIds.map((r) => context.rowData.find((d) => d.id === r)?.pos);
        const rowNum = rowPos.map((r) => context.rowPos.indexOf(r) + 1);
        rowNum.sort((a, b) => a - b);
        validation.description = [{...col, rowIds: validation.rowIds, rowNumbers: rowNum, value}];
      } else {
        const rowPos = validation.rowIds.map((r) => context.rowData.find((d) => d.id === r)?.pos);
        const rowNum = rowPos.map((r) => context.rowPos.indexOf(r) + 1);
        rowNum.sort((a, b) => a - b);
        validation.id = validation.message + rowNum.join('.');
        validation.description = [{columnName: 'id', rowIds: validation.rowIds, rowNumbers: rowNum, value: ''}];
      }

      formatted.push(validation);
    }
    setMessages(groupArrayByKey(formatted, 'levelId'));
  }, [context, api]);

  const mm = measurements.concat(extendedMeasurements);

  const isContiguous = (sorted) => {
    const first = sorted[0];
    const last = sorted[sorted.length - 1];
    return last - first + 1 === sorted.length;
  };

  const summary = (
    <Box m={2}>
      <TreeView defaultCollapseIcon={<ArrowDropDownIcon />} defaultExpandIcon={<ArrowRightIcon />}>
        {['BLOCKING', 'WARNING', 'DUPLICATE', 'INFO'].map((level) => (
          <div key={level}>
            <Typography variant="button">{messages[level] ? level : 'No ' + level + '✔'}</Typography>
            {messages[level]?.map((m) => (
              <Box key={m.id}>
                <TreeItem
                  nodeId={`${m.id}`}
                  key={`${m.id}`}
                  label={
                    <Typography variant="body2">
                      {m.message} {m.description?.length > 1 ? '(' + m.description.length + ')' : ''}
                    </Typography>
                  }
                >
                  {m.description?.map((d) => {
                    const mmHeader = mm.find((m) => d.columnName && m.field === d.columnName.replace('measurements.', ''));
                    const label = mmHeader ? `${d.isInvertSize ? mmHeader.invertSize : mmHeader.fishSize}cm` : d.columnName;
                    return (
                      <TreeItem
                        nodeId={`${m.id}-${d.columnName}`}
                        key={`${m.id}-${d.columnName}`}
                        onClick={() => handleItemClick(d)}
                        label={
                          <Typography variant="body2">
                            {d.value ? (
                              <span>
                                {d.rowNumbers ? `${d.rowNumbers[0]}: ` : ''}
                                <b>{label}</b> {d.value}
                              </span>
                            ) : d.label ? (
                              <b>{label} is empty</b>
                            ) : d.columnNames ? (
                              <b>
                                Check Column{d.columnNames.length > 1 ? 's' : ''} {d.columnNames.join(', ')}
                              </b>
                            ) : (
                              <b>Rows {isContiguous(d.rowNumbers) ? `[${d.rowNumbers[0]}-${d.rowNumbers[d.rowNumbers.length-1]}]` : d.rowNumbers.join(', ')}</b>
                            )}
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
