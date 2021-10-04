import {Box, Divider, Table, TableBody, TableCell, TableRow, Tooltip, Typography} from '@material-ui/core';
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
      const summary = generateErrorSummary(ctx, e);
      tree.push(summary);
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
          if (rowIds.length < 50) acc[existingIdx] = {...val, rowIds: [...rowIds, val.rowId]};
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
  return {key: `error-${e.id}`, message: e.message, count: e.rowIds.length, description: summary};
};

const ValidationPanel = (props) => {
  const context = props.api.gridOptionsWrapper.gridOptions.context;

  const [errorList, setErrorList] = useState({blocking: {}, warning: {}, duplicateRows: {}});
  const [summary, setSummary] = useState({});

  useEffect(() => {
    setSummary(context.summary);
    const errors = context.errors.sort((a, b) => {
      return a.message < b.message ? -1 : a.message > b.message ? 1 : 0;
    });
    if (errors && errors.length > 0) {
      const blocking = generateErrorTree(context, errors, 'BLOCKING');
      const warning = generateErrorTree(context, errors, 'WARNING');
      const info = generateErrorTree(context, errors, 'INFO');
      let duplicateRowDescriptions = [];
      errors
        .filter((e) => e.levelId === 'DUPLICATE')
        .forEach((e) => {
          const firstRowId = e.rowIds[0];
          const data = context.rowData.find((d) => d.id === firstRowId);
          e.rowIds.forEach((rowId) => {
            const description = data.siteCode && data.date && data.depth ? `${data.siteCode}/${data.date}/${data.depth} ...` : '...';
            duplicateRowDescriptions = [...duplicateRowDescriptions, {value: description, row: rowId}];
          });
        });
      const duplicateRows =
        duplicateRowDescriptions.length > 0
          ? [
              {
                key: 'duplicateRowDescriptions',
                count: duplicateRowDescriptions.length,
                message: 'Duplicate Rows',
                description: duplicateRowDescriptions
              }
            ]
          : {};
      setErrorList({blocking, warning, duplicateRows, info});
    }
  }, [context]);

  const handleItemClick = (item) => {
    if (item.rowIds) {
      focusCell(props.api, item.columnNames || [item.columnName], item.rowIds);
    } else if (item.row) {
      const row = props.api.gridOptionsWrapper.gridOptions.context.rowData.find((r) => r.id === item.row);
      let visible = false;
      props.api.forEachNodeAfterFilter((n) => (visible = n.data.id === row.id || visible));
      if (visible) {
        props.api.ensureNodeVisible(row, 'middle');
      }
    }
    props.api.redrawRows();
  };

  const siteTooltip = summary.foundSites
    ? Object.keys(summary.foundSites).map((key) => (
        <>
          {key}
          <br />
        </>
      ))
    : '';
  const newSitesTooltip = summary.foundSites
    ? Object.keys(summary.foundSites)
        .filter((key) => summary.foundSites[key] === true)
        .map((key) => (
          <>
            {key}
            <br />
          </>
        ))
    : '';

  return (
    <>
      <Box m={2}>
        <Typography variant="button">Summary</Typography>
        <Table size="small">
          <TableBody>
            <TableRow>
              <TableCell>{summary.rowCount}</TableCell>
              <TableCell>rows found</TableCell>
            </TableRow>
            <TableRow>
              <TableCell>{summary.surveyCount}</TableCell>
              <TableCell>distinct surveys found</TableCell>
            </TableRow>
            <TableRow>
              <TableCell>{summary.existingSurveyCount}</TableCell>
              <TableCell>existing surveys found</TableCell>
            </TableRow>
            <TableRow>
              <TableCell>{summary.incompleteSurveyCount}</TableCell>
              <TableCell>incomplete surveys found</TableCell>
            </TableRow>
            <TableRow>
              <TableCell>{summary.siteCount}</TableCell>
              <Tooltip title={siteTooltip} interactive>
                <TableCell>distinct sites found</TableCell>
              </Tooltip>
            </TableRow>
            <TableRow>
              <TableCell></TableCell>
              <Tooltip title={newSitesTooltip} interactive>
                <TableCell>
                  <small>{summary.newSiteCount} new sites found</small>
                </TableCell>
              </Tooltip>
            </TableRow>
            <TableRow>
              <TableCell>{summary.obsItemCount}</TableCell>
              <TableCell>distinct observable items found</TableCell>
            </TableRow>
            <TableRow>
              <TableCell></TableCell>
              <TableCell>
                <small>{summary.newObsItemCount} new observable items found</small>
              </TableCell>
            </TableRow>
            <TableRow>
              <TableCell>{summary.diverCount}</TableCell>
              <TableCell>distinct divers found</TableCell>
            </TableRow>
            <TableRow>
              <TableCell></TableCell>
              <TableCell>
                <small>{summary.newDiverCount} new divers found</small>
              </TableCell>
            </TableRow>
          </TableBody>
        </Table>
      </Box>
      <Box m={2} mt={1}>
        <Typography variant="button">{Object.keys(errorList.blocking).length > 0 ? `Blocking` : 'No Blocking ✔'}</Typography>
        {Object.keys(errorList.blocking).length > 0 && <ValidationSummary data={errorList.blocking} onItemClick={handleItemClick} />}
      </Box>
      <Divider />
      {Object.keys(errorList.duplicateRows).length > 0 && (
        <>
          <Box m={2} mt={1}>
            <Typography variant="button">GLOBAL</Typography>
            <ValidationSummary data={errorList.duplicateRows} onItemClick={handleItemClick} />
          </Box>
          <Divider />
        </>
      )}
      <Box m={2} mt={1}>
        <Typography variant="button">{Object.keys(errorList.warning).length > 0 ? `Warning` : 'No Warning ✔'}</Typography>
        {Object.keys(errorList.warning).length > 0 && <ValidationSummary data={errorList.warning} onItemClick={handleItemClick} />}
      </Box>
      {errorList.info && Object.keys(errorList.info).length > 0 && (
        <>
          <Divider />
          <Box m={2} mt={1}>
            <Typography variant="button">Info</Typography>
            <ValidationSummary data={errorList.info} onItemClick={handleItemClick} />
          </Box>
        </>
      )}
    </>
  );
};

ValidationPanel.propTypes = {
  api: PropTypes.object,
  columnApi: PropTypes.object
};

export default ValidationPanel;
