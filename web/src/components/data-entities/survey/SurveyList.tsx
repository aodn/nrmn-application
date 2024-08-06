import React, { useCallback, useRef, useState, useEffect } from 'react';
import { Box, Button, Typography, Autocomplete, CircularProgress, TextField, Collapse, IconButton } from '@mui/material';
import { Navigate, useLocation } from 'react-router-dom';
import { getResult } from '../../../api/api';
import stateFilterHandler from '../../../common/state-event-handler/StateFilterHandler';
import { AuthContext } from '../../../contexts/auth-context';
import { AppConstants } from '../../../common/constants';

import 'ag-grid-enterprise';
import { AgGridReact } from 'ag-grid-react';
import { AgPromise, CellClickedEvent, ColDef, GridReadyEvent, SelectionChangedEvent } from 'ag-grid-community';

import { createFilterOptions } from '@mui/material/Autocomplete';
import Backdrop from '@mui/material/Backdrop';
import ResetIcon from '@mui/icons-material/LayersClear';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';

interface Option {
  name: string;
  observableItemId: string;
}

interface ModelType {
  [key: string]: {
    filter: string;
    filterType: string,
    type: string
  } | string;
}

// We want to keep the value between pages, so we only need to load it once.
const cachedOptions: Array<Option> = [];
const OBSERVABLE_ITEM_ID_FIELD = 'survey.observableItemId';

const defaultColDef = {
  lockVisible: true,
  minWidth: AppConstants.AG_GRID.dataColWidth,
  sortable: true,
  resizable: true,
  filter: 'agTextColumnFilter',
  suppressMenu: true,
  floatingFilterComponentParams: { suppressFilterButton: true },
  filterParams: { debounceMs: AppConstants.Filter.WAIT_TIME_ON_FILTER_APPLY },
  floatingFilter: true
};

type LocationState = {
  resetFilters: boolean;
}

const SurveyList = () => {
  const rowsPerPage = 50;
  const location = useLocation();
  const gridRef = useRef<AgGridReact>(null);
  const [redirect, setRedirect] = useState<string | undefined>();
  const [loading, setLoading] = useState(false);
  const [selected, setSelected] = useState<Array<string> | undefined>();
  const [open, setOpen] = useState(false);
  const [options, setOptions] = useState<Array<Option>>([]);
  const [expanded, setExpanded] = React.useState(false);
  const [isFiltered, setIsFiltered] = useState(false);
  const optionLoading = open && options.length === 0;

  // change the default icons: hide the triangle filter icon in the table head
  const icons = { menu: ' ', filter: ' ' };

  const createColumns = useCallback((allowCorrection: boolean | undefined): ColDef[] => {

    const cols: ColDef[] = [];

    if (allowCorrection) {
      cols.push(
        {
          minWidth: 10,
          field: 'surveyId',
          headerName: '',
          suppressMovable: true,
          filter: false,
          tooltipValueGetter: () => 'Correct Survey',
          resizable: false,
          sortable: false,
          checkboxSelection: (e: { data: { locked: boolean; }; }) => e.data?.locked !== true,
          valueFormatter: () => '',
          cellStyle: { paddingLeft: '10px', color: 'grey', cursor: 'pointer' },
          onCellClicked: (event: CellClickedEvent) => {
            const mouseEvent = event.event as MouseEvent;
            if (mouseEvent.ctrlKey) {
              if (event.data) {
                window.open(`/data/survey/${event.data.surveyId}/correct`, '_blank')?.focus();
              }
            } else {
              setRedirect(`${event.data.surveyId}/correct`);
            }
          },
        });
    }

    cols.push(
      {
        minWidth: 40,
        field: 'surveyId',
        headerName: '',
        suppressMovable: true,
        filter: false,
        resizable: false,
        sortable: false,
        tooltipValueGetter: () => 'Edit Survey Metadata',
        valueFormatter: () => '✎',
        cellStyle: { paddingLeft: '10px', color: 'grey', cursor: 'pointer' },
        onCellClicked: (event: CellClickedEvent) => {
          const mouseEvent = event.event as MouseEvent;
          if (mouseEvent.ctrlKey) {
            if (event.data) {
              window.open(`/data/survey/${event.data.surveyId}/edit`, '_blank')?.focus();
            }
          } else {
            setRedirect(`${event.data.surveyId}/edit`);
          }
        },
      });

    cols.push(
      {
        field: 'surveyId',
        headerName: 'Survey ID',
        colId: 'survey.surveyId',
        sort: 'desc',
        sortable: false,
        cellStyle: { cursor: 'pointer' },
        onCellClicked: (event: CellClickedEvent) => {
          const mouseEvent = event.event as MouseEvent;
          if (mouseEvent.ctrlKey) {
            if (event.data) {
              window.open(`/data/survey/${event.data.surveyId}`, '_blank')?.focus();
            }
          } else {
            setRedirect(event.data.surveyId);
          }
        },
      });

    cols.push(
      {
        field: 'surveyDate',
        colId: 'survey.surveyDate',
      });

    cols.push(
      {
        field: 'latitude',
        colId: 'survey.latitude',
      });

    cols.push(
      {
        field: 'longitude',
        colId: 'survey.longitude',
      });

    cols.push(
      {
        headerName: 'Has PQs',
        field: 'pqCatalogued',
        colId: 'survey.pqCatalogued',
      });

    cols.push(
      {
        field: 'siteCode',
        colId: 'survey.siteCode',
      });

    cols.push(
      {
        flex: 1,
        field: 'siteName',
        colId: 'survey.siteName',
      });

    cols.push(
      {
        width: 50,
        field: 'depth',
        colId: 'survey.depth',
      });

    cols.push(
      {
        width: 250,
        field: 'diverName',
        colId: 'survey.diverName',
      });

    cols.push(
      {
        width: 50,
        field: 'method',
        colId: 'survey.method',
      });

    cols.push(
      {
        field: 'programName',
        headerName: 'Program',
        colId: 'survey.programName',
      });

    cols.push(
      {
        flex: 1,
        field: 'country',
        colId: 'survey.country',
      });

    cols.push(
      {
        flex: 1,
        field: 'state',
        colId: 'survey.state'
      });

    cols.push(
      {
        flex: 1,
        field: 'ecoregion',
        colId: 'survey.ecoregion',
      });

    cols.push(
      {
        flex: 1,
        field: 'locationName',
        colId: 'survey.locationName',
      });

    cols.push(
      {
        hide: true,
        flex: 1,
        colId: OBSERVABLE_ITEM_ID_FIELD,
      });

    return cols;
  }, []);

  useEffect(() => {
    document.title = 'Surveys';
    // Empty array
    cachedOptions.splice(0, cachedOptions.length);
  }, []);

  useEffect(() => {
    let active = true;

    if (!optionLoading) {
      return undefined;
    }

    if (cachedOptions.length !== 0) {
      // If user select more than two species, we will not show the dropdown with value such that user cannot enter
      // more than two species
      const f = gridRef?.current?.api.getFilterInstance(OBSERVABLE_ITEM_ID_FIELD);
      if (!f?.getModel() || !f.getModel().operator) {
        // operator attribute valid if filter on two species
        setOptions(cachedOptions);
      } else {
        // Close the dropdown
        setOpen(false);
      }
      return undefined;
    }

    (async () => {
      const url = `reference/observableItems?pageSize=-1&sort=${encodeURIComponent('[{"field":"observation.name","order":"asc"}]')}`;

      getResult(url)
        .then((res) => {
          res.data.items.forEach((i: Option) => cachedOptions.push(i));
        })
        .finally(() => {
          if (active) {
            setOptions(cachedOptions);
          }
        });
    })();

    return () => {
      active = false;
    };
  }, [optionLoading]);

  useEffect(() => {
    if (!open) {
      setOptions([]);
    }
  }, [open]);

  // Create filter for species and push it to the ag grid to trigger page reload
  const handleSpeciesFilterChange = useCallback((
    event: React.SyntheticEvent,
    value: Option[]
  ) => {
    const f = gridRef?.current?.api.getFilterInstance(OBSERVABLE_ITEM_ID_FIELD);

    if (value.length === 0) {
      (f?.setModel(null) as AgPromise<void>)
        .then(() => gridRef?.current?.api.onFilterChanged());
    } else if (value.length === 1) {
      (f?.setModel({
        filter: value[0].observableItemId,
        filterType: 'text',
        type: 'equals'
      }) as AgPromise<void>).then(() => gridRef?.current?.api.onFilterChanged());
    } else {

      const model: ModelType = {
        filterType: 'text',
        operator: 'OR'
      };

      for (let i = 0; i < value.length; i++) {
        model['condition' + (i + 1)] = {
          filter: value[i].observableItemId,
          filterType: 'text',
          type: 'equals'
        };
      }

      (f?.setModel(model) as AgPromise<void>).then(() => gridRef?.current?.api.onFilterChanged());
    }
  }, []);

  const onGridReady = useCallback(
    (event: GridReadyEvent) => {
      async function fetchSurveys(e: GridReadyEvent) {
        e.api.setDatasource({
          // This is the functional structure need for datasource
          rowCount: rowsPerPage,
          getRows: (params) => {
            let url = `data/surveys?page=${params.startRow / 100}`;
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
                e.columnApi.autoSizeAllColumns(false);
              });
          }
        });
      }

      fetchSurveys(event).then(() => {
        const filter = location.state as LocationState;
        if (!filter?.resetFilters) {
          stateFilterHandler.restoreStateFilters(gridRef);
        } else {
          stateFilterHandler.resetStateFilters(gridRef);
        }
      });
    },
    [location]
  );

  if (redirect) return <Navigate to={`/data/survey/${redirect}`} />;

  return (
    <AuthContext.Consumer>
      {({ auth }) => (
        <>
          <Backdrop sx={{ color: '#fff', zIndex: (theme) => theme.zIndex.drawer + 1 }} open={loading}>
            <CircularProgress color='inherit' />
          </Backdrop>
          <Box display='flex' flexDirection='row' p={1} pb={1}>
            <Box flexGrow={1}>
              <Typography variant='h4'>Surveys</Typography>
            </Box>
            <Box m={1} ml={0}>
              <IconButton
                onClick={() => setExpanded((v) => !v)}
                aria-expanded={expanded}
                aria-label='Show more'
                sx={{
                  transform: expanded ? 'rotate(180deg)' : 'rotate(0deg)',
                  marginLeft: 'auto',
                  transition: (theme) => theme.transitions?.create('transform', {
                    duration: theme.transitions.duration.shortest
                  })
                }}
              >
                <ExpandMoreIcon />
              </IconButton>
            </Box>
            <Box m={1} ml={0}>
              <Button variant="outlined"
                startIcon={<ResetIcon />}
                disabled={!isFiltered}
                onClick={() => {
                  gridRef?.current?.api.setFilterModel(null);
                  setIsFiltered(false);
                }}>
                Reset Filter
              </Button>
            </Box>
            <Box m={1} ml={0}>
              <Button
                variant="outlined"
                onClick={() => setRedirect(`${selected?.join(',')}/correct`)}
                disabled={!selected || selected.length < 1 || selected.length > 25}
              >
                Correct Survey Data
              </Button>
            </Box>
          </Box>
          <Box display="flex" flexDirection="row" p={1} pb={1}>
            <Box flexGrow={1}>
              <Collapse in={expanded} timeout="auto" unmountOnExit>
                <Autocomplete
                  id="species-filter"
                  multiple
                  style={{ width: '100%' }}
                  open={open}
                  onOpen={() => setOpen(true)}
                  onClose={() => setOpen(false)}
                  getOptionLabel={(option) => option.name}
                  options={options}
                  loading={optionLoading}
                  filterSelectedOptions
                  onChange={handleSpeciesFilterChange}
                  filterOptions={createFilterOptions({
                    matchFrom: 'start',
                    stringify: (option) => option.name
                  })}
                  renderInput={(params) => (
                    <TextField
                      {...params}
                      label='Species filter (max two species)'
                      variant='outlined'
                      InputProps={{
                        ...params.InputProps,
                        endAdornment: (
                          <React.Fragment>
                            {optionLoading ? <CircularProgress color="inherit" size={20} /> : null}
                            {params.InputProps.endAdornment}
                          </React.Fragment>
                        )
                      }}
                    />
                  )}
                />
              </Collapse>
            </Box>
          </Box>
          <AgGridReact
            ref={gridRef}
            key={'survey-list'}
            className='ag-theme-material'
            rowHeight={24}
            rowSelection={'multiple'}
            animateRows={true}
            enableCellTextSelection={true}
            pagination={true}
            paginationPageSize={rowsPerPage}
            tooltipShowDelay={0}
            tooltipHideDelay={10000}
            rowModelType={'infinite'}
            // row
            onGridReady={(e) => onGridReady(e)}
            onSelectionChanged={(e: SelectionChangedEvent) => {
              setSelected(e.api.getSelectedRows().map((i) => i.surveyId));
            }}
            onFilterChanged={(e) => {
              stateFilterHandler.stateFilterEventHandler(gridRef, e);

              const filterModel = e.api.getFilterModel();
              setIsFiltered(Object.getOwnPropertyNames(filterModel).length > 0);
            }}
            suppressCellFocus={true}
            defaultColDef={defaultColDef}
            columnDefs={createColumns(auth.features?.includes('corrections'))}

            // customized setting of table icons
            icons={icons}
          >
          </AgGridReact>
        </>
      )}
    </AuthContext.Consumer>
  );
};

export default SurveyList;
