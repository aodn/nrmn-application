import React, {useState} from 'react';
import {Box, Button, Paper, Typography, makeStyles} from '@material-ui/core';
import {
  CloudUpload as CloudUploadIcon,
  CloudDownload as CloudDownloadIcon,
  PlaylistAddCheckOutlined as PlaylistAddCheckOutlinedIcon
} from '@material-ui/icons/';
import {red, orange, yellow, grey, lightGreen} from '@material-ui/core/colors';
import {NavLink} from 'react-router-dom';
import 'ag-grid-community/dist/styles/ag-grid.css';
import 'ag-grid-community/dist/styles/ag-theme-material.css';
import {AgGridColumn, AgGridReact} from 'ag-grid-react';
import 'ag-grid-enterprise';
import {PropTypes} from 'prop-types';
import {getDataJob, validateJob, updateRows, submitIngest} from '../../axios/api';
import {measurements, measureKey} from '../../constants';
import FindReplacePanel from './panel/FindReplacePanel';
import ValidationPanel from './panel/ValidationPanel';
import LinearProgressWithLabel from '../ui/LinearProgressWithLabel';
import AlertDialog from '../ui/AlertDialog';

const useStyles = makeStyles(() => {
  return {
    fishSize: {
      color: red[200],
      borderBottom: '1px solid'
    },
    invertSize: {
      color: lightGreen[200]
    }
  };
});

const LoadingOverlay = (e) => {
  const ctx = e.api.gridOptionsWrapper.gridOptions.context;
  return (
    <Box component={Paper} width={500} p={3}>
      <LinearProgressWithLabel determinate={false} label={`${ctx.useOverlay}...`} />
    </Box>
  );
};

// |delta| is an array of rowData
const pushUndo = (api, delta) => {
  const ctx = api.gridOptionsWrapper.gridOptions.context;
  ctx.undoStack.push(
    delta.map((d) => {
      ctx.putRowIds.push(d.id);
      return {...d};
    })
  );
};

const popUndo = (api) => {
  const ctx = api.gridOptionsWrapper.gridOptions.context;
  const deltaSet = ctx.undoStack.pop();
  let rowData = ctx.rowData;
  for (const deltaIdx in deltaSet) {
    const deltaId = deltaSet[deltaIdx].id;
    ctx.putRowIds.push(deltaId);
    const rowIdx = rowData.findIndex((d) => d.id === deltaId);
    if (Object.keys(deltaSet[deltaIdx]).length < 2) {
      rowData.splice(rowIdx, 1);
    } else {
      if (rowIdx < 0) {
        rowData.push(deltaSet[deltaIdx]);
      } else {
        rowData[rowIdx] = deltaSet[deltaIdx];
      }
    }
  }
  api.setRowData(rowData);
};

// |context| is where all custom properties and helper functions
// associated with the ag-grid are stored
// see: https://www.ag-grid.com/javascript-grid/context/
const context = {
  useOverlay: 'Loading',
  rowData: [],
  highlighted: [],
  putRowIds: [],
  undoStack: [],
  summaries: [],
  errors: [],
  globalErrors: [],
  pushUndo: pushUndo,
  popUndo: popUndo,

  // paste operations must be done row-by-row. build up |pendingPasteUndo|
  // while in paste mode, then when onPasteEnd is called then call pushUndo
  pendingPasteUndo: [],
  pasteMode: false
};

const defaultSideBar = {
  toolPanels: [
    {
      id: 'findReplace',
      labelDefault: 'Find Replace',
      labelKey: 'findReplace',
      iconKey: 'columns',
      toolPanel: 'findReplacePanel'
    },
    {
      id: 'columns',
      labelDefault: 'Pivot',
      labelKey: 'columns',
      iconKey: 'columns',
      toolPanel: 'agColumnsToolPanel'
    }
  ],
  defaultToolPanel: ''
};

const IngestState = Object.freeze({Loading: 0, Edited: 1, Valid: 2, ConfirmSubmit: 3});

const DataSheetView = ({jobId, onIngest}) => {
  const classes = useStyles();
  const [job, setJob] = useState({});
  const [gridApi, setGridApi] = useState(null);
  const [state, setState] = useState(IngestState.Loading);
  const [sideBar, setSideBar] = useState(defaultSideBar);

  const handleValidate = () => {
    setState(IngestState.Loading);
    context.useOverlay = 'Validating';
    setSideBar(defaultSideBar);
    gridApi.showLoadingOverlay();
    gridApi.setFilterModel(null);
    context.errors = [];
    context.globalErrors = [];
    validateJob(jobId, (result) => {
      context.validationResults = result.data;

      result.data.errors.reduce((a, e) => {
        e.errors.forEach((err) => {
          if (err.columnTarget.includes(',')) {
            const subErrors = err.columnTarget.split(',');
            subErrors.forEach((subCol) => {
              a.push({row: e.id, column: subCol, message: err.message, type: err.errorLevel});
            });
          } else {
            a.push({row: e.id, column: err.columnTarget, message: err.message, type: err.errorLevel});
          }
        });
        return a;
      }, context.errors);

      result.data.errorGlobal.reduce((a, e) => {
        const rowData = {row: e.rowId, columnTarget: '', message: e.message, level: e.errorLevel};
        a[e.columnTarget]?.length > 0 ? a[e.columnTarget].push(rowData) : (a[e.columnTarget] = [rowData]);
        context.globalErrors.push({rowId: e.rowId, type: e.errorLevel, message: e.message});
        return a;
      }, result.data.summaries);

      if (!context.errors.some((e) => e.type === 'BLOCKING')) {
        setState(IngestState.Valid);
      }

      context.summaries = result.data.summaries;
      gridApi.hideOverlay();
      gridApi.redrawRows();

      setSideBar((sideBar) => {
        return {
          defaultToolPanel: 'validation',
          toolPanels: [
            {
              id: 'validation',
              labelDefault: 'Validation',
              labelKey: 'validation',
              iconKey: 'columns',
              toolPanel: 'validationPanel'
            },
            ...sideBar.toolPanels
          ]
        };
      });
    });
  };

  const handleSubmit = () => {
    setState(IngestState.Loading);
    setSideBar(defaultSideBar);
    context.useOverlay = 'Submitting';
    gridApi.showLoadingOverlay();
    submitIngest(
      jobId,
      (res) => onIngest({success: res}),
      (err) => onIngest({error: err})
    );
  };

  const handleSaveAndValidate = () => {
    setState(IngestState.Loading);
    setSideBar(defaultSideBar);
    context.useOverlay = 'Saving';
    gridApi.showLoadingOverlay();
    const rowUpdateDtos = [];
    let fullRefresh = false;

    Array.from(new Set(context.putRowIds)).forEach((rowId) => {
      const row = context.rowData.find((r) => r.id === rowId);

      // Serialise the measure JSON
      if (row) {
        let measure = {};
        Object.getOwnPropertyNames(row || {})
          .filter((key) => !isNaN(parseFloat(key)))
          .forEach((numKey) => {
            const pos = measureKey.indexOf(numKey);
            measure[pos] = row[numKey];
          });
        row.measureJson = measure;
      }

      // Null row data means that the row is to be deleted
      rowUpdateDtos.push({rowId: rowId, row: row});

      // HACK: use the fact that new rows are assigned a very high string
      // to determine if we need a full reload to get the server-assigned
      // row id. A better way would be to do a full reload based on a server
      // response.
      fullRefresh = rowId.toString().length > 10;
    });
    updateRows(jobId, rowUpdateDtos, () => {
      if (fullRefresh) {
        reload(gridApi, jobId, handleValidate);
      } else {
        handleValidate();
      }
    });
  };

  const onPasteStart = (e) => {
    e.api.gridOptionsWrapper.gridOptions.context.pasteMode = true;
  };

  const onCellValueChanged = (e) => {
    if (e.context.pasteMode) {
      e.context.pendingPasteUndo.push({id: e.data.id, field: e.colDef.field, value: e.oldValue});
    }
  };

  const onPasteEnd = (e) => {
    const ctx = e.api.gridOptionsWrapper.gridOptions.context;
    ctx.pasteMode = false;
    let oldRows = [];
    Array.from(new Set(ctx.pendingPasteUndo.map((u) => u.id))).forEach((id) => {
      let oldRow = {};
      let rowData = ctx.rowData;
      const newRow = rowData.find((r) => r.id === id);
      Object.keys(newRow).forEach(function (key) {
        oldRow[key] = newRow[key];
      });
      ctx.pendingPasteUndo
        .filter((u) => u.id === id)
        .forEach((p) => {
          const field = p.field;
          oldRow[field] = p.value;
        });
      oldRows.push(oldRow);
    });
    ctx.pushUndo(e.api, [...oldRows]);
    ctx.pendingPasteUndo = [];
  };

  const onCopyRegion = (e) => {
    e.api.copySelectedRangeToClipboard();
  };

  const getContextMenuItems = (e) => {
    const [cells] = e.api.getCellRanges();
    const colId = cells.startColumn.colId;
    const row = e.api.getDisplayedRowAtIndex(cells.startRow.rowIndex);
    const label = row.data[colId];

    let rowData = e.context.rowData;
    const items = [];

    if (label) {
      items.push({
        name: `Fill with '${label}'`,
        action: () => fillRegion(e, label)
      });
    }

    if (cells.startRow.rowIndex === cells.endRow.rowIndex && Object.keys(e.api.getFilterModel()).length < 1) {
      if (items.length > 0) items.push('separator');
      items.push({
        name: 'Delete Row',
        action: () => deleteRow(e)
      });
      items.push({
        name: 'Clone Row',
        action: () => {
          const [cells] = e.api.getCellRanges();
          const row = e.api.getDisplayedRowAtIndex(cells.startRow.rowIndex);
          const data = rowData.find((d) => d.id == row.data.id);
          const newId = +new Date().valueOf();
          const newData = {...data, id: newId, pos: data.pos + 1};
          pushUndo(e.api, [{id: newId}]);
          rowData.push(newData);
          e.api.setRowData(rowData);
          e.api.refreshCells();
        }
      });
    } else {
      if (items.length > 0) items.push('separator');
      items.push({
        name: 'Delete Selected Rows',
        action: () => deleteRow(e)
      });
    }
    return items;
  };

  const fillRegion = (e, fill) => {
    const rowData = e.context.rowData;
    const [cells] = e.api.getCellRanges();
    const fields = cells.columns.map((col) => col.colId);
    const delta = [];
    const startIdx = Math.min(cells.startRow.rowIndex, cells.endRow.rowIndex);
    const endIdx = Math.max(cells.startRow.rowIndex, cells.endRow.rowIndex);
    for (let i = startIdx; i < endIdx + 1; i++) {
      const row = e.api.getDisplayedRowAtIndex(i);
      const dataIdx = rowData.findIndex((d) => d.id == row.data.id);
      const data = {...rowData[dataIdx]};
      delta.push(data);
      let newData = {};
      Object.keys(data).forEach(function (key) {
        newData[key] = data[key];
      });
      fields.forEach((key) => {
        if (key != 'pos') {
          newData[key] = fill;
        }
      });
      rowData[dataIdx] = newData;
    }
    pushUndo(e.api, delta);
    e.api.setRowData(rowData);
  };

  const onClearRegion = (e) => fillRegion(e, '');

  const onCutRegion = (e) => {
    onCopyRegion(e);
    onClearRegion(e);
  };

  const onUndo = (e) => {
    popUndo(e.api);
    e.api.refreshCells();
  };

  const onCellEditingStopped = (e) => {
    if (e.oldValue === e.newValue) return;
    const row = {...e.data};
    row[e.column.colId] = e.oldValue;
    pushUndo(e.api, [row]);
    setState(IngestState.Edited);
  };

  const overrideKeyboardEvents = (e) => {
    if (e.event.key === 'Delete') {
      if (e.event.type === 'keydown') onClearRegion(e);
      return true;
    }

    if (e.event.ctrlKey || e.event.metaKey) {
      if (e.event.key === 'c' && e.event.type === 'keydown') {
        onCopyRegion(e);
        return true;
      }
      if (e.event.key === 'x' && e.event.type === 'keydown') {
        onCutRegion(e);
        return true;
      }
      if (e.event.key === 'z' && e.event.type === 'keydown') {
        onUndo(e);
        return true;
      }
      return false;
    }
  };

  const reload = (api, jobId, completion) => {
    getDataJob(jobId).then((res) => {
      const job = {
        program: res.data.job.program.programName,
        reference: res.data.job.reference,
        isExtendedSize: res.data.job.isExtendedSize,
        source: res.data.job.source,
        status: res.data.job.status
      };
      if (res.data.rows && res.data.rows.length > 0) {
        const rowData = res.data.rows.map((row) => {
          const {measureJson} = {...row};
          Object.getOwnPropertyNames(measureJson || {}).forEach((numKey) => {
            row[measureKey[numKey]] = measureJson[numKey];
          });
          delete row.measureJson;
          return row;
        });
        api.gridOptionsWrapper.gridOptions.context.rowData = rowData;
        api.setRowData(rowData);
      }
      setJob(job);
      if (completion) completion();
    });
  };

  const onGridReady = (p) => {
    setGridApi(p.api);
    reload(p.api, jobId, () => setState(IngestState.Edited));
  };

  const onFirstDataRendered = (e) => {
    // HACK: workaround ag-grid bug preventing consistent column auto-sizing
    // see https://github.com/ag-grid/ag-grid/issues/2662
    setTimeout(() => {
      e.columnApi.autoSizeAllColumns();
    }, 25);
  };

  const chooseCellStyle = (params) => {
    // Highlight global validations
    const global = params.context.globalErrors.find((g) => g.rowId === params.data.id);
    if (global) {
      let color = params.colDef.field === 'row' ? grey[500] : grey[900];
      if (global.type === 'BLOCKING') return {color: color, backgroundColor: red[100]};
      if (global.type === 'WARNING') return {color: color, backgroundColor: orange[100]};
    }

    // Grey-out the first  column containing the row number
    if (params.colDef.field === 'row') return {color: grey[500]};

    // Highlight and search results
    const row = params.context.highlighted[params.rowIndex];
    if (row && row[params.colDef.field]) return {backgroundColor: yellow[100]};

    // HACK: to work around the fact that invert validation returns the column as an invert size
    // and NOT as the row data column name.
    let fieldName = params.colDef.field.toUpperCase();
    if (params.data && params.data.isInvertSizing && params.data.isInvertSizing.toUpperCase() === 'YES') {
      const invertSizeMap = measurements.find((m) => m.invertSize === params.colDef.field);
      if (invertSizeMap) {
        fieldName = invertSizeMap.field;
      }
    }

    // Highlight cell validations
    const error = params.context.errors.find((e) => e.row === params.data.id && e.column.toUpperCase() === fieldName);
    if (error) {
      if (error.type === 'BLOCKING') return {backgroundColor: red[100]};
      if (error.type === 'WARNING') return {backgroundColor: orange[100]};
    }
    return null;
  };

  const onSortChanged = (e) => {
    e.api.refreshCells();
  };

  const onRowDataUpdated = (e) => {
    const ctx = e.api.gridOptionsWrapper.gridOptions.context;
    if (ctx.putRowIds.length > 0) {
      setState(IngestState.Edited);
    }
  };

  const toolTipValueGetter = (e) => {
    const error =
      e.context.errors.find((r) => r.row === e.data.id && e.column.colId.toUpperCase() === r.column.toUpperCase()) ||
      e.context.globalErrors.find((g) => e.data.id === g.rowId);
    return error?.message;
  };

  const deleteRow = (e) => {
    const rowData = e.context.rowData;
    const rows = [];
    const [cells] = e.api.getCellRanges();
    const startIdx = Math.min(cells.startRow.rowIndex, cells.endRow.rowIndex);
    const endIdx = Math.max(cells.startRow.rowIndex, cells.endRow.rowIndex);
    const delta = [];
    for (let i = startIdx; i < endIdx + 1; i++) {
      const row = e.api.getDisplayedRowAtIndex(i);
      const data = rowData.find((d) => d.id === row.data.id);
      delta.push({...data});
      rowData.splice(rowData.indexOf(data), 1);
      rows.push(row);
    }
    pushUndo(e.api, delta);
    e.api.setRowData(rowData);
    e.api.refreshCells();
  };

  return (
    <>
      <AlertDialog
        open={state === IngestState.ConfirmSubmit}
        text="Submit Sheet?"
        action="Submit"
        onClose={() => setState(IngestState.Valid)}
        onConfirm={handleSubmit}
      />
      <Box pt={1} pl={1}>
        <Box width={200}>
          <NavLink to="/jobs" color="secondary">
            <Typography>{'<< Back to Jobs'}</Typography>
          </NavLink>
        </Box>
        {job && job.status === 'STAGED' && (
          <Box display="flex" flexDirection="row">
            <Box ml={1} p={2} flexGrow={1}>
              <Typography>{`${job.status} ${job.source} ${job.program} ${job.isExtendedSize ? 'Extended Size' : ''}  ${
                job.reference
              } `}</Typography>
            </Box>
            <Box p={1}>
              <Button
                onClick={() => gridApi.exportDataAsExcel({sheetName: 'DATA', author: 'NRMN', fileName: `export_${job.reference}`})}
                startIcon={<CloudDownloadIcon />}
              >
                Export to Excel
              </Button>
            </Box>
            <Box p={1}>
              <Button onClick={handleSaveAndValidate} startIcon={<PlaylistAddCheckOutlinedIcon />}>
                {`Save & Validate`}
              </Button>
            </Box>
            <Box p={1}>
              <Button
                disabled={state !== IngestState.Valid}
                onClick={() => setState(IngestState.ConfirmSubmit)}
                startIcon={<CloudUploadIcon />}
              >
                Submit
              </Button>
            </Box>
          </Box>
        )}
      </Box>
      <Box flexGrow={1} overflow="hidden" className="ag-theme-material" id="validation-grid">
        <AgGridReact
          getRowNodeId={(r) => r.id}
          className={classes.agGrid}
          context={context}
          immutableData={true}
          cellFlashDelay={100}
          cellFadeDelay={100}
          defaultColDef={{
            editable: true,
            sortable: true,
            resizable: true,
            flex: 1,
            minWidth: 80,
            filter: true,
            floatingFilter: true,
            suppressMenu: true,
            suppressKeyboardEvent: overrideKeyboardEvents,
            cellStyle: chooseCellStyle,
            enableCellChangeFlash: true,
            tooltipValueGetter: toolTipValueGetter
          }}
          rowHeight={20}
          enableBrowserTooltips
          enableRangeSelection={true}
          animateRows={true}
          enableRangeHandle={true}
          onPasteStart={onPasteStart}
          onPasteEnd={onPasteEnd}
          onCellValueChanged={onCellValueChanged}
          onSortChanged={onSortChanged}
          onFilterChanged={onSortChanged}
          onRowDataUpdated={onRowDataUpdated}
          fillHandleDirection="y"
          getContextMenuItems={getContextMenuItems}
          undoRedoCellEditing={false}
          onCellEditingStopped={onCellEditingStopped}
          frameworkComponents={{
            validationPanel: ValidationPanel,
            findReplacePanel: FindReplacePanel,
            loadingOverlay: LoadingOverlay
          }}
          loadingOverlayComponent="loadingOverlay"
          pivotMode={false}
          pivotColumnGroupTotals="before"
          sideBar={sideBar}
          onGridReady={onGridReady}
          onFirstDataRendered={onFirstDataRendered}
        >
          <AgGridColumn field="id" editable={false} hide={true} />
          <AgGridColumn field="pos" editable={false} hide={true} sort="asc" />
          <AgGridColumn
            field="row"
            headerName=""
            suppressMovable={true}
            editable={false}
            valueGetter="node.rowIndex + 1"
            minWidth={40}
            enableCellChangeFlash={false}
            filter={false}
            sortable={false}
          />
          <AgGridColumn field="diver" headerName="Diver" pivot={true} enablePivot={false} />
          <AgGridColumn field="buddy" headerName="Buddy" />
          <AgGridColumn field="siteCode" headerName="Site Code" rowGroup={false} enableRowGroup={true} />
          <AgGridColumn field="siteName" headerName="Site Name" minWidth={160} />
          <AgGridColumn field="latitude" headerName="Latitude" />
          <AgGridColumn field="longitude" headerName="Longitude" />
          <AgGridColumn field="date" headerName="Date" rowGroup={false} enableRowGroup={true} />
          <AgGridColumn field="vis" headerName="Vis" />
          <AgGridColumn field="direction" headerName="Direction" />
          <AgGridColumn field="time" headerName="Time" />
          <AgGridColumn field="P-Qs" headerName="P-Qs" />
          <AgGridColumn field="depth" headerName="Depth" rowGroup={false} enableRowGroup={true} />
          <AgGridColumn field="method" headerName="Method" rowGroup={false} enableRowGroup={true} />
          <AgGridColumn field="block" headerName="Block" rowGroup={false} enableRowGroup={true} />
          <AgGridColumn field="code" headerName="Code" />
          <AgGridColumn field="species" headerName="Species" pivot={true} enablePivot={false} />
          <AgGridColumn field="commonName" headerName="Common Name" />
          <AgGridColumn field="total" headerName="Total" aggFunc="count" />
          <AgGridColumn field="inverts" headerName="Inverts" />
          {measurements.map((m) => (
            <AgGridColumn
              field={m.field}
              key={m.field}
              editable={true}
              width={35}
              headerComponentParams={{
                template: `<div style="width: 48px; float: left; text-align:center"><div style="color: #c4d79b; border-bottom: 1px solid rgba(0, 0, 0, 0.12)">${m.fishSize}</div><div style="color: #da9694">${m.invertSize}</div></div>`
              }}
            />
          ))}
          {job.isExtendedSize &&
            measurements.map((m) => (
              <AgGridColumn
                field={m.field}
                key={m.field}
                editable={true}
                width={35}
                headerComponentParams={{
                  template: `<div style="width: 48px; float: left; text-align:center"><div style="color: #c4d79b; border-bottom: 1px solid rgba(0, 0, 0, 0.12)">${m.fishSize}</div><div style="color: #da9694">${m.invertSize}</div></div>`
                }}
              />
            )) && <AgGridColumn field="isInvertSizing" headerName="Use Invert Sizing" />}
        </AgGridReact>
      </Box>
    </>
  );
};

DataSheetView.propTypes = {
  jobId: PropTypes.string.required,
  onIngest: PropTypes.func.required
};

export default DataSheetView;
