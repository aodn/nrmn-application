import React, {useEffect, useState} from 'react';
import {PropTypes} from 'prop-types';
import {Button, Divider, List, ListItem, ListItemText} from '@material-ui/core';
import {Accordion, AccordionDetails, AccordionSummary, Box} from '@material-ui/core';
import Typography from '@material-ui/core/Typography';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import {measurements} from '../../../constants';

const focusCell = (api, column, ids) => {
  api.ensureColumnVisible(column);
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

const ValidationPanel = (props) => {
  const [errList, setErrList] = useState([]);

  useEffect(() => {
    const errorsByMsg = props.api.gridOptionsWrapper.gridOptions.context.summaries;

    const errList = Object.keys(errorsByMsg).map((label) => {
      const b = errorsByMsg[label].find((e) => e.level === 'BLOCKING');
      return {
        key: label,
        total: errorsByMsg[label].length,
        value: errorsByMsg[label],
        blocking: b ? true : false
      };
    });

    if (errList && errList.length > 0) {
      setErrList(errList);
    }
  }, [props.api.gridOptionsWrapper.gridOptions.context.summaries]);

  // HACK: To work around the fact that the column returned in the validation results
  // and the staged rows have different cases for the column names.
  const mapColumnTargetToGridColumn = (columnTarget) => {
    const col = columnTarget.split(',')[0].toLowerCase();
    const matchingColumn = props.columnApi.columnController.columnDefs.find((d) => d.field.toLowerCase() === col);
    if (!matchingColumn) {
      // Assume invert sizing
      const invertSizeMap = measurements.find((m) => m.invertSize === col);
      console.assert(invertSizeMap);
      return invertSizeMap?.field || '';
    }
    return matchingColumn.field;
  };

  const blocking = errList.filter((e) => e.blocking);
  const warning = errList.filter((e) => !e.blocking);

  const handleItemClick = (item) => {
    if (item.ids) {
      focusCell(props.api, mapColumnTargetToGridColumn(item.columnTarget), item.ids);
    } else if (item.row) {
      props.api.setFilterModel(null);
      const rowIdx = props.api.gridOptionsWrapper.gridOptions.context.rowData.findIndex((r) => r.id === item.row);
      props.api.ensureIndexVisible(rowIdx, 'middle');
    }
    props.api.redrawRows();
  };

  return (
    <Box m={2} mr={4}>
      <Button onClick={() => props.api.setFilterModel(null)}>Reset</Button>
      <Box p={1}>
        <Typography variant="button">{blocking.length > 0 ? `${blocking.length} Errors:` : 'No Errors ✔'}</Typography>
      </Box>
      {blocking.map((err) => (
        <Accordion key={err.key}>
          <AccordionSummary expandIcon={<ExpandMoreIcon />} id="panel1c-header">
            <Typography variant="button">{`${err.key} (${err.total})`}</Typography>
          </AccordionSummary>
          <AccordionDetails>
            <List>
              {err.value.map((item, i) => (
                <ListItem key={i} onClick={() => handleItemClick(item)} style={{backgroundColor: '#ffcdd2'}} button>
                  <ListItemText color="secondary" primary={item.message} />
                </ListItem>
              ))}
            </List>
          </AccordionDetails>
        </Accordion>
      ))}
      <Divider />
      {warning.length > 0 && (
        <Box p={1}>
          <Typography variant="button">{warning.length > 0 ? `${warning.length} Warnings:` : 'No Warnings ✔'}</Typography>
        </Box>
      )}
      {warning.map((err) => (
        <Accordion key={err.key}>
          <AccordionSummary expandIcon={<ExpandMoreIcon />} id="panel1c-header">
            <div>
              <Typography>{err.key}</Typography>
            </div>
            <div>
              <Typography> ({err.total}) </Typography>
            </div>
          </AccordionSummary>
          <AccordionDetails>
            <List>
              {err.value.map((item, i) => (
                <ListItem onClick={() => handleItemClick(item)} style={{backgroundColor: '#ffe0b2'}} key={i} button>
                  <ListItemText color="secondary" primary={item.message} />
                </ListItem>
              ))}
            </List>
          </AccordionDetails>
        </Accordion>
      ))}
    </Box>
  );
};

ValidationPanel.propTypes = {
  api: PropTypes.object,
  columnApi: PropTypes.object
};

export default ValidationPanel;
