import {CloudUpload as CloudUploadIcon, PlaylistAddCheckOutlined as PlaylistAddCheckOutlinedIcon} from '@mui/icons-material/';
import {Box, Button, Typography} from '@mui/material';
import {grey, orange, red} from '@mui/material/colors';
import 'ag-grid-community/dist/styles/ag-theme-material.css';
import 'ag-grid-enterprise';
import {AgGridColumn, AgGridReact} from 'ag-grid-react';
import React, {useEffect, useMemo, useRef, useState} from 'react';
import {Navigate, useParams} from 'react-router-dom';
import {getCorrections, submitSurveyCorrection, validateSurveyCorrection} from '../../../api/api';
import {allMeasurements} from '../../../common/correctionsConstants';
import ValidationPanel from '../../import/panel/ValidationPanel';
import LoadingOverlay from '../../overlays/LoadingOverlay';
import SummaryPanel from './panel/SummaryPanel';
import SurveyMeasurementHeader from './SurveyMeasurementHeader';

const chooseCellStyle = (params) => {
  // Grey-out the first  column containing the row number
  if (params.colDef.field === 'id') return {color: grey[500]};

  if (!params.context.cellValidations) return;

  // Highlight cell validations
  const row = params.data.id;
  const field = params.colDef.field;
  const level = params.context.cellValidations[row]?.[field]?.levelId;
  switch (level) {
    case 'BLOCKING':
      return {backgroundColor: red[100]};
    case 'WARNING':
      return {backgroundColor: orange[100]};
    case 'INFO':
      return {backgroundColor: grey[100]};
    default:
      return null;
  }
};

const toolTipValueGetter = ({context, data, colDef}) => {
  if (!context.cellValidations) return;
  const row = data.id;
  const field = colDef.field;
  const error = context.cellValidations[row]?.[field];
  if (error?.levelId === 'DUPLICATE') {
    const rowPositions = error.rowIds.map((r) => context.rowData.find((d) => d.id === r)?.pos).filter((r) => r);
    const duplicates = rowPositions.map((r) => context.rowPos.indexOf(r) + 1);
    return duplicates.length > 1 ? 'Rows are duplicated: ' + duplicates.join(', ') : 'Duplicate rows have been removed';
  }
  return error?.message;
};

const removeNullProperties = (obj) => {
  return Object.fromEntries(Object.entries(obj).filter((v) => v[1] !== ''));
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
  const [editMode, setEditMode] = useState(true);
  const [rowData, setRowData] = useState();
  const [loading, setLoading] = useState(false);
  const [validationResult, setValidationResult] = useState();
  const [cellValidations, setCellValidations] = useState([]);
  const [redirect, setRedirect] = useState();

  useEffect(() => {
    if (gridRef.current?.api) {
      gridRef.current.api.gridOptionsWrapper.gridOptions.context.cellValidations = [...cellValidations];
      gridRef.current.api.redrawRows();
    }
  }, [cellValidations]);

  useEffect(() => {
    if (!validationResult) return;
    const cellFormat = [];
    for (const res of validationResult) {
      for (const row of res.rowIds) {
        for (const col of res.columnNames) {
          if (!cellFormat[row]) cellFormat[row] = {};
          cellFormat[row][col] = {levelId: res.levelId, message: res.message};
        }
      }
    }
    setCellValidations(cellFormat);
    gridRef.current.api.hideOverlay();
  }, [validationResult]);

  const headers = useMemo(() => {
    return [
      {field: 'id', label: '', hide: false},
      {field: 'surveyId', label: 'Survey', hide: false},
      {field: 'diverId', label: 'Diver ID', hide: true},
      {field: 'diver', label: 'Diver', hide: false, editable: false},
      {field: 'siteCode', label: 'Site Code', hide: true},
      {field: 'depth', label: 'Depth', hide: false, editable: false},
      {field: 'date', label: 'Survey Date', hide: false, editable: false},
      {field: 'time', label: 'Survey Time', hide: false, editable: false},
      {field: 'vis', label: 'Visibility', hide: false, editable: false},
      {field: 'direction', label: 'Direction', hide: false, editable: false},
      {field: 'latitude', label: 'Latitude', hide: false, editable: false},
      {field: 'longitude', label: 'Longitude', hide: false, editable: false},
      {field: 'observableItemId', hide: true},
      {field: 'species', label: 'Species Name', hide: false, editable: false},
      {field: 'letterCode', label: 'Letter Code', hide: false, editable: false},
      {field: 'method', label: 'Method', hide: false, editable: false},
      {field: 'block', label: 'Block', hide: false, editable: false},
      {field: 'surveyNotDone', label: 'Survey Not Done', hide: false, isBoolean: false, editable: false},
      {field: 'isInvertSizing', label: 'Use Invert Sizing', hide: false, isBoolean: false, editable: false}
    ];
  }, []);

  const defaultColDef = useMemo(() => {
    return {
      editable: false,
      enableCellChangeFlash: false,
      filter: false,
      floatingFilter: false,
      resizable: false,
      sortable: false,
      suppressMenu: false,
      cellStyle: chooseCellStyle,
      tooltipValueGetter: toolTipValueGetter,
      valueParser: ({newValue}) => (newValue ? newValue.trim() : '')
    };
  }, []);

  const components = useMemo(() => {
    return {
      loadingOverlay: LoadingOverlay,
      summaryPanel: SummaryPanel,
      validationPanel: ValidationPanel
    };
  }, []);

  const defaultSideBar = useMemo(() => {
    return {
      toolPanels: [
        {
          id: 'summaryPanel',
          labelDefault: 'Summary',
          labelKey: 'summary',
          iconKey: 'columns',
          toolPanel: 'summaryPanel'
        }
      ],
      defaultToolPanel: ''
    };
  }, []);

  const [sideBar, setSideBar] = useState(defaultSideBar);

  const onGridReady = ({api}) => {
    getCorrections(surveyId).then((res) => {
      api.hideOverlay();
      if (res.status !== 200) return;
      const unpackedData = res.data.map((data, idx) => {
        const measurements = data.observationIds === '' ? {} : JSON.parse(data.measureJson);
        const observationIds = data.observationIds === '' ? {} : JSON.parse(data.observationIds);
        delete data.measureJson;
        return {id: idx + 1, ...data, observationIds, measurements};
      });
      setRowData(unpackedData);
    });
  };

  const validate = async () => {
    setLoading(true);
    const api = gridRef.current.api;
    const context = api.gridOptionsWrapper.gridOptions.context;
    context.rowData = [...rowData];
    context.useOverlay = 'Validating Survey Correction...';
    api.showLoadingOverlay();
    setSideBar(defaultSideBar);
    const result = await validateSurveyCorrection(surveyId, packedData(api));
    setValidationResult(result.data.errors);
    context.validations = result.data.errors;
    setLoading(false);
  };

  const onValidate = async () => {
    await validate();
    setSideBar((s) => ({
      ...s,
      defaultToolPanel: 'summaryPanel'
    }));
  };

  const onSubmit = async () => {
    await validate();
    setEditMode(false);
  };

  const onSubmitConfirm = async () => {
    setEditMode(true);
    setLoading(true);
    const api = gridRef.current.api;
    const context = api.gridOptionsWrapper.gridOptions.context;
    context.useOverlay = 'Correcting Survey...';
    api.showLoadingOverlay();
    const result = await submitSurveyCorrection(surveyId, packedData(gridRef.current.api));
    setRedirect(`/data/job/${result.data}/view`);
  };

  const canSubmitCorrection = validationResult && validationResult.filter((res) => res.levelId === 'BLOCKING').length < 1;

  if (redirect) return <Navigate push to={redirect} />;

  return (
    <>
      <Box display="flex" flexDirection="row" p={1} pb={1}>
        <Box flexGrow={1}>
          <Typography variant="h6">Correct Survey {rowData && '[' + rowData[0].siteCode + ', ' + rowData[0].date + ', ' + rowData[0].depth + '.' + rowData[0].surveyNum + ']'}</Typography>
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
            disabled={!canSubmitCorrection || !editMode || loading}
            startIcon={<CloudUploadIcon />}
          >
            Submit Correction
          </Button>
        </Box>
      </Box>
      <Box display={editMode ? 'block' : 'none'} flexGrow={1} overflow="hidden" className="ag-theme-material" id="validation-grid">
        <AgGridReact
          ref={gridRef}
          gridOptions={{context: {useOverlay: 'Loading Survey Correction...', validations: []}}}
          animateRows
          cellFadeDelay={100}
          cellFlashDelay={100}
          components={components}
          defaultColDef={defaultColDef}
          enableBrowserTooltips
          enableRangeSelection
          getRowId={(r) => r.data.id}
          loadingOverlayComponent="loadingOverlay"
          onGridReady={onGridReady}
          onRowDataUpdated={(e) => e.columnApi.autoSizeAllColumns()}
          pivotMode={false}
          rowData={rowData}
          rowHeight={20}
          rowSelection="multiple"
          sideBar={sideBar}
        >
          {headers.map((header, idx) =>
            header.isBoolean ? (
              <AgGridColumn
                key={idx}
                field={header.field}
                headerName={header.label}
                hide={header.hide}
                cellEditor="agSelectCellEditor"
                cellEditorParams={{values: [true, false]}}
                valueFormatter={(e) => (e.value === true ? 'Yes' : 'No')}
              />
            ) : (
              <AgGridColumn
                key={idx}
                field={header.field}
                headerName={header.label}
                hide={header.hide}
                cellEditor="agTextCellEditor"
                editable={header.editable ?? false}
              />
            )
          )}
          <AgGridColumn editable field={'measurements.0'} headerName="Unsized" />
          {allMeasurements.map((_, idx) => {
            const field = `measurements.${idx + 1}`;
            return <AgGridColumn editable field={field} headerComponent={SurveyMeasurementHeader} key={idx} width={35} />;
          })}
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
          <Box>
            <Typography variant="h5">Confirm Survey Correction?</Typography>
          </Box>
          <Box border={0} borderColor="grey" p={2} margin={3}>
            {validationResult?.map((res, idx) => (
              <p key={idx}>{res.message}</p>
            ))}
          </Box>
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
