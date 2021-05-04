import React, {useEffect, useRef, useState} from 'react';
import {PropTypes} from 'prop-types';
import {useHotkeys} from 'react-hotkeys-hook';
import {Search as SearchIcon, FindReplace as FindReplaceIcon, Undo as UndoIcon} from '@material-ui/icons/';
import {Box, Button, Checkbox, FormControlLabel, Grid, makeStyles, Paper, TextField} from '@material-ui/core';

const useStyles = makeStyles(() => {
  return {
    status: {paddingLeft: '20px', fontStyle: 'italic'},
    button: {width: '100%', marginTop: '8px'},
    checkbox: {marginTop: '4px', marginLeft: '10px'},
    textfield: {width: '100%', paddingLeft: '10px'}
  };
});

const GridFindReplace = ({gridApi, onRowsChanged, onSelectionChanged}) => {
  const classes = useStyles();
  const [doUndo, setDoUndo] = useState(false);
  const [undoStack, setUndoStack] = useState([]);
  const [searchString, setSearchString] = useState('');
  const [replaceString, setReplaceString] = useState('');
  const [matchCase, setMatchCase] = useState(true);
  const [status, setStatus] = useState('');
  const undoBtnEl = useRef(null);

  useEffect(() => {
    console.log('doUndo:', doUndo);
    if (doUndo) {
      setDoUndo(false);
      undo();
      undoBtnEl.current.focus();
    }
  }, [doUndo]);

  const stringCompare = (source, target) => {
    if (typeof source !== 'string' || typeof target !== 'string' || source.length === 0 || target.length === 0) return -1;
    if (matchCase) return source.indexOf(target);
    else return source.toUpperCase().indexOf(target.toUpperCase());
  };

  const find = () => {
    let count = 0;
    gridApi.forEachNode((node) => {
      node.data.selected = [];
      for (let p in node.data) {
        const idx = stringCompare(node.data[p].toString(), searchString);
        if (idx >= 0) {
          node.data.selected.push({key: p, value: {idx: idx, length: searchString.length}});
          count++;
        }
      }
    });
    setStatus(count > 0 ? `${count} cell(s) found.` : 'No cells found.');
    if (onSelectionChanged) onSelectionChanged();
  };

  const replace = () => {
    let updatedNodes = [];
    const ts = +new Date();
    gridApi.forEachNode((node) => {
      node.data.selected = [];
      node.data.undo = node.data.undo ? [...node.data.undo] : [];
      for (let p in node.data) {
        let didUpdate = false;
        if (stringCompare(node.data[p], searchString) >= 0) {
          node.data.undo.push({key: p, data: node.data[p], ts: ts});
          node.data.selected?.splice(node.data.selected.indexOf(p), 1);
          const value = node.data[p];
          const idx = stringCompare(value, searchString);
          if (idx >= 0) {
            didUpdate = true;
            const prefix = value.substr(0, idx);
            const suffix = value.substr(idx + searchString.length);
            node.data[p] = prefix + replaceString + suffix;
            setUndoStack([...undoStack, ts]);
          }
        }
        if (didUpdate) {
          node.data.errors = [];
          updatedNodes.push(node.data);
        }
      }
    });
    gridApi.applyTransaction({update: updatedNodes});
    if (onRowsChanged) onRowsChanged(updatedNodes);
    setStatus(updatedNodes.length > 0 ? `All done. We made ${updatedNodes.length} replacements.` : 'Cannot find anything to replace.');
  };

  const undo = () => {
    let updatedNodes = [];
    const undoId = undoStack.pop();
    setUndoStack([...undoStack]);
    gridApi.forEachNode((node) => {
      const rowUndos = node.data.undo ? [...node.data.undo] : [];
      let didUpdate = false;
      for (let u in rowUndos) {
        let undo = rowUndos[u];
        if (undo.ts === undoId) {
          didUpdate = true;
          node.data[undo.key] = undo.data;
          node.data.selected?.splice(node.data.selected.indexOf(undo.key), 1);
          node.data.undo.splice(u, 1);
        }
      }
      if (didUpdate) {
        node.data.errors = [];
        updatedNodes.push(node.data);
      }
    });
    gridApi.applyTransaction({update: updatedNodes});
    if (onRowsChanged) onRowsChanged(updatedNodes);
    setStatus('');
  };

  useHotkeys('ctrl+z', () => {
    setDoUndo(true);
  });

  return (
    <Paper pl={4}>
      <Grid container spacing={2}>
        <Grid item xs={4}>
          <TextField className={classes.textfield} onChange={(e) => setSearchString(e.target.value)} placeholder="Find.." />
        </Grid>
        <Grid item xs={2}>
          <Button className={classes.button} variant="contained" startIcon={<SearchIcon />} onClick={find} color="primary">
            Find All
          </Button>
        </Grid>
        <Grid item xs={5} className={classes.checkbox}>
          <FormControlLabel
            control={
              <Checkbox
                checked={matchCase}
                onChange={(e) => {
                  setMatchCase(e.target.checked);
                }}
              />
            }
            label="Match case"
          />
        </Grid>
        <Grid item xs={4}>
          <TextField className={classes.textfield} onChange={(e) => setReplaceString(e.target.value)} placeholder="Replace With.." />
        </Grid>
        <Grid item xs={2}>
          <Button className={classes.button} variant="contained" startIcon={<FindReplaceIcon />} onClick={replace} color="primary">
            Replace All
          </Button>
        </Grid>
        <Grid item xs={2}>
          <Button
            ref={undoBtnEl}
            className={classes.button}
            disabled={undoStack.length === 0}
            variant="contained"
            startIcon={<UndoIcon />}
            onClick={undo}
            color="primary"
          >
            Undo
          </Button>
        </Grid>
        <Grid item xs={12}>
          <Box className={classes.status}>{status}</Box>
        </Grid>
      </Grid>
    </Paper>
  );
};

export default GridFindReplace;

GridFindReplace.propTypes = {
  gridApi: PropTypes.object.isRequired,
  onSelectionChanged: PropTypes.func,
  onRowsChanged: PropTypes.func
};
