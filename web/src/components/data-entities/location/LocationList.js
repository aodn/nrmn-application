import React, {useState} from 'react';
import {Box, Button, Typography} from '@mui/material';
import {AgGridColumn, AgGridReact} from 'ag-grid-react';
import { Navigate, NavLink, useLocation } from 'react-router-dom';
import {getEntity} from '../../../api/api';
import {useRef} from 'react';
import LoadingOverlay from '../../overlays/LoadingOverlay';
import {Add} from '@mui/icons-material';
import stateFilterHandler from '../../../common/state-event-handler/StateFilterHandler';

import 'ag-grid-enterprise';

const LocationList = () => {
  const location = useLocation();
  const [redirect, setRedirect] = useState();
  const gridRef = useRef(null);

  // Auto size function to be call each time data changed, so the grid always autofit
  const autoSizeAll = (evt, skipHeader) => {
    if (evt) {
      evt.columnApi.autoSizeAllColumns(skipHeader);
    }
  };

  const onGridReady = (event) => {
    async function fetchLocations(event) {
      await getEntity('locations').then((res) => {
        // Use setRowData in api will not trigger onGridReady but onDataChange event.
        // if you use useState and connect row to setRowData then you will
        // keep fire onGridReady as row change
        event.api.setRowData(res.data);
      });
    }

    fetchLocations(event).then(() => {
      if(!(location?.state?.resetFilters)) {
        stateFilterHandler.restoreStateFilters(gridRef);
      }
      else {
        stateFilterHandler.resetStateFilters(gridRef);
      }

      autoSizeAll(event, false);
    });
  };

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
          ref={gridRef}
          id={'location-list'}
          rowHeight={24}
          pagination={true}
          enableCellTextSelection={true}
          onGridReady={(e) => onGridReady(e)}
          onBodyScroll={(e) => autoSizeAll(e, false)}
          onFilterChanged={(e) => stateFilterHandler.stateFilterEventHandler(gridRef, e)}
          context={{useOverlay: 'Loading Locations'}}
          components={{loadingOverlay: LoadingOverlay}}
          loadingOverlayComponent="loadingOverlay"
          suppressCellFocus={true}
          defaultColDef={{
            sortable: true,
            resizable: true,
            filter: 'agTextColumnFilter',
            floatingFilter: true
          }}
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
          <AgGridColumn field="locationName" sort="asc" cellStyle={{cursor: 'pointer'}} onCellClicked={(e) => setRedirect(e.data.id)} />
          <AgGridColumn maxWidth={80} field="status" />
          <AgGridColumn minWidth={600} field="ecoRegions" />
          <AgGridColumn field="countries" />
          <AgGridColumn minWidth={600} field="areas" />
        </AgGridReact>
      </Box>
    </>
  );
};

export default LocationList;
