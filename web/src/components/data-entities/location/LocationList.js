import React, {useEffect, useState} from 'react';
import {Box, Button, Typography} from '@mui/material';
import {AgGridColumn, AgGridReact} from 'ag-grid-react';
import {Navigate, NavLink} from 'react-router-dom';
import {getEntity} from '../../../api/api';
import LoadingOverlay from '../../overlays/LoadingOverlay';
import {Add} from '@mui/icons-material';

import 'ag-grid-enterprise';

const LocationList = () => {
  const [gridApi, setGridApi] = useState();
  const [redirect, setRedirect] = useState();

  useEffect(() => {
    async function fetchLocations() {
      await getEntity('locations').then((res) => gridApi.setRowData(res.data));
    }
    if (gridApi) fetchLocations();
  }, [gridApi]);

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
          rowHeight={24}
          pagination={true}
          enableCellTextSelection={true}
          onGridReady={(e) => setGridApi(e.api)}
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
          <AgGridColumn flex={2} field="ecoRegions" />
          <AgGridColumn flex={2} field="countries" />
          <AgGridColumn flex={2} field="areas" />
        </AgGridReact>
      </Box>
    </>
  );
};

export default LocationList;
