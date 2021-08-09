import React, {useEffect, useState} from 'react';
import {useSelector, useDispatch} from 'react-redux';
import config from 'react-global-configuration';
import {useHistory} from 'react-router';
import {NavLink} from 'react-router-dom';
import {PropTypes} from 'prop-types';

import {Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, Grid, IconButton, Tooltip, Typography} from '@material-ui/core';
import {Add, Edit, FileCopy, Delete} from '@material-ui/icons';
import Alert from '@material-ui/lab/Alert';

import 'ag-grid-community/dist/styles/ag-grid.css';
import 'ag-grid-community/dist/styles/ag-theme-material.css';
import {AgGridReact} from 'ag-grid-react';
import 'ag-grid-enterprise';

import CustomLoadingOverlay from './CustomLoadingOverlay';
import {selectRequested, deleteEntityRequested} from './middleware/entities';
import {resetState} from './form-reducer';

const getCellFilter = (format) => {
  const filterTypes = {
    int64: 'agNumberColumnFilter',
    'date-time': 'agDateColumnFilter'
  };
  return filterTypes[format] ? filterTypes[format] : 'agTextColumnFilter';
};

const renderError = (msgArray) => {
  return (
    <Box>
      <Alert style={{height: 'auto', lineHeight: '28px', whiteSpace: 'pre-line'}} severity="error" variant="filled">
        {msgArray.join('\r\n ')}
      </Alert>
    </Box>
  );
};

const saveFilterModel = (entityName, filterModel) => {
  window[`AgGrid-FilterModel-${entityName}`] = JSON.stringify(filterModel);
};

const restoreFilterModel = (entityName) => {
  const serialisedFilter = window[`AgGrid-FilterModel-${entityName}`];
  return serialisedFilter ? JSON.parse(serialisedFilter) : null;
};

const EntityList = (props) => {
  const history = useHistory();
  const dispatch = useDispatch();
  const entities = useSelector((state) => state.form.entities);
  const errors = useSelector((state) => state.form.errors);
  const [disableResetFilter, setResetFilterDisabled] = useState(true);
  const [agGridApi, setAgGridApi] = useState(null);
  const [agGridColumnApi, setAgGridColumnApi] = useState(null);

  useEffect(() => {
    dispatch(resetState());
    dispatch(selectRequested(props.entity.list.endpoint));
  }, [dispatch, props.entity]);

  let items = entities?._embedded ? entities?._embedded[props.entity.list.key] : entities;
  useEffect(() => {
    if (agGridApi && agGridColumnApi) {
      let allColumnIds = [];
      agGridColumnApi.getAllColumns().forEach(function (column) {
        if (props.entity.flexField !== column.colId && column.colId !== '0') allColumnIds.push(column.colId);
      });
      agGridColumnApi.autoSizeColumns(allColumnIds, false);
      agGridApi.setFilterModel(restoreFilterModel(props.entity.name));
    }
  }, [agGridApi, agGridColumnApi, items, props.entity]);

  const schematoColDef = (schema, entity) => {
    const fields = entity.list.headers ?? Object.keys(schema.properties);

    const coldefs = fields.reduce((acc, field) => {
      const fieldSchema = schema.properties[field];
      if (fieldSchema.title) {
        const sortable = entity.list.sort?.includes(field) ?? true;
        acc.push({
          headerName: fieldSchema.title,
          field: field,
          tooltipField: field,
          suppressMovable: true,
          sortable: sortable,
          flex: 1,
          filter: getCellFilter(fieldSchema?.type || 'string')
        });
      }
      return acc;
    }, []);
    const scale = 1 + (entity.can.clone | 0) + (entity.can.delete | 0);
    coldefs.push({
      field: '',
      filter: null,
      suppressMovable: true,
      width: Math.max(60 * scale, 100),
      // eslint-disable-next-line react/display-name
      cellRendererFramework: function (cell) {
        return (
          <>
            <Tooltip title="Edit" aria-label="edit">
              <IconButton component={NavLink} to={`${entity.route.base}/${cell.data[entity.idKey]}/edit`}>
                <Edit />
              </IconButton>
            </Tooltip>
            {entity.can.clone && (
              <Tooltip title="Clone" aria-label="clone">
                <IconButton component={NavLink} to={`${entity.route.base}/${cell.data[entity.idKey]}/clone`}>
                  <FileCopy />
                </IconButton>
              </Tooltip>
            )}
            {entity.can.delete && (
              <Tooltip title="Delete" aria-label="delete">
                <span>
                  <IconButton
                    name="delete"
                    disabled={cell.data.isActive}
                    onClick={() => {
                      setDialogState({
                        open: true,
                        id: cell.data[entity.idKey],
                        index: cell.rowIndex,
                        // HACK: making the assumption that all entities called `Entity` have a property `entityName`
                        description: cell.data[`${entity.name.toLowerCase()}Name`]
                      });
                    }}
                  >
                    <Delete />
                  </IconButton>
                </span>
              </Tooltip>
            )}
          </>
        );
      }
    });

    return coldefs;
  };

  const agGridReady = (agGrid) => {
    setAgGridApi(agGrid.api);
    setAgGridColumnApi(agGrid.columnApi);
    agGrid.api.showLoadingOverlay();
    agGrid.api.setSortModel(props.entity.list.initialSortModel);
  };

  const [dialogState, setDialogState] = useState({open: false});

  const onRowClick = (e, history, entity) => {
    if (e.node.isSelected() && !e.colDef.cellRendererFramework) {
      history.push(`${entity.route.base}/${e.data[entity.idKey]}`);
    }
  };

  const schemas = config.get('api');
  const colDef = schematoColDef(schemas[props.entity.list.schemaKey], props.entity);

  const dialog = (
    <Dialog disableBackdropClick disableEscapeKeyDown maxWidth="xs" open>
      <DialogTitle>Delete {props.entity.name}?</DialogTitle>
      <DialogContent>
        Are you sure you want to permanently delete this {props.entity.name.toLowerCase()}?
        <Box p={2}>
          <Typography variant="subtitle2">{dialogState.description}</Typography>
        </Box>
      </DialogContent>
      <DialogActions>
        <Button autoFocus onClick={() => setDialogState({open: false})}>
          Cancel
        </Button>
        <Button
          onClick={() => {
            const item = items.find((i) => i.siteId === dialogState.id);
            dispatch(deleteEntityRequested({entity: props.entity, id: dialogState.id}));
            agGridApi.applyTransaction({remove: [item]});
            setDialogState({open: false});
          }}
          style={{color: 'red'}}
        >
          Delete {props.entity.name}
        </Button>
      </DialogActions>
    </Dialog>
  );

  return (
    <>
      {dialogState.open && dialog}
      <Box m={2}>
        <Grid container direction="row" justify="space-between">
          <Grid item xs={8}>
            <Typography variant="h4">{props.entity.list.name}</Typography>
          </Grid>
          <Grid item xs={2}>
            <Button
              style={{width: '75%'}}
              disabled={disableResetFilter}
              onClick={() => agGridApi.setFilterModel(null)}
              color="primary"
              variant={'contained'}
            >
              Reset Filter
            </Button>
          </Grid>
          <Grid item xs={2}>
            {props.entity.list.showNew && (
              <Button
                {...props}
                style={{width: '100%'}}
                to={props.entity.route.base}
                component={NavLink}
                color="secondary"
                variant={'contained'}
                startIcon={<Add></Add>}
              >
                New {props.entity.name}
              </Button>
            )}
          </Grid>
        </Grid>
      </Box>
      {errors.length > 0 ? renderError(errors) : ''}
      <Box flexGrow={1} overflow="hidden" className="ag-theme-material" id="validation-grid">
        <AgGridReact
          columnDefs={colDef}
          rowSelection="single"
          rowData={items}
          onGridReady={agGridReady}
          onFilterChanged={(e) => {
            const filterModel = e.api.getFilterModel();
            saveFilterModel(props.entity.name, filterModel);
            setResetFilterDisabled(Object.keys(filterModel)?.length < 1);
          }}
          onCellClicked={(e) => onRowClick(e, history, props.entity)}
          frameworkComponents={{
            customLoadingOverlay: CustomLoadingOverlay
          }}
          loadingOverlayComponent={'customLoadingOverlay'}
          tooltipShowDelay={0}
          defaultColDef={{
            sortable: true,
            resizable: true,
            floatingFilter: true,
            suppressMenu: true
          }}
        />
      </Box>
    </>
  );
};

EntityList.propTypes = {
  entity: PropTypes.object
};

export default EntityList;
