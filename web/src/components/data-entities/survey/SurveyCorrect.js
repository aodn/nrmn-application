import {Box, Typography} from '@mui/material';
import React, {useState} from 'react';
import {useParams} from 'react-router-dom';
import {getCorrections} from '../../../api/api';
import {AgGridColumn, AgGridReact} from 'ag-grid-react';
import {extendedMeasurements, measurements} from '../../../common/constants';
import LoadingOverlay from '../../overlays/LoadingOverlay';
import SummaryPanel from './panel/SummaryPanel';

import 'ag-grid-community/dist/styles/ag-theme-material.css';
import 'ag-grid-enterprise';

const headers = [
  {label: 'Observation', visible: true},
  {label: 'Survey', visible: false},
  {label: 'Diver ID', visible: false},
  {label: 'Diver', visible: true},
  {label: 'Site Code', visible: false},
  {label: 'Depth', visible: true},
  {label: 'Survey Date', visible: true},
  {label: 'Survey Time', visible: true},
  {label: 'Visibility', visible: true},
  {label: 'Direction', visible: true},
  {label: 'Latitude', visible: true},
  {label: 'Longitude', visible: true},
  {label: 'observable_item_id', visible: false},
  {label: 'Species Name', visible: true},
  {label: 'Letter Code', visible: true},
  {label: 'Method', visible: true},
  {label: 'Block', visible: true},
  {label: 'Survey Not Done', visible: true}
];

const defaultSideBar = {
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

const measurementColumns = measurements.concat(extendedMeasurements);

const SurveyCorrect = () => {
  const surveyId = useParams()?.id;

  const [rowData, setRowData] = useState(null);

  const onGridReady = () =>
    getCorrections(surveyId).then((res) => {
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
        {surveyId && (
          <AgGridReact
            getRowId={(r) => r.data[0]}
            cellFlashDelay={100}
            rowData={rowData}
            cellFadeDelay={100}
            defaultColDef={{
              editable: false,
              sortable: true,
              resizable: true,
              width: 'auto',
              filter: true,
              floatingFilter: true,
              suppressMenu: true,
              enableCellChangeFlash: true,
              valueParser: ({newValue}) => (newValue ? newValue.trim() : '')
            }}
            rowHeight={20}
            sideBar={defaultSideBar}
            enableBrowserTooltips
            rowSelection="multiple"
            enableRangeSelection={true}
            animateRows={true}
            onModelUpdated={onModelUpdated}
            enableRangeHandle={true}
            fillHandleDirection="y"
            undoRedoCellEditing={false}
            loadingOverlayComponent="loadingOverlay"
            pivotMode={false}
            context={{useOverlay: 'Loading Survey Correction'}}
            components={{loadingOverlay: LoadingOverlay, summaryPanel: SummaryPanel}}
            pivotColumnGroupTotals="before"
            onGridReady={onGridReady}
            onRowDataUpdated={(e) => e.columnApi.autoSizeAllColumns()}
          >
            {headers.map((header, idx) => (
              <AgGridColumn key={idx} field={idx.toString()} headerName={header.label} hide={!header.visible} />
            ))}
            {measurementColumns.map((m, idx) => (
              <AgGridColumn
                field={headers.length + '.' + idx}
                key={(headers.length + idx).toString()}
                width={35}
                headerComponentParams={{
                  template: `<div style="width: 48px; float: left; text-align:center"><div style="color: #c4d79b; border-bottom: 1px solid rgba(0, 0, 0, 0.12)">${m.fishSize}</div><div style="color: #da9694">${m.invertSize}</div></div>`
                }}
              />
            ))}
          </AgGridReact>
        )}
      </Box>
    </>
  );
};

export default SurveyCorrect;
