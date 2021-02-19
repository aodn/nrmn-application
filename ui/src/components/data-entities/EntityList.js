import React from 'react';
import {Box, Typography} from '@material-ui/core';
import {useSelector, useDispatch} from 'react-redux';
import {useEffect} from 'react';
import {useParams} from 'react-router-dom';
import pluralize from 'pluralize';
import {AgGridReact} from 'ag-grid-react/lib/agGridReact';
import useWindowSize from '../utils/useWindowSize';
import Alert from '@material-ui/lab/Alert';
import config from 'react-global-configuration';
import {titleCase} from 'title-case';
import Grid from '@material-ui/core/Grid';
import CustomTooltip from './customTooltip';
import CustomLoadingOverlay from './CustomLoadingOverlay';
import {selectRequested} from './middleware/entities';
import {resetState} from './form-reducer';
import LinkCell from './customWidgetFields/LinkCell';
import LinkButton from './LinkButton';
import {useHistory} from 'react-router';

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

const schematoColDef = (schema, size, entityName) => {
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
  const cloneButtonWidth = nonGenericEntities[entityName]?.cloneButton ? widthUnit : 0;
  const deleteButtonWidth = nonGenericEntities[entityName]?.hideDeleteButton ? 0 : widthUnit;
  const linksWidth = widthUnit + cloneButtonWidth + deleteButtonWidth;

  coldefs.push({
    field: 'Links',
    maxWidth: linksWidth,
    minWidth: linksWidth,
    filter: undefined,
    cellRendererFramework: function (params) {
      const linkPath = nonGenericEntities[entityName]?.entityLinkPath;

      if (params.data._links) {
        const hrefSplit = params.data._links.self.href.split('/');
        const id = hrefSplit.pop();
        const ent = hrefSplit.pop();

        let link = '/edit/' + ent + '/' + id;
        const cloneLink = '/edit/' + ent + '/clone/' + id;
        const deleteLink = '/delete/' + ent + '/' + id;
        let linkLabel = 'Edit';

        if (linkPath) {
          link = '/' + linkPath.replace(/{(.*?)}/, id);
          linkLabel = nonGenericEntities[entityName]?.entityLinkLabel ? nonGenericEntities[entityName]?.entityLinkLabel : linkLabel;
        }

        return (
          <>
            <LinkCell label={linkLabel} link={link}></LinkCell>
            {nonGenericEntities[entityName]?.cloneButton ? <LinkCell label={'Clone'} link={cloneLink}></LinkCell> : null}
            {nonGenericEntities[entityName]?.hideDeleteButton ? null : <LinkCell label={'Delete'} link={deleteLink}></LinkCell>}
          </>
        );
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

// nonGenericEntities parameters
//
// title: 'Job',
// createButtonPath:  - attribute required if entity defined, or no create button will show
// hideDeleteButton - delete button shown by default
// cloneButton: true - show a clone link button
// entityLinkLabel: 'Details', - defaults to be the main edit button per item
// entityLinkPath: 'view/stagedJobs/{}',
// additionalPageLinks: {label, link} - additional links for the page
const nonGenericEntities = {
  Diver: {
    cloneButton: true,
    hideDeleteButton: false
  },
  StagedJob: {
    title: 'Job',
    createButtonPath: '/upload',
    entityLinkLabel: 'Details',
    entityLinkPath: 'view/stagedJobs/{}'
    // additionalPageLinks: [
    //   {
    //     label: 'New Corrections',
    //     link: 'corrections'
    //   }
    // ]
  },
  Site: {
    entityListName: 'SiteListItem',
    createButtonPath: '/edit/sites',
    entityLinkLabel: 'Details',
    entityLinkPath: 'view/sites/{}'
  },
  ObservableItem: {
    entityListName: 'ObservableItemListItem',
    createButtonPath: '/edit/observableItems',
    entityLinkLabel: 'Details',
    entityLinkPath: 'view/observableItems/{}'
  }
};

const gotoDetailsView = (event, history) => {
  if (event.node.isSelected() && event.colDef.field !== 'Links' && event.node.data._links) {
    const hrefSplit = event.node.data._links.self.href.split('/');
    const id = hrefSplit.pop();
    const ent = hrefSplit.pop();
    history.push('/view/' + ent + '/' + id);
  }
};

const EntityList = () => {
  const {entityName} = useParams();
  const history = useHistory();

  const entityPluralise = (thisEntityName) => {
    const plural = pluralize.plural(thisEntityName);
    return plural.charAt(0).toLowerCase() + plural.slice(1);
  };

  const getListEntity = () => {
    let entityListName = nonGenericEntities[entityName]?.entityListName;
    if (entityListName) {
      return entityListName;
    } else {
      return schemaDefinition[titleCase(entityName)] ? titleCase(entityName) : entityName;
    }
  };

  const schemaDefinition = config.get('api');
  const size = useWindowSize();
  const columnFit = useSelector((state) => state.theme.columnFit);
  const dispatch = useDispatch();
  const entities = useSelector((state) => state.form.entities);
  const errors = useSelector((state) => state.form.errors);
  const items = entities?._embedded ? entities._embedded[entityPluralise(getListEntity())] : undefined;

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
    const entityListName = nonGenericEntities[entityName]?.entityListName ? nonGenericEntities[entityName]?.entityListName : entityName;
    dispatch(selectRequested(entityPluralise(entityListName)));
  }, [entityName]); // reset when new or entityName prop changes

  const getTitle = () => {
    return nonGenericEntities[entityName]?.title ? nonGenericEntities[entityName]?.title : entityName;
  };

  const additionalPageLinks = () => {
    let links = nonGenericEntities[entityName]?.additionalPageLinks;
    return links?.map((item) => {
      return <LinkButton key={getTitle() + item.label} title={item.label} label={item.label} to={item.link} />;
    });
  };

  const newEntityButton = () => {
    let createButtonPath = nonGenericEntities[entityName]?.createButtonPath;
    if (!(entityName in nonGenericEntities) || createButtonPath) {
      const to = createButtonPath ? createButtonPath : '/edit/' + entityPluralise(entityName);
      return <LinkButton key={getTitle() + to} title={'New ' + getTitle()} label={'New ' + getTitle()} to={to} />;
    }
  };

  if (Object.keys(schemaDefinition).length === 0) {
    return renderError(['Error: API not yet loaded']);
  } else {
    if (!schemaDefinition[getListEntity()]) {
      return renderError(["ERROR: Entity '" + titleCase(entityName) + "' missing from API Schema"]);
    }

    const colDef = schematoColDef(schemaDefinition[getListEntity()], size, entityName);
    const agGridParentWidth = columnFit ? '100%' : '99%'; // triggers agGrid layout

    if (items !== undefined && agGridApi.setRowData) {
      agGridApi.setRowData(items);
    }

    return (
      <>
        <Box>
          <Grid container direction="row" justify="space-between" alignItems="center">
            <Grid item>
              <Typography variant="h4">{getTitle()}</Typography>
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
          {!colDef ? renderError(["Entity '" + entityName + "' can not be found!"]) : ''}
          {errors.length > 0 ? renderError(errors) : ''}
        </Box>
      </>
    );
  }
};

export default EntityList;
