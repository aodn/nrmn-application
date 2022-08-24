import React, {useEffect, useState} from 'react';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import CloudDownloadIcon from '@mui/icons-material/CloudDownload';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';
import PlaylistAddCheckOutlinedIcon from '@mui/icons-material/PlaylistAddCheckOutlined';
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
import eh from './DataSheetEventHandlers';

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

  // needed by the panels
  pushUndo: eh.pushUndo,
  popUndo: eh.popUndo,

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
    }
  ],
  defaultToolPanel: ''
};

const reload = (api, id, completion, isAdmin) => {
  const context = api.gridOptionsWrapper.gridOptions.context;
  eh.resetContext();
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
      context.rowData = rowData;
      context.rowPos = rowData.map((r) => r.pos).sort((a, b) => a - b);
      api.setRowData(rowData.length > 0 ? rowData : null);
    }
    if (completion) completion(job);
  });
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
        eh.handleUndo({api: gridApi});
      }
    };
    document.body.addEventListener('keydown', undoKeyboardHandler);
    return () => {
      document.body.removeEventListener('keydown', undoKeyboardHandler);
    };
  }, [gridApi]);

  useEffect(() => {
    if (gridApi && state === IngestState.Loading) gridApi.showLoadingOverlay();
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
      context.errorList = eh.generateErrorTree(context.rowData, context.rowPos, context.errors);

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
        reload(gridApi, id, handleValidate, isAdmin);
        context.fullRefresh = false;
      } else {
        handleValidate();
      }
    });
  };

  const onCellValueChanged = (e) => {
    setUndoSize(eh.handleCellValueChanged(e));
  };

  const onPasteEnd = (e) => {
    setUndoSize(eh.handlePasteEnd(e));
  };

  const onCellEditingStopped = (e) => {
    const undos = eh.handleCellEditingStopped(e);
    setUndoSize(undos);
    setState(IngestState.Edited);
  };

  const onGridReady = (p) => {
    setGridApi(p.api);
    reload(
      p.api,
      id,
      (job) => {
        setState(IngestState.Edited);
        setJob(job);
      },
      isAdmin
    );
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

  const editable = ['STAGED'].includes(job.status);
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
        {job?.status && (
          <Box display="flex" flexDirection="row">
            <Box p={1} flexGrow={1}>
              <Typography noWrap variant="subtitle2">
                {job.reference}
              </Typography>
              <Typography variant="body2">{`${job.source} (${job.status}) - ${job.program} ${
                job.isExtendedSize ? 'Extended Size' : ''
              } `}</Typography>
            </Box>
            <Box m={1} ml={0}>
              <Button variant="outlined" disabled={undoSize < 1} startIcon={<UndoIcon />} onClick={() => eh.handleUndo({api: gridApi})}>
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
                onClick={() => eh.onClickExcelExport(gridApi, job.reference, job.isExtendedSize)}
                startIcon={<CloudDownloadIcon />}
              >
                Export
              </Button>
            </Box>
            {editable && (
              <>
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
              </>
            )}
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
              cellStyle: eh.chooseCellStyle,
              editable,
              enableCellChangeFlash: true,
              filter: true,
              floatingFilter: true,
              minWidth: 70,
              resizable: true,
              sortable: true,
              suppressKeyboardEvent: eh.overrideKeyboardEvents,
              suppressMenu: true,
              tooltipValueGetter: eh.toolTipValueGetter,
              valueParser: ({newValue}) => (newValue ? newValue.trim() : '')
            }}
            rowHeight={20}
            enableBrowserTooltips
            rowSelection="multiple"
            enableRangeSelection
            animateRows
            enableRangeHandle
            onCellKeyDown={eh.onCellKeyDown}
            onPasteStart={eh.onPasteStart}
            onPasteEnd={onPasteEnd}
            tabToNextCell={eh.onTabToNextCell}
            onCellValueChanged={onCellValueChanged}
            onSortChanged={eh.onSortChanged}
            onFilterChanged={onFilterChanged}
            onRowDataUpdated={onRowDataUpdated}
            fillHandleDirection="y"
            getContextMenuItems={(e) => eh.getContextMenuItems(e, eh)}
            undoRedoCellEditing={false}
            onCellEditingStopped={onCellEditingStopped}
            components={{
              validationPanel: ValidationPanel,
              findReplacePanel: FindReplacePanel,
              loadingOverlay: LoadingOverlay
            }}
            loadingOverlayComponent="loadingOverlay"
            sideBar={sideBar}
            onGridReady={onGridReady}
          >
            <AgGridColumn field="id" headerName="ID" editable={false} hide />
            <AgGridColumn field="pos" editable={false} hide sort="asc" />
            <AgGridColumn
              field="row"
              headerName=""
              suppressMovable
              editable={false}
              valueGetter={eh.rowValueGetter}
              minWidth={40}
              enableCellChangeFlash={false}
              filter={false}
              sortable={false}
            />
            <AgGridColumn field="diver" headerName="Diver" />
            <AgGridColumn field="buddy" headerName="Buddy" />
            <AgGridColumn field="siteCode" headerName="Site No." rowGroup={false} enableRowGroup />
            <AgGridColumn field="siteName" headerName="Site Name" minWidth={160} />
            <AgGridColumn field="latitude" headerName="Latitude" />
            <AgGridColumn field="longitude" headerName="Longitude" />
            <AgGridColumn field="date" headerName="Date" rowGroup={false} enableRowGroup comparator={eh.dateComparator} />
            <AgGridColumn field="vis" headerName="Vis" />
            <AgGridColumn field="direction" headerName="Direction" />
            <AgGridColumn field="time" headerName="Time" />
            <AgGridColumn field="P-Qs" headerName="P-Qs" />
            <AgGridColumn field="depth" headerName="Depth" rowGroup={false} enableRowGroup />
            <AgGridColumn field="method" headerName="Method" rowGroup={false} enableRowGroup />
            <AgGridColumn field="block" headerName="Block" rowGroup={false} enableRowGroup />
            <AgGridColumn field="code" headerName="Code" />
            <AgGridColumn field="species" headerName="Species" />
            <AgGridColumn field="commonName" headerName="Common Name" />
            <AgGridColumn field="total" headerName="Total" aggFunc="count" />
            <AgGridColumn field="inverts" headerName="Inverts" />
            {measurementColumns.map((m) => (
              <AgGridColumn
                field={m.field}
                headerName={m.fishSize}
                key={m.field}
                editable={editable}
                width={35}
                headerComponentParams={{
                  template: `<div style="width: 48px; float: left; text-align:center"><div style="color: #c4d79b; border-bottom: 1px solid rgba(0, 0, 0, 0.12)">${m.fishSize}</div><div style="color: #da9694">${m.invertSize}</div></div>`
                }}
              />
            ))}
            {job.isExtendedSize && <AgGridColumn minWidth={120} field="isInvertSizing" headerName="Use InvertSizing" />}
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
