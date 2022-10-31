import React, {useState, useCallback} from 'react';
import {Box, Button, Typography} from '@mui/material';
import {AgGridColumn, AgGridReact} from 'ag-grid-react';
import { Navigate, NavLink, useLocation } from 'react-router-dom';
import { getResult } from '../../../api/api';
import {useRef} from 'react';
import {Add} from '@mui/icons-material';
import stateFilterHandler from '../../../common/state-event-handler/StateFilterHandler';
import 'ag-grid-enterprise';
import Backdrop from '@mui/material/Backdrop';
import CircularProgress from '@mui/material/CircularProgress';

const LocationList = () => {
  const rowsPerPage = 50;
  const [loading, setLoading] = React.useState(false);

  const location = useLocation();
  const [redirect, setRedirect] = useState();
  const gridRef = useRef(null);

  // Auto size function to be call each time data changed, so the grid always autofit
  const autoSizeAll = (evt, skipHeader) => {
    if (evt) {
      evt.columnApi.autoSizeAllColumns(skipHeader);
    }
  };

  const onGridReady = useCallback((event) => {
    async function fetchLocations(e) {
      e.api.setDatasource({
        // This is the functional structure need for datasource
        rowCount: rowsPerPage,
        getRows: (params) => {
          let url = `locations?page=${params.startRow / 100}`;
          let conditions = [];
          // Filter section
          for(let name in params.filterModel) {
            const p = params.filterModel[name];

            if(p.type) {
              // This is single condition
              conditions.push({
                field: name,
                ops: p.type,
                val: p.filter
              });
            }
            else {
              // This is a multiple condition, currently max two conditions
              conditions.push({
                field: name,
                ops: p.operator,
                conditions: [
                  { ops: p.condition1.type, val: p.condition1.filter },
                  { ops: p.condition2.type, val: p.condition2.filter }
                ]
              });
            }
          };

          // Sorting section, order is important
          let sort = [];
          params.sortModel.forEach(i => {
            sort.push({
              field: i.colId,
              order: i.sort
            });
          });

          url = conditions.length !== 0 ? url + `&filters=${encodeURIComponent(JSON.stringify(conditions))}` : url;
          url = sort.length !== 0 ? url + `&sort=${encodeURIComponent(JSON.stringify(sort))}` : url;

          setLoading(true);
          getResult(url)
            .then(res => {
              params.successCallback(res.data.items, res.data.lastRow);
            })
            .finally(()=> {
              setLoading(false);
            });
        }
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
  }, [location]);

  if (redirect) return <Navigate push to={`/reference/location/${redirect}`} />;

  return (
    <>
      <Backdrop
        sx={{ color: '#fff', zIndex: (theme) => theme.zIndex.drawer + 1 }}
        open={loading}>
        <CircularProgress color="inherit" />
      </Backdrop>
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
          paginationPageSize={rowsPerPage}
          rowModelType={'infinite'}
          enableCellTextSelection={true}
          onGridReady={(e) => onGridReady(e)}
          onBodyScrollEnd={(e) => autoSizeAll(e, false)}
          onFilterChanged={(e) => stateFilterHandler.stateFilterEventHandler(gridRef, e)}
          suppressCellFocus={true}
          defaultColDef={{
            lockVisible: true,
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
          <AgGridColumn field="locationName" colId="location.locationName" sort="asc" cellStyle={{cursor: 'pointer'}} onCellClicked={(e) => setRedirect(e.data.id)} />
          <AgGridColumn maxWidth={80} field="status" colId="location.status"/>
          <AgGridColumn minWidth={600} field="ecoRegions" colId="location.ecoRegions"/>
          <AgGridColumn field="countries" colId="location.countries"/>
          <AgGridColumn minWidth={600} field="areas" colId="location.areas"/>
        </AgGridReact>
      </Box>
    </>
  );
};

export default LocationList;
