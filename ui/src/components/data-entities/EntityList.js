import React from 'react';
import { Box, Typography } from '@material-ui/core';
import { useSelector, useDispatch } from 'react-redux';
import { useEffect } from 'react';
import { useParams } from 'react-router-dom';
import pluralize from 'pluralize';
import { AgGridReact } from 'ag-grid-react/lib/agGridReact';
import useWindowSize from '../utils/useWindowSize';
import Alert from '@material-ui/lab/Alert';
import config from 'react-global-configuration';
import { titleCase } from 'title-case';
import Grid from '@material-ui/core/Grid';
import CustomTooltip from './customTooltip';
import CustomLoadingOverlay from './CustomLoadingOverlay';
import { selectRequested } from './middleware/entities';
import { resetState } from './form-reducer';
import LinkCell from './customWidgetFields/LinkCell';
import LinkButton from './LinkButton';

const cellRenderer = (params) => {
  if (typeof params.value === 'object') {
    return (
      JSON.stringify(params.value)?.replaceAll(/["{}]/g, '')
        .replaceAll(',', ', ').trim()
    );
  };
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
  const widthSize = size.width / (fields.length + 1);
  const coldefs = fields.map(field => {

    let type = schema.properties[field] ? schema.properties[field]?.format : 'string';
    return {
      field: field,
      width: widthSize,
      tooltipField: field,
      filter: getCellFilter(type),
      cellRenderer: cellRenderer
    };
  });

  coldefs.push({
    field: 'Links',
    filter: undefined,
    cellRendererFramework: function (params) {

      const linkPath = nonGenericEntities[entityName]?.entityLinkPath;
      let linkLabel = 'Edit';
      let link = '/';
      if (params.data._links) {
        const hrefSplit = params.data._links.self.href.split('/');
        const id = hrefSplit.pop();
        const ent = hrefSplit.pop();

        if ((entityName in nonGenericEntities) && linkPath) {
          link = '/' + linkPath.replace(/{(.*?)}/, id);
          linkLabel = (nonGenericEntities[entityName]?.entityLinkLabel) ? nonGenericEntities[entityName]?.entityLinkLabel : linkLabel;
        }
        else {
          link = '/edit/' + ent + '/' + id;
          linkLabel = 'Edit';
        }
        return (<LinkCell label={linkLabel} link={link}></LinkCell>);
      }
    }
  });
  return coldefs;
};

let agGridApi = {};

const renderError = (msgArray) => {
  return <Box>
    <Alert style={{ height: 'auto', lineHeight: '28px', whiteSpace: 'pre-line' }}
      severity="error"
      variant="filled"
    >{msgArray.join('\r\n ')}
    </Alert></Box>;
};

const nonGenericEntities = {
  // createButtonPath attribute required or no create button will show
  'StagedJob': {
    title: 'Job',
    createButtonPath: '/upload',
    entityLinkLabel: 'Details',
    entityLinkPath: 'view/stagedJobs/{}',
    // additionalPageLinks: [
    //   {
    //     label: 'New Corrections',
    //     link: 'corrections'
    //   }
    // ]
  }
};

const EntityList = () => {

  const { entityName } = useParams();
  const plural = pluralize.plural(entityName);
  const entityNamePlural = plural.charAt(0).toLowerCase() + plural.slice(1);

  const schemaDefinition = config.get('api');
  const size = useWindowSize();

  const dispatch = useDispatch();
  const entities = useSelector(state => state.form.entities);
  const errors = useSelector(state => state.form.errors);
  const items = (entities?._embedded) ? entities._embedded[entityNamePlural] : undefined;

  const agGridReady = (agGrid) => {
    agGridApi = Object.create(agGrid.api);
    agGridApi.setRowData(items);
    Object.freeze(agGridApi);
  };

  useEffect(() => {
    dispatch(resetState());
    if (agGridApi.setRowData) {
      agGridApi.setRowData([]);
      agGridApi.showLoadingOverlay();
    }
    dispatch(selectRequested(entityNamePlural));
  }, [entityName]); // reset when new or entityName prop changes

  const getEntitySchema = () => {
    return (schemaDefinition[titleCase(entityName)]) ? (schemaDefinition[titleCase(entityName)]) :
      (schemaDefinition[entityName]);
  };

  const getTitle = () => {
    let thisTitle = nonGenericEntities[entityName]?.title;
    if ((entityName in nonGenericEntities) && thisTitle) {
      return thisTitle;
    }
    else {
      return entityName;
    }
  };


  const additionalPageLinks = () => {
    let links = nonGenericEntities[entityName]?.additionalPageLinks;
    return links?.map((item) => {
      return <LinkButton
          key={getTitle() + item.label}
          title={item.label}
          label={item.label}
          to={item.link}
        />;
    });
  };

  const newEntityButton = () => {
    let createButtonPath = nonGenericEntities[entityName]?.createButtonPath;
    if (!(entityName in nonGenericEntities) || createButtonPath) {
      const to = (createButtonPath) ? createButtonPath : '/edit/' + entityNamePlural;
      return <LinkButton
          key={getTitle() + to}
          title={'New ' + getTitle()}
          label={'New ' + getTitle()}
          to={to}
      />;
    }
  };

  if (Object.keys(schemaDefinition).length === 0) {
    return (renderError(['Error: API not yet loaded']));
  }
  else {

    if (!getEntitySchema()) {
      return renderError(["ERROR: Entity '" + titleCase(entityName) + "' missing from API Schema"]);
    }

    const colDef = schematoColDef(getEntitySchema(), size, entityName);

    if (items !== undefined && agGridApi.setRowData) {
      agGridApi.setRowData(items);
    }

    return (
      <>
        <Box >
          <Grid
            container
            direction="row"
            justify="space-between"
            alignItems="center"
          >
            <Grid item>
              <Typography variant="h4">{getTitle()}</Typography>
            </Grid>
            <Grid item>
              <Grid container  spacing={2}>
                {additionalPageLinks()}
                {newEntityButton()}
              </Grid>
            </Grid>
          </Grid>

            <div style={{width: '100%', height: size.height - 170, marginTop: 25}}
                 className={'ag-theme-material'}>
              <AgGridReact
                  columnDefs={colDef}
                  rowSelection="single"
                  animateRows={true}
                  onGridReady={agGridReady}
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
                  }}/>
            </div>
            {(!colDef) ? renderError(["Entity '" + entityName + "' can not be found!"]) : ''}
            {(errors.length > 0) ? renderError(errors) : ''}
          </Box>
        </>
    );
  }
};

export default EntityList;



