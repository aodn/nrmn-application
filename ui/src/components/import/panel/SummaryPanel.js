import {Box, Button, Table, TableBody, TableCell, TableRow, Tooltip, Typography, makeStyles} from '@material-ui/core';
import {ArrowDropDown as ArrowDropDownIcon, ArrowRight as ArrowRightIcon} from '@material-ui/icons';
import TreeItem from '@material-ui/lab/TreeItem';
import TreeView from '@material-ui/lab/TreeView';
import React, {useEffect, useState} from 'react';
import {PropTypes} from 'prop-types';

const useTreeItemStyles = makeStyles((theme) => ({
  content: {
    color: theme.palette.text.secondary,
    borderTopRightRadius: theme.spacing(2),
    borderBottomRightRadius: theme.spacing(2),
    fontWeight: theme.typography.fontWeightMedium
  },
  group: {
    marginLeft: 0,
    '& $content': {
      paddingLeft: theme.spacing(2)
    }
  },
  label: {
    fontWeight: 'inherit',
    color: 'inherit'
  },
  labelRoot: {
    display: 'flex',
    alignItems: 'center',
    padding: theme.spacing(0.5, 0)
  },
  labelIcon: {
    marginRight: theme.spacing(1)
  },
  labelText: {
    fontWeight: 'inherit',
    flexGrow: 1
  }
}));

const SummaryPanel = (props) => {
  const classes = useTreeItemStyles();

  const context = props.api.gridOptionsWrapper.gridOptions.context;

  const [info, setInfo] = useState({});

  useEffect(() => {
    setInfo(context.summary);
    const errors = context.errors;
    if (errors && errors.length > 0) {
      let duplicateRowDescriptions = [];
      errors
        .filter((e) => e.categoryId === 'GLOBAL')
        .forEach((e) => {
          const firstRowId = e.rowIds[0];
          const data = context.rowData.find((d) => d.id === firstRowId);
          e.rowIds.forEach((rowId) => {
            const description = data.siteCode && data.date && data.depth ? `${data.siteCode}/${data.date}/${data.depth} ...` : '...';
            duplicateRowDescriptions = [...duplicateRowDescriptions, {value: description, row: rowId}];
          });
        });
    }
  }, [context]);

  const siteTooltip = info.foundSites
    ? Object.keys(info.foundSites).map((key) => (
        <div key={key}>
          {key}
          <br />
        </div>
      ))
    : '';

  const newSites = info.foundSites
    ? Object.keys(info.foundSites)
        .filter((key) => info.foundSites[key] === true)
        .map((key) => (
          <TreeItem className={classes.labelText} nodeId={key} label={<div className={classes.labelRoot}>{key}</div>} key={key} />
        ))
    : '';

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
              <TableCell>Rows Found</TableCell>
            </TableRow>
            <TableRow>
              <TableCell>{info.siteCount}</TableCell>
              <Tooltip title={siteTooltip} interactive>
                <TableCell>Distinct Sites Found</TableCell>
              </Tooltip>
            </TableRow>
            <TableRow>
              <TableCell></TableCell>
              <TableCell>
                <small>{info.siteCount - info.newSiteCount} Existing</small>
              </TableCell>
            </TableRow>
            <TableRow>
              <TableCell></TableCell>
              <TableCell>
                <TreeView defaultCollapseIcon={<ArrowDropDownIcon />} defaultExpandIcon={<ArrowRightIcon />}>
                  <TreeItem className={classes.labelText} nodeId={'1'} label={`mismatchedSites mismatched`}>
                    {newSites}
                  </TreeItem>
                </TreeView>
              </TableCell>
            </TableRow>
            <TableRow>
              <TableCell></TableCell>
              <TableCell>
                <small>MANY Co-ordinates Mismatching</small>
              </TableCell>
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
              <TableCell></TableCell>
              <TableCell>
                <small>{info.newObsItemCount} new observable items found</small>
              </TableCell>
            </TableRow>
            <TableRow>
              <TableCell>{info.diverCount}</TableCell>
              <TableCell>distinct divers found</TableCell>
            </TableRow>
            <TableRow>
              <TableCell></TableCell>
              <TableCell>
                <small>{info.newDiverCount} new divers found</small>
              </TableCell>
            </TableRow>
          </TableBody>
        </Table>
      </Box>
    </>
  );
};

SummaryPanel.propTypes = {
  api: PropTypes.object,
  columnApi: PropTypes.object
};

export default SummaryPanel;
