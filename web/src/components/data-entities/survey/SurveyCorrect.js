import {Box, Button, Typography} from '@mui/material';
import 'ag-grid-community/dist/styles/ag-theme-material.css';
import 'ag-grid-enterprise';
import {AgGridColumn, AgGridReact} from 'ag-grid-react';
import React, {useMemo, useState} from 'react';
import {useParams} from 'react-router-dom';
import {getCorrections} from '../../../api/api';
import {allMeasurements} from '../../../common/constants';
import LoadingOverlay from '../../overlays/LoadingOverlay';
import SummaryPanel from './panel/SummaryPanel';
import SurveyMeasurementHeader from './SurveyMeasurementHeader';
import {PlaylistAddCheckOutlined as PlaylistAddCheckOutlinedIcon} from '@mui/icons-material/';

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
      editable: true,
      enableCellChangeFlash: false,
      filter: false,
      floatingFilter: false,
      resizable: false,
      sortable: true,
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
      {field: 'visibility', label: 'Visibility', hide: false}
      // {label: 'Direction', hide: false},
      // {label: 'Latitude', hide: false},
      // {label: 'Longitude', hide: false},
      // {label: 'observable_item_id', hide: true},
      // {label: 'Species Name', hide: false},
      // {label: 'Letter Code', hide: false},
      // {label: 'Method', hide: false},
      // {label: 'Block', hide: false},
      // {label: 'Survey Not Done', hide: false, isBoolean: true},
      // {label: 'Use Invert Sizing', hide: false, isBoolean: true}
    ];
  }, []);

  const onGridReady = ({api}) => {
    setGridApi(api);
    getCorrections(surveyId).then((res) => {
      api.hideOverlay();
      if (res.status !== 200) return;
      const unpackedData = res.data.map((data, idx) => {
        const measurements = JSON.parse(data.measurementJson);
        delete data.measurementJson;
        return {id: idx + 1, ...data, measurements};
      });
      setRowData(unpackedData);
    });
  };

  const onSaveValidate = () => {
    const packedData = [];
    gridApi.forEachNode((rowNode, index) => {
      const data = rowNode.data;
      packedData.push({id: index, ...data, 19: JSON.stringify(data[19])});
    });
  };

  const onModelUpdated = () => {};

  return (
    <>
      <Box display="flex" flexDirection="row" p={1} pb={1}>
        <Box flexGrow={1}>
          <Typography variant="h4">Survey Correction</Typography>
        </Box>
        <Box p={1} minWidth={180}>
          <Button onClick={onSaveValidate} variant="contained" startIcon={<PlaylistAddCheckOutlinedIcon />}>
            {`Save & Validate`}
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
                field={idx.toString()}
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
            return <AgGridColumn field={field} headerComponent={SurveyMeasurementHeader} key={idx} width={35} />;
          })}
        </AgGridReact>
      </Box>
    </>
  );
};

export default SurveyCorrect;
