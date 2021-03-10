import React, {useEffect} from 'react';
import {useSelector, useDispatch} from 'react-redux';
import config from 'react-global-configuration';
import {useHistory} from 'react-router';
import {NavLink} from 'react-router-dom';
import {PropTypes} from 'prop-types';
import {Box, Button, Grid, IconButton, Tooltip, Typography} from '@material-ui/core';
import {Add, Edit, FileCopy, Delete} from '@material-ui/icons';
import Alert from '@material-ui/lab/Alert';
import {AgGridReact} from 'ag-grid-react/lib/agGridReact';
import useWindowSize from '../utils/useWindowSize';
import CustomTooltip from './customTooltip';
import CustomLoadingOverlay from './CustomLoadingOverlay';
import {selectRequested} from './middleware/entities';
import {resetState} from './form-reducer';

const getCellFilter = (format) => {
  const filterTypes = {
    int64: 'agNumberColumnFilter',
    'date-time': 'agDateColumnFilter'
  };
  return filterTypes[format] ? filterTypes[format] : 'agTextColumnFilter';
};

const schematoColDef = (schema, entity) => {
  const fields = Object.keys(schema.properties);
  const coldefs = fields.map((field) => {
    let type = schema.properties[field] ? schema.properties[field]?.type : 'string';
    return {
      field: field,
      tooltipField: field,
      suppressMovable: true,
      flex: field === entity.flexField ? true : false,
      filter: getCellFilter(type)
    };
  });
  const scale = 1 + (entity.can.clone | 0) + (entity.can.delete | 0);
  coldefs.push({
    field: '',
    filter: null,
    suppressMovable: true,
    minWidth: 60 * scale,
    // eslint-disable-next-line react/display-name
    cellRendererFramework: function (e) {
      return (
        <>
          <Tooltip title="Edit" aria-label="edit">
            <IconButton component={NavLink} to={`${entity.route.base}/${e.data[entity.idKey]}/edit`}>
              <Edit />
            </IconButton>
          </Tooltip>
          {entity.can.clone && (
            <Tooltip title="Clone" aria-label="clone">
              <IconButton component={NavLink} to={`${entity.route.base}/${e.data[entity.idKey]}/clone`}>
                <FileCopy />
              </IconButton>
            </Tooltip>
          )}
          {entity.can.delete && (
            <Tooltip title="Delete" aria-label="delete">
              <IconButton component={NavLink} name="delete" to="/wip">
                <Delete />
              </IconButton>
            </Tooltip>
          )}
        </>
      );
    }
  });

  return coldefs;
};

let agGridApi = {};
let agGridColumnApi = {};

const renderError = (msgArray) => {
  return (
    <Box>
      <Alert style={{height: 'auto', lineHeight: '28px', whiteSpace: 'pre-line'}} severity="error" variant="filled">
        {msgArray.join('\r\n ')}
      </Alert>
    </Box>
  );
};

const gotoEntity = (e, history, entity) => {
  if (e.node.isSelected() && !e.colDef.cellRendererFramework) {
    history.push(`${entity.route.base}/${e.data[entity.idKey]}`);
  }
};

const EntityList = (props) => {
  const history = useHistory();
  const schemaDefinition = config.get('api');
  const size = useWindowSize();
  const dispatch = useDispatch();
  const entities = useSelector((state) => state.form.entities);
  const errors = useSelector((state) => state.form.errors);
  const items = entities?._embedded[props.entity.list.name];

  const agGridReady = (agGrid) => {
    agGridApi = Object.create(agGrid.api);
    agGridColumnApi = agGrid.columnApi;
    Object.freeze(agGridApi);
  };

  function autoSizeAll() {
    let allColumnIds = [];
    agGridColumnApi.getAllColumns().forEach(function (column) {
      if (props.entity.flexField !== column.colId) allColumnIds.push(column.colId);
    });
    agGridColumnApi.autoSizeColumns(allColumnIds, false);
  }

  useEffect(() => {
    dispatch(resetState());
    if (agGridApi.setRowData) {
      agGridApi.setRowData([]);
      agGridApi.showLoadingOverlay();
    }
    dispatch(selectRequested(props.entity.list.endpoint));
  }, [props.entity.name]);

  const colDef = schematoColDef(schemaDefinition[props.entity.list.schemaKey], props.entity);

  return (
    <>
      <Grid container direction="row" justify="space-between" style={{paddingLeft: 20}} alignItems="center">
        <Grid item>
          <Typography variant="h4">{props.entity.name}</Typography>
        </Grid>
        <Grid item>
          <Button
            {...props}
            to={props.entity.route.base}
            component={NavLink}
            color="secondary"
            variant={'contained'}
            startIcon={<Add></Add>}
          >
            New {props.entity.name}
          </Button>
        </Grid>
      </Grid>

      <div style={{height: size.height - 150, marginTop: 20}} className={'ag-theme-material'}>
        <AgGridReact
          columnDefs={colDef}
          rowSelection="single"
          animateRows={false}
          rowData={items}
          onGridReady={agGridReady}
          onFirstDataRendered={autoSizeAll}
          onCellClicked={(e) => {
            gotoEntity(e, history, props.entity);
          }}
          frameworkComponents={{
            customTooltip: CustomTooltip,
            customLoadingOverlay: CustomLoadingOverlay
          }}
          loadingOverlayComponent={'customLoadingOverlay'}
          tooltipShowDelay={0}
          defaultColDef={{
            sortable: true,
            resizable: true,
            tooltipComponent: 'customTooltip',
            floatingFilter: true,
            headerComponentParams: {
              menuIcon: 'fa-bars'
            }
          }}
        />
      </div>
      {errors.length > 0 ? renderError(errors) : ''}
    </>
  );
};

EntityList.propTypes = {
  entity: PropTypes.object
};
export default EntityList;
