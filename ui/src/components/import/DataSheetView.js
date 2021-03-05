import React, {useState} from 'react';
import {useDispatch, useSelector} from 'react-redux';
import {AgGridReact} from 'ag-grid-react';
import {AllModules} from 'ag-grid-enterprise';
import {useEffect} from 'react';
import {JobFinished, AddRowIndex} from './reducers/create-import';
import {ColumnDef, ExtendedSize} from './ColumnDef';
import {Box, Dialog, TextField} from '@material-ui/core';
import useWindowSize from '../utils/useWindowSize';
import {ChangeDetectionStrategyType} from 'ag-grid-react/lib/changeDetectionService';

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
  const dispatch = useDispatch();
  const rows = useSelector((state) => state.import.rows);
  const errSelected = useSelector((state) => state.import.errSelected);
  const [addDialogueOpen, setAddDialogueOpen] = useState(false);
  const [gridApi, setGridApi] = useState(null);
  const job = useSelector((state) => state.import.job);
  const colDefinition = job && job.isExtendedSize ? ColumnDef.concat(ExtendedSize) : ColumnDef;
  const agGridReady = (params) => {
    setGridApi(params.api);
    dispatch(JobFinished());
    params.api.setRowData(rows);
    var allColumnIds = [];
    params.columnApi.getAllColumns().forEach(function (column) {
      allColumnIds.push(column.colId);
    });
    params.columnApi.autoSizeColumns(allColumnIds, true);
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
      },
      {
        name: 'Add row(s)',
        action: () => {
          //   const selected  = params.api.getSelectedRows();
          console.log(params.node.rowIndex);
          setAddDialogueOpen(true);
        }
      }
    ];
  };

  const handleAdd = (evt) => {
    console.log(evt);
    setAddDialogueOpen(false);
  };

  const onKeyDown = (evt) => {
    if (gridApi && evt.key == 'x' && (evt.ctrlKey || evt.metaKey)) {
      const cells = gridApi.getCellRanges();
      gridApi.copySelectedRangeToClipboard();
      const fields = cells[0].columns.map((col) => col.colId);
      for (let i = cells[0].startRow.rowIndex; i <= cells[0].endRow.rowIndex; i++) {
        const row = gridApi.getRowNode(rows[i].id);
        fields.forEach((field) => {
          row.setDataValue(field, '');
        });
      }
    }

    if (gridApi && evt.key == 'z' && (evt.ctrlKey || evt.metaKey)) {
      gridApi.undoCellEditing();
    }
  };

  useEffect(() => {
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
    <Box>
      {rows && (
        <div
          onKeyDown={onKeyDown}
          id="validation-grid"
          style={{height: size.height - 165, width: '100%', marginTop: 25}}
          className={'ag-theme-material'}
        >
          <AgGridReact
            immutable
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
            rowData={rows}
            defaultColDef={{
              minWidth: 80,
              filter: true,
              sortable: true,
              resizable: true,
              valueSetter: (params) => {
                dispatch(AddRowIndex({id: params.node.childIndex, field: params.colDef.field, value: params.newValue}));
                return false;
              }
            }}
            onGridReady={agGridReady}
            modules={AllModules}
            getContextMenuItems={getContextMenuItems}
          ></AgGridReact>
          <Dialog onClose={handleAdd} aria-labelledby="Add Rows Dialogue" open={addDialogueOpen}>
            <Box p={2}>
              <form noValidate autoComplete="off">
                <TextField id="outlined-basic" label="Number of rows" variant="outlined" />
              </form>
            </Box>
          </Dialog>
        </div>
      )}
    </Box>
  );
};

export default DataSheetView;
