import React from "react";
import { Box, Typography, ButtonGroup, Button, Tooltip, Fab } from "@material-ui/core";
import AddIcon from '@material-ui/icons/Add'

import { useSelector, useDispatch } from "react-redux";
import { useEffect } from 'react';
import { definitionRequested, resetState, selectRequested } from "./form-reducer";
import { useParams, NavLink } from "react-router-dom";
import pluralize from 'pluralize';
import { AgGridReact } from "ag-grid-react/lib/agGridReact";
import useWindowSize from "../utils/useWindowSize";

const schematoColDef = (schema, size) => {
    const fields = Object.keys(schema.properties)
    const widthSize = size.width / (fields.length + 1)

    const coldefs = fields.map(field => {
        return {
            field: field,
            width: widthSize
        }
    });
    coldefs.push({
        field: "Edit",
        cellRenderer: function (params) {
            const hrefSplit = params.data._links.self.href.split("/")
            const id = hrefSplit.pop();
            const entities = hrefSplit.pop();
            const link = "/form/" + entities + "/" + id
            console.log("link:" + link)
            return '<a href="' + link + '">Edit</a>'
        }


    })
    return coldefs;
}
let agGridApi = {};

const Collection = () => {
    const { entities } = useParams();
    const size = useWindowSize();
    const themeType = useSelector(state => state.theme.themeType);

    const items = useSelector(state => state.form.entities);
    const definition = useSelector(state => state.form.definition);
    const dispatch = useDispatch();
    const singular = pluralize.singular(entities);
    const entity = singular.charAt(0).toUpperCase() + singular.slice(1);

    const agGridReady = (agGrid) => {
        console.log("ready")
        agGridApi = Object.create(agGrid.api);
        agGridApi.setRowData(items);
        Object.freeze(agGridApi);

    }

    useEffect(() => {
        dispatch(resetState());
        console.log("resteState");
        console.log("req: definition")
        if (Object.keys(definition).length === 0)
            dispatch(definitionRequested());
        console.log("Req:" + entities)
        if (entities != undefined)
            dispatch(selectRequested(entities))
    }, []);

    console.log(definition);
    if (Object.keys(definition).length === 0)
        return (<></>);

    const colDef = schematoColDef(definition[entity], size);
    console.log("def:", colDef);
    console.log("items:", items);
    console.log("api grid:", agGridApi);
    if (items && agGridApi.setRowData)
        agGridApi.setRowData(items);
    return (colDef && items) ? (
        <Box>
            <Typography variant="h3" >{"List of " + entities}</Typography>
            <Tooltip title="Add" aria-label="Import Excel Data">
                <Fab size="small" href={"/form/" + entities} color="primary" aria-label={"Add " + entity} to><AddIcon></AddIcon></Fab>
            </Tooltip>
            <div style={{ height: size.height - 200, width: '100%', marginTop: 25 }} className={themeType ? "ag-theme-alpine-dark" : "ag-theme-alpine"} >

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
                    }} />
            </div>
        </Box>) : (<Box>
            <Typography variant="h2" >{"List of " + entities + " is empty!"}</Typography>
        </Box>)
}

export default Collection;