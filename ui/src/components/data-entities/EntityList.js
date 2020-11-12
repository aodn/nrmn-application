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

const renderError = (msgArray) => {
  return <Box>
    <Alert style={{ height: 'auto', lineHeight: '28px', whiteSpace: 'pre-line' }}
          severity="error"
          variant="filled"
    >{msgArray.join('\r\n ')}
    </Alert></Box>
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

  if (Object.keys(schemaDefinition).length === 0) {
    return (renderError(["Error: API not yet loaded"]));
  }
  else {

    if (!getEntitySchema()) {
      return renderError(["ERROR: Entity '" + entityName + "' missing from API Schema"]);
    }

    const colDef = schematoColDef(getEntitySchema(), size);

    if (items !== undefined && agGridApi.setRowData) {
      agGridApi.setRowData(items);
    }

    return (
        <>
        <Box>
          <Grid
              container
              direction="row"
              justify="space-between"
              alignItems="center"
          >
            <Typography variant="h4">{titleCase(entityNamePlural)}</Typography>
            <Button title={"Add new " + titleCase(entityName)}
                    component={NavLink}
                    to={"/form/" + entityNamePlural}
                    color="secondary"
                    aria-label={"Add " + entityName}
                    variant={"contained"}
            >New {titleCase(entityName)}

            </Button>
          </Grid>

          <div style={{ width: '100%', marginTop: 25}}
               className={themeType ? "ag-theme-alpine-dark" : "ag-theme-alpine"}>
            <AgGridReact
                columnDefs={colDef}
                rowSelection="multiple"
                domLayout='autoHeight'
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
          { (!colDef) ? renderError(["Entity '" + entityName + "' can not be found!"]) : "" }
          { (errors.length > 0) ? renderError(errors) : "" }
        </Box>

      </>
    )

  }
}

export default EntityList;