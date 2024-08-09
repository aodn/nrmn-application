import React, { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { CloudUpload as CloudUploadIcon, PlaylistAddCheckOutlined as PlaylistAddCheckOutlinedIcon } from '@mui/icons-material/';
import { Alert, Box, Button, Typography } from '@mui/material';
import UndoIcon from '@mui/icons-material/Undo';
import ResetIcon from '@mui/icons-material/LayersClear';
import 'ag-grid-community/dist/styles/ag-theme-material.css';
import 'ag-grid-enterprise';
import { AgGridReact } from 'ag-grid-react';
import { Navigate, useParams } from 'react-router-dom';
import CloudDownloadIcon from '@mui/icons-material/CloudDownload';
import { getCorrections, submitSurveyCorrection, validateSurveyCorrection } from '../../../api/api';
import { allMeasurements, SizeCorrection, unsized } from '../../../common/correctionsConstants';
import ValidationPanel from '../../import/panel/ValidationPanel';
import LoadingOverlay from '../../overlays/LoadingOverlay';
import SurveyCorrectPanel from './panel/SurveyCorrectPanel';
import FindReplacePanel from '../../import/panel/FindReplacePanel';
import SurveyMeasurementHeader from './SurveyMeasurementHeader';
import eh from '../../import/DataSheetEventHandlers';
import SurveyDiff from './SurveyDiff';
import { AppConstants } from '../../../common/constants';
import PropTypes from 'prop-types';
import { ColDef, ITooltipParams } from 'ag-grid-enterprise';
import { CellEditingStoppedEvent, CellStyle, CellStyleFunc, CellValueChangedEvent, FilterChangedEvent, GetContextMenuItemsParams, GridApi, GridReadyEvent, KeyCreatorParams, MenuItemDef, PasteEndEvent, RowDataUpdatedEvent, SuppressKeyboardEventParams } from 'ag-grid-community';
import { CellFormatType, CorrectionDiff, CorrectionRequestBody, CorrectionRow, CorrectionRows, ExtCorrectionRow, StagedRow, SurveyValidationError } from '../../../common/types';

interface Header {
  field: string,
  label: string,
  editable?: boolean,
  hide?: boolean,
  sort?: 'asc' | 'desc'
}

interface InternalContext {
  errors: Array<SurveyValidationError>,
  highlighted: [],
  popUndo: (api: GridApi) => any,
  pushUndo: (api: GridApi) => any,
  putRowIds: [],
  undoStack: [],
  fullRefresh: false,
  useOverlay: string,
  validations?: Array<SurveyValidationError>,
  diffSummary?: CorrectionDiff,
  pendingPasteUndo: [],
  pasteMode: boolean,
  cellValidations?: CellFormatType[],
  originalData?: CorrectionRow[],
  rowData?: CorrectionRow[],
  rowPos?: number[],
  originalRowPos?: number[],
}

const toolTipValueGetter = ({ context, data, colDef }: ITooltipParams) => {
  if (!context.cellValidations) return;
  const row = data.id;
  const field = (colDef as ColDef).field;
  const error = field ? context.cellValidations[row]?.[field] : undefined;

  if (error) return error?.message;

  if (context.cellValidations[row]?.id?.levelId === 'DUPLICATE') {
    return 'Duplicate rows';
  }
  return error?.message;
};

const removeNullProperties = (obj: object) => {
  return obj ? Object.fromEntries(Object.entries(obj).filter((v) => v[1] !== '')) : null;
};

const packedData = (api: GridApi): Array<StagedRow> => {

  const packedData: Array<StagedRow> = [];
  api.forEachNode((rowNode, index) => {
    const data = rowNode.data;
    const measure: Record<string, string> = {};

    Object.getOwnPropertyNames(data || {})
      .filter((key) => !isNaN(parseFloat(key)))
      .forEach((numKey) => {
        measure[numKey] = data[numKey];
      });
    packedData.push({ id: index, ...data, measureJson: removeNullProperties(measure) });

  });
  return packedData;
};

interface SurveyCorrectProps {
  suppressColumnVirtualisation?: boolean
}

const SurveyCorrect: React.FC<SurveyCorrectProps> = ({ suppressColumnVirtualisation = false }) => {
  const surveyId = useParams()?.id;
  const gridRef = useRef<AgGridReact>(null);

  // FUTURE: useReducer
  const [error, setError] = useState();
  const [editMode, setEditMode] = useState(true);
  const [rowData, setRowData] = useState<Array<ExtCorrectionRow>>();
  const [gridApi, setGridApi] = useState<GridApi>();
  const [isFiltered, setIsFiltered] = useState(false);
  const [loading, setLoading] = useState(false);
  const [validationResult, setValidationResult] = useState<Array<SurveyValidationError>>();
  const [surveyDiff, setSurveyDiff] = useState<CorrectionDiff>();
  const [cellValidations, setCellValidations] = useState<Array<CellFormatType>>([]);
  const [redirect, setRedirect] = useState<string>();
  const [undoSize, setUndoSize] = useState(0);
  const [metadata, setMetadata] = useState<{ programId?: number, programName?: string, surveyIds?: Array<number> }>({ programName: 'NONE', surveyIds: [] });
  const [hasPassedValidation, setHasPassedValidation] = useState(false);

  useEffect(() => {
    document.title = 'Survey Correction';
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
    if (gridRef.current?.api) {
      context.current.cellValidations = cellValidations;
      gridRef.current.api.redrawRows();
    }
  }, [cellValidations]);

  useEffect(() => {
    if (!validationResult) return;
    const cellFormat: Array<CellFormatType> = [];

    for (const res of validationResult) {
      for (const row of res.rowIds) {
        if (!cellFormat[row]) cellFormat[row] = {};
        if (res.columnNames) {
          for (const col of res.columnNames) {
            cellFormat[row][col] = { levelId: res.levelId, message: res.message };
          }
        } else {
          cellFormat[row]['id'] = { levelId: res.levelId, message: res.message, rowIds: res.rowIds };
        }
      }
    }

    setCellValidations(cellFormat);
    gridRef.current?.api.hideOverlay();

  }, [validationResult]);

  const headers = useMemo((): Array<Header> => {
    return [
      { field: 'pos', label: '', editable: false, hide: true, sort: 'asc' },
      { field: 'id', label: 'ID', editable: false, hide: true },
      { field: 'surveyId', label: 'Survey', editable: false },
      { field: 'diver', label: 'Diver' },
      { field: 'buddy', label: 'Buddy', hide: true },
      { field: 'siteCode', label: 'Site No.', editable: false },
      { field: 'siteName', label: 'Site Name', editable: false },
      { field: 'latitude', label: 'Latitude' },
      { field: 'longitude', label: 'Longitude' },
      { field: 'date', label: 'Date', editable: false },
      { field: 'vis', label: 'Vis' },
      { field: 'direction', label: 'Direction' },
      { field: 'time', label: 'Time' },
      { field: 'P-Qs', label: 'P-Qs' },
      { field: 'depth', label: 'Depth', editable: false },
      { field: 'method', label: 'Method' },
      { field: 'block', label: 'Block' },
      { field: 'code', label: 'Code' },
      { field: 'species', label: 'Species' },
      { field: 'commonName', label: 'Common Name' },
      { field: 'total', label: 'Total' },
      { field: 'inverts', label: 'Inverts' }
    ];
  }, []);

  const defaultColDef = useMemo((): ColDef => {
    return {
      cellStyle: eh.chooseCellStyle as CellStyle | CellStyleFunc | undefined,
      editable: true,
      enableCellChangeFlash: true,
      filter: true,
      filterParams: { debounceMs: AppConstants.Filter.WAIT_TIME_ON_FILTER_APPLY },
      floatingFilter: true,
      lockVisible: true,
      minWidth: AppConstants.AG_GRID.dataColWidth,
      resizable: true,
      sortable: true,
      suppressKeyboardEvent: (params: SuppressKeyboardEventParams) => eh.overrideKeyboardEvents(params) as boolean,
      suppressMenu: true,
      tooltipValueGetter: toolTipValueGetter,
      valueParser: ({ newValue }) => (newValue ? newValue.trim() : '')
    };
  }, []);

  const createColumns = useCallback((headers: Array<Header>, allMeasurements: Array<SizeCorrection>): ColDef[] => {
    const cols: ColDef[] = [];

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

    headers.map((header, idx) =>
      cols.push({
        keyCreator: (_: KeyCreatorParams) => '' + idx,
        field: header.field,
        headerName: header.label,
        hide: header.hide,
        sort: header.sort,
        cellEditor: 'agTextCellEditor',
        editable: header.editable ?? true,
      })
    );

    allMeasurements.map((m, idx) =>
      cols.push({
        editable: true,
        field: `${idx + 1}`,
        headerComponent: SurveyMeasurementHeader,
        headerName: m.fishSize,
        keyCreator: (params: KeyCreatorParams) => '' + idx,
        width: AppConstants.AG_GRID.measureColWidth,
      })
    );

    cols.push({
      field: 'isInvertSizing',
      headerName: 'Use InvertSizing',
      cellEditor: 'agTextCellEditor'
    });

    return cols;

  }, []);

  const components = useMemo(() => {
    return {
      findReplacePanel: FindReplacePanel,
      loadingOverlay: LoadingOverlay,
      summaryPanel: SurveyCorrectPanel,
      validationPanel: ValidationPanel
    };
  }, []);

  const defaultSideBar = useMemo(
    () => ({
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
    }),
    []
  );

  const summarySideBar = useMemo(
    () => ({
      toolPanels: [
        {
          id: 'findReplace',
          labelDefault: 'Find Replace',
          labelKey: 'findReplace',
          iconKey: 'columns',
          toolPanel: 'findReplacePanel'
        },
        {
          id: 'summaryPanel',
          labelDefault: 'Summary',
          labelKey: 'summary',
          iconKey: 'columns',
          toolPanel: 'summaryPanel'
        }
      ],
      defaultToolPanel: 'summaryPanel'
    }),
    []
  );

  const context = useRef<InternalContext>(
    {
      errors: [],
      highlighted: [],
      popUndo: eh.popUndo,
      pushUndo: eh.pushUndo,
      putRowIds: [],
      undoStack: [],
      fullRefresh: false,
      useOverlay: 'Loading Survey Correction...',
      validations: [],
      pendingPasteUndo: [],
      pasteMode: false
    });

  const [sideBar, setSideBar] = useState(defaultSideBar);

  const onGridReady = ({ api, columnApi }: GridReadyEvent) => {
    getCorrections(surveyId).then((res) => {
      api.hideOverlay();
      if (res.status !== 200) {
        setError(res.data);
        return;
      }
      const { rows, programName, programId, surveyIds } = (res.data as CorrectionRows);

      const unpackedData: Array<ExtCorrectionRow> = rows?.map((data: CorrectionRow, idx: number) => {
        const { measureJson } = { ...data };
        const measure = measureJson ? JSON.parse(measureJson) : {};

        const d = (data as { [key: string]: string | number });
        Object.getOwnPropertyNames(measure).forEach((numKey: string | number) => {
          d[numKey == 0 ? 'inverts' : numKey] = measure[numKey];
        });

        d['inverts'] = d['inverts'] || 0;

        delete data.measureJson;

        if (data.code?.toUpperCase() === 'SND') {
          data.diver = rows.find((row) => row.surveyId === data.surveyId && row.diver)?.diver;
        };

        return { id: (idx + 1) * 100, pos: (idx + 1) * 1000, ...data };
      });

      const ctx = context.current;
      const rowData = [...unpackedData];

      ctx.originalData = JSON.parse(JSON.stringify(rowData));

      ctx.rowData = rowData;
      ctx.rowPos = rowData.map((r) => r.pos).sort((a, b) => a - b);
      ctx.originalRowPos = JSON.parse(JSON.stringify(ctx.rowPos));

      api.setRowData(ctx.rowData.length > 0 ? ctx.rowData : []);

      setMetadata({
        programName, programId,
        surveyIds: surveyIds ? [...surveyIds] : []
      });
      setRowData(rowData);
      setGridApi(api);
      columnApi.autoSizeAllColumns();
    });
  };

  const onCellValueChanged = (e: CellValueChangedEvent) => {
    setUndoSize(eh.handleCellValueChanged(e));
    if (isFiltered) {
      const filterModel = e.api.getFilterModel();
      const field = e.colDef.field;
      if (field && filterModel[field]) {
        filterModel[field].values.push(e.newValue);
        e.api.setFilterModel(filterModel);
      }
    }
    setHasPassedValidation(false);
  };

  const onPasteEnd = useCallback((e: PasteEndEvent) => {
    setUndoSize(eh.handlePasteEnd(e));
  }, []);

  const onCellEditingStopped = useCallback((e: CellEditingStoppedEvent) => {
    setUndoSize(eh.handleCellEditingStopped(e));
  }, []);

  const onRowDataUpdated = (e: RowDataUpdatedEvent) => {
    setUndoSize(context.current.undoStack.length);
  };

  const onFilterChanged = (e: FilterChangedEvent) => {
    e.api.refreshCells();
    const filterModel = e.api.getFilterModel();
    setIsFiltered(Object.getOwnPropertyNames(filterModel).length > 0);
  };

  const validate = async () => {
    setLoading(true);
    const api = gridRef.current?.api;

    if (api) {
      context.current.useOverlay = 'Validating Survey Correction...';
      api.showLoadingOverlay();
      setSideBar(defaultSideBar);

      const bodyDto: CorrectionRequestBody = { ...metadata, rows: packedData(api) };
      const result = await validateSurveyCorrection(surveyId, bodyDto);
      const errors: Array<SurveyValidationError> = result.data.errors;
      const diffSummary: CorrectionDiff = result.data.summary;

      if (errors) {
        setValidationResult(errors);
        setHasPassedValidation(errors && errors.filter((res) => res.levelId === 'BLOCKING').length < 1);
        context.current.validations = errors;
        context.current.diffSummary = diffSummary;
      }
    }
    setLoading(false);
  };

  useEffect(() => {
    if (gridApi && !isFiltered) gridApi.setFilterModel(null);
  }, [gridApi, isFiltered]);

  const onValidate = async () => {
    validate()
      .then(() => setSideBar(summarySideBar));
  };

  const onSubmit = async () => {
    await validate();

    const formattedDiff = context.current.diffSummary?.cellDiffs?.map((s) => {
      const c = context.current.rowData?.find((r) => r.diffRowId === s.diffRowId);

      const mm = ([...unsized, allMeasurements] as Array<SizeCorrection>)
        .find((m) => m.field === `measurements.${s.columnName}`);

      const columnName = mm ? (c?.isInvertSizing?.toUpperCase() === 'YES' ? mm.invertSize : mm.fishSize) : s.columnName;

      return { ...s, columnName };
    });

    setSurveyDiff({ ...context.current.diffSummary, cellDiffs: formattedDiff });

    setEditMode(false);
  };

  const onSubmitConfirm = async () => {
    setEditMode(true);
    setLoading(true);
    const api = gridRef.current?.api;

    if (api) {
      context.current.useOverlay = 'Correcting Survey...';
      api.showLoadingOverlay();
      const result = await submitSurveyCorrection(surveyId, { ...metadata, rows: packedData(api) });

      if (result.data) {
        setRedirect(`/data/job/${result.data}/view`);
      }
    }
  };

  if (redirect) return <Navigate to={redirect} />;

  if (error)
    return (
      <Box m={2}>
        <Alert severity="error" variant="filled">
          {error}
        </Alert>
      </Box>
    );

  const row = rowData ? rowData[0] : null;

  return (
    <>
      <Box display="flex" flexDirection="row" p={1} pb={1}>
        <Box flexGrow={1}>
          {row && (
            <Typography variant="h6">
              {metadata.surveyIds && metadata.surveyIds.length > 1
                ? `Correct ${metadata.surveyIds.length} Surveys`
                : 'Correct Survey Data' + ` [${row.siteCode}, ${row.date}, ${row.depth}] ${metadata.programName}`}
            </Typography>
          )}
        </Box>
        {editMode && (
          <>
            <Box m={1} ml={0}>
              <Button
                variant="outlined"
                disabled={undoSize < 1 || !editMode || loading}
                startIcon={<UndoIcon />}
                onClick={() => eh.handleUndo({ api: gridApi })}
              >
                Undo
              </Button>
            </Box>
            <Box m={1} ml={0} minWidth={150}>
              <Button
                variant="outlined"
                disabled={!isFiltered || !editMode || loading}
                startIcon={<ResetIcon />}
                onClick={() => setIsFiltered(false)}
              >
                Reset Filter
              </Button>
            </Box>
            <Box m={1} ml={0}>
              <Button
                variant="outlined"
                disabled={!editMode || loading}
                onClick={() => gridApi && eh.onClickExcelExport(gridApi, 'Export', true)}
                startIcon={<CloudDownloadIcon />}
              >
                Export
              </Button>
            </Box>
            <Box p={1} minWidth={120}>
              <Button onClick={onValidate} variant="contained" disabled={!editMode || loading} startIcon={<PlaylistAddCheckOutlinedIcon />}>
                Validate
              </Button>
            </Box>
            <Box p={1} minWidth={180}>
              <Button
                onClick={onSubmit}
                variant="contained"
                disabled={!editMode || loading || !hasPassedValidation}
                startIcon={<CloudUploadIcon />}
              >
                Submit Correction
              </Button>
            </Box>
          </>
        )}
      </Box>
      <Box display={editMode ? 'block' : 'none'} flexGrow={1} overflow="hidden" className="ag-theme-material" id="validation-grid">
        <AgGridReact
          animateRows
          context={context.current}
          cellFadeDelay={10}
          cellFlashDelay={10}
          components={components}
          defaultColDef={defaultColDef}
          enableBrowserTooltips
          enableRangeHandle
          enableRangeSelection
          fillHandleDirection="y"
          getContextMenuItems={(params: GetContextMenuItemsParams) => eh.getContextMenuItems(params, eh) as (string | MenuItemDef)[]}
          getRowId={(r) => r.data.id}
          loadingOverlayComponent="loadingOverlay"
          onCellKeyDown={eh.onCellKeyDown}
          onCellEditingStopped={onCellEditingStopped}
          onCellValueChanged={onCellValueChanged}
          onFilterChanged={onFilterChanged}
          onGridReady={onGridReady}
          onPasteEnd={onPasteEnd}
          onPasteStart={eh.onPasteStart}
          onRowDataUpdated={onRowDataUpdated}
          onSortChanged={eh.onSortChanged}
          processDataFromClipboard={eh.processDataFromClipboard}
          suppressColumnVirtualisation={suppressColumnVirtualisation}
          ref={gridRef}
          rowHeight={20}
          rowSelection="multiple"
          sideBar={sideBar}
          undoRedoCellEditing={false}
          columnDefs={createColumns(headers, allMeasurements)}
        >
        </AgGridReact>
      </Box>
      <Box display={editMode ? 'none' : 'flex'} justifyContent="center">
        <Box
          style={{ background: 'white', width: 900 }}
          boxShadow={1}
          padding={3}
          margin={3}
          display="flex"
          justifyContent="center"
          flexDirection="column"
        >
          {hasPassedValidation ? (
            <>
              <Box>
                <Typography variant="h5">Confirm Survey Data Correction?</Typography>
              </Box>
              <Box border={0} borderColor="divider" p={2} margin={3}>
                <SurveyDiff surveyDiff={surveyDiff} />
              </Box>
            </>
          ) : (
            <Box border={0} borderColor="grey" p={2} margin={3}>
              <Typography variant="h6">Survey Correction cannot be submitted until all validation errors are resolved.</Typography>
              {validationResult
                ?.filter((r) => r.levelId === 'BLOCKING')
                .map((res, idx) => (
                  <p key={idx}>{res.message}</p>
                ))}
            </Box>
          )}
          <Box flexDirection="row">
            <Button sx={{ width: '25px', marginLeft: '20%' }} variant="outlined" onClick={() => setEditMode(true)}>
              Cancel
            </Button>
            <Button sx={{ width: '50%', marginLeft: '10px' }} variant="contained" disabled={!hasPassedValidation} onClick={onSubmitConfirm}>
              Apply Correction
            </Button>
          </Box>
        </Box>
      </Box>
    </>
  );
};

export default SurveyCorrect;

