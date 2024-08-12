import React from 'react';
import { Box, Table, TableBody, TableCell, TableRow, Tooltip, Typography } from '@mui/material';
import { PropTypes } from 'prop-types';
import ValidationSummary from './ValidationSummary';

const ValidationPanel = ({ api, context }) => {
  const summary = context.summary;
  const errorList = context.errorList;

  const handleItemClick = (item, noFilter) => {
    const rowId = item.row || item.rowIds[0];
    context.focusedRows = noFilter ? item.rowIds || [item.row] : [];
    const row = context.rowData.find((r) => r.id === rowId);
    let visible = false;
    api.forEachNodeAfterFilter((n) => (visible = n.data.id === row?.id || visible));

    if (visible) api.ensureNodeVisible(row, 'middle');
    if (item.columnName) api.ensureColumnVisible(item.columnName);
    if (item.columnNames) for (const column of item.columnNames) api.ensureColumnVisible(column);

    api.setFilterModel(noFilter ? null : { id: { type: 'set', values: item.rowIds.map((id) => id.toString()) } });
    api.redrawRows();
  };

  const siteTooltip = summary.foundSites
    ? Object.keys(summary.foundSites).map((key) => (
      <div key={key}>
        {key}
        <br />
      </div>
    ))
    : '';
  const newSitesTooltip = summary.foundSites
    ? Object.keys(summary.foundSites)
      .filter((key) => summary.foundSites[key] === true)
      .map((key) => (
        <div key={key}>
          {key}
          <br />
        </div>
      ))
    : '';

  return (
    <>
      <Box m={2}>
        <Typography variant="button">Summary</Typography>
        <Table size="small">
          <TableBody>
            <TableRow>
              <TableCell align="right">{summary.rowCount}</TableCell>
              <TableCell>rows</TableCell>
            </TableRow>
            <TableRow>
              <TableCell align="right">{summary.surveyCount}</TableCell>
              <TableCell>distinct surveys</TableCell>
            </TableRow>
            <TableRow>
              <TableCell align="right">{summary.existingSurveyCount}</TableCell>
              <TableCell>existing surveys</TableCell>
            </TableRow>
            <TableRow>
              <TableCell align="right">{summary.incompleteSurveyCount}</TableCell>
              <TableCell>incomplete surveys</TableCell>
            </TableRow>
            <TableRow>
              <TableCell align="right">{summary.siteCount}</TableCell>
              <Tooltip title={siteTooltip}>
                <TableCell style={{ textDecoration: 'underline dotted' }}>distinct sites</TableCell>
              </Tooltip>
            </TableRow>
            <TableRow>
              <TableCell align="right">{summary.newSiteCount}</TableCell>
              {summary.newSiteCount > 0 ? (
                <Tooltip title={newSitesTooltip}>
                  <TableCell style={{ textDecoration: 'underline dotted' }}>new sites</TableCell>
                </Tooltip>
              ) : (
                <TableCell>new sites</TableCell>
              )}
            </TableRow>
            <TableRow>
              <TableCell align="right">{summary.obsItemCount}</TableCell>
              <TableCell>distinct observable items</TableCell>
            </TableRow>
            <TableRow>
              <TableCell align="right">{summary.newObsItemCount}</TableCell>
              <TableCell>new observable items</TableCell>
            </TableRow>
            <TableRow>
              <TableCell align="right">{summary.diverCount}</TableCell>
              <TableCell>distinct divers</TableCell>
            </TableRow>
            <TableRow>
              <TableCell align="right">{summary.newDiverCount}</TableCell>
              <TableCell>new divers</TableCell>
            </TableRow>
          </TableBody>
        </Table>
      </Box>
      <Box m={2}>
        <ValidationSummary data={errorList} onItemClick={handleItemClick} />
      </Box>
    </>
  );
};

ValidationPanel.propTypes = {
  api: PropTypes.object,
  context: PropTypes.object
};

export default ValidationPanel;
