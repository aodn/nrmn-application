import React, {useState} from 'react';
import {useDispatch, useSelector} from 'react-redux';
import {AgGridReact} from 'ag-grid-react';
import {AllModules} from 'ag-grid-enterprise';
import {useEffect} from 'react';
import {
  exportRow,
  JobFinished,
  RowUpdateRequested,
  SubmitingestRequested,
  ValidationRequested,
  RowDeleteRequested,
  ValidationFinished
} from './reducers/create-import';
import {ColumnDef, ExtendedSize} from './ColumnDef';
import {Box, ButtonGroup, Fab, makeStyles, Dialog, TextField, DialogTitle, DialogActions, DialogContent, Button} from '@material-ui/core';
import useWindowSize from '../utils/useWindowSize';
import {getDataJob} from '../../axios/api';
import CloudUploadIcon from '@material-ui/icons/CloudUpload';
import PlaylistAddCheckOutlinedIcon from '@material-ui/icons/PlaylistAddCheckOutlined';
import SaveOutlinedIcon from '@material-ui/icons/SaveOutlined';

const useStyles = makeStyles((theme) => {
  return {
    root: {
      display: 'flex',
      '& > *': {
        margin: theme.spacing(1)
      }
    },
    hide: {
      display: 'none'
    },
    extendedIcon: {
      marginRight: theme.spacing(1)
    },
    fab: {
      marginRight: theme.spacing(2)
    }
  };
});

const DataSheetView = () => {
  const classes = useStyles();
  const dispatch = useDispatch();
  const errSelected = useSelector((state) => state.import.errSelected);
  const [gridApi, setGridApi] = useState(null);
  const job = useSelector((state) => state.import.job);
  const colDefinition = job && job.isExtendedSize ? ColumnDef.concat(ExtendedSize) : ColumnDef;
  const enableSubmit = useSelector((state) => state.import.enableSubmit);
  const [indexMap, setIndexMap] = useState({});
  const [canSaved, setCanSaved] = useState(true);
  const [addDialog, setAddDialog] = useState({open: false, rowIndex: -1, number: 1, lastId: 0});

  const validationErrors = useSelector((state) => state.import.validationErrors);

  const handleValidate = () => {
    if (job.id) {
      dispatch(ValidationRequested(job.id));
    }
  };

  const handleSubmit = () => {
    if (job.id) {
      dispatch(SubmitingestRequested(job.id));
    }
  };

  const handleSave = () => {
    dispatch(RowUpdateRequested({jobId: job.id, rows: indexMap}));
    setCanSaved(false);
  };

  const agGridReady = (params) => {
    setGridApi(params.api);
    getDataJob(job.id).then((res) => {
      if (res.data.rows && res.data.rows.length > 0) {
        const rowsTmp = res.data.rows.map((row) => {
          return exportRow(row);
        });
        const lastId = rowsTmp[rowsTmp.length - 1].id;

        setAddDialog((prevDialog) => {
          return {...prevDialog, lastId: lastId};
        });

        params.api.setRowData([...rowsTmp]);
        var allColumnIds = [];
        params.columnApi.getAllColumns().forEach(function (column) {
          allColumnIds.push(column.colId);
        });
        params.columnApi.autoSizeColumns(allColumnIds, true);
        dispatch(JobFinished());
      }
    });
  };

  const getContextMenuItems = (params) => {
    return [
      {
        name: 'Delete selected Row(s)',
        action: () => {
          const selectedRows = params.api.getSelectedRows();
          const indexes = params.api.getSelectedNodes().map((n) => n.childIndex);
          dispatch(RowDeleteRequested({id: job.id, rows: selectedRows, indexes: indexes}));
          params.api.applyTransaction({remove: selectedRows});
        },
        cssClasses: ['redBoldFont']
      },
      {
        name: 'Add row(s)',
        action: () => {
          setAddDialog((preDialog) => ({...preDialog, open: true, rowIndex: params.node.data.pos}));
        }
      }
    ];
  };

  const handleAdd = () => {
    let toUpdate ={};
    const rowPosUpdated = getAllRows()
      .filter((row) => row.pos >= addDialog.rowIndex)
      .map((row) => {
        toUpdate[row.pos] = {...row, pos: row.pos + addDialog.number};
        return toUpdate[row.pos];
      });
    setIndexMap({...indexMap, ...toUpdate});
    const newLines = [];
    for (let i = 0; i < addDialog.number; i++) {
      newLines.push({id: addDialog.lastId + (i + 1) + '', pos: addDialog.rowIndex + i});
    }
    gridApi.applyTransaction({update: rowPosUpdated});
    gridApi.applyTransaction({add: newLines, addIndex: addDialog.rowIndex});
    setAddDialog({...addDialog, open: false, number: 1, lastId: addDialog.lastId + addDialog.number, rowIndex: -1});
  };

  const handAddleClose = () => {
    setAddDialog({...addDialog, open: false, number: 1, rowIndex: -1});
  };

  const onKeyDown = (evt) => {
    if (gridApi && evt.key == 'x' && (evt.ctrlKey || evt.metaKey)) {
      const [cells] = gridApi.getCellRanges();
      gridApi.copySelectedRangeToClipboard();
      const rows = getAllRows();
      const fields = cells.columns.map((col) => col.colId);
      for (let i = cells.startRow.rowIndex; i <= cells.endRow.rowIndex; i++) {
        const row = rows[i];
        fields.forEach((field) => {
          row[field] = '';
        });
        let toAdd = {};
        toAdd[i] = row;
        gridApi.applyTransaction({update: [row]});
        setIndexMap({...indexMap, ...toAdd});
      }
    }
  };

  const onCellChanged = (evt) => {
    let toAdd = {};
    toAdd[evt.rowIndex] = evt.data;
    setIndexMap({...indexMap, ...toAdd});
    setCanSaved(true);
  };
  const getAllRows = () => {
    let rowData = [];
    gridApi.forEachNode((node) => rowData.push(node.data));
    return rowData;
  };

  useEffect(() => {
    if (gridApi && Object.keys(validationErrors).length > 0) {
      const updatedRows = getAllRows().map((row) => {
        return {...row, errors: validationErrors[row.id] || []};
      });
      gridApi.setRowData(updatedRows);
      dispatch(ValidationFinished());
    }

    if (gridApi && errSelected.ids && errSelected.ids.length > 0) {
      const firstRow = gridApi.getRowNode(errSelected.ids[0]);
      gridApi.ensureIndexVisible(firstRow.rowIndex, 'middle');
      gridApi.deselectAll();
      errSelected.ids.forEach((id) => {
        const row = gridApi.getRowNode(id);
        if (row.isSelected) {
          row.setSelected(true);
        }
        return row;
      });
    }
  });
  const size = useWindowSize();
  return (
    <Box mt={2}>
      <ButtonGroup disableElevation spacing={2} size="small" variant="text" aria-label="small outlined button group">
        <Fab className={classes.fab} onClick={handleSave} disabled={!canSaved} variant="extended" size="small" color="primary">
          <SaveOutlinedIcon className={classes.extendedIcon} />
          Save
        </Fab>
        <Fab className={classes.fab} variant="extended" onClick={() => handleValidate()} size="small" label="Validate" color="secondary">
          <PlaylistAddCheckOutlinedIcon className={classes.extendedIcon} />
          Validate
        </Fab>
        <Fab
          className={classes.fab}
          variant="extended"
          size="small"
          onClick={handleSubmit}
          label="Submit"
          disabled={!enableSubmit}
          color="primary"
        >
          <CloudUploadIcon className={classes.extendedIcon} />
          Submit
        </Fab>
      </ButtonGroup>
      <div
        onKeyDown={onKeyDown}
        id="validation-grid"
        style={{height: size.height - 210, width: '100%', marginTop: 25}}
        className={'ag-theme-material'}
      >
        <AgGridReact
          getRowNodeId={(data) => data.id}
          pivotMode={false}
          pivotColumnGroupTotals={'before'}
          sideBar={true}
          autoGroupColumnDef={{
            width: 20,
            cellRendererParams: {
              suppressCount: true,
              innerRenderer: 'nameCellRenderer'
            }
          }}
          onCellValueChanged={onCellChanged}
          columnDefs={colDefinition}
          groupDefaultExpanded={4}
          rowHeight={18}
          animateRows={true}
          groupMultiAutoColumn={true}
          groupHideOpenParents={true}
          rowSelection="multiple"
          enableRangeSelection={true}
          undoRedoCellEditing={true}
          undoRedoCellEditingLimit={20}
          ensureDomOrder={true}
          defaultColDef={{
            minWidth: 80,
            filter: true,
            sortable: true,
            resizable: true,
            editable: true
          }}
          onGridReady={agGridReady}
          modules={AllModules}
          getContextMenuItems={getContextMenuItems}
        ></AgGridReact>
        <Dialog aria-labelledby="Add Rows Dialogue" open={addDialog.open}>
          <DialogTitle id="form-dialog-title">Add Rows</DialogTitle>
          <DialogContent>
            <TextField
              id="outlined-basic"
              default={addDialog.number}
              onChange={(evt) => setAddDialog({...addDialog, number: parseInt(evt.target.value, 10)})}
              label="Number of rows"
              variant="outlined"
            />
          </DialogContent>
          <DialogActions>
            <Button onClick={handAddleClose} color="primary">
              Cancel
            </Button>
            <Button onClick={handleAdd} color="primary">
              Add
            </Button>
          </DialogActions>{' '}
        </Dialog>
      </div>
    </Box>
  );
};

export default DataSheetView;
