import React, {useCallback, useState} from 'react';
import {Box, Button, Typography} from '@mui/material';
import {AgGridColumn, AgGridReact} from 'ag-grid-react';
import {Navigate, NavLink} from 'react-router-dom';
import {getEntity} from '../../../api/api';
import {useRef} from 'react';
import LoadingOverlay from '../../overlays/LoadingOverlay';
import {Add} from '@mui/icons-material';
import 'ag-grid-enterprise';

const LocationList = () => {
  const [redirect, setRedirect] = useState();
  const [rowData, setRowData] = useState();
  const lGridRef = useRef(null);

  // Auto size function to be call each time data changed, so the grid always autofit
  const autoSizeAll = (skipHeader) => {
    if(lGridRef.current != null) {
      lGridRef.current.columnApi.autoSizeAllColumns(skipHeader);
    }};

  const onGridReady = useCallback(() => {
    async function fetchLocations() {
      await getEntity('locations').then(
          (res) => {
            setRowData(res.data);
            autoSizeAll(false);
          });
    }
    fetchLocations();
  }, []);

  if (redirect) return <Navigate push to={`/reference/location/${redirect}`} />;

  return (
    <>
      <Box display="flex" flexDirection="row" p={1} pb={1}>
        <Box flexGrow={1}>
          <Typography variant="h4">Locations</Typography>
        </Box>
        <Box>
          <Button variant="contained" to="/reference/location" component={NavLink} startIcon={<Add />}>
            New Location
          </Button>
        </Box>
      </Box>
      <Box flexGrow={1} overflow="hidden" className="ag-theme-material">
        <AgGridReact
          ref={lGridRef}
          rowHeight={24}
          pagination={true}
          enableCellTextSelection={true}
          onGridReady={onGridReady()}
          rowData={rowData}
          context={{useOverlay: 'Loading Locations'}}
          components={{loadingOverlay: LoadingOverlay}}
          loadingOverlayComponent="loadingOverlay"
          suppressCellFocus={true}
          defaultColDef={{sortable: true, resizable: true, filter: 'agTextColumnFilter', floatingFilter: true}}
        >
          <AgGridColumn
            width={40}
            field="id"
            headerName=""
            suppressMovable={true}
            filter={false}
            resizable={false}
            sortable={false}
            valueFormatter={() => '✎'}
            cellStyle={{paddingLeft: '10px', color: 'grey', cursor: 'pointer'}}
            onCellClicked={(e) => {
              if (e.event.ctrlKey) {
                window.open(`/reference/location/${e.data.id}/edit`, '_blank').focus();
              } else {
                setRedirect(`${e.data.id}/edit`);
              }
            }}
          />
          <AgGridColumn
            flex={1}
            field="locationName"
            sort="asc"
            cellStyle={{cursor: 'pointer'}}
            onCellClicked={(e) => setRedirect(e.data.id)}
          />
          <AgGridColumn maxWidth={80} field="status" />
          <AgGridColumn field="ecoRegions" />
          <AgGridColumn field="countries" />
          <AgGridColumn field="areas" />
        </AgGridReact>
      </Box>
    </>
  );
};

export default LocationList;
