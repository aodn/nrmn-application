import React from "react";
import {Box, Typography, Button,} from "@material-ui/core";

import {useSelector, useDispatch} from "react-redux";
import {useEffect} from 'react';
import { resetState, selectRequested} from "./form-reducer";
import {useParams, NavLink} from "react-router-dom";
import pluralize from 'pluralize';
import {AgGridReact} from "ag-grid-react/lib/agGridReact";
import useWindowSize from "../utils/useWindowSize";
import Alert from "@material-ui/lab/Alert";
import config from "react-global-configuration";
import {titleCase} from "title-case";
import Grid from "@material-ui/core/Grid";
import {LoadingSpinner} from "../layout/loadingSpinner";
import {LoadingBanner} from "../layout/loadingBanner";

const schematoColDef = (schema, size) => {

  const fields = Object.keys(schema.properties);
  const widthSize = size.width / (fields.length + 1 );

  const coldefs = fields.map(field => {
    return {
      field: field,
      width: widthSize
    }

  });
  coldefs.push({
    field: "Edit",
    cellRenderer: function (params) {
      if (params.data._links) {
        const hrefSplit = params.data._links.self.href.split("/");
        const id = hrefSplit.pop();
        const ent = hrefSplit.pop();
        const link = "/form/" + ent + "/" + id;
        return '<a href="' + link + '">Edit</a>';
      }
    }
  })
  return coldefs;
}

let agGridApi = {};

const renderError = (msg) => {
  return <Box><Alert  severity="error" variant="filled" >{msg}</Alert></Box>
}

const EntityList = () => {

  const {entityName} = useParams();
  const plural = pluralize.plural(entityName);
  const entityNamePlural = plural.charAt(0).toLowerCase() + plural.slice(1);

  const schemaDefinition = config.get('api');
  const size = useWindowSize();

  const themeType = useSelector(state => state.theme.themeType);
  const dispatch = useDispatch();
  const entities = useSelector(state => state.form.entities);
  const items =  (entities._embedded) ? entities._embedded[entityNamePlural] : undefined;
  // TODO handle paging from entities.

  const agGridReady = (agGrid) => {
    agGridApi = Object.create(agGrid.api);
    agGridApi.setRowData(items);
    Object.freeze(agGridApi);
  }

  useEffect(() => {
    dispatch(resetState());
    console.log("entityName is: " + entityName);
    console.log("Requesting data for: " + entityNamePlural);
    dispatch(selectRequested(entityNamePlural));
  }, []);

  if (typeof schemaDefinition === "undefined") {
    return (renderError("Error: API not yet loaded"));
  }
  else {
    const colDef = (schemaDefinition[entityName]) ?
        schematoColDef(schemaDefinition[entityName], size) : undefined;

    if (items !== undefined && agGridApi.setRowData) {

      agGridApi.setRowData(items);
    }

    return (colDef && items !== undefined) ? (
        <Box>
          <Grid
              container
              direction="row"
              justify="space-between"
              alignItems="center"
          >
          <Typography variant="h4">{titleCase(entityName)}</Typography>
          <Button title={"Add new " + titleCase(entityName)}
                  href={"/form/" + entityNamePlural}
                  color="secondary"
                  aria-label={"Add " + entityName}
                  variant={"contained"}
          >New {titleCase(entityName)}

          </Button>
          </Grid>

          <div style={{height: size.height - 200, width: '100%', marginTop: 25}}
               className={themeType ? "ag-theme-alpine-dark" : "ag-theme-alpine"}>
            <AgGridReact

                columnDefs={colDef}
                rowSelection="multiple"
                animateRows={true}
                onGridReady={agGridReady}

                defaultColDef={{
                  filter: true,
                  sortable: true,
                  resizable: true,
                  headerComponentParams: {
                    menuIcon: 'fa-bars'
                  }
                }}/>
          </div>
        </Box>) :

        (colDef) ? <LoadingBanner variant={"h4"} msg={"Listing " + titleCase(entityNamePlural)} />:
            renderError("Entity '" + entityName + "' can not be found!");

  }
}

export default EntityList;