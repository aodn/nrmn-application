import React, {useEffect, useCallback, useState} from 'react';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import CloudDownloadIcon from '@mui/icons-material/CloudDownload';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';
import PlaylistAddCheckOutlinedIcon from '@mui/icons-material/PlaylistAddCheckOutlined';
import UndoIcon from '@mui/icons-material/Undo';
import ResetIcon from '@mui/icons-material/LayersClear';
import {AgGridColumn, AgGridReact} from 'ag-grid-react';
import { PropTypes } from 'prop-types';
import {useParams, NavLink} from 'react-router-dom';
import {getDataJob, submitIngest, updateRows, validateJob} from '../../api/api';
import { AppConstants, extendedMeasurements, measurements } from '../../common/constants';
import LoadingOverlay from '../overlays/LoadingOverlay';
import AlertDialog from '../ui/AlertDialog';
import FindReplacePanel from './panel/FindReplacePanel';
import ValidationPanel from './panel/ValidationPanel';
import eh from './DataSheetEventHandlers';
import {Paper} from '@mui/material';
import ReportProblemIcon from '@mui/icons-material/ReportProblem';

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

const IngestState = Object.freeze({Loading: 0, Edited: 1, Locked: 2, Valid: 3, ConfirmSubmit: 4});

const DataSheetView = ({onIngest, roles}) => {
  const {id} = useParams();
  const [job, setJob] = useState({});
  const [gridApi, setGridApi] = useState();
  const [isFiltered, setIsFiltered] = useState(false);
  const [undoSize, setUndoSize] = useState(0);
  const [state, setState] = useState(IngestState.Loading);
  const [sideBar, setSideBar] = useState(defaultSideBar);

  const isAdmin = roles.includes(AppConstants.ROLES.ADMIN);
  const isDataOfficer = roles.includes(AppConstants.ROLES.DATA_OFFICER);

  const defaultColDef = {
    lockVisible: true,
    cellStyle: eh.chooseCellStyle,
    editable: true,
    enableCellChangeFlash: true,
    filter: true,
    filterParams: {debounceMs: AppConstants.Filter.WAIT_TIME_ON_FILTER_APPLY },
    floatingFilter: true,
    minWidth: AppConstants.AG_GRID.dataColWidth,
    resizable: true,
    sortable: true,
    suppressKeyboardEvent: eh.overrideKeyboardEvents,
    suppressMenu: true,
    tooltipValueGetter: eh.toolTipValueGetter,
    valueParser: ({newValue}) => (newValue ? newValue.trim() : '')
  };

  const reload = useCallback((api, id, completion, isAdmin) => {
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
  },[]);

  useEffect(() => {
    document.title = 'Ingest Sheet';
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
    if (!gridApi) return;
    if (state === IngestState.Loading) {
      gridApi.showLoadingOverlay();
    } else {
      gridApi.hideOverlay();
      gridApi.redrawRows();
    }
  }, [gridApi, state]);

  useEffect(() => {
    if (gridApi && !isFiltered) gridApi.setFilterModel(null);
  }, [gridApi, isFiltered]);

  const handleValidate = () => {
    context.useOverlay = 'Validating';
    setState(IngestState.Loading);
    setSideBar(defaultSideBar);
    context.errors = [];
    validateJob(id, (result) => {
      if (result.data === 'locked') {
        setState(IngestState.Locked);
        return;
      }

      context.errors = result.data.errors;
      delete result.data.errors;
      delete result.data.job;
      context.summary = result.data;
      context.errorList = eh.generateErrorTree(context.rowData, context.errors);

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
    submitIngest(id, () => setState(IngestState.Locked), (res) => onIngest(res));
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

      // HACK: use the fact that new rows are assigned a long identifier
      // to determine if we need a full reload to get the server-assigned
      // row id. A better way would be to do a full reload based on a server
      // response.
      context.fullRefresh = context.fullRefresh || rowId.toString().length === 10 || row === null;
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
    if (isFiltered) {
      const filterModel = e.api.getFilterModel();
      const field = e.colDef.field;
      if (filterModel[field]) {
        filterModel[field].values.push(e.newValue);
        e.api.setFilterModel(filterModel);
      }
    }
  };

  const onPasteEnd = (e) => {
    setUndoSize(eh.handlePasteEnd(e));
  };

  const onCellEditingStopped = (e) => {
    setUndoSize(eh.handleCellEditingStopped(e));
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
        document.title = job.reference;
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
        <Box display="flex" flexDirection="row">
          <Box flexGrow={1}>
            <NavLink to="/data/jobs" color="secondary">
              <Typography>{'<< Back to Jobs'}</Typography>
            </NavLink>
          </Box>
          {state === IngestState.Locked && (
            <Paper minWidth={180} display="flex" alignItems="center">
              <Box display="flex" flexDirection="row">
                <Box m={1}>
                  <ReportProblemIcon  sx={{color: 'darkorange'}} />
                </Box>
                <Box mx={1} my={2}>Server is busy. Try again in a moment.</Box>
              </Box>
            </Paper>
          )}
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
                    disabled={state === IngestState.Loading || !(isDataOfficer || isAdmin)}
                    onClick={handleSaveAndValidate}
                    startIcon={<PlaylistAddCheckOutlinedIcon />}
                  >
                    {`Save & Validate`}
                  </Button>
                </Box>
                <Box p={1} mr={2}>
                  <Button
                    variant="contained"
                    disabled={state !== IngestState.Valid || !(isDataOfficer || isAdmin)}
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
            animateRows
            cellFadeDelay={100}
            cellFlashDelay={100}
            context={context}
            defaultColDef={defaultColDef}
            enableBrowserTooltips
            enableRangeHandle
            enableRangeSelection
            fillHandleDirection="y"
            getContextMenuItems={(e) => eh.getContextMenuItems(e, eh)}
            getRowId={(r) => r.data.id}
            loadingOverlayComponent="loadingOverlay"
            onCellEditingStopped={onCellEditingStopped}
            onCellKeyDown={eh.onCellKeyDown}
            onCellValueChanged={onCellValueChanged}
            onFilterChanged={onFilterChanged}
            onGridReady={onGridReady}
            onPasteEnd={onPasteEnd}
            onPasteStart={eh.onPasteStart}
            onRowDataUpdated={onRowDataUpdated}
            onSortChanged={eh.onSortChanged}
            processDataFromClipboard={eh.processDataFromClipboard}
            rowHeight={20}
            rowSelection="multiple"
            sideBar={sideBar}
            undoRedoCellEditing={false}
            components={{
              validationPanel: ValidationPanel,
              findReplacePanel: FindReplacePanel,
              loadingOverlay: LoadingOverlay
            }}
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
            <AgGridColumn minWidth={160} field="siteName" headerName="Site Name" />
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
                width={AppConstants.AG_GRID.measureColWidth}
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
  roles: PropTypes.array.isRequired
};

export default DataSheetView;
