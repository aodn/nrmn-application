import React, {useState} from 'react';
import {useDispatch, useSelector} from 'react-redux';
import {AgGridReact} from 'ag-grid-react';
import {AllModules} from 'ag-grid-enterprise';
import {useEffect} from 'react';
import {
  AddRowIndex,
  exportRow,
  JobFinished,
  RowUpdateRequested,
  SubmitingestRequested,
  ValidationRequested,
  ValidationFinished
} from './reducers/create-import';
import {ColumnDef, ExtendedSize} from './ColumnDef';
import {Box, ButtonGroup, Fab, makeStyles} from '@material-ui/core';
import useWindowSize from '../utils/useWindowSize';
import {ChangeDetectionStrategyType} from 'ag-grid-react/lib/changeDetectionService';
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

Object.unfreeze = function (o) {
  var oo = undefined;
  if (o instanceof Array) {
    oo = [];
    var clone = function (v) {
      oo.push(v);
    };
    o.forEach(clone);
  } else if (o instanceof String) {
    oo = new String(o).toString();
  } else if (typeof o == 'object') {
    oo = {};
    for (var property in o) {
      oo[property] = o[property];
    }
  }
  return oo;
};

const DataSheetView = () => {
  const classes = useStyles();
  const dispatch = useDispatch();
  const errSelected = useSelector((state) => state.import.errSelected);
  const [gridApi, setGridApi] = useState(null);
  const job = useSelector((state) => state.import.job);
  const colDefinition = job && job.isExtendedSize ? ColumnDef.concat(ExtendedSize) : ColumnDef;
  const enableSubmit = useSelector((state) => state.import.enableSubmit);
  const indexMap = useSelector((state) => state.import.indexChanged);
  const enableSaved = Object.keys(indexMap || {}).length > 0;
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
  };

  const agGridReady = (params) => {
    setGridApi(params.api);
    getDataJob(job.id).then((res) => {
      if (res.data.rows && res.data.rows.length > 0) {
        const rowsTmp = res.data.rows.map((row) => exportRow(row));
        params.api.setRowData(rowsTmp);
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
          params.api.applyTransaction({remove: selectedRows});
        },
        cssClasses: ['redBoldFont']
      }
    ];
  };

  const onKeyDown = (evt) => {
    if (gridApi && evt.key == 'x' && (evt.ctrlKey || evt.metaKey)) {
     // const cells = gridApi.getCellRanges();
      gridApi.copySelectedRangeToClipboard();
     // const fields = cells[0].columns.map((col) => col.colId);
      // for (let i = cells[0].startRow.rowIndex; i <= cells[0].endRow.rowIndex; i++) {
        // const row = gridApi.getRowNode(rows[i].id);
        // fields.forEach((field) => {
        //   row.setDataValue(field, '');
        // });
     // }
    }
  };

  const onCellChanged = (evt) => {
    dispatch(AddRowIndex({id: evt.rowIndex, row: evt.data}));
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
      const instance = gridApi.getFilterInstance('id');
      instance.setModel({values: errSelected.ids.map((id) => id.toString())}).then(() => gridApi.onFilterChanged());
    }

    if (errSelected.ids === null) {
      const instance = gridApi.getFilterInstance('id');
      instance.setModel(null).then(() => gridApi.onFilterChanged());
    }
  });

  const size = useWindowSize();
  return (
    <Box mt={2}>
      <ButtonGroup disableElevation spacing={2} size="small" variant="text" aria-label="small outlined button group">
        <Fab className={classes.fab} onClick={handleSave} disabled={!enableSaved} variant="extended" size="small" color="primary">
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
          style={{height: size.height - 165, width: '100%', marginTop: 25}}
          className={'ag-theme-material'}
        >
          <AgGridReact
            getRowNodeId={(data) => data.id}
            rowDataChangeDetectionStrategy={ChangeDetectionStrategyType.IdentityCheck}
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
        </div>
    </Box>
  );
};

export default DataSheetView;
