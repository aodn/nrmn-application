import React, {useEffect} from 'react';
import {useSelector, useDispatch} from 'react-redux';
import config from 'react-global-configuration';
import {useHistory} from 'react-router';
import {NavLink} from 'react-router-dom';
import {PropTypes} from 'prop-types';
import {Box, Grid, IconButton, Typography} from '@material-ui/core';
import {Edit, FileCopy, Delete} from '@material-ui/icons';
import Alert from '@material-ui/lab/Alert';
import {AgGridReact} from 'ag-grid-react/lib/agGridReact';
import useWindowSize from '../utils/useWindowSize';
import CustomTooltip from './customTooltip';
import CustomLoadingOverlay from './CustomLoadingOverlay';
import {selectRequested} from './middleware/entities';
import {resetState} from './form-reducer';
import LinkButton from './LinkButton';

const cellRenderer = (params) => {
  if (typeof params.value === 'object') {
    return JSON.stringify(params.value)?.replaceAll(/["{}]/g, '').replaceAll(',', ', ').trim();
  }
  return params.value;
};

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
      filter: getCellFilter(type),
      cellRenderer: cellRenderer
    };
  });

  coldefs.push({
    field: '',
    filter: null,
    // eslint-disable-next-line react/display-name
    cellRendererFramework: function (e) {
      return (
        <>
          <IconButton component={NavLink} to={`${entity.route.base}/${e.data[`${entity.name.toLowerCase()}Id`]}/edit`}>
            <Edit />
          </IconButton>
          {entity.can.clone && (
            <IconButton component={NavLink} name="copy" to="/wip">
              <FileCopy />
            </IconButton>
          )}
          {entity.can.delete && (
            <IconButton component={NavLink} name="delete" to="/wip">
              <Delete />
            </IconButton>
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
    history.push(`${entity.route.base}/${e.data[`${entity.name.toLowerCase()}Id`]}`);
  }
};

const EntityList = (props) => {
  const history = useHistory();
  const schemaDefinition = config.get('api');
  const size = useWindowSize();
  const dispatch = useDispatch();
  const entities = useSelector((state) => state.form.entities);
  const errors = useSelector((state) => state.form.errors);
  const items = entities?._embedded ? entities._embedded[props.entity.list.name] : undefined;

  const agGridReady = (agGrid) => {
    agGridApi = Object.create(agGrid.api);
    agGridColumnApi = agGrid.columnApi;
    agGridApi.setRowData(items);
    Object.freeze(agGridApi);
  };

  function autoSizeAll() {
    let allColumnIds = [];
    agGridColumnApi.getAllColumns().forEach(function (column) {
      allColumnIds.push(column.colId);
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
  }, [props.entity.name]); // reset when new or entityName prop changes

  const colDef = schematoColDef(schemaDefinition[props.entity.list.schemaKey], props.entity);

  if (items !== undefined && agGridApi.setRowData) {
    agGridApi.setRowData(items);
    autoSizeAll();
  }

  return (
    <>
      <Box>
        <Grid container direction="row" justify="space-between" alignItems="center">
          <Grid item>
            <Typography variant="h4">{props.entity.name}</Typography>
          </Grid>
          <Grid item>
            <Grid container spacing={2}>
              <LinkButton key={props.entity.name} title={`New ${props.entity.name}`} to={props.entity.route.base} />
            </Grid>
          </Grid>
        </Grid>

        <div style={{height: size.height - 150}} className={'ag-theme-material'}>
          <AgGridReact
            columnDefs={colDef}
            rowSelection="single"
            animateRows={true}
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
              sortable: false,
              resizable: true,
              tooltipComponent: 'customTooltip',
              floatingFilter: true,
              headerComponentParams: {
                menuIcon: 'fa-bars'
              }
            }}
          />
        </div>
        {!colDef ? renderError(["Entity '" + props.entity.name + "' can not be found!"]) : ''}
        {errors.length > 0 ? renderError(errors) : ''}
      </Box>
    </>
  );
};

EntityList.propTypes = {
  entity: PropTypes.object
};
export default EntityList;
