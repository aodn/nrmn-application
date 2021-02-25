import React from 'react';
import {Box, Typography} from '@material-ui/core';
import {useSelector, useDispatch} from 'react-redux';
import {useEffect} from 'react';
import {AgGridReact} from 'ag-grid-react/lib/agGridReact';
import useWindowSize from '../utils/useWindowSize';
import Alert from '@material-ui/lab/Alert';
import config from 'react-global-configuration';
import Grid from '@material-ui/core/Grid';
import CustomTooltip from './customTooltip';
import CustomLoadingOverlay from './CustomLoadingOverlay';
import {selectRequested} from './middleware/entities';
import {resetState} from './form-reducer';
import LinkCell from './customWidgetFields/LinkCell';
import LinkButton from './LinkButton';
import {useHistory} from 'react-router';
import {PropTypes} from 'prop-types';

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

  const widthUnit = 100;

  coldefs.push({
    field: 'Links',
    maxWidth: widthUnit,
    minWidth: widthUnit,
    filter: undefined,
    cellRendererFramework: function (params) {
      if (params.data._links) {
        const hrefSplit = params.data._links.self.href.split('/');
        const id = hrefSplit.pop();
        const ent = hrefSplit.pop();

        const link = '/edit/' + ent + '/' + id;
        const linkLabel = entity.entityLinkLabel ?? 'Edit';

        return <LinkCell label={linkLabel} link={link}></LinkCell>;
      }
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

const gotoDetailsView = (event, history) => {
  if (event.node.isSelected() && event.colDef.field !== 'Links' && event.node.data._links) {
    const hrefSplit = event.node.data._links.self.href.split('/');
    const id = hrefSplit.pop();
    const ent = hrefSplit.pop();
    history.push('/view/' + ent + '/' + id);
  }
};

const EntityList = (props) => {
  const history = useHistory();
  const schemaDefinition = config.get('api');
  const size = useWindowSize();
  const columnFit = useSelector((state) => state.theme.columnFit);
  const dispatch = useDispatch();
  const entities = useSelector((state) => state.form.entities);
  const errors = useSelector((state) => state.form.errors);
  const items = entities?._embedded ? entities._embedded[props.entity.entityListName] : undefined;

  const agGridReady = (agGrid) => {
    agGridApi = Object.create(agGrid.api);
    agGridColumnApi = agGrid.columnApi;
    agGridApi.setRowData(items);
    Object.freeze(agGridApi);
  };

  const onGridSizeChanged = () => {
    if (columnFit) {
      autoSizeAll();
    } else {
      agGridApi.sizeColumnsToFit();
    }
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
      onGridSizeChanged();
      agGridApi.showLoadingOverlay();
    }
    const entityListName = props.entity.entityListName ?? props.entity.name;
    dispatch(selectRequested(entityListName));
  }, [props.entity.name]); // reset when new or entityName prop changes

  const additionalPageLinks = () => {
    let links = props.entity.additionalPageLinks;
    return links?.map((item) => {
      return <LinkButton key={props.entity.entityName + item.label} title={item.label} label={item.label} to={item.link} />;
    });
  };

  const newEntityButton = () => {
    let createButtonPath = props.entity.createButtonPath;
    const to = createButtonPath ? createButtonPath : '/edit/' + props.entity.name;
    return (
      <LinkButton
        key={props.entity.entityName + to}
        title={'New ' + props.entity.entityName}
        label={'New ' + props.entity.entityName}
        to={to}
      />
    );
  };

  const colDef = schematoColDef(schemaDefinition[props.entity.entityName], size, props.entity.name);
  const agGridParentWidth = columnFit ? '100%' : '99%'; // triggers agGrid layout

  if (items !== undefined && agGridApi.setRowData) {
    agGridApi.setRowData(items);
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
              {additionalPageLinks()}
              {newEntityButton()}
            </Grid>
          </Grid>
        </Grid>

        <div style={{width: agGridParentWidth, height: size.height - 170, marginTop: 25}} className={'ag-theme-material'}>
          <AgGridReact
            columnDefs={colDef}
            rowSelection="single"
            animateRows={true}
            onGridReady={agGridReady}
            onFirstDataRendered={onGridSizeChanged}
            onGridSizeChanged={onGridSizeChanged}
            onCellClicked={(e) => {
              gotoDetailsView(e, history);
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
        {!colDef ? renderError(["Entity '" + props.entity.entityName + "' can not be found!"]) : ''}
        {errors.length > 0 ? renderError(errors) : ''}
      </Box>
    </>
  );
};

EntityList.propTypes = {
  entity: PropTypes.object
};
export default EntityList;
