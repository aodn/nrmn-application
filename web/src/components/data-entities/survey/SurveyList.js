import {Box, Button, Typography, Autocomplete, CircularProgress, TextField, Collapse, IconButton} from '@mui/material';
import Backdrop from '@mui/material/Backdrop';
import 'ag-grid-enterprise';
import {AgGridColumn, AgGridReact} from 'ag-grid-react';
import React, {useCallback, useRef, useState, useEffect} from 'react';
import {Navigate, useLocation} from 'react-router-dom';
import {getResult} from '../../../api/api';
import stateFilterHandler from '../../../common/state-event-handler/StateFilterHandler';
import {AuthContext} from '../../../contexts/auth-context';
import clsx from 'clsx';
import {makeStyles} from '@mui/styles';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import {createFilterOptions} from '@mui/material/Autocomplete';

// We want to keep the value between pages, so we only need to load it once.
const cachedOptions = [];
const OBSERVABLE_ITEM_ID_FIELD = 'survey.observableItemId';

const useStyles = makeStyles((theme) => ({
  root: {
    maxWidth: 345
  },
  media: {
    height: 0,
    paddingTop: '56.25%' // 16:9
  },
  expand: {
    transform: 'rotate(0deg)',
    marginLeft: 'auto',
    transition: theme.transitions.create('transform', {
      duration: theme.transitions.duration.shortest
    })
  },
  expandOpen: {
    transform: 'rotate(180deg)'
  }
}));

const SurveyList = () => {
  const rowsPerPage = 50;
  const classes = useStyles();
  const location = useLocation();
  const gridRef = useRef(null);
  const [redirect, setRedirect] = useState();
  const [loading, setLoading] = useState(false);
  const [selected, setSelected] = useState();
  const [open, setOpen] = useState(false);
  const [options, setOptions] = useState([]);
  const [expanded, setExpanded] = React.useState(false);
  const optionLoading = open && options.length === 0;

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
            <Box>
              <IconButton
                className={clsx(classes.expand, {
                  [classes.expandOpen]: expanded
                })}
                onClick={() => setExpanded((v) => !v)}
                aria-expanded={expanded}
                aria-label="Show more"
              >
                <ExpandMoreIcon />
              </IconButton>
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
            }}
            suppressCellFocus={true}
            defaultColDef={{
              lockVisible: true,
              sortable: true,
              resizable: true,
              filter: 'agTextColumnFilter',
              suppressMenu: true,
              floatingFilterComponentParams: {suppressFilterButton:true},
              filterParams: {debounceMs: 2000 },
              floatingFilter: true
            }}
          >
            {auth.features?.includes('corrections') && (
              <AgGridColumn
                width={10}
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
              width={40}
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
              width={110}
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
            <AgGridColumn width={100} field="surveyDate" colId="survey.surveyDate" />
            <AgGridColumn width={100} field="latitude" colId="survey.latitude" />
            <AgGridColumn width={100} field="longitude" colId="survey.longitude" />
            <AgGridColumn width={100} headerName="Has PQs" field="pqCatalogued" colId="survey.pqCatalogued" />
            <AgGridColumn width={100} field="siteCode" colId="survey.siteCode" />
            <AgGridColumn flex={1} field="siteName" colId="survey.siteName" />
            <AgGridColumn width={50} field="depth" colId="survey.depth" />
            <AgGridColumn width={250} field="diverName" colId="survey.diverName" />
            <AgGridColumn width={50} field="method" colId="survey.method" />
            <AgGridColumn width={100} field="programName" headerName="Program" colId="survey.programName" />
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
