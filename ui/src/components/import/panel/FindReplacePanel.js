import React, {useState} from 'react';
import {PropTypes} from 'prop-types';
import {Search as SearchIcon, FindReplace as FindReplaceIcon} from '@material-ui/icons/';
import {Box, Button, Checkbox, FormControlLabel, makeStyles, TextField} from '@material-ui/core';

const useStyles = makeStyles(() => {
  return {
    status: {paddingLeft: '20px', fontStyle: 'italic'},
    button: {width: '100%', marginTop: '8px', marginLeft: '0px'},
    checkbox: {marginLeft: '5px'},
    textfield: {width: '100%'}
  };
});

const stringCompare = (source, target, matchCase, matchWholeString) => {
  if (typeof source !== 'string' || typeof target !== 'string' || source.length === 0 || target.length === 0) return -1;
  if (matchWholeString) return source.normalize() === target.normalize() ? 0 : -1;
  if (matchCase) return source.indexOf(target);
  else return source.toUpperCase().indexOf(target.toUpperCase());
};

const FindReplacePanel = (props) => {
  const [matchCase, setMatchCase] = useState(false);
  const [matchColumn, setMatchColumn] = useState(false);
  const [matchCell, setMatchCell] = useState(false);
  const [inProgress, setInProgress] = useState(false);

  const [status, setStatus] = useState('');
  const [findString, setFindString] = useState('');
  const [replaceString, setReplaceString] = useState('');
  const [currentFindString, setCurrentFindString] = useState('');

  const [initialPosition, setInitialPosition] = useState({});

  const classes = useStyles();

  // where all grid data and metadata is stored
  const context = props.agGridReact.gridOptions.context;

  const reset = () => {
    setInProgress(false);
    setStatus('');
    setFindString('');
    setReplaceString('');
    setCurrentFindString('');
    context.findResults = [];
    context.highlighted = [];
    props.api.redrawRows();
    if (initialPosition) {
      props.api.setFocusedCell(initialPosition.rowIndex, initialPosition.column);
      props.api.ensureIndexVisible(initialPosition.rowIndex);
      props.api.ensureColumnVisible(initialPosition.column);
      setInitialPosition({});
    }
  };

  const highlightNextResult = () => {
    if (context.findResults.length < 1) return;
    const result = context.findResults.shift();
    props.api.ensureIndexVisible(result.row);
    props.api.ensureColumnVisible(result.col);
    props.api.setFocusedCell(result.row, result.col);
    context.findResults.push(result);
  };

  const findInGrid = (findString, matchCase) => {
    const focusedCell = props.api.getFocusedCell();
    const selectedRanges = props.api.getCellRanges();
    const selectedColumns = selectedRanges.map((c) => c.columns[0].colId);
    if (focusedCell) setInitialPosition({rowIndex: focusedCell.rowIndex, column: focusedCell.column});
    context.findResults = [];
    context.highlighted = [];
    props.api.forEachNodeAfterFilterAndSort((node) => {
      for (let columnIdx in node.columnApi.columnController.columnDefs) {
        const columnDef = node.columnApi.columnController.columnDefs[columnIdx];
        const fieldValue = node.data[columnDef.field];
        if ((matchColumn && !selectedColumns.includes(columnDef.field)) || !fieldValue || columnDef.editable === false) continue;
        const idx = stringCompare(fieldValue.toString(), findString, matchCase, matchCell);
        if (idx >= 0) {
          context.highlighted[node.rowIndex] = context.highlighted[node.rowIndex] || [];
          context.highlighted[node.rowIndex][columnDef.field] = true;
          context.findResults.push({row: node.rowIndex, col: columnDef.field});
        }
      }
    });
    props.api.redrawRows();
    const count = context.findResults.length;
    setCurrentFindString(count > 0 ? findString : '');
    setStatus(`Found ${count} matches`);
  };

  const onFind = () => {
    setInProgress(true);
    if (findString !== currentFindString) findInGrid(findString, matchCase);
    highlightNextResult();
  };

  const onReplace = () => {
    var newRow = {};
    var oldRow = {};
    const focusedCell = props.api.getFocusedCell();
    const row = props.api.getDisplayedRowAtIndex(focusedCell.rowIndex);
    Object.keys(row.data).forEach(function (key) {
      newRow[key] = row.data[key];
      oldRow[key] = row.data[key];
    });

    const colId = focusedCell.column.colId;
    const findRegex = new RegExp(findString, matchCase ? 'g' : 'gi');
    newRow[colId] = matchCell ? replaceString : oldRow[colId].replace(findRegex, replaceString);

    context.pushUndo(props.api, [oldRow]);
    const rowIndex = context.rowData.findIndex((r) => r.id === row.id);
    context.rowData[rowIndex] = newRow;
    props.api.setRowData(context.rowData);
    highlightNextResult();
  };

  return (
    <Box m={2} mr={4}>
      <Button onClick={reset}>Reset</Button>
      <TextField
        placeholder="Find.."
        autoFocus={false}
        className={classes.textfield}
        value={findString}
        onChange={(e) => {
          setFindString(e.target.value);
        }}
      />
      <Button className={classes.button} startIcon={<SearchIcon />} onClick={onFind}>
        Find
      </Button>
      <TextField
        placeholder="Replace With.."
        className={classes.textfield}
        value={replaceString}
        autoFocus={false}
        onChange={(e) => {
          setReplaceString(e.target.value);
        }}
      />
      <Button className={classes.button} startIcon={<FindReplaceIcon />} onClick={onReplace} disabled={currentFindString.length < 1}>
        Replace
      </Button>
      <FormControlLabel
        label="Match case"
        className={classes.checkbox}
        control={<Checkbox disabled={inProgress} checked={matchCase} onChange={(e) => setMatchCase(e.target.checked)} />}
      />
      <FormControlLabel
        label="Only this column"
        className={classes.checkbox}
        control={<Checkbox disabled={inProgress} checked={matchColumn} onChange={(e) => setMatchColumn(e.target.checked)} />}
      />
      <FormControlLabel
        label="Match whole cell"
        className={classes.checkbox}
        control={<Checkbox disabled={inProgress} checked={matchCell} onChange={(e) => setMatchCell(e.target.checked)} />}
      />
      <Box className={classes.status}>{status}</Box>
    </Box>
  );
};

FindReplacePanel.propTypes = {
  api: PropTypes.any,
  agGridReact: PropTypes.any
};

export default FindReplacePanel;
