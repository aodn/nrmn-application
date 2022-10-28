import {CloudUpload as CloudUploadIcon, PlaylistAddCheckOutlined as PlaylistAddCheckOutlinedIcon} from '@mui/icons-material/';
import {Alert, Box, Button, Typography} from '@mui/material';
import UndoIcon from '@mui/icons-material/Undo';
import ResetIcon from '@mui/icons-material/LayersClear';
import 'ag-grid-community/dist/styles/ag-theme-material.css';
import 'ag-grid-enterprise';
import {AgGridColumn, AgGridReact} from 'ag-grid-react';
import React, {useEffect, useMemo, useRef, useState} from 'react';
import {Navigate, useParams} from 'react-router-dom';
import CloudDownloadIcon from '@mui/icons-material/CloudDownload';
import {getCorrections, submitSurveyCorrection, validateSurveyCorrection} from '../../../api/api';
import {allMeasurements} from '../../../common/correctionsConstants';
import ValidationPanel from '../../import/panel/ValidationPanel';
import LoadingOverlay from '../../overlays/LoadingOverlay';
import SurveyCorrectPanel from './panel/SurveyCorrectPanel';
import FindReplacePanel from '../../import/panel/FindReplacePanel';
import SurveyMeasurementHeader from './SurveyMeasurementHeader';
import eh from '../../../components/import/DataSheetEventHandlers';

const toolTipValueGetter = ({context, data, colDef}) => {
  if (!context.cellValidations) return;
  const row = data.id;
  const field = colDef.field;
  const error = context.cellValidations[row]?.[field];

  if (error) return error?.message;

  if (context.cellValidations[row]?.id?.levelId === 'DUPLICATE') {
    return 'Duplicate rows';
  }
  return error?.message;
};

const removeNullProperties = (obj) => {
  return obj ? Object.fromEntries(Object.entries(obj).filter((v) => v[1] !== '')) : null;
};

const packedData = (api) => {
  const packedData = [];
  api.forEachNode((rowNode, index) => {
    const data = rowNode.data;
    packedData.push({id: index, ...data, measureJson: removeNullProperties(data.measurements)});
  });
  return packedData;
};

const SurveyCorrect = () => {
  const surveyId = useParams()?.id;
  const gridRef = useRef();

  // FUTURE: useReducer
  const [error, setError] = useState();
  const [editMode, setEditMode] = useState(true);
  const [rowData, setRowData] = useState();
  const [gridApi, setGridApi] = useState();
  const [isFiltered, setIsFiltered] = useState(false);
  const [loading, setLoading] = useState(false);
  const [validationResult, setValidationResult] = useState();
  const [surveyDiff, setSurveyDiff] = useState([]);
  const [cellValidations, setCellValidations] = useState([]);
  const [redirect, setRedirect] = useState();
  const [undoSize, setUndoSize] = useState(0);
  const [metadata, setMetadata] = useState({programName: 'NONE', surveyIds: [], isExtended: false});

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
    if (gridRef.current?.api) {
      gridRef.current.api.gridOptionsWrapper.gridOptions.context.cellValidations = cellValidations;
      gridRef.current.api.redrawRows();
    }
  }, [cellValidations]);

  useEffect(() => {
    if (!validationResult) return;
    const cellFormat = [];
    for (const res of validationResult) {
      for (const row of res.rowIds) {
        if (!cellFormat[row]) cellFormat[row] = {};
        if (res.columnNames) {
          for (const col of res.columnNames) {
            cellFormat[row][col] = {levelId: res.levelId, message: res.message};
          }
        } else {
          cellFormat[row]['id'] = {levelId: res.levelId, message: res.message, rowIds: res.rowIds};
        }
      }
    }
    setCellValidations(cellFormat);
    gridRef.current.api.hideOverlay();
  }, [validationResult]);

  const headers = useMemo(() => {
    return [
      {field: 'pos', label: '', editable: false, hide: true, sort: 'asc'},
      {field: 'id', label: '', editable: false, hide: true},
      {field: 'surveyId', label: 'Survey', editable: false},
      {field: 'diver', label: 'Diver'},
      {field: 'siteCode', label: 'Site No.', editable: false},
      {field: 'siteName', label: 'Site Name', editable: false},
      {field: 'latitude', label: 'Latitude'},
      {field: 'longitude', label: 'Longitude'},
      {field: 'date', label: 'Date', editable: false},
      {field: 'vis', label: 'Vis'},
      {field: 'direction', label: 'Direction'},
      {field: 'time', label: 'Time'},
      {field: 'P-Qs', label: 'P-Qs'},
      {field: 'depth', label: 'Depth', editable: false},
      {field: 'method', label: 'Method'},
      {field: 'block', label: 'Block'},
      {field: 'code', label: 'Code'},
      {field: 'species', label: 'Species'},
      {field: 'commonName', label: 'Common Name'},
      {field: 'total', label: 'Total'},
      {field: 'inverts', label: 'Inverts'}
    ];
  }, []);

  const defaultColDef = useMemo(() => {
    return {
      cellStyle: eh.chooseCellStyle,
      editable: true,
      enableCellChangeFlash: true,
      filter: true,
      floatingFilter: true,
      minWidth: 40,
      resizable: true,
      sortable: true,
      suppressKeyboardEvent: eh.overrideKeyboardEvents,
      suppressMenu: true,
      tooltipValueGetter: toolTipValueGetter,
      valueParser: ({newValue}) => (newValue ? newValue.trim() : '')
    };
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

  const context = useMemo(
    () => ({
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
    }),
    []
  );

  const [sideBar, setSideBar] = useState(defaultSideBar);

  const onGridReady = ({api}) => {
    getCorrections(surveyId).then((res) => {
      api.hideOverlay();
      if (res.status !== 200) {
        setError(res.data);
        return;
      }
      const {rows, programName, programId, surveyIds} = res.data;
      const unpackedData = rows.map((data, idx) => {
        const measurements = data.observationIds === '' ? {} : JSON.parse(data.measureJson);
        const inverts = measurements[0] || '0';
        delete measurements[0];
        const observationIds = data.observationIds === '' ? [] : JSON.parse(data.observationIds);
        delete data.measureJson;
        return {id: (idx + 1) * 100, pos: (idx + 1) * 1000, ...data, inverts, observationIds, measurements};
      });
      const isExtended = unpackedData.includes((r) => Object.keys(r.measurements).includes((k) => k > 28));
      const context = api.gridOptionsWrapper.gridOptions.context;
      const rowData = [...unpackedData];
      context.originalData = JSON.parse(JSON.stringify(rowData));
      context.rowData = rowData;
      context.rowPos = rowData.map((r) => r.pos).sort((a, b) => a - b);
      api.setRowData(context.rowData.length > 0 ? context.rowData : null);
      setMetadata({programName, programId, isExtended, surveyIds: [...surveyIds]});
      setRowData(rowData);
      setGridApi(api);
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
  };

  const onRowDataUpdated = (e) => {
    const ctx = e.api.gridOptionsWrapper.gridOptions.context;
    e.columnApi.autoSizeAllColumns();
    setUndoSize(ctx.undoStack.length);
  };

  const onFilterChanged = (e) => {
    e.api.refreshCells();
    const filterModel = e.api.getFilterModel();
    setIsFiltered(Object.getOwnPropertyNames(filterModel).length > 0);
  };

  const validate = async () => {
    setLoading(true);
    const api = gridRef.current.api;
    const context = api.gridOptionsWrapper.gridOptions.context;
    context.useOverlay = 'Validating Survey Correction...';
    api.showLoadingOverlay();
    setSideBar(defaultSideBar);

    const bodyDto = {...metadata, rows: packedData(api)};
    const result = await validateSurveyCorrection(surveyId, bodyDto);
    setValidationResult(result.data.errors);
    context.validations = result.data.errors;
    setLoading(false);
  };

  useEffect(() => {
    if (gridApi && !isFiltered) gridApi.setFilterModel(null);
  }, [gridApi, isFiltered]);

  const onValidate = async () => {
    await validate();
    setSideBar(summarySideBar);
  };

  const diffSheet = () => {
    const api = gridRef.current.api;
    const context = api.gridOptionsWrapper.gridOptions.context;

    const edited = context.rowData.reduce((acc, r) => {
      const rowPos = context.rowPos.indexOf(r.pos) + 1;
      const original = context.originalData.find((o) => o.id === r.id);
      if (!original) {
        acc.push(`Row ${rowPos} added`);
        return acc;
      }
      for (var key of Object.keys(r)) {
        if(key === 'observationIds') continue;
        if (typeof r[key] === 'object') {
          if (JSON.stringify(removeNullProperties(r[key])) !== JSON.stringify(original[key])) acc.push(`Row ${rowPos} ${key} changed`);
        } else if (r[key] !== original[key]) {
          acc.push(`Row ${rowPos}: ${key} change from ${original[key]} to ${r[key]}`);
        }
      }
      return acc;
    }, []);

    const deleted = context.originalData.reduce((acc, r) => {
      const rowPos = context.rowPos.indexOf(r.pos) + 1;
      if (!context.rowData.find((o) => o.id === r.id)) {
        acc.push(`Row ${rowPos} deleted`);
      }
      return acc;
    }, []);

    setSurveyDiff([...edited, ...deleted]);
  };

  const onSubmit = async () => {
    await validate();

    diffSheet();

    setEditMode(false);
  };

  const onSubmitConfirm = async () => {
    setEditMode(true);
    setLoading(true);
    const api = gridRef.current.api;
    const context = api.gridOptionsWrapper.gridOptions.context;
    context.useOverlay = 'Correcting Survey...';
    api.showLoadingOverlay();
    const result = await submitSurveyCorrection(surveyId, {...metadata, rows: packedData(gridRef.current.api)});
    setRedirect(`/data/job/${result.data}/view`);
  };

  const canSubmitCorrection = validationResult && validationResult.filter((res) => res.levelId === 'BLOCKING').length < 1;

  if (redirect) return <Navigate push to={redirect} />;

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
              {metadata.surveyIds.length > 1
                ? `Correct ${metadata.surveyIds.length} Surveys`
                : 'Correct Survey' +
                  ` [${row.siteCode}, ${row.date}, ${row.depth}] ` +
                  metadata.programName +
                  (metadata.isExtended ? ' Extended' : '')}
            </Typography>
          )}
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
          <Button variant="outlined" onClick={() => eh.onClickExcelExport(gridApi, 'Export', true)} startIcon={<CloudDownloadIcon />}>
            Export
          </Button>
        </Box>
        <Box p={1} minWidth={120}>
          <Button onClick={onValidate} variant="contained" disabled={!editMode || loading} startIcon={<PlaylistAddCheckOutlinedIcon />}>
            Validate
          </Button>
        </Box>
        <Box p={1} minWidth={180}>
          <Button onClick={onSubmit} variant="contained" disabled={!editMode || loading} startIcon={<CloudUploadIcon />}>
            Submit Correction
          </Button>
        </Box>
      </Box>
      <Box display={editMode ? 'block' : 'none'} flexGrow={1} overflow="hidden" className="ag-theme-material" id="validation-grid">
        <AgGridReact
          animateRows
          cellFadeDelay={10}
          cellFlashDelay={10}
          components={components}
          defaultColDef={defaultColDef}
          enableBrowserTooltips
          enableRangeHandle
          enableRangeSelection
          fillHandleDirection="y"
          getContextMenuItems={(e) => eh.getContextMenuItems(e, eh)}
          getRowId={(r) => r.data.id}
          gridOptions={{context}}
          loadingOverlayComponent="loadingOverlay"
          onCellEditingStopped={onCellEditingStopped}
          onCellValueChanged={onCellValueChanged}
          onFilterChanged={onFilterChanged}
          onGridReady={onGridReady}
          onPasteEnd={onPasteEnd}
          onPasteStart={eh.onPasteStart}
          onRowDataUpdated={onRowDataUpdated}
          onSortChanged={eh.onSortChanged}
          ref={gridRef}
          rowHeight={20}
          rowSelection="multiple"
          sideBar={sideBar}
          tabToNextCell={eh.onTabToNextCell}
          undoRedoCellEditing={false}
        >
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
          {headers.map((header, idx) => (
            <AgGridColumn
              key={idx}
              field={header.field}
              headerName={header.label}
              hide={header.hide}
              sort={header.sort}
              cellEditor="agTextCellEditor"
              editable={header.editable ?? true}
            />
          ))}
          {allMeasurements.map((_, idx) => {
            const field = `measurements.${idx + 1}`;
            return <AgGridColumn editable field={field} headerComponent={SurveyMeasurementHeader} key={idx} width={35} />;
          })}
          <AgGridColumn field="isInvertSizing" headerName="Use Invert Sizing" cellEditor="agTextCellEditor" />
        </AgGridReact>
      </Box>
      <Box display={editMode ? 'none' : 'flex'} justifyContent="center">
        <Box
          style={{background: 'white', width: 900}}
          boxShadow={1}
          padding={3}
          margin={3}
          display="flex"
          justifyContent="center"
          flexDirection="column"
        >
          {canSubmitCorrection ? (
            <>
              <Box>
                <Typography variant="h5">Confirm Survey Correction?</Typography>
              </Box>
              <Box border={0} borderColor="grey" p={2} margin={3}>
                {surveyDiff?.map((res, idx) => (
                  <p key={idx}>{res}</p>
                ))}
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
            <Button sx={{width: '25px', marginLeft: '20%'}} variant="outlined" onClick={() => setEditMode(true)}>
              Cancel
            </Button>
            <Button sx={{width: '50%', marginLeft: '10px'}} variant="contained" disabled={!canSubmitCorrection} onClick={onSubmitConfirm}>
              Apply Correction
            </Button>
          </Box>
        </Box>
      </Box>
    </>
  );
};

export default SurveyCorrect;
