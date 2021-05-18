import React, {useEffect, useState} from 'react';
import {useDispatch, useSelector} from 'react-redux';
import {AgGridReact} from 'ag-grid-react';
import {AllModules} from 'ag-grid-enterprise';
import moment from 'moment';
import Alert from '@material-ui/lab/Alert';
import {Box, Fab, makeStyles, Dialog, TextField, DialogTitle, DialogActions, DialogContent, Button} from '@material-ui/core';
import {
  CloudUpload as CloudUploadIcon,
  FindReplace as FindReplaceIcon,
  SaveOutlined as SaveOutlinedIcon,
  PlaylistAddCheckOutlined as PlaylistAddCheckOutlinedIcon
} from '@material-ui/icons/';
import {
  exportRow,
  JobFinished,
  RowUpdateRequested,
  SubmitingestRequested,
  ValidationRequested,
  RowDeleteRequested,
  ValidationFinished
} from './reducers/create-import';
import useWindowSize from '../utils/useWindowSize';
import {getDataJob} from '../../axios/api';
import GridFindReplace from '../search/GridFindReplace';
import {ColumnDef, ExtendedSize} from './ColumnDef';

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
    fabFlat: {
      marginRight: theme.spacing(2),
      boxShadow: 'none'
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
  const errors = useSelector((state) => state.import.errors);

  const [indexAdd, setIndexAdd] = useState({});
  const [indexDelete, setIndexDelete] = useState({});

  const [canSave, setCanSave] = useState(false);
  const [showFindReplace, setShowFindReplace] = useState(false);
  const [addDialog, setAddDialog] = useState({open: false, rowIndex: -1, number: 1, lastId: 0});

  const validationErrors = useSelector((state) => state.import.validationErrors);
  const [selectedCells, setSelectedCells] = useState([]);
  const globalErrors = useSelector((state) => state.import.globalErrors);
  const globalWarnings = useSelector((state) => state.import.globalWarnings);

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
    gridApi.forEachNode((node) => {
      node.data.selected = [];
    });
    setSelectedCells(true);
    var toSave = Object.values(indexAdd);
    if (toSave.length > 0) {
      dispatch(RowUpdateRequested({jobId: job.id, rows: toSave}));
    }
    var toDelete = Object.values(indexDelete);
    if (toDelete.length > 0) {
      dispatch(RowDeleteRequested({jobId: job.id, rows: toDelete}));
    }
    setCanSave(false);
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
        params.columnApi.autoSizeColumns(allColumnIds, false);
        dispatch(JobFinished());
      }
    });
  };

  const errorAlert =
    errors && errors.length > 0 ? (
      <Box mb={2}>
        <Alert severity="error" variant="filled">
          {errors.map((item, key) => {
            return <div key={key}>{item}</div>;
          })}
        </Alert>
      </Box>
    ) : (
      ''
    );

  const getContextMenuItems = (params) => {
    return [
      {
        name: 'Delete selected Row(s)',
        action: () => {
          const selectedRows = params.api.getSelectedRows();
          var deleteRows = {};
          selectedRows.forEach((drow) => {
            if (indexAdd[drow.id]) {
              var tmp = indexAdd;
              delete tmp[drow.id];
              setIndexAdd(tmp);
            } else {
              deleteRows[drow.id] = drow;
            }
          });

          setIndexDelete({...indexDelete, ...deleteRows});
          setCanSave(true);
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
    let toUpdate = {};
    const rowPosUpdated = getAllRows()
      .filter((row) => row.pos >= addDialog.rowIndex)
      .map((row) => {
        toUpdate[row.id] = {...row, selected: [], pos: row.pos + addDialog.number};
        return toUpdate[row.id];
      });
    const newLines = [];
    const time = moment(new Date().toISOString()).utcOffset(0, false).format('YYYY-DD-MMTHH:mm:ss.SSSZZ');
    for (let i = 0; i < addDialog.number; i++) {
      const newRow = {id: addDialog.lastId + (i + 1) + '', pos: addDialog.rowIndex + i, isNew: true, created: time};
      toUpdate[newRow.id] = newRow;
      newLines.push(newRow);
    }
    setIndexAdd({...indexAdd, ...toUpdate});
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
        toAdd[row.id] = row;
        gridApi.applyTransaction({update: [row]});
        setIndexAdd({...indexAdd, ...toAdd});
      }
    }
  };

  const onCellChanged = (evt) => {
    let toAdd = {};
    toAdd[evt.data.id] = evt.data;
    setIndexAdd({...indexAdd, ...toAdd});
    setCanSave(true);
  };

  const getAllRows = () => {
    let rowData = [];
    gridApi.forEachNode((node) => rowData.push(node.data));
    return rowData;
  };

  useEffect(() => {
    if (gridApi && (Object.keys(validationErrors).length > 0 || selectedCells)) {
      const updatedRows = getAllRows().map((row) => {
        return {...row, errors: validationErrors[row.id] || []};
      });
      gridApi.setRowData(updatedRows);
      dispatch(ValidationFinished());
      setSelectedCells(false);
    }
  }, [validationErrors, selectedCells]);

  useEffect(() => {
    if (gridApi && errSelected.ids && errSelected.ids.length > 0) {
      const firstRow = gridApi.getRowNode(errSelected.ids[0]);
      gridApi.ensureIndexVisible(firstRow.rowIndex, 'middle');
      gridApi.deselectAll();
      errSelected.ids.forEach((id) => {
        const row = gridApi.getRowNode(id);
        row.setSelected(true);
        return row;
      });
      gridApi.columnApi.setColumnVisible('100', false);

    }
  }, [errSelected]);

  const size = useWindowSize();
  return (
    <Box mt={2}>
      {errorAlert}
      {job && job.status == 'STAGED' && (
        <>
          <Box spacing={2} mb={3} size="small" variant="text" aria-label="small outlined button group">
            <Fab
              className={showFindReplace ? classes.fabFlat : classes.fab}
              onClick={() => setShowFindReplace(!showFindReplace)}
              variant="extended"
              size="small"
              color="primary"
            >
              <FindReplaceIcon className={classes.extendedIcon} />
              Find & Replace
            </Fab>
            <Fab
              className={classes.fab}
              onClick={handleSave}
              disabled={canSave ? false : true}
              variant="extended"
              size="small"
              color="primary"
            >
              <SaveOutlinedIcon className={classes.extendedIcon} />
              Save
            </Fab>
            <Fab
              className={classes.fab}
              variant="extended"
              disabled={canSave ? true : false}
              onClick={() => handleValidate()}
              size="small"
              label="Validate"
              color="secondary"
            >
              <PlaylistAddCheckOutlinedIcon className={classes.extendedIcon} />
              Validate
            </Fab>
            <Fab
              className={classes.fab}
              variant="extended"
              size="small"
              onClick={handleSubmit}
              label="Submit"
              disabled={!enableSubmit || canSave}
              color="primary"
            >
              <CloudUploadIcon className={classes.extendedIcon} />
              Submit
            </Fab>
          </Box>
          {showFindReplace && (
            <GridFindReplace
              gridApi={gridApi}
              onSelectionChanged={() => setSelectedCells(true)}
              onRowsChanged={(toAdd) => {
                setIndexAdd({...indexAdd, ...toAdd});
                setCanSave(true);
              }}
            />
          )}
        </>
      )}
      {globalErrors.length > 0 && (
        <Box mt={1}>
          <Alert m={2} severity="error">
            {globalErrors.map((e) => (
              <span key={e.message}>
                {e.errorLevel}: {e.message} <br />
              </span>
            ))}
          </Alert>
        </Box>
      )}
      {globalWarnings.length > 0 && (
        <Box mt={1}>
          <Alert m={2} severity="warning">
            {globalWarnings.map((e) => (
              <span key={e.message}>
                {e.errorLevel}: {e.message} <br />
              </span>
            ))}
          </Alert>
        </Box>
      )}
      <div
        onKeyDown={onKeyDown}
        id="validation-grid"
        style={{height: size.height - 230, width: '100%', marginTop: 25}}
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
          enableFillHandle={true}
          fillHandleDirection="xy"
          ensureDomOrder={true}
          defaultColDef={{
            filter: true,
            sortable: true,
            resizable: true,
            suppressMenu: true
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
              type="number"
              InputLabelProps={{
                shrink: true
              }}
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
