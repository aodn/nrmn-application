import React, { useRef, useState, useCallback } from 'react';
import { Box, Button, Typography } from '@mui/material';
import { Navigate, NavLink, useLocation } from 'react-router-dom';
import { getResult } from '../../../api/api';
import { AgGridReact } from 'ag-grid-react';
import { Add } from '@mui/icons-material';
import stateFilterHandler, { LocationState } from '../../../common/state-event-handler/StateFilterHandler';
import Backdrop from '@mui/material/Backdrop';
import CircularProgress from '@mui/material/CircularProgress';
import { AuthContext } from '../../../contexts/auth-context';
import { AppConstants } from '../../../common/constants';

import 'ag-grid-enterprise';
import { ColDef } from 'ag-grid-enterprise';
import { BodyScrollEndEvent, CellClickedEvent, FilterChangedEvent, GridReadyEvent } from 'ag-grid-community';

const OBSERVABLE_ITEM_GRID_ID = 'observable-item-list';

const defaultColDef = {
  lockVisible: true,
  sortable: true,
  resizable: true,
  filter: 'agTextColumnFilter',
  floatingFilter: true,
  filterParams: { debounceMs: AppConstants.Filter.WAIT_TIME_ON_FILTER_APPLY },
};

const ObservableItemList = () => {
  const rowsPerPage = 50;
  const [loading, setLoading] = React.useState(false);

  const location = useLocation();
  const [redirect, setRedirect] = useState<string | undefined>();
  const gridRef = useRef<AgGridReact>(null);

  const createColumns = useCallback((roles: Array<string> | undefined) => {
    const cols: ColDef[] = [];

    cols.push({
      field: 'observableItemId',
      headerName: '',
      suppressMovable: true,
      filter: false,
      resizable: false,
      sortable: false,
      valueFormatter: () => '✎',
      cellStyle: { paddingLeft: '10px', color: 'grey', cursor: 'pointer' },
      onCellClicked: (event: CellClickedEvent) => {
        if (roles?.includes(AppConstants.ROLES.DATA_OFFICER) || roles?.includes(AppConstants.ROLES.ADMIN)) {
          const mouseEvent = event.event as MouseEvent;
          if (mouseEvent.ctrlKey) {
            if (event.data) {
              window.open(`/reference/observableItem/${event.data.observableItemId}/edit`, '_blank')?.focus();
            }
          } else {
            setRedirect(`${event.data.observableItemId}/edit`);
          }
        }
      }
    });

    cols.push({
      field: 'observableItemId',
      headerName: 'ID',
      sort: 'desc',
      colId: 'observation.observableItemId',
      cellStyle: { cursor: 'pointer' },
      onCellClicked: (event: CellClickedEvent) => {
        const mouseEvent = event.event as MouseEvent;
        if (mouseEvent.ctrlKey) {
          if (event.data) {
            window.open(`/reference/observableItem/${event.data.observableItemId}`, '_blank')?.focus();
          }
        } else {
          setRedirect(event.data.observableItemId);
        }
      }
    });

    cols.push({
      field: 'typeName',
      headerName: 'Type',
      colId: 'observation.typeName',
    });

    cols.push({
      minWidth: 200,
      field: 'name',
      colId: 'observation.name',
    });

    cols.push({
      field: 'commonName',
      colId: 'observation.commonName',
    });

    cols.push({
      field: 'supersededBy',
      colId: 'observation.supersededBy',
    });

    cols.push({
      field: 'supersededNames',
      colId: 'observation.supersededNames',
    });

    cols.push({
      field: 'supersededIDs',
      colId: 'observation.supersededIds',
    });

    cols.push({
      field: 'phylum',
      colId: 'observation.phylum',
    });

    cols.push({
      field: 'class',
      colId: 'observation.class',
    });

    cols.push({
      field: 'order',
      colId: 'observation.order',
    });

    cols.push({
      field: 'family',
      colId: 'observation.family',
    });

    cols.push({
      field: 'genus',
      colId: 'observation.genus',
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
    document.title = 'Observable Items';
    async function fetchObservableItems(e: GridReadyEvent) {
      e.api.setDatasource({
        // This is the functional structure need for datasource
        rowCount: rowsPerPage,
        getRows: (params) => {
          let url = `reference/observableItems?page=${params.startRow / 100}`;
          const conditions = [];
          // Filter section
          for (const name in params.filterModel) {
            const p = params.filterModel[name];

            if (p.type) {
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
            .finally(() => {
              setLoading(false);
            });
        }
      });
    }

    fetchObservableItems(event).then(() => {
      const filter = location.state as LocationState;
      if (!(filter?.resetFilters)) {
        stateFilterHandler.restoreStateFilters(gridRef, OBSERVABLE_ITEM_GRID_ID);
      }
      else {
        stateFilterHandler.resetStateFilters(OBSERVABLE_ITEM_GRID_ID);
      }

      autoSizeAll(event, false);
    });
  }, [location]);

  if (redirect) return <Navigate to={`/reference/observableItem/${redirect}`} />;

  return (
    <AuthContext.Consumer>
      {({ auth }) =>
        <>
          <Backdrop
            sx={{ color: '#fff', zIndex: (theme) => theme.zIndex.drawer + 1 }}
            open={loading}>
            <CircularProgress color="inherit" />
          </Backdrop>
          <Box display="flex" flexDirection="row" pt={1}>
            <Box p={1} pl={2} flexGrow={1}>
              <Typography variant="h4">Observable Items</Typography>
            </Box>
            <Box mr={4}>
              <Button style={{ width: '100%' }}
                to="/reference/observableItem"
                component={NavLink}
                variant="contained"
                disabled={!(auth.roles?.includes(AppConstants.ROLES.DATA_OFFICER) || auth.roles?.includes(AppConstants.ROLES.ADMIN))}
                startIcon={<Add />}>
                New Observable Item
              </Button>
            </Box>
          </Box>
          <AgGridReact
            ref={gridRef}
            className="ag-theme-material"
            rowHeight={24}
            pagination={true}
            paginationPageSize={rowsPerPage}
            rowModelType={'infinite'}
            enableCellTextSelection={true}
            onGridReady={(e) => onGridReady(e)}
            onBodyScrollEnd={(e: BodyScrollEndEvent) => autoSizeAll(e, false)}
            onFilterChanged={(e: FilterChangedEvent) => stateFilterHandler.stateFilterEventHandler(OBSERVABLE_ITEM_GRID_ID, e)}
            suppressCellFocus={true}
            defaultColDef={defaultColDef}
            columnDefs={createColumns(auth.roles)}
          >
          </AgGridReact>
        </>}
    </AuthContext.Consumer>
  );
};

export default ObservableItemList;
