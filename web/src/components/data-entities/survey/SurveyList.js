import {Box, Button, Typography, Autocomplete, CircularProgress, TextField, Collapse, IconButton} from '@mui/material';
import Backdrop from '@mui/material/Backdrop';
import 'ag-grid-enterprise';
import {AgGridColumn, AgGridReact} from 'ag-grid-react';
import React, {useCallback, useRef, useState, useEffect} from 'react';
import {Navigate, useLocation} from 'react-router-dom';
import {getResult} from '../../../api/api';
import stateFilterHandler from '../../../common/state-event-handler/StateFilterHandler';
import {AuthContext} from '../../../contexts/auth-context';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import {createFilterOptions} from '@mui/material/Autocomplete';
import ResetIcon from '@mui/icons-material/LayersClear';
import { AppConstants } from '../../../common/constants';

// We want to keep the value between pages, so we only need to load it once.
const cachedOptions = [];
const OBSERVABLE_ITEM_ID_FIELD = 'survey.observableItemId';



const SurveyList = () => {
  const rowsPerPage = 50;
  const location = useLocation();
  const gridRef = useRef(null);
  const [redirect, setRedirect] = useState();
  const [loading, setLoading] = useState(false);
  const [selected, setSelected] = useState();
  const [open, setOpen] = useState(false);
  const [options, setOptions] = useState([]);
  const [expanded, setExpanded] = React.useState(false);
  const [isFiltered, setIsFiltered] = useState(false);
  const optionLoading = open && options.length === 0;

  // change the default icons: hide the triangle filter icon in the table head
  const icons = { menu: ' ', filter: ' '};


  const defaultColDef = {
    lockVisible: true,
    minWidth: AppConstants.AG_GRID.dataColWidth,
    sortable: true,
    resizable: true,
    filter: 'agTextColumnFilter',
    suppressMenu: true,
    floatingFilterComponentParams: {suppressFilterButton:true},
    filterParams: {debounceMs: AppConstants.Filter.WAIT_TIME_ON_FILTER_APPLY },
    floatingFilter: true
  };

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
      const f = gridRef.current.api.getFilterInstance(OBSERVABLE_ITEM_ID_FIELD);
      if (!f.getModel() || !f.getModel().operator) {
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
            res.data.items.forEach((i) => cachedOptions.push(i));
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
  const handleSpeciesFilterChange = useCallback((evt, newValue) => {
    const f = gridRef.current.api.getFilterInstance(OBSERVABLE_ITEM_ID_FIELD);

    if (newValue.length === 0) {
      f.setModel(null).then(() => gridRef.current.api.onFilterChanged());
    } else if (newValue.length === 1) {
      f.setModel({
        filter: newValue[0].observableItemId,
        filterType: 'text',
        type: 'equals'
      }).then(() => gridRef.current.api.onFilterChanged());
    } else {
      const model = {
        filterType: 'text',
        operator: 'OR'
      };

      for (let i = 0; i < newValue.length; i++) {
        model['condition' + (i + 1)] = {
          filter: newValue[i].observableItemId,
          filterType: 'text',
          type: 'equals'
        };
      }

      f.setModel(model).then(() => gridRef.current.api.onFilterChanged());
    }
  }, []);

  const onGridReady = useCallback(
      (event) => {
        async function fetchSurveys(e) {
          e.api.setDatasource({
            // This is the functional structure need for datasource
            rowCount: rowsPerPage,
            getRows: (params) => {
              let url = `data/surveys?page=${params.startRow / 100}`;
              let conditions = [];
              // Filter section
              for (let name in params.filterModel) {
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
                      {ops: p.condition1.type, val: p.condition1.filter},
                      {ops: p.condition2.type, val: p.condition2.filter}
                    ]
                  });
                }
              }

              // Sorting section, order is important
              let sort = [];
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
          if (!location?.state?.resetFilters) {
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
        {({auth}) => (
            <>
              <Backdrop sx={{color: '#fff', zIndex: (theme) => theme.zIndex.drawer + 1}} open={loading}>
                <CircularProgress color="inherit" />
              </Backdrop>
              <Box display="flex" flexDirection="row" p={1} pb={1}>
                <Box flexGrow={1}>
                  <Typography variant="h4">Surveys</Typography>
                </Box>
                <Box m={1} ml={0}>
                  <IconButton
                      onClick={() => setExpanded((v) => !v)}
                      aria-expanded={expanded}
                      aria-label="Show more"
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
                            gridRef.current.api.setFilterModel(null);
                            setIsFiltered(false);
                          }}>
                    Reset Filter
                  </Button>
                </Box>
                <Box m={1} ml={0}>
                  <Button
                      variant="outlined"
                      onClick={() => setRedirect(`${selected.join(',')}/correct`)}
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
                        style={{width: '100%'}}
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
                                label="Species filter (max two species)"
                                variant="outlined"
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
                  id={'survey-list'}
                  className="ag-theme-material"
                  rowHeight={24}
                  rowSelection={'multiple'}
                  animateRows={true}
                  enableCellTextSelection={true}
                  pagination={true}
                  paginationPageSize={rowsPerPage}
                  tooltipShowDelay={0}
                  tooltipHideDelay={10000}
                  rowModelType={'infinite'}
                  row
                  onGridReady={(e) => onGridReady(e)}
                  onSelectionChanged={(e) => {
                    setSelected(e.api.getSelectedRows().map((i) => i.surveyId));
                  }}
                  onFilterChanged={(e) => {
                    stateFilterHandler.stateFilterEventHandler(gridRef, e);

                    const filterModel = e.api.getFilterModel();
                    setIsFiltered(Object.getOwnPropertyNames(filterModel).length > 0);
                  }}
                  suppressCellFocus={true}
                  defaultColDef={defaultColDef}

                  // customized setting of table icons
                  icons={icons}
              >
                {auth.features?.includes('corrections') && (
                    <AgGridColumn
                        minWidth={10}
                        field="surveyId"
                        headerName=""
                        suppressMovable={true}
                        filter={false}
                        tooltipValueGetter={() => 'Correct Survey'}
                        resizable={false}
                        sortable={false}
                        checkboxSelection={(e) => e.data?.locked !== true}
                        valueFormatter={() => ''}
                        cellStyle={{paddingLeft: '10px', color: 'grey', cursor: 'pointer'}}
                        onCellClicked={(e) => {
                          if (e.event.ctrlKey) {
                            window.open(`/data/survey/${e.data.surveyId}/correct`, '_blank').focus();
                          } else {
                            setRedirect(`${e.data.surveyId}/correct`);
                          }
                        }}
                    />
                )}
                <AgGridColumn
                    minWidth={40}
                    field="surveyId"
                    headerName=""
                    suppressMovable={true}
                    filter={false}
                    resizable={false}
                    sortable={false}
                    tooltipValueGetter={() => 'Edit Survey Metadata'}
                    valueFormatter={() => '✎'}
                    cellStyle={{paddingLeft: '10px', color: 'grey', cursor: 'pointer'}}
                    onCellClicked={(e) => {
                      if (e.event.ctrlKey) {
                        window.open(`/data/survey/${e.data.surveyId}/edit`, '_blank').focus();
                      } else {
                        setRedirect(`${e.data.surveyId}/edit`);
                      }
                    }}
                />
                <AgGridColumn
                    field="surveyId"
                    headerName="Survey ID"
                    colId="survey.surveyId"
                    sort="desc"
                    sortable="false"
                    cellStyle={{cursor: 'pointer'}}
                    onCellClicked={(e) => {
                      if (e.event.ctrlKey) {
                        window.open(`/data/survey/${e.data.surveyId}`, '_blank').focus();
                      } else {
                        setRedirect(e.data.surveyId);
                      }
                    }}
                />
                <AgGridColumn field="surveyDate" colId="survey.surveyDate" />
                <AgGridColumn field="latitude" colId="survey.latitude" />
                <AgGridColumn field="longitude" colId="survey.longitude" />
                <AgGridColumn headerName="Has PQs" field="pqCatalogued" colId="survey.pqCatalogued" />
                <AgGridColumn field="siteCode" colId="survey.siteCode" />
                <AgGridColumn flex={1} field="siteName" colId="survey.siteName" />
                <AgGridColumn width={50} field="depth" colId="survey.depth" />
                <AgGridColumn width={250} field="diverName" colId="survey.diverName" />
                <AgGridColumn width={50} field="method" colId="survey.method" />
                <AgGridColumn field="programName" headerName="Program" colId="survey.programName" />
                <AgGridColumn flex={1} field="country" colId="survey.country" />
                <AgGridColumn flex={1} field="state" colId="survey.state" />
                <AgGridColumn flex={1} field="ecoregion" colId="survey.ecoregion" />
                <AgGridColumn flex={1} field="locationName" colId="survey.locationName" />
                {/**
                 * Hidden field for apply filter to species.
                 */}
                <AgGridColumn hide={true} flex={1} colId={OBSERVABLE_ITEM_ID_FIELD} />
              </AgGridReact>
            </>
        )}
      </AuthContext.Consumer>
  );
};

export default SurveyList;