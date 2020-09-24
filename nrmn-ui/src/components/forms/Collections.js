import React from "react";
import { Box, Typography, ButtonGroup, Button } from "@material-ui/core";
import { useSelector, useDispatch } from "react-redux";
import { useEffect } from 'react';
import { definitionRequested, selectRequested } from "./redux-form";
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
            console.log(params.data._links.self.href.split)
            const hrefSplit = params.data._links.self.href.split("/")
            const id = hrefSplit.pop();
            const entities = hrefSplit.pop();
            const link = "/form/" + entities + "/" + id
            return '<a href="' + link + '" target="_blank" rel="noopener">Edit</a>'
        }


    })
    return coldefs;
}

const Colection = () => {
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
        console.log(agGrid);

    }

    useEffect(() => {
        if (Object.keys(definition).length === 0 || definition[entity] == undefined)
            dispatch(definitionRequested());
        if (entities != undefined)
            dispatch(selectRequested(entities))
    }, []);

    if (Object.keys(definition).length === 0)
        return (<></>);

    const colDef = schematoColDef(definition[entity], size);
    console.log("def:", colDef);
    console.log("items:", items);

    return (colDef && items) ? (
        <Box>
            <Typography variant="h3" >{"List of " + entities}</Typography>
            <ButtonGroup color="primary" aria-label="outlined primary button group">
                <Button component={NavLink} to={"/form/"+ entities} >Add {entity}</Button>
            </ButtonGroup>
            <div style={{ height: size.height - 200, width: '100%', marginTop: 25 }} className={themeType ? "ag-theme-alpine-dark" : "ag-theme-alpine"} >

                <AgGridReact

                    columnDefs={colDef}
                    rowData={items}
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

export default Colection;