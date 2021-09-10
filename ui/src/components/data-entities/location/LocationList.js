import React, {useEffect, useState} from 'react';
import {Box, Button, Typography} from '@material-ui/core';
import {AgGridColumn, AgGridReact} from 'ag-grid-react';
import {Redirect, NavLink} from 'react-router-dom';
import {getEntity} from '../../../axios/api';
import LoadingOverlay from '../../overlays/LoadingOverlay';
import {Add} from '@material-ui/icons';

import 'ag-grid-community/dist/styles/ag-theme-material.css';
import 'ag-grid-enterprise';

const LocationList = () => {
  const [gridApi, setGridApi] = useState(null);
  const [redirect, setRedirect] = useState(null);

  useEffect(() => {
    if (gridApi) getEntity('locationList').then((res) => gridApi.setRowData(res.data));
  }, [gridApi]);

  if (redirect) return <Redirect to={`/reference/location/${redirect}`} />;

  return (
    <>
      <Box display="flex" flexDirection="row" p={1} pb={1}>
        <Box flexGrow={1}>
          <Typography variant="h4">Locations</Typography>
        </Box>
        <Box>
          <Button to="/reference/location" component={NavLink} startIcon={<Add />}>
            New Location
          </Button>
        </Box>
      </Box>
      <Box flexGrow={1} overflow="hidden" className="ag-theme-material">
        <AgGridReact
          rowHeight={25}
          animateRows={true}
          enableCellTextSelection={true}
          onGridReady={(e) => setGridApi(e.api)}
          context={{useOverlay: 'Loading Locations'}}
          frameworkComponents={{loadingOverlay: LoadingOverlay}}
          loadingOverlayComponent="loadingOverlay"
          suppressCellSelection={true}
          defaultColDef={{sortable: true, resizable: true, filter: true, floatingFilter: true}}
        >
          <AgGridColumn field="" headerName="" maxWidth={10} />
          <AgGridColumn maxWidth={80} field="isActive" headerName="Active" />
          <AgGridColumn flex={1} field="locationName" sort="asc" />
          <AgGridColumn flex={2} field="ecoRegions" />
          <AgGridColumn flex={2} field="countries" />
          <AgGridColumn flex={2} field="areas" />
          <AgGridColumn
            maxWidth={80}
            field="id"
            headerName=""
            suppressMovable={true}
            filter={false}
            sortable={false}
            valueFormatter={() => '>>'}
            cellStyle={{textAlign: 'center', textDecoration: 'underline', cursor: 'pointer'}}
            onCellClicked={(e) => setRedirect(e.value)}
          />
        </AgGridReact>
      </Box>
    </>
  );
};

export default LocationList;
