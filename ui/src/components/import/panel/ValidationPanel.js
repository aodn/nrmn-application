import {Box, Button, Divider, Table, TableBody, TableCell, TableRow, Typography} from '@material-ui/core';
import {PropTypes} from 'prop-types';
import React, {useEffect, useState} from 'react';
import ValidationSummary from './ValidationSummary';

const focusCell = (api, columns, ids) => {
  if (columns) for (const column of columns) api.ensureColumnVisible(column);
  if (ids) {
    const values = ids.map((id) => id.toString());
    api.setFilterModel({
      id: {
        type: 'set',
        values: values
      }
    });
  }
};

const generateErrorTree = (ctx, errors, level) => {
  const tree = [];
  errors
    .filter((e) => e.levelId === level)
    .sort((a, b) => {
      return a.message < b.message ? -1 : a.message > b.message ? 1 : 0;
    })
    .forEach((e) => {
      tree.push(generateErrorSummary(ctx, e));
    });
  return tree;
};

const generateErrorSummary = (ctx, e) => {
  const rows = ctx.rowData.filter((r) => e.rowIds.includes(r.id));
  let summary = [];
  if (e.columnNames && e.categoryId !== 'SPAN') {
    for (const col of e.columnNames) {
      let values = [];
      rows.forEach((r) => {
        values.push({columnName: col, rowId: r.id, value: r[col]});
      });

      summary = values.reduce((acc, val) => {
        const existingIdx = acc.findIndex((m) => m.columnName === val.columnName && m.value === val.value);
        if (existingIdx >= 0) {
          const rowIds = acc[existingIdx].rowIds;
          acc[existingIdx] = {...val, rowIds: [...rowIds, val.rowId]};
        } else {
          const rowIds = [val.rowId];
          delete val.rowId;
          acc.push({...val, rowIds});
        }
        return acc;
      }, []);
    }
  } else {
    summary.push({rowIds: e.rowIds, columnNames: e.columnNames});
  }
  return {message: e.message, count: e.rowIds.length, description: summary};
};

const ValidationPanel = (props) => {
  const context = props.api.gridOptionsWrapper.gridOptions.context;
  const errors = context.errors;

  const [blocking, setBlocking] = useState([]);
  const [warning, setWarning] = useState([]);
  const [info, setInfo] = useState({});

  useEffect(() => {
    setInfo(context.summary);
    if (errors && errors.length > 0) {
      setBlocking(generateErrorTree(context, errors, 'BLOCKING'));
      setWarning(generateErrorTree(context, errors, 'WARNING'));
    }
  }, [errors, context]);

  const handleItemClick = (item) => {
    if (item.rowIds) {
      focusCell(props.api, item.columnNames || [item.columnName], item.rowIds);
    } else if (item.row) {
      props.api.setFilterModel(null);
      const rowIdx = props.api.gridOptionsWrapper.gridOptions.context.rowData.findIndex((r) => r.id === item.row);
      props.api.ensureIndexVisible(rowIdx, 'middle');
    }
    props.api.redrawRows();
  };

  return (
    <>
      <Box m={2} mt={1}>
        <Button onClick={() => props.api.setFilterModel(null)}>Reset Filter</Button>
      </Box>
      <Box m={2}>
        <Typography variant="button">Summary</Typography>
        <Table size="small">
          <TableBody>
            <TableRow>
              <TableCell>{info.rowCount}</TableCell>
              <TableCell>rows found</TableCell>
            </TableRow>
            <TableRow>
              <TableCell>{info.siteCount}</TableCell>
              <TableCell>distinct sites found</TableCell>
            </TableRow>
            <TableRow>
              <TableCell>{info.surveyCount}</TableCell>
              <TableCell>distinct surveys found</TableCell>
            </TableRow>
            <TableRow>
              <TableCell>{info.incompleteSurveyCount}</TableCell>
              <TableCell>incomplete surveys found</TableCell>
            </TableRow>
            <TableRow>
              <TableCell>{info.obsItemCount}</TableCell>
              <TableCell>distinct observable items found</TableCell>
            </TableRow>
            <TableRow>
              <TableCell>{info.diverCount}</TableCell>
              <TableCell>distinct divers found</TableCell>
            </TableRow>
          </TableBody>
        </Table>
      </Box>
      <Box m={2} mt={1}>
        <Typography variant="button">{Object.keys(blocking).length > 0 ? `Blocking` : 'No Blocking ✔'}</Typography>
        {Object.keys(blocking).length > 0 && <ValidationSummary data={blocking} onItemClick={handleItemClick} />}
      </Box>
      <Divider />
      <Box m={2} mt={1}>
        <Typography variant="button">{Object.keys(warning).length > 0 ? `Warning` : 'No Warning ✔'}</Typography>
        {Object.keys(warning).length > 0 && <ValidationSummary data={warning} onItemClick={handleItemClick} />}
      </Box>
    </>
  );
};

ValidationPanel.propTypes = {
  api: PropTypes.object,
  columnApi: PropTypes.object
};

export default ValidationPanel;
