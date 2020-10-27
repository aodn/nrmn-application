import React from "react";
import {Box, Typography, ButtonGroup, Button, Tooltip, Fab} from "@material-ui/core";
import AddIcon from '@material-ui/icons/Add'

import {useSelector, useDispatch} from "react-redux";
import {useEffect} from 'react';
import { resetState, selectRequested} from "./form-reducer";
import {useParams, NavLink} from "react-router-dom";
import pluralize from 'pluralize';
import {AgGridReact} from "ag-grid-react/lib/agGridReact";
import useWindowSize from "../utils/useWindowSize";
import Alert from "@material-ui/lab/Alert";

import config from "react-global-configuration";

const schematoColDef = (schema, size) => {
  const fields = Object.keys(schema.properties);
  const widthSize = size.width / (fields.length + 1);

  const coldefs = fields.map(field => {
    return {
      field: field,
      width: widthSize
    }

  });
  coldefs.push({
    field: "Edit",
    cellRenderer: function (params) {
      const hrefSplit = params.data._links.self.href.split("/");
      const id = hrefSplit.pop();
      const entities = hrefSplit.pop();
      const link = "/form/" + entities + "/" + id;
      console.log("link:" + link);
      return '<a href="' + link + '">Edit</a>'
    }
  })
  return coldefs;
}

let agGridApi = {};

const renderError = (msg) => {
  return (<Box><Alert  severity="error" variant="filled" >{msg}</Alert></Box>)
}

const ApiEntityList = () => {

  const {entities} = useParams();

  const size = useWindowSize();

  const themeType = useSelector(state => state.theme.themeType);
  const dispatch = useDispatch();

  const items = useSelector(state => state.form.entities);

  const schemaDefinition = config.get('api');
  const singular = pluralize.singular(entities);
  const entity = singular.charAt(0).toUpperCase() + singular.slice(1);

  const agGridReady = (agGrid) => {
    agGridApi = Object.create(agGrid.api);
    agGridApi.setRowData(items);
    Object.freeze(agGridApi);
  }

  useEffect(() => {
    dispatch(resetState());
    if (entities !== undefined) {
      console.log("Requesting :" + entities)
      dispatch(selectRequested(entities))
    }
  }, []);

  if (typeof schemaDefinition === "undefined") {
    return (renderError("Error: API not yet loaded"));
  }
  else {
    const colDef = (schemaDefinition[entity]) ? schematoColDef(schemaDefinition[entity], size) : undefined;
    if (items && agGridApi.setRowData) {
      agGridApi.setRowData(items);
    }

    return (colDef && items) ? (
        <Box>
          <Typography variant="h4">{"List of " + entities}</Typography>
          <Tooltip title="Add" aria-label="Import Excel Data">
            <Fab size="small" href={"/form/" + entities} color="primary" aria-label={"Add " + entity}
                 to><AddIcon></AddIcon></Fab>
          </Tooltip>
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
        </Box>) : renderError("List of entities '" + entities + "' is empty!");
  }
}

export default ApiEntityList;