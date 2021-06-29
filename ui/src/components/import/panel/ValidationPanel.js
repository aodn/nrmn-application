import React, {useEffect, useState} from 'react';
import {PropTypes} from 'prop-types';
import {Button, Divider, List, ListItem, ListItemIcon, Badge, ListItemText} from '@material-ui/core';
import {Accordion, AccordionDetails, AccordionSummary, Box} from '@material-ui/core';
import {BlockOutlined as BlockOutlinedIcon, WarningOutlined as WarningOutlinedIcon} from '@material-ui/icons';
import Typography from '@material-ui/core/Typography';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';

const focusCell = (api, column, ids) => {
  api.ensureColumnVisible(column.toLowerCase());
  const values = ids.map((id) => id.toString());
  api.setFilterModel({
    id: {
      type: 'set',
      values: values
    }
  });
  api.redrawRows();
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

  const blocking = errList.filter((e) => e.blocking);
  const warning = errList.filter((e) => !e.blocking);

  return (
    <Box m={2} mr={4}>
      <Button onClick={() => props.api.setFilterModel(null)}>Reset</Button>
      <Typography>{blocking.length} blocking</Typography>
      {blocking.map((err) => (
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
            <List style={{width: '100%'}}>
              {err.value.map((item, i) => (
                <ListItem
                  onClick={() => {
                    focusCell(props.api, item.columnTarget, item.ids);
                  }}
                  style={{backgroundColor: '#ffcdd2'}}
                  key={i}
                  button
                >
                  <ListItemIcon>
                    <Badge badgeContent={item.count} color="primary">
                      {item.level == 'WARNING' ? <WarningOutlinedIcon color="error" /> : <BlockOutlinedIcon color="error" />}
                    </Badge>
                  </ListItemIcon>
                  <ListItemText
                    style={{overflow: 'hidden', width: '100%', whiteSpace: 'break-spaces'}}
                    color="secondary"
                    primary={item.message}
                  />
                </ListItem>
              ))}
            </List>
          </AccordionDetails>
        </Accordion>
      ))}
      <Divider />
      <Typography>{warning.length} warning</Typography>
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
            <List style={{width: '100%'}}>
              {err.value.map((item, i) => (
                <ListItem
                  onClick={() => {
                    focusCell(props.api, item.columnTarget, item.ids);
                  }}
                  style={{backgroundColor: item.level == 'WARNING' ? '#ffe0b2' : '#ffcdd2'}}
                  key={i}
                  button
                >
                  <ListItemIcon>
                    <Badge badgeContent={item.count} color="primary">
                      {item.level == 'WARNING' ? <WarningOutlinedIcon color="error" /> : <BlockOutlinedIcon color="error" />}
                    </Badge>
                  </ListItemIcon>
                  <ListItemText
                    style={{overflow: 'hidden', width: '100%', whiteSpace: 'break-spaces'}}
                    color="secondary"
                    primary={item.message}
                  />
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
  api: PropTypes.any,
  agGridReact: PropTypes.any
};

export default ValidationPanel;
