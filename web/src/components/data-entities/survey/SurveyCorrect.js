import {Box, Typography} from '@mui/material';
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

const SurveyCorrect = () => {
  const surveyId = useParams()?.id;

  const [rowData, setRowData] = useState(null);

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
      valueParser: ({newValue}) => (newValue ? newValue.trim() : ''),
      width: 'auto'
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
      defaultToolPanel: 'summaryPanel'
    };
  }, []);

  const headers = useMemo(() => {
    return [
      {label: 'Observation', hide: false},
      {label: 'Survey', hide: true},
      {label: 'Diver ID', hide: true},
      {label: 'Diver', hide: false},
      {label: 'Site Code', hide: true},
      {label: 'Depth', hide: false},
      {label: 'Survey Date', hide: false},
      {label: 'Survey Time', hide: false},
      {label: 'Visibility', hide: false},
      {label: 'Direction', hide: false},
      {label: 'Latitude', hide: false},
      {label: 'Longitude', hide: false},
      {label: 'observable_item_id', hide: true},
      {label: 'Species Name', hide: false},
      {label: 'Letter Code', hide: false},
      {label: 'Method', hide: false},
      {label: 'Block', hide: false},
      {label: 'Survey Not Done', hide: false}
    ];
  }, []);

  const onGridReady = ({api}) =>
    getCorrections(surveyId).then((res) => {
      api.hideOverlay();
      if (res.status !== 200) return;
      const unpackedData = res.data.map((d) => {
        return {...d, 18: JSON.parse(d.at(-1))};
      });
      setRowData(unpackedData);
    });

  const onModelUpdated = () => {};

  return (
    <>
      <Box display="flex" flexDirection="row" p={1} pb={1}>
        <Box flexGrow={1}>
          <Typography variant="h4">Survey Correction</Typography>
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
          {headers.map((header, idx) => (
            <AgGridColumn key={idx} field={idx.toString()} headerName={header.label} hide={header.hide} />
          ))}
          <AgGridColumn field={headers.length + '.0'} headerName="Unsized" />
          {allMeasurements.map((_, idx) => {
            const id = headers.length + '.' + idx;
            return <AgGridColumn field={id} headerComponent={SurveyMeasurementHeader} key={id} width={35} />;
          })}
        </AgGridReact>
      </Box>
    </>
  );
};

export default SurveyCorrect;
