import React, {useState, useCallback} from 'react';
import {Box, Button, Typography} from '@mui/material';
import { AgGridReact } from 'ag-grid-react';
import { Navigate, NavLink, useLocation } from 'react-router-dom';
import { getResult } from '../../../api/api';
import {useRef} from 'react';
import {Add} from '@mui/icons-material';
import stateFilterHandler, { LocationState } from '../../../common/state-event-handler/StateFilterHandler';
import 'ag-grid-enterprise';
import Backdrop from '@mui/material/Backdrop';
import CircularProgress from '@mui/material/CircularProgress';
import {AppConstants} from '../../../common/constants';
import { BodyScrollEndEvent, CellClickedEvent, ColDef, FilterChangedEvent, GridReadyEvent } from 'ag-grid-community';
import { AuthContext } from '../../../contexts/auth-context';

const LOCATION_LIST_GRID_ID = 'location-list';

const defaultColDef = {
  lockVisible: true,
  sortable: true,
  resizable: true,
  filter: 'agTextColumnFilter',
  floatingFilter: true,
  filterParams: {debounceMs: AppConstants.Filter.WAIT_TIME_ON_FILTER_APPLY },
};

const LocationList = () => {
  const rowsPerPage = 50;
  const [loading, setLoading] = React.useState(false);

  const location = useLocation();
  const [redirect, setRedirect] = useState<string | undefined>();
  const gridRef = useRef<AgGridReact>(null);

  const createColumns = useCallback((roles: Array<string> | undefined): ColDef[] => {
    const cols: ColDef[] = [];

    cols.push({
      width: 40,
      field: 'id',
      headerName: '',
      suppressMovable: true,
      filter: false,
      resizable: false,
      sortable: false,
      valueFormatter: () => '✎',
      cellStyle: {paddingLeft: '10px', color: 'grey', cursor: 'pointer'},
      onCellClicked: (event: CellClickedEvent) => {
        if(roles?.includes(AppConstants.ROLES.DATA_OFFICER) || roles?.includes(AppConstants.ROLES.ADMIN)) {
          const mouseEvent = event.event as MouseEvent;
          if (mouseEvent.ctrlKey) {
            if(event.data) {
              window.open(`/reference/location/${event.data.id}/edit`, '_blank')?.focus();
            }
          } else {
            setRedirect(`${event.data.id}/edit`);
          }
        }
      }

    });

    cols.push({
      field: 'locationName',
      colId: 'location.locationName',
      sort: 'asc',
      cellStyle: {cursor: 'pointer'},
      onCellClicked: (event: CellClickedEvent) => setRedirect(event.data.id),
    });

    cols.push({
      maxWidth: 80,
      field: 'status',
      colId: 'location.status',
    });

    cols.push({
      minWidth: 600,
      field: 'ecoRegions',
      colId: 'location.ecoRegions',
    });

    cols.push({
      field: 'countries',
      colId: 'location.countries',
    });

    cols.push({
      minWidth: 600,
      field: 'areas',
      colId: 'location.areas',
    });

    return cols;

  }, [setRedirect]);

  // Auto size function to be call each time data changed, so the grid always autofit
  const autoSizeAll = (evt: BodyScrollEndEvent | GridReadyEvent, skipHeader: boolean) => {
    if (evt) {
      evt.columnApi.autoSizeAllColumns(skipHeader);
    }
  };

  const onGridReady = useCallback((event: GridReadyEvent) => {
    document.title = 'Locations';
    async function fetchLocations(e: GridReadyEvent) {
      e.api.setDatasource({
        // This is the functional structure need for datasource
        rowCount: rowsPerPage,
        getRows: (params) => {
          let url = `locations?page=${params.startRow / 100}`;
          const conditions = [];
          // Filter section
          for(const name in params.filterModel) {
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
          }

          // Sorting section, order is important
          const sort: Array<{ field: string; order: string }> = [];

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
      const filter = location.state as LocationState;
      if(!filter?.resetFilters) {
        stateFilterHandler.restoreStateFilters(gridRef, LOCATION_LIST_GRID_ID);
      }
      else {
        stateFilterHandler.resetStateFilters(LOCATION_LIST_GRID_ID);
      }

      autoSizeAll(event, false);
    });
  }, [location]);

  if (redirect) return <Navigate to={`/reference/location/${redirect}`} />;

  return (
    <AuthContext.Consumer>
      {({auth}) =>
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
            <Button
              variant="contained" to="/reference/location"
              component={NavLink}
              startIcon={<Add />}
              disabled={!(auth?.roles?.includes(AppConstants.ROLES.DATA_OFFICER) || auth?.roles?.includes(AppConstants.ROLES.ADMIN))}>New Location
            </Button>
          </Box>
        </Box>
        <Box flexGrow={1} overflow="hidden" className="ag-theme-material">
          <AgGridReact
            ref={gridRef}
            rowHeight={24}
            pagination={true}
            paginationPageSize={rowsPerPage}
            rowModelType={'infinite'}
            enableCellTextSelection={true}
            onGridReady={(e) => onGridReady(e)}
            onBodyScrollEnd={(e: BodyScrollEndEvent) => autoSizeAll(e, false)}
            onFilterChanged={(e: FilterChangedEvent) => stateFilterHandler.stateFilterEventHandler(LOCATION_LIST_GRID_ID, e)}
            suppressCellFocus={true}
            defaultColDef={defaultColDef}
            columnDefs={createColumns(auth.roles)}
          >
          </AgGridReact>
        </Box>
      </>}
    </AuthContext.Consumer>
  );
};

export default LocationList;
