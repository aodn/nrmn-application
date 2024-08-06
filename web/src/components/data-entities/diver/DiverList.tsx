import React, { useState, useRef, useCallback } from 'react';
import { Box, Button, Typography } from '@mui/material';
import { NavLink, useLocation } from 'react-router-dom';
import { grey, red } from '@mui/material/colors';
import { getResult, entityEdit, entityDelete } from '../../../api/api';
import { AgGridReact } from 'ag-grid-react';
import { Add, Save, Delete } from '@mui/icons-material';
import 'ag-grid-enterprise';
import stateFilterHandler, { LocationState } from '../../../common/state-event-handler/StateFilterHandler';
import Backdrop from '@mui/material/Backdrop';
import CircularProgress from '@mui/material/CircularProgress';
import { AuthContext } from '../../../contexts/auth-context';
import { AppConstants } from '../../../common/constants';
import { ITooltipParams } from 'ag-grid-enterprise';
import { CellClassParams, CellClickedEvent, CellStyle, CellValueChangedEvent, ColDef, GridReadyEvent } from 'ag-grid-community';

const DIVER_LIST_GRID_ID = 'diver-list';

const tooltipValueGetter = (params: ITooltipParams) => {
  if (params.context !== undefined) {
    const error = params.context.errors?.find((e: { id: string }) => e.id === params.data.diverId);
    if (error) return error.message;
  }
};

const chooseCellStyle = (params: CellClassParams): CellStyle | null | undefined => {
  if (params.context !== undefined) {
    if (params.context.errors?.some((e: { id: string }) => e.id === params.data?.diverId)) {
      return { backgroundColor: red[100] };
    }

    if (params.context.delta[params.data?.diverId]) return { backgroundColor: grey[100] };
  }
  return null;
};

const defaultColDef: ColDef = {
  lockVisible: true,
  editable: true,
  sortable: true,
  resizable: true,
  suppressMenu: true,
  floatingFilter: true,
  filter: 'agTextColumnFilter',
  cellStyle: chooseCellStyle,
  tooltipValueGetter: tooltipValueGetter,
  filterParams: { debounceMs: AppConstants.Filter.WAIT_TIME_ON_FILTER_APPLY },
};

interface DataType {
  [key: string]: object;
}

const DiverList = () => {
  const rowsPerPage = 50;
  const [loading, setLoading] = React.useState(false);

  const location = useLocation();
  const [delta, setDelta] = useState<object>([]);
  const [errors, setErrors] = useState<Array<{ id: string, message: string }>>([]);
  const gridRef = useRef(null);

  const onGridReady = useCallback(
    (event: GridReadyEvent) => {
      document.title = 'Divers';
      async function fetchDivers(e: GridReadyEvent) {
        e.api.setDatasource({
          // This is the functional structure need for datasource
          rowCount: rowsPerPage,
          getRows: (params) => {
            let url = `divers?page=${params.startRow / 100}`;
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
              } else {
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

            params.sortModel.forEach((i) => {
              sort.push({
                field: i.colId,
                order: i.sort
              });
            });

            url = conditions.length !== 0 ? url + `&filters=${encodeURIComponent(JSON.stringify(conditions))}` : url;
            url = sort.length !== 0 ? url + `&sort=${encodeURIComponent(JSON.stringify(sort))}` : url;

            setLoading(true);
            getResult(url)
              .then((res) => {
                params.successCallback(res.data.items, res.data.lastRow);
              })
              .finally(() => {
                setLoading(false);
              });
          }
        });
      }

      fetchDivers(event).then(() => {
        const filter = location.state as LocationState;

        if (!filter?.resetFilters) {
          stateFilterHandler.restoreStateFilters(gridRef, DIVER_LIST_GRID_ID);
        } else {
          stateFilterHandler.resetStateFilters(DIVER_LIST_GRID_ID);
        }
      });
    },
    [location]
  );

  const onCellValueChanged = useCallback((e: CellValueChangedEvent) => {
    setDelta((data: object) => {
      const newData: DataType = { ...data };
      newData[e.data.diverId] = e.data;
      return newData;
    });
  }, [setDelta]);

  const saveGrid = () => {
    setErrors([]);
    entityEdit('divers', Object.values(delta))
      .then((res) => {
        res.status === 400 ? setErrors(res.data) : setDelta({});
      });
  };

  const deleteDiver = (e: CellClickedEvent) => {
    entityDelete(`diver`, e.data.diverId)
      .then((res) => {
        if (res.data) {
          setErrors([{ id: e.data.diverId, message: res.data }]);
        } else {
          e.api.purgeInfiniteCache();
        }
      });
  };

  const createColumns = useCallback((): ColDef[] => {
    const cols: ColDef[] = [];

    cols.push({
      field: '',
      filter: false,
      width: 50,
      editable: false,
      cellStyle: { cursor: 'pointer', textAlign: 'center' },
      cellRenderer: () => <Delete fontSize="small" />,
      onCellClicked: deleteDiver,
    });

    cols.push({
      field: 'initials',
      colId: 'diver.initials',
    });

    cols.push({
      flex: 1,
      field: 'fullName',
      colId: 'diver.fullName',
    });

    return cols;
  }, []);

  return (
    <>
      <Backdrop sx={{ color: '#fff', zIndex: (theme) => theme.zIndex.drawer + 1 }} open={loading}>
        <CircularProgress color="inherit" />
      </Backdrop>
      <Box display="flex" flexDirection="row" p={1} pb={1}>
        <Box flexGrow={1}>
          <Typography variant="h4">Divers</Typography>
        </Box>
        <AuthContext.Consumer>
          {({ auth }) => (
            <>
              <Box>
                <Button variant="contained" to="/reference/diver"
                  component={NavLink}
                  startIcon={<Add />}
                  disabled={!(auth.roles?.includes(AppConstants.ROLES.DATA_OFFICER) || auth.roles?.includes(AppConstants.ROLES.ADMIN))}>
                  New Diver
                </Button>
              </Box>
              <Box mx={2}>
                <Button variant="contained"
                  startIcon={<Save />}
                  onClick={saveGrid}
                  disabled={
                    Object.keys(delta).length < 1
                    || !(auth.roles?.includes(AppConstants.ROLES.DATA_OFFICER) || auth.roles?.includes(AppConstants.ROLES.ADMIN))
                  }>
                  Save Changes
                </Button>
              </Box>
            </>)}
        </AuthContext.Consumer>
      </Box>
      <AgGridReact
        ref={gridRef}
        className="ag-theme-material"
        getRowId={(r) => r.data.diverId}
        rowHeight={25}
        fillHandleDirection="y"
        pagination={true}
        paginationPageSize={rowsPerPage}
        rowModelType={'infinite'}
        enableBrowserTooltips
        onCellValueChanged={onCellValueChanged}
        context={{ delta, errors }}
        onGridReady={(e) => onGridReady(e)}
        onFilterChanged={(e) => stateFilterHandler.stateFilterEventHandler(DIVER_LIST_GRID_ID, e)}
        defaultColDef={defaultColDef}
        columnDefs={createColumns()}
      >
      </AgGridReact>
    </>
  );
};

export default DiverList;
