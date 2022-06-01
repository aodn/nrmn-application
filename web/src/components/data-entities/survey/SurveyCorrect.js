import {Box, Button, Typography} from '@mui/material';
import 'ag-grid-community/dist/styles/ag-theme-material.css';
import 'ag-grid-enterprise';
import {AgGridColumn, AgGridReact} from 'ag-grid-react';
import React, {useMemo, useState} from 'react';
import {useParams} from 'react-router-dom';
import {getCorrections, validateSurveyCorrection, submitSurveyCorrection} from '../../../api/api';
import {allMeasurements} from '../../../common/constants';
import LoadingOverlay from '../../overlays/LoadingOverlay';
import SummaryPanel from './panel/SummaryPanel';
import SurveyMeasurementHeader from './SurveyMeasurementHeader';
import {PlaylistAddCheckOutlined as PlaylistAddCheckOutlinedIcon, CloudUpload as CloudUploadIcon} from '@mui/icons-material/';

const SurveyCorrect = () => {
  const surveyId = useParams()?.id;

  const [rowData, setRowData] = useState();
  const [gridApi, setGridApi] = useState();

  const components = useMemo(() => {
    return {
      loadingOverlay: LoadingOverlay,
      summaryPanel: SummaryPanel
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
      {field: 'depth', label: 'Depth', hide: false},
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
    setGridApi(api);
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
    gridApi.forEachNode((rowNode, index) => {
      const data = rowNode.data;
      packedData.push({id: index, ...data, 19: JSON.stringify(data[19])});
    });
    return packedData;
  };

  const onValidate = () => validateSurveyCorrection(surveyId, packedData());

  const onSubmit = () => submitSurveyCorrection(surveyId, packedData());

  const onModelUpdated = () => {};

  return (
    <>
      <Box display="flex" flexDirection="row" p={1} pb={1}>
        <Box flexGrow={1}>
          <Typography variant="h4">Survey Correction</Typography>
        </Box>
        <Box p={1} minWidth={120}>
          <Button onClick={onValidate} variant="contained" startIcon={<PlaylistAddCheckOutlinedIcon />}>
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
          animateRows
          cellFadeDelay={100}
          cellFlashDelay={100}
          components={components}
          context={{useOverlay: 'Loading Survey Correction'}}
          defaultColDef={defaultColDef}
          enableBrowserTooltips
          enableRangeSelection
          getRowId={(r) => r.data[0]}
          loadingOverlayComponent="loadingOverlay"
          onGridReady={onGridReady}
          onModelUpdated={onModelUpdated}
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
              <AgGridColumn key={idx} field={header.field} headerName={header.label} hide={header.hide} cellEditor="agTextCellEditor" />
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
