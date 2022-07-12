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

const SummaryPanel = ({api, context}) => {
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
      if (validation.rowIds.length == 1 || validation.columnNames.length == 1) {
        const rowId = validation.rowIds[0];
        const rowData = context.rowData.find((r) => r.id === rowId);
        const columnPath = validation.columnNames[0];
        const columnParts = columnPath.split('.');
        const datum = columnParts.length > 1 ? rowData[columnParts[0]][columnParts[1]] : rowData[columnParts[0]];
        validation.description = [{columnNames: validation.columnNames, rowIds: [rowId], value: datum}];
      }
      validation.rowNumbers = validation.rowIds; // FIXME

      formatted.push(validation);
    }
    setMessages(groupArrayByKey(formatted, 'levelId'));
  }, [context]);

  const mm = measurements.concat(extendedMeasurements);

  const summary = (
    <TreeView defaultCollapseIcon={<ArrowDropDownIcon />} defaultExpandIcon={<ArrowRightIcon />}>
      {Object.keys(messages).map((level) => (
        <>
          <Typography variant="button">{level}</Typography>
          {messages[level].map((m) => (
            <Box my={0.25} key={m.id}>
              <TreeItem
                nodeId={m.id}
                label={
                  <Typography variant="body2">
                    {m.message} {m.description.length > 1 ? '(' + m.description.length + ')' : ''}
                  </Typography>
                }
              >
                {m.description.map((d) => {
                  const mmHeader = mm.find((m) => m.field === d.columnName);
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
                            <b>Rows {d.rowNumbers.join(', ')}</b>
                          )}
                        </Typography>
                      }
                    />
                  );
                })}
              </TreeItem>
            </Box>
          ))}
        </>
      ))}
    </TreeView>
  );

  return (
    <Box m={2} mr={4}>
      <Box m={2} mt={1}>
        {summary}
        {/* <ValidationSummary data={blocking} onItemClick={(item) => handleItemClick(item, true)} /> */}
      </Box>
    </Box>
  );
};

SummaryPanel.propTypes = {
  api: PropTypes.any,
  context: PropTypes.any
};

export default SummaryPanel;
