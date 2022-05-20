import React, {useEffect, useState} from 'react';
import {Box, Button, Typography} from '@mui/material';
import {blue, grey, orange, red, yellow} from '@mui/material/colors';
import {
  CloudDownload as CloudDownloadIcon,
  CloudUpload as CloudUploadIcon,
  PlaylistAddCheckOutlined as PlaylistAddCheckOutlinedIcon
} from '@mui/icons-material/';
import UndoIcon from '@mui/icons-material/Undo';
import ResetIcon from '@mui/icons-material/LayersClear';
import {AgGridColumn, AgGridReact} from 'ag-grid-react';
import {PropTypes} from 'prop-types';
import {useParams, NavLink} from 'react-router-dom';
import {getDataJob, submitIngest, updateRows, validateJob} from '../../api/api';
import {extendedMeasurements, measurements} from '../../common/constants';
import LoadingOverlay from '../overlays/LoadingOverlay';
import AlertDialog from '../ui/AlertDialog';
import FindReplacePanel from './panel/FindReplacePanel';
import ValidationPanel from './panel/ValidationPanel';

// |delta| is an array of rowData
const pushUndo = (api, delta) => {
  const ctx = api.gridOptionsWrapper.gridOptions.context;
  ctx.undoStack.push(
    delta.map((d) => {
      ctx.putRowIds.push(d.id);
      return {...d};
    })
  );
  return ctx.undoStack.length;
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
  context.fullRefresh = true;
  api.setRowData(rowData);
  ctx.rowPos = rowData.map((r) => r.pos).sort((a, b) => a - b);
  return ctx.undoStack.length;
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
  summary: [],
  errors: [],
  fullRefresh: false,
  pushUndo: pushUndo,
  popUndo: popUndo,

  // paste operations must be done row-by-row. build up |pendingPasteUndo|
  // while in paste mode, then when onPasteEnd is called then call pushUndo
  pendingPasteUndo: [],
  pasteMode: false
};

const resetContext = () => {
  context.useOverlay = 'Loading';
  context.rowData = [];
  context.rowPos = [];
  context.highlighted = [];
  context.putRowIds = [];
  context.summary = [];
  context.errorList = {};
  context.errors = [];
  context.pendingPasteUndo = [];
  context.focusedRows = [];
  context.pasteMode = false;
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

const generateErrorTree = (rowData, rowPos, errors) => {
  const tree = {blocking: [], warning: [], info: [], duplicate: []};
  errors
    .sort((a, b) => (a.message < b.message ? -1 : a.message > b.message ? 1 : 0))
    .forEach((e) => {
      const rows = rowData.filter((r) => e.rowIds.includes(r.id));
      let summary = [];
      if (e.columnNames && e.categoryId !== 'SPAN') {
        const col = e.columnNames[0];
        summary = rows.reduce((acc, r) => {
          const rowPosition = rowData.find((d) => d.id === r.id)?.pos;
          const rowNumber = rowPos.indexOf(rowPosition) + 1;
          const existingIdx = acc.findIndex((m) => m.columnName === col && m.value === r[col]);
          if (existingIdx >= 0 && isNaN(parseInt(acc[existingIdx].columnName)))
            acc[existingIdx] = {
              columnName: col,
              value: r[col],
              rowIds: [...acc[existingIdx].rowIds, r.id],
              rowNumbers: [...acc[existingIdx].rowNumbers, rowNumber]
            };
          else
            acc.push({columnName: col, value: r[col], rowIds: [r.id], rowNumbers: [rowNumber], isInvertSize: r.isInvertSizing === 'Yes'});
          return acc;
        }, []);
      } else {
        const rowPositions = e.rowIds.map((r) => rowData.find((d) => d.id === r)?.pos).filter((r) => r);
        const rowNumbers = rowPositions.map((r) => rowPos.indexOf(r) + 1);
        summary = [{rowIds: e.rowIds, columnNames: e.columnNames, rowNumbers}];
      }
      tree[e.levelId.toLowerCase()].push({key: `err-${e.id}`, message: e.message, count: e.rowIds.length, description: summary});
    });
  return tree;
};

const IngestState = Object.freeze({Loading: 0, Edited: 1, Valid: 2, ConfirmSubmit: 3});

const DataSheetView = ({onIngest, isAdmin}) => {
  const {id} = useParams();
  const [job, setJob] = useState({});
  const [gridApi, setGridApi] = useState();
  const [isFiltered, setIsFiltered] = useState(false);
  const [undoSize, setUndoSize] = useState(0);
  const [state, setState] = useState(IngestState.Loading);
  const [sideBar, setSideBar] = useState(defaultSideBar);

  useEffect(() => {
    const undoKeyboardHandler = (event) => {
      if (event.ctrlKey && event.key === 'z') {
        event.preventDefault();
        event.stopPropagation();
        onUndo({api: gridApi});
      }
    };
    document.body.addEventListener('keydown', undoKeyboardHandler);
    return () => {
      document.body.removeEventListener('keydown', undoKeyboardHandler);
    };
  });

  useEffect(() => {
    if (gridApi && state === IngestState.Loading) {
      // HACK: workaround ag-grid bug preventing consistent column auto-sizing. See https://github.com/ag-grid/ag-grid/issues/2662.
      // AG Grid version 26 has a 'pure' React grid implementation should remove the need for these imperative calls entirely:
      // https://blog.ag-grid.com/whats-new-in-ag-grid-26/
      setTimeout(() => gridApi.showLoadingOverlay(), 25);
    }
  }, [gridApi, state]);

  useEffect(() => {
    if (gridApi && !isFiltered) gridApi.setFilterModel(null);
  }, [gridApi, isFiltered]);

  useEffect(() => {
    if (gridApi) {
      gridApi.hideOverlay();
      gridApi.redrawRows();
    }
  }, [gridApi, sideBar]);

  const handleValidate = () => {
    context.useOverlay = 'Validating';
    setState(IngestState.Loading);
    setSideBar(defaultSideBar);
    context.errors = [];
    validateJob(id, (result) => {
      context.errors = result.data.errors;
      delete result.data.errors;
      delete result.data.job;
      context.summary = result.data;
      context.errorList = generateErrorTree(context.rowData, context.rowPos, context.errors);

      setState(context.errors.some((e) => e.levelId === 'BLOCKING') ? IngestState.Edited : IngestState.Valid);

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
    context.useOverlay = 'Submitting';
    setState(IngestState.Loading);
    setSideBar(defaultSideBar);
    submitIngest(id, (res) => onIngest(res));
  };

  const handleSaveAndValidate = () => {
    context.useOverlay = 'Saving';
    setState(IngestState.Loading);
    setSideBar(defaultSideBar);

    const rowUpdateDtos = [];

    Array.from(new Set(context.putRowIds)).forEach((rowId) => {
      const row = context.rowData.find((r) => r.id === rowId);

      // Serialise the measure JSON
      if (row) {
        let measure = {};
        Object.getOwnPropertyNames(row || {})
          .filter((key) => !isNaN(parseFloat(key)))
          .forEach((numKey) => {
            measure[numKey] = row[numKey];
          });
        row.measureJson = measure;
      }

      // Null row data means that the row is to be deleted
      rowUpdateDtos.push({rowId: rowId, row: row});

      // HACK: use the fact that new rows are assigned a very high string
      // to determine if we need a full reload to get the server-assigned
      // row id. A better way would be to do a full reload based on a server
      // response.
      context.fullRefresh = context.fullRefresh || rowId.toString().length > 10 || row === null;
    });
    updateRows(id, rowUpdateDtos, () => {
      if (context.fullRefresh) {
        reload(gridApi, id, handleValidate);
        context.fullRefresh = false;
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
    setUndoSize(e.context.undoStack.length);
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
    setUndoSize(ctx.pushUndo(e.api, [...oldRows]));
    ctx.pendingPasteUndo = [];
  };

  const onCopyRegion = (e) => {
    e.api.copySelectedRangeToClipboard();
  };

  const getContextMenuItems = (e) => {
    const [cells] = e.api.getCellRanges();
    if (!cells) return;

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

    const cloneRow = (clearData) => {
      const [cells] = e.api.getCellRanges();
      const row = e.api.getDisplayedRowAtIndex(cells.startRow.rowIndex);
      const data = rowData.find((d) => d.id == row.data.id);
      const newId = +new Date().valueOf();
      const posMap = rowData.map((r) => r.pos).sort((a, b) => a - b);
      const currentPosIdx = posMap.findIndex((p) => p == data.pos);
      let newData = {};
      Object.keys(data).forEach(function (key) {
        newData[key] = clearData ? '' : data[key];
      });
      delete newData.errors;
      newData.pos = posMap[currentPosIdx + 1] ? posMap[currentPosIdx + 1] - 1 : posMap[currentPosIdx] + 1000;
      newData.id = newId;
      pushUndo(e.api, [{id: newId}]);
      rowData.push(newData);
      e.api.setRowData(rowData);
      e.context.rowPos = rowData.map((r) => r.pos).sort((a, b) => a - b);
      const values = e.api.getRenderedNodes().reduce((acc, field) => acc.concat(field.id.toString()), [newId.toString()]);
      e.api.setFilterModel({
        id: {
          type: 'set',
          values: values
        }
      });
    };

    const multiRowsSelected = e.api.getSelectedRows().length > 1 || cells.startRow.rowIndex !== cells.endRow.rowIndex;
    if (!multiRowsSelected) {
      if (items.length > 0) items.push('separator');
      items.push({
        name: 'Delete Row',
        action: () => deleteRow(e)
      });
      items.push({
        name: 'Clone Row',
        action: () => cloneRow(false)
      });
      items.push({
        name: 'Insert Row',
        action: () => cloneRow(true)
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
    if (undoSize < 1) return;
    popUndo(e.api);
    e.api.refreshCells();
  };

  const onCellEditingStopped = (e) => {
    if (e.oldValue === e.newValue) return;
    const row = {...e.data};
    row[e.column.colId] = e.oldValue;
    setUndoSize(pushUndo(e.api, [row]));
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
      return false;
    }
  };

  const reload = (api, id, completion) => {
    resetContext();
    context.isAdmin = isAdmin;
    getDataJob(id).then((res) => {
      const job = {
        program: res.data.job.program.programName,
        reference: res.data.job.reference,
        isExtendedSize: res.data.job.isExtendedSize,
        source: res.data.job.source,
        status: res.data.job.status
      };
      if (res.data.rows) {
        const rowData = res.data.rows.map((row) => {
          const {measureJson} = {...row};
          Object.getOwnPropertyNames(measureJson || {}).forEach((numKey) => {
            row[numKey] = measureJson[numKey];
          });
          delete row.measureJson;
          return row;
        });
        api.gridOptionsWrapper.gridOptions.context.rowData = rowData;
        api.gridOptionsWrapper.gridOptions.context.rowPos = rowData.map((r) => r.pos).sort((a, b) => a - b);
        api.setRowData(rowData.length > 0 ? rowData : null);
      }
      if (completion) completion(job);
    });
  };

  const onGridReady = (p) => {
    setGridApi(p.api);
    reload(p.api, id, (job) => {
      setState(IngestState.Edited);
      setJob(job);
    });
  };

  const chooseCellStyle = (params) => {
    // Grey-out the first  column containing the row number
    if (params.colDef.field === 'row') return {color: grey[500]};

    // Highlight and search results
    const row = params.context.highlighted[params.rowIndex];
    if (row && row[params.colDef.field]) return {backgroundColor: yellow[100]};

    // Highlight cell validations
    const error = params.context.errors.find(
      (e) => e.rowIds.includes(params.data.id) && (!e.columnNames || e.columnNames.includes(params.colDef.field))
    );

    switch (error?.levelId) {
      case 'BLOCKING':
        return {backgroundColor: red[100]};
      case 'WARNING':
        return {backgroundColor: orange[100]};
      case 'DUPLICATE':
        if (context.focusedRows?.includes(params.data.id)) {
          return {backgroundColor: blue[100], fontWeight: 'bold'};
        } else {
          return {backgroundColor: blue[100]};
        }
      case 'INFO':
        return {backgroundColor: grey[100]};
      default:
        return null;
    }
  };

  const onSortChanged = (e) => {
    e.api.refreshCells();
  };

  const onFilterChanged = (e) => {
    e.api.refreshCells();
    const filterModel = e.api.getFilterModel();
    setIsFiltered(Object.getOwnPropertyNames(filterModel).length > 0);
  };

  const onRowDataUpdated = (e) => {
    const ctx = e.api.gridOptionsWrapper.gridOptions.context;
    if (ctx.putRowIds.length > 0) {
      setState(IngestState.Edited);
    }
    e.columnApi.autoSizeAllColumns();
    setUndoSize(ctx.undoStack.length);
  };

  const toolTipValueGetter = (params) => {
    const error = params.context.errors.find(
      (e) => e.rowIds.includes(params.data.id) && (!e.columnNames || e.columnNames.includes(params.colDef.field))
    );

    if (error?.levelId === 'DUPLICATE') {
      const rowPositions = error.rowIds.map((r) => params.context.rowData.find((d) => d.id === r)?.pos).filter((r) => r);
      const duplicates = rowPositions.map((r) => params.context.rowPos.indexOf(r) + 1);
      return duplicates.length > 1 ? 'Rows are duplicated: ' + duplicates.join(', ') : 'Duplicate rows have been removed';
    }

    return error?.message;
  };

  const rowValueGetter = (params) => {
    return params.context.rowPos ? params.context.rowPos.indexOf(params.data.pos) + 1 : 0;
  };

  const deleteRow = (e) => {
    const rowData = e.context.rowData;
    const [cells] = e.api.getCellRanges();
    const startIdx = Math.min(cells.startRow.rowIndex, cells.endRow.rowIndex);
    const endIdx = Math.max(cells.startRow.rowIndex, cells.endRow.rowIndex);
    const delta = [];

    if (startIdx === endIdx && startIdx === e.node.rowIndex) {
      e.api.getSelectedRows().forEach(() => {
        const data = e.node.data;
        delta.push({...data});
        rowData.splice(rowData.indexOf(data), 1);
      });
    } else if (startIdx === endIdx) {
      e.api.getSelectedRows().forEach((row) => {
        const data = rowData.find((d) => d.id === row.id);
        delta.push({...data});
        rowData.splice(rowData.indexOf(data), 1);
      });
    } else {
      for (let i = startIdx; i < endIdx + 1; i++) {
        const row = e.api.getDisplayedRowAtIndex(i);
        const data = rowData.find((d) => d.id === row.data.id);
        delta.push({...data});
        rowData.splice(rowData.indexOf(data), 1);
      }
    }
    pushUndo(e.api, delta);
    e.api.setRowData(rowData);
    e.context.rowPos = rowData.map((r) => r.pos).sort((a, b) => a - b);
    e.api.refreshCells();
  };

  const onCellKeyDown = (e) => {
    const editingCells = e.api.getEditingCells();
    if (editingCells.length === 1) {
      e.api.gridOptionsWrapper.gridOptions.context.navigationKey = e.event.key;
      if (['ArrowLeft', 'ArrowUp'].includes(e.event.key)) {
        e.event.preventDefault();
        e.api.stopEditing();
        e.api.tabToPreviousCell();
      }
      if (['ArrowRight', 'ArrowDown'].includes(e.event.key)) {
        e.event.preventDefault();
        e.api.stopEditing();
        e.api.tabToNextCell();
      }
      e.api.gridOptionsWrapper.gridOptions.context.navigationKey = '';
    }
  };

  const onTabToNextCell = (params) => {
    let context = gridApi.gridOptionsWrapper.gridOptions.context;
    let result;

    if (['ArrowUp', 'ArrowDown'].includes(context.navigationKey) && params.previousCellPosition) {
      let previousCell = params.previousCellPosition,
        lastRowIndex = previousCell.rowIndex,
        nextRowIndex = params.backwards ? lastRowIndex - 1 : lastRowIndex + 1,
        renderedRowCount = gridApi.getModel().getRowCount();

      if (nextRowIndex < 0) nextRowIndex = -1;
      if (nextRowIndex >= renderedRowCount) nextRowIndex = renderedRowCount - 1;

      result = {
        rowIndex: nextRowIndex,
        column: previousCell.column,
        floating: previousCell.floating
      };
    }

    if (['ArrowLeft', 'ArrowRight'].includes(context.navigationKey) && params.nextCellPosition) {
      result = {
        rowIndex: params.nextCellPosition.rowIndex,
        column: params.nextCellPosition.column,
        floating: params.nextCellPosition.floating
      };
    }

    return result;
  };

  const onClickExcelExport = (api, name) => {
    api.exportDataAsExcel({
      sheetName: 'DATA',
      author: 'NRMN',
      columnKeys: [
        'id','diver','buddy','siteCode','siteName','latitude','longitude','date','vis','direction','time',
        'P-Qs','depth','method','block','code','species','commonName','total','inverts','1','2',
        '3','4','5','6','7','8','9','10','11','12','13','14','15','16','17','18','19','20','21','22','23','24','25','26','27','28'],
      fileName: `export_${name}`
    });
  };

  const measurementColumns = job.isExtendedSize ? measurements.concat(extendedMeasurements) : measurements;
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
          <NavLink to="/data/jobs" color="secondary">
            <Typography>{'<< Back to Jobs'}</Typography>
          </NavLink>
        </Box>
        {job && job.status === 'STAGED' && (
          <Box display="flex" flexDirection="row">
            <Box p={1} flexGrow={1}>
              <Typography noWrap variant="subtitle2">
                {job.reference}
              </Typography>
              <Typography variant="body2">{`${job.status} ${job.source} ${job.program} ${
                job.isExtendedSize ? 'Extended Size' : ''
              } `}</Typography>
            </Box>
            <Box m={1} ml={0}>
              <Button variant="outlined" disabled={undoSize < 1} startIcon={<UndoIcon />} onClick={() => onUndo({api: gridApi})}>
                Undo
              </Button>
            </Box>
            <Box m={1} ml={0} minWidth={150}>
              <Button variant="outlined" startIcon={<ResetIcon />} disabled={!isFiltered} onClick={() => setIsFiltered()}>
                Reset Filter
              </Button>
            </Box>
            <Box m={1} ml={0}>
              <Button
                variant="outlined"
                onClick={ () => onClickExcelExport(gridApi, job.reference) }
                startIcon={<CloudDownloadIcon />}
              >
                Export
              </Button>
            </Box>
            <Box p={1} minWidth={180}>
              <Button
                variant="contained"
                disabled={state === IngestState.Loading}
                onClick={handleSaveAndValidate}
                startIcon={<PlaylistAddCheckOutlinedIcon />}
              >
                {`Save & Validate`}
              </Button>
            </Box>
            <Box p={1} mr={2}>
              <Button
                variant="contained"
                disabled={state !== IngestState.Valid && !isAdmin}
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
        {job && (
          <AgGridReact
            getRowId={(r) => r.data.id}
            context={context}
            cellFlashDelay={100}
            cellFadeDelay={100}
            defaultColDef={{
              editable: true,
              sortable: true,
              resizable: true,
              minWidth: 70,
              filter: true,
              floatingFilter: true,
              suppressMenu: true,
              suppressKeyboardEvent: overrideKeyboardEvents,
              cellStyle: chooseCellStyle,
              enableCellChangeFlash: true,
              tooltipValueGetter: toolTipValueGetter,
              valueParser: ({newValue}) => (newValue ? newValue.trim() : '')
            }}
            rowHeight={20}
            enableBrowserTooltips
            rowSelection="multiple"
            enableRangeSelection={true}
            animateRows={true}
            enableRangeHandle={true}
            onCellKeyDown={onCellKeyDown}
            onPasteStart={onPasteStart}
            onPasteEnd={onPasteEnd}
            tabToNextCell={onTabToNextCell}
            onCellValueChanged={onCellValueChanged}
            onSortChanged={onSortChanged}
            onFilterChanged={onFilterChanged}
            onRowDataUpdated={onRowDataUpdated}
            fillHandleDirection="y"
            getContextMenuItems={getContextMenuItems}
            undoRedoCellEditing={false}
            onCellEditingStopped={onCellEditingStopped}
            components={{
              validationPanel: ValidationPanel,
              findReplacePanel: FindReplacePanel,
              loadingOverlay: LoadingOverlay
            }}
            loadingOverlayComponent="loadingOverlay"
            pivotMode={false}
            pivotColumnGroupTotals="before"
            sideBar={sideBar}
            onGridReady={onGridReady}
          >
            <AgGridColumn field="id" headerName="ID" editable={false} hide={true} />
            <AgGridColumn field="pos" editable={false} hide={true} sort="asc" />
            <AgGridColumn
              field="row"
              headerName=""
              suppressMovable={true}
              editable={false}
              valueGetter={rowValueGetter}
              minWidth={40}
              enableCellChangeFlash={false}
              filter={false}
              sortable={false}
            />
            <AgGridColumn field="diver" headerName="Diver" pivot={true} enablePivot={false} />
            <AgGridColumn field="buddy" headerName="Buddy" />
            <AgGridColumn field="siteCode" headerName="Site No." rowGroup={false} enableRowGroup={true} />
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
            {measurementColumns.map((m) => (
              <AgGridColumn
                field={m.field}
                headerName={m.fishSize}
                key={m.field}
                editable={true}
                width={35}
                headerComponentParams={{
                  template: `<div style="width: 48px; float: left; text-align:center"><div style="color: #c4d79b; border-bottom: 1px solid rgba(0, 0, 0, 0.12)">${m.fishSize}</div><div style="color: #da9694">${m.invertSize}</div></div>`
                }}
              />
            ))}
            {job.isExtendedSize && <AgGridColumn minWidth={120} field="isInvertSizing" headerName="Use Invert Sizing" />}
          </AgGridReact>
        )}
      </Box>
    </>
  );
};

DataSheetView.propTypes = {
  onIngest: PropTypes.func.isRequired,
  isAdmin: PropTypes.bool.isRequired
};

export default DataSheetView;
