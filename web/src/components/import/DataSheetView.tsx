import React, { useEffect, useCallback, useState, useRef } from 'react';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import CloudDownloadIcon from '@mui/icons-material/CloudDownload';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';
import PlaylistAddCheckOutlinedIcon from '@mui/icons-material/PlaylistAddCheckOutlined';
import UndoIcon from '@mui/icons-material/Undo';
import ResetIcon from '@mui/icons-material/LayersClear';
import { AgGridReact } from 'ag-grid-react';
import { useParams } from 'react-router-dom';
import { getDataJob, submitIngest, updateRows, validateJob } from '../../api/api';
import { AppConstants, extendedMeasurements, measurements } from '../../common/constants';
import LoadingOverlay from '../overlays/LoadingOverlay';
import AlertDialog from '../ui/AlertDialog';
import FindReplacePanel from './panel/FindReplacePanel';
import ValidationPanel from './panel/ValidationPanel';
import eh from './DataSheetEventHandlers';
import { Paper } from '@mui/material';
import ReportProblemIcon from '@mui/icons-material/ReportProblem';
import BackButton from '../ui/BackButton';
import DataRectificationHandler from './DataRectificationHandler';
import { CellStyle, CellStyleFunc, ColDef, GridApi, SuppressKeyboardEventParams } from 'ag-grid-enterprise';
import { AxiosResponse } from 'axios';
import { ValidationResponse, InternalContext, RowUpdate, StagedRow, JobResponse, SourceJobType, StatusJobType, ExtRow, Measurement, SurveyValidationErrorLevel } from '../../common/types';
import { CellEditingStoppedEvent, CellValueChangedEvent, FilterChangedEvent, GetContextMenuItemsParams, GridReadyEvent, MenuItemDef, PasteEndEvent, RowDataUpdatedEvent } from 'ag-grid-community';

// |context| is where all custom properties and helper functions
// associated with the ag-grid are stored
// see: https://www.ag-grid.com/javascript-grid/context/

const context: InternalContext = {
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

const IngestState = Object.freeze({ Loading: 0, Edited: 1, Locked: 2, Valid: 3, ConfirmSubmit: 4 });

interface JobType {
  program?: string,
  reference?: string,
  isExtendedSize?: boolean,
  source?: SourceJobType,
  status?: StatusJobType,
}

interface DataSheetViewProps {
  onIngest: (resp: AxiosResponse) => void,
  roles: Array<string>,
}

const DataSheetView: React.FC<DataSheetViewProps> = ({ onIngest, roles }) => {
  const { id } = useParams();
  const [job, setJob] = useState<JobType>({});
  const [gridApi, setGridApi] = useState<GridApi>();
  const [isFiltered, setIsFiltered] = useState(false);
  const [undoSize, setUndoSize] = useState(0);
  const [state, setState] = useState<number>(IngestState.Loading);
  const [sideBar, setSideBar] = useState(defaultSideBar);

  const isAdmin = roles.includes(AppConstants.ROLES.ADMIN);
  const isDataOfficer = roles.includes(AppConstants.ROLES.DATA_OFFICER);

  const dataRectificationHandlerRef = useRef<DataRectificationHandler>(new DataRectificationHandler());
  const editable = ['STAGED'].includes(job.status || '');

  const defaultColDef: ColDef = {
    lockVisible: true,
    cellStyle: eh.chooseCellStyle as CellStyle | CellStyleFunc | undefined,
    editable: true,
    enableCellChangeFlash: true,
    filter: true,
    filterParams: { debounceMs: AppConstants.Filter.WAIT_TIME_ON_FILTER_APPLY },
    floatingFilter: true,
    minWidth: AppConstants.AG_GRID.dataColWidth,
    resizable: true,
    sortable: true,
    suppressKeyboardEvent: (params: SuppressKeyboardEventParams) => eh.overrideKeyboardEvents(params) as boolean,
    suppressMenu: true,
    tooltipValueGetter: eh.toolTipValueGetter,
    valueParser: ({ newValue }) => (newValue ? newValue.trim() : '')
  };

  const reload = useCallback((api: GridApi, id: string, completion: (job: JobType) => void, isAdmin: boolean) => {
    eh.resetContext();
    context.isAdmin = isAdmin;

    getDataJob(id).then((res: AxiosResponse<JobResponse>) => {
      const job: JobType = {
        program: res.data.job.program.programName,
        reference: res.data.job.reference,
        isExtendedSize: res.data.job.isExtendedSize,
        source: res.data.job.source,
        status: res.data.job.status
      };
      if (res?.data.rows) {
        const rowData = res.data.rows.map((row) => {
          const { measureJson } = { ...row };
          const json = measureJson || '{}';

          Object.getOwnPropertyNames(json)
            .forEach((numKey) => {
              if (measureJson) {
                (row as ExtRow)[numKey] = json[numKey];
              }
            }
            );
          delete row.measureJson;
          return row;
        });

        if (rowData) {
          context.rowData = rowData as ExtRow[];
          context.rowPos = rowData.map((r: StagedRow) => r.pos as number)
            .sort((a: number, b: number) => a - b) as number[];

          api.setRowData(rowData.length > 0 ? rowData : []);
        }
      }
      if (completion) completion(job);
    })
      .catch(() => {
        // Do nothing here
      });
  }, []);

  useEffect(() => {
    document.title = 'Ingest Sheet';
    const undoKeyboardHandler = (event: KeyboardEvent) => {
      if (event.ctrlKey && event.key === 'z') {
        event.preventDefault();
        event.stopPropagation();
        eh.handleUndo({ api: gridApi });
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

  const handleValidate = useCallback(() => {
    context.useOverlay = 'Validating';
    setState(IngestState.Loading);
    setSideBar(defaultSideBar);
    context.errors = [];

    validateJob(id, (result: AxiosResponse<ValidationResponse>) => {
      // Bad design that it comes with either string or structure
      // we need to cast here just to handle this case.
      if (result.data as string === 'locked') {
        setState(IngestState.Locked);
        return;
      }

      const data = result.data;

      if (data.errors) {
        context.errors = data.errors;

        delete result.data.errors;
        delete result.data.job;

        context.summary = result.data;
        context.errorList = eh.generateErrorTree(context.rowData, context.errors);

        // After validation, the system will set the validation result to the handler in case dev
        // there are some data need to be rectified by system automatically
        dataRectificationHandlerRef.current.setValidationResult(context.errors);

        setState(context.errors.some((e) => e.levelId === SurveyValidationErrorLevel.BLOCKING) ? IngestState.Edited : IngestState.Valid);

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
      };
    });
  }, [id, dataRectificationHandlerRef, setState, setSideBar]);

  const handleSubmit = useCallback((event: React.MouseEvent<HTMLButtonElement>): void => {
    context.useOverlay = 'Submitting';
    setState(IngestState.Loading);
    setSideBar(defaultSideBar);

    // Before submit, the system will rectify the data according to the validation result
    dataRectificationHandlerRef
      .current
      .submitRectification(id)
      .then(() => submitIngest(id, () => setState(IngestState.Locked), (res: AxiosResponse) => onIngest(res))
      );
  }, [id, dataRectificationHandlerRef, setState, setSideBar, onIngest]);

  const handleSaveAndValidate = useCallback(() => {
    context.useOverlay = 'Saving';
    setState(IngestState.Loading);
    setSideBar(defaultSideBar);

    const rowUpdateDtos: Array<RowUpdate> = [];

    Array.from(new Set(context.putRowIds)).forEach((rowId: number) => {
      const row = context.rowData?.find((r) => r.id === rowId);

      if (row) {
        // Serialise the measure JSON
        const measure: { [key: string]: number | string } = {};
        Object.getOwnPropertyNames(row || {})
          .filter((key) => !isNaN(parseFloat(key)))
          .forEach((numKey) => {
            measure[numKey] = row[numKey];
          });
        row.measureJson = measure;
      }

      // Null row data means that the row is to be deleted
      rowUpdateDtos.push({ rowId: rowId, row: row as StagedRow });

      // HACK: use the fact that new rows are assigned a long identifier
      // to determine if we need a full reload to get the server-assigned
      // row id. A better way would be to do a full reload based on a server
      // response.
      context.fullRefresh = context.fullRefresh || rowId.toString().length === 10 || row === null;
    });

    // set rows to the handler, because the handler will rectify the data according to the validation result later
    dataRectificationHandlerRef.current.setRows(rowUpdateDtos);

    updateRows(id, rowUpdateDtos, (res: AxiosResponse) => {
      if (res && id) {
        if (context.fullRefresh && gridApi) {
          reload(gridApi, id, handleValidate, isAdmin);
          context.fullRefresh = false;
        } else {
          handleValidate();
        }
      }
    });
  }, [dataRectificationHandlerRef, gridApi, id, isAdmin, handleValidate, setState, setSideBar, reload]);

  const onCellValueChanged = useCallback((e: CellValueChangedEvent) => {
    setUndoSize(eh.handleCellValueChanged(e));
    if (isFiltered) {

      const filterModel = e.api.getFilterModel();
      const field = e.colDef.field;

      if (field && filterModel[field]) {
        filterModel[field].values.push(e.newValue);
        e.api.setFilterModel(filterModel);
      }
    }
  }, [isFiltered]);

  const onPasteEnd = useCallback((e: PasteEndEvent) => {
    setUndoSize(eh.handlePasteEnd(e));
  }, [setUndoSize]);

  const onCellEditingStopped = useCallback((e: CellEditingStoppedEvent) => {
    setUndoSize(eh.handleCellEditingStopped(e));
    setState(IngestState.Edited);
  }, [setUndoSize, setState]);

  const onGridReady = (p: GridReadyEvent) => {
    setGridApi(p.api);

    if (id) {
      reload(
        p.api,
        id,
        (job) => {
          if (job.reference) {
            setState(IngestState.Edited);
            setJob(job);
            document.title = job.reference;
          }
        },
        isAdmin
      );
    }
  };

  const onFilterChanged = useCallback((e: FilterChangedEvent) => {
    e.api.refreshCells();
    const filterModel = e.api.getFilterModel();
    setIsFiltered(Object.getOwnPropertyNames(filterModel).length > 0);
  }, []);

  const onRowDataUpdated = useCallback((e: RowDataUpdatedEvent) => {
    const ctx = context;
    if (ctx.putRowIds.length > 0) {
      setState(IngestState.Edited);
    }
    e.columnApi.autoSizeAllColumns();
    setUndoSize(ctx.undoStack.length);
  }, [setState, setUndoSize]);

  const createColumns = useCallback((measurementColumns: Array<Measurement>, isExtendedSize: boolean | undefined): ColDef[] => {
    // It is a hack, for some reason it use a field that is not def in ColDef
    const cols: (ColDef & { key?: string })[] = [];

    cols.push({
      field: 'id',
      headerName: 'ID',
      editable: false,
      hide: true,
    });

    cols.push({
      field: 'pos',
      editable: false,
      hide: true,
      sort: 'asc'
    });

    cols.push({
      field: 'row',
      headerName: '',
      suppressMovable: true,
      editable: false,
      valueGetter: eh.rowValueGetter,
      minWidth: 40,
      enableCellChangeFlash: false,
      filter: false,
      sortable: false,
    });

    cols.push({
      field: 'diver',
      headerName: 'Diver',
    });

    cols.push({
      field: 'buddy',
      headerName: 'Buddy',
    });

    cols.push({
      field: 'siteCode',
      headerName: 'Site No.',
      rowGroup: false,
      enableRowGroup: true
    });

    cols.push({
      minWidth: 160,
      field: 'siteName',
      headerName: 'Site Name',
    });

    cols.push({
      field: 'latitude',
      headerName: 'Latitude',
    });

    cols.push({
      field: 'longitude',
      headerName: 'Longitude',
    });

    cols.push({
      field: 'date',
      headerName: 'Date',
      rowGroup: false,
      enableRowGroup: true,
      comparator: eh.dateComparator,
    });

    cols.push({
      field: 'vis',
      headerName: 'Vis',
    });

    cols.push({
      field: 'direction',
      headerName: 'Direction',
    });

    cols.push({
      field: 'time',
      headerName: 'Time',
    });

    cols.push({
      field: 'P-Qs',
      headerName: 'P-Qs',
    });

    cols.push({
      field: 'depth',
      headerName: 'Depth',
      rowGroup: false,
      enableRowGroup: true,
    });

    cols.push({
      field: 'method',
      headerName: 'Method',
      rowGroup: false,
      enableRowGroup: true,
    });

    cols.push({
      field: 'block',
      headerName: 'Block',
      rowGroup: false,
      enableRowGroup: true,
    });

    cols.push({
      field: 'code',
      headerName: 'Code',
    });

    cols.push({
      field: 'species',
      headerName: 'Species',
    });

    cols.push({
      field: 'commonName',
      headerName: 'Common Name',
    });

    cols.push({
      field: 'total',
      headerName: 'Total',
      aggFunc: 'count',
    });

    cols.push({
      field: 'inverts',
      headerName: 'Inverts',
    });

    measurementColumns.map((m) => (
      cols.push({
        field: m.field,
        headerName: m.fishSize,
        key: m.field,
        editable: editable,
        width: AppConstants.AG_GRID.measureColWidth,
        headerComponentParams: {
          template: `<div style="width: 48px; float: left; text-align:center"><div style="color: #c4d79b; border-bottom: 1px solid rgba(0, 0, 0, 0.12)">${m.fishSize}</div><div style="color: #da9694">${m.invertSize}</div></div>`
        }
      })
    ));

    if (isExtendedSize) {
      cols.push({
        minWidth: 120,
        field: 'isInvertSizing',
        headerName: 'Use InvertSizing',
      });
    }

    return cols as ColDef[];

  }, [editable]);

  const measurementColumns = (job.isExtendedSize ? measurements.concat(extendedMeasurements) : measurements) as Array<Measurement>;
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
            <BackButton goBackTo={`/data/jobs`} name={`Jobs`} />
          </Box>
          {state === IngestState.Locked && (
            <Paper sx={{ minWidth: 180, display: 'flex', alignItems: 'center' }}>
              <Box display="flex" flexDirection="row">
                <Box m={1}>
                  <ReportProblemIcon sx={{ color: 'darkorange' }} />
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
              <Typography variant="body2">{`${job.source} (${job.status}) - ${job.program} ${job.isExtendedSize ? 'Extended Size' : ''
                } `}</Typography>
            </Box>
            <Box m={1} ml={0}>
              <Button variant="outlined" disabled={undoSize < 1} startIcon={<UndoIcon />} onClick={() => eh.handleUndo({ api: gridApi })}>
                Undo
              </Button>
            </Box>
            <Box m={1} ml={0} minWidth={150}>
              <Button variant="outlined" startIcon={<ResetIcon />} disabled={!isFiltered} onClick={() => setIsFiltered(false)}>
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
                    data-testid="save-and-validate-button"
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
            getContextMenuItems={(params: GetContextMenuItemsParams) => eh.getContextMenuItems(params, eh) as (string | MenuItemDef)[]}
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
            columnDefs={createColumns(measurementColumns, job.isExtendedSize)}
          >
          </AgGridReact>
        )}
      </Box>
    </>
  );
};

export default DataSheetView;
