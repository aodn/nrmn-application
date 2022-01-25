import React from 'react';
import {useSelector} from 'react-redux';
import {Box, Divider, Table, TableBody, TableCell, TableRow, Tooltip, Typography} from '@material-ui/core';
import {PropTypes} from 'prop-types';
import ValidationSummary from './ValidationSummary';

const ValidationPanel = (props) => {
  const context = props.api.gridOptionsWrapper.gridOptions.context;
  const summary = context.summary;
  const errorList = context.errorList;
  const isAdmin = useSelector((state) => state.auth.roles)?.includes('ROLE_ADMIN');

  const handleItemClick = (item, noFilter) => {
    const rowId = item.row || item.rowIds[0];
    context.focusedRows = noFilter ? item.rowIds || [item.row] : [];
    const row = context.rowData.find((r) => r.id === rowId);
    let visible = false;
    props.api.forEachNodeAfterFilter((n) => (visible = n.data.id === row.id || visible));
    if (visible) props.api.ensureNodeVisible(row, 'middle');
    if (item.columnName) props.api.ensureColumnVisible(item.columnName);
    if (item.columnNames) for (const column of item.columnNames) props.api.ensureColumnVisible(column);
    props.api.setFilterModel(noFilter ? null : {id: {type: 'set', values: item.rowIds.map((id) => id.toString())}});
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
                <TableCell>{summary.newSiteCount} new sites found</TableCell>
              </Tooltip>
            </TableRow>
            <TableRow>
              <TableCell>{summary.obsItemCount}</TableCell>
              <TableCell>distinct observable items found</TableCell>
            </TableRow>
            <TableRow>
              <TableCell></TableCell>
              <TableCell>{summary.newObsItemCount} new observable items found</TableCell>
            </TableRow>
            <TableRow>
              <TableCell>{summary.diverCount}</TableCell>
              <TableCell>distinct divers found</TableCell>
            </TableRow>
            <TableRow>
              <TableCell></TableCell>
              <TableCell>{summary.newDiverCount} new divers found</TableCell>
            </TableRow>
          </TableBody>
        </Table>
      </Box>
      {isAdmin ? (
        <Box m={2} mt={1}>
          <Typography style={{color: 'red'}} variant="button">
            Blocking validations disabled. Proceed with caution!
          </Typography>
        </Box>
      ) : (
        <Box m={2} mt={1}>
          <Typography variant="button">{Object.keys(errorList.blocking).length > 0 ? `Blocking` : 'No Blocking ✔'}</Typography>
          {Object.keys(errorList.blocking).length > 0 && <ValidationSummary data={errorList.blocking} onItemClick={handleItemClick} />}
        </Box>
      )}
      <Divider />
      {errorList.duplicate && Object.keys(errorList.duplicate).length > 0 && (
        <>
          <Box m={2} mt={1}>
            <Typography variant="button">DUPLICATE</Typography>
            <ValidationSummary data={errorList.duplicate} onItemClick={(item) => handleItemClick(item, true)} />
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
