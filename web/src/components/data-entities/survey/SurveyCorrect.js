import {Box, Button, Typography} from '@mui/material';
import 'ag-grid-community/dist/styles/ag-theme-material.css';
import 'ag-grid-enterprise';
import {AgGridColumn, AgGridReact} from 'ag-grid-react';
import React, {useRef, useMemo, useState} from 'react';
import {useParams} from 'react-router-dom';
import {getCorrections, validateSurveyCorrection, submitSurveyCorrection} from '../../../api/api';
import {allMeasurements} from '../../../common/constants';
import LoadingOverlay from '../../overlays/LoadingOverlay';
import SummaryPanel from './panel/SummaryPanel';
import SurveyMeasurementHeader from './SurveyMeasurementHeader';
import {PlaylistAddCheckOutlined as PlaylistAddCheckOutlinedIcon, CloudUpload as CloudUploadIcon} from '@mui/icons-material/';
import ValidationPanel from '../../import/panel/ValidationPanel';

const SurveyCorrect = () => {
  const surveyId = useParams()?.id;
  const gridRef = useRef();
  const [rowData, setRowData] = useState();

  const components = useMemo(() => {
    return {
      loadingOverlay: LoadingOverlay,
      summaryPanel: SummaryPanel,
      validationPanel: ValidationPanel
    };
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
      valueParser: ({newValue}) => (newValue ? newValue.trim() : '')
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

  const headers = useMemo(() => {
    return [
      {field: 'id', label: '', hide: false},
      {field: 'surveyId', label: 'Survey', hide: true},
      {field: 'diverId', label: 'Diver ID', hide: true},
      {field: 'initials', label: 'Diver', hide: false},
      {field: 'siteCode', label: 'Site Code', hide: true},
      {field: 'depth', label: 'Depth', hide: false, editable: true},
      {field: 'surveyDate', label: 'Survey Date', hide: false},
      {field: 'surveyTime', label: 'Survey Time', hide: false},
      {field: 'visibility', label: 'Visibility', hide: false},
      {field: 'direction', label: 'Direction', hide: false},
      {field: 'latitude', label: 'Latitude', hide: false},
      {field: 'longitude', label: 'Longitude', hide: false},
      {field: 'observableItemId', hide: true},
      {field: 'observableItemName', label: 'Species Name', hide: false},
      {field: 'letterCode', label: 'Letter Code', hide: false},
      {field: 'methodId', label: 'Method', hide: false},
      {field: 'blockNum', label: 'Block', hide: false},
      {field: 'surveyNotDone', label: 'Survey Not Done', hide: false, isBoolean: true},
      {field: 'useInvertSizing', label: 'Use Invert Sizing', hide: false, isBoolean: true}
    ];
  }, []);

  const onGridReady = ({api}) => {
    getCorrections(surveyId).then((res) => {
      api.hideOverlay();
      if (res.status !== 200) return;
      const unpackedData = res.data.map((data, idx) => {
        const measurements = JSON.parse(data.measurementJson);
        const observationIds = JSON.parse(data.observationIds);
        delete data.measurementJson;
        return {id: idx + 1, ...data, observationIds, measurements};
      });
      setRowData(unpackedData);
    });
  };

  const packedData = () => {
    const packedData = [];
    gridRef.current.api.forEachNode((rowNode, index) => {
      const data = rowNode.data;
      packedData.push({id: index, ...data, 19: JSON.stringify(data[19])});
    });
    return packedData;
  };

  const gridOptions = {context: {useOverlay: 'Loading Survey Correction...', validations: []}};

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
            const rowPosition = rowData.find((d) => d.id === r.id)?.id;
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
          const rowPositions = e.rowIds.map((r) => rowData.find((d) => d.id === r)?.id).filter((r) => r);
          const rowNumbers = rowPositions.map((r) => rowPos.indexOf(r) + 1);
          summary = [{rowIds: e.rowIds, columnNames: e.columnNames, rowNumbers}];
        }
        tree[e.levelId.toLowerCase()].push({key: `err-${e.id}`, message: e.message, count: e.rowIds.length, description: summary});
      });
    return tree;
  };

  const onValidate = async () => {
    const result = await validateSurveyCorrection(surveyId, packedData());
    let context = gridRef.current.api.gridOptionsWrapper.gridOptions.context;

    const rowPos = rowData.map((r) => r.id).sort((a, b) => a - b);
    const errorTree = generateErrorTree(rowData,rowPos, result.data.errors);
    context.validations = errorTree;
  };

  const onSubmit = () => submitSurveyCorrection(surveyId, packedData());

  return (
    <>
      <Box display="flex" flexDirection="row" p={1} pb={1}>
        <Box flexGrow={1}>
          <Typography variant="h4">Survey Correction</Typography>
        </Box>
        <Box p={1} minWidth={120}>
          <Button onClick={() => onValidate(gridOptions)} variant="contained" startIcon={<PlaylistAddCheckOutlinedIcon />}>
            {`Validate`}
          </Button>
        </Box>
        <Box p={1} minWidth={180}>
          <Button onClick={onSubmit} variant="contained" startIcon={<CloudUploadIcon />}>
            Submit Correction
          </Button>
        </Box>
      </Box>
      <Box flexGrow={1} overflow="hidden" className="ag-theme-material" id="validation-grid">
        <AgGridReact
          ref={gridRef}
          gridOptions={gridOptions}
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
          sideBar={defaultSideBar}
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
              <AgGridColumn key={idx} field={header.field} headerName={header.label} hide={header.hide} cellEditor="agTextCellEditor" editable={header.editable ?? false} />
            )
          )}
          <AgGridColumn field={'measurements.0'} headerName="Unsized" />
          {allMeasurements.map((_, idx) => {
            const field = `measurements.${idx + 1}`;
            return <AgGridColumn editable field={field} headerComponent={SurveyMeasurementHeader} key={idx} width={35} />;
          })}
        </AgGridReact>
      </Box>
    </>
  );
};

export default SurveyCorrect;
