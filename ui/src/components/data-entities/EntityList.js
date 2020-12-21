import React from "react";
import {Box, Typography, Button} from "@material-ui/core";
import {useSelector, useDispatch} from "react-redux";
import {useEffect} from 'react';
import {useParams, NavLink} from "react-router-dom";
import pluralize from 'pluralize';
import {AgGridReact} from "ag-grid-react/lib/agGridReact";
import useWindowSize from "../utils/useWindowSize";
import Alert from "@material-ui/lab/Alert";
import config from "react-global-configuration";
import {titleCase} from "title-case";
import Grid from "@material-ui/core/Grid";
import CustomTooltip from "./customTooltip";
import CustomLoadingOverlay from "./customLoadingOverlay";
import {selectRequested} from "./middleware/entities";
import {resetState} from "./form-reducer";


const cellRenderer = (params) => {
  if (typeof params.value === 'object') {
    return (
        JSON.stringify(params.value)?.replaceAll(/["{}]/g,'')
            .replaceAll(',',', ').trim()
    );
  };
  return params.value;
}

const schematoColDef = (schema, size, entityName) => {

  const fields = Object.keys(schema.properties);
  const widthSize = size.width / (fields.length + 1 );
  const coldefs = fields.map(field => {

    return {
      field: field,
      width: widthSize,
      tooltipField: field,
      // make every column use 'text' filter by default
      filter: 'agTextColumnFilter',
      cellRenderer: cellRenderer
    }
  });

  coldefs.push({
    field: "Links",
    filter: undefined,
    cellRenderer: function (params) {
      const linkPath = nonGenericEntities[entityName]?.linkPath;
      let linkLabel = "Edit";
      let link = "/";
      if (params.data._links) {
        const hrefSplit = params.data._links.self.href.split("/");
        const id = hrefSplit.pop();
        const ent = hrefSplit.pop();

        if ((entityName in nonGenericEntities) && linkPath) {
          link =  "/" + linkPath.replace(/{(.*?)}/, id);
          linkLabel = (nonGenericEntities[entityName]?.linkLabel) ? nonGenericEntities[entityName]?.linkLabel : linkLabel;
        }
        else {
          link = "/edit/" + ent + "/" + id;
          linkLabel = "Edit";
        }
        return '<a href="' + link + '">' + linkLabel + '</a>';
      }
    }
  })
  return coldefs;
}

let agGridApi = {};

const renderError = (msgArray) => {
  return <Box>
    <Alert style={{ height: 'auto', lineHeight: '28px', whiteSpace: 'pre-line' }}
          severity="error"
          variant="filled"
    >{msgArray.join('\r\n ')}
    </Alert></Box>
}

const nonGenericEntities = {
  'StagedJob': {
    title: "Jobs",
    createButtonPath: "/import-file", // createButtonPath absence means no create button will show
    linkPath: "linkToSomewherePath/{}"
  }
};

const EntityList = () => {

  const {entityName} = useParams();
  const plural = pluralize.plural(entityName);
  const entityNamePlural = plural.charAt(0).toLowerCase() + plural.slice(1);

  const schemaDefinition = config.get('api');
  const size = useWindowSize();

  const themeType = useSelector(state => state.theme.themeType);
  const dispatch = useDispatch();
  const entities = useSelector(state => state.form.entities);
  const errors = useSelector(state => state.form.errors);
  const items =  (entities?._embedded) ? entities._embedded[entityNamePlural] : undefined;

  const agGridReady = (agGrid) => {
    agGridApi = Object.create(agGrid.api);
    agGridApi.setRowData(items);
    Object.freeze(agGridApi);
  }

  useEffect(() => {
    dispatch(resetState());
    dispatch(selectRequested(entityNamePlural));
  }, [entityName]); // reset when new or entityName prop changes

  const getEntitySchema = () => {
    return (schemaDefinition[titleCase(entityName)]) ? (schemaDefinition[titleCase(entityName)]) :
        (schemaDefinition[entityName]);
  }

  const getTitle =  () => {
    let thisTitle = nonGenericEntities[entityName]?.title;
    if ((entityName in nonGenericEntities) && thisTitle) {
      return thisTitle;
    }
    else {
      return entityName
    }
  }

  const newEntityButton = () => {
    let createButtonPath = nonGenericEntities[entityName]?.createButtonPath;
    if (!(entityName in nonGenericEntities) || createButtonPath) {
      const to = (createButtonPath) ? createButtonPath : "/form/" + entityNamePlural;
      return <Button title={"Add new " + getTitle()}
                     component={NavLink}
                     to={to}
                     color="secondary"
                     aria-label={"Add " + getTitle()}
                     variant={"contained"}
      >New {titleCase(getTitle())}
      </Button>
    }
  }

  if (Object.keys(schemaDefinition).length === 0) {
    return (renderError(["Error: API not yet loaded"]));
  }
  else {

    if (!getEntitySchema()) {
      return renderError(["ERROR: Entity '" + titleCase(entityName) + "' missing from API Schema"]);
    }

    const colDef = schematoColDef(getEntitySchema(), size, entityName );

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
              <Typography variant="h4">{getTitle()}</Typography>
              {newEntityButton()}
            </Grid>

            <div style={{width: '100%', height: size.height - 170, marginTop: 25}}
                 className={themeType ? "ag-theme-alpine-dark" : "ag-theme-alpine"}>
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
                    resizable: true,// make every column use 'text' filter by default
                    filter: 'agTextColumnFilter',
                    tooltipComponent: 'customTooltip',
                    floatingFilter: true,
                    headerComponentParams: {
                      menuIcon: 'fa-bars'
                    }
                  }}/>
            </div>
            {(!colDef) ? renderError(["Entity '" + entityName + "' can not be found!"]) : ""}
            {(errors.length > 0) ? renderError(errors) : ""}
          </Box>
        </>
    )
  }
}

export default EntityList;



