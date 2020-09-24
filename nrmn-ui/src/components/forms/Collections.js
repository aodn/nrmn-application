import React from "react";
import { Box, Typography } from "@material-ui/core";
import { useSelector, useDispatch } from "react-redux";
import { useEffect } from 'react';
import { definitionRequested, selectRequested } from "./redux-form";
import { useParams } from "react-router-dom";
import pluralize from 'pluralize';
import { AgGridReact } from "ag-grid-react/lib/agGridReact";
import useWindowSize from "../utils/useWindowSize";

const schematoColDef = (schema, size) => {
    const fields = Object.keys(schema.properties)
    const widthSize = size.width / fields.length

    return fields.map(field => {
        return {
            field: field,
            width: widthSize
        }
    })
}

const Colection = () => {
    const { entity } = useParams();
    const size = useWindowSize();
    const themeType = useSelector(state =>  state.theme.themeType);

    const items = useSelector(state => state.form.entities);
    const definition = useSelector(state => state.form.definition);
    const dispatch = useDispatch();

    const pluralEntity = pluralize(entity);
    const entities = pluralEntity.charAt(0).toLowerCase() + pluralEntity.slice(1);

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
            <Typography variant="h2" >{"List of " + entities}</Typography>
            <div style={{ height: size.height - 200, width: '100%', marginTop: 25 }} className={themeType ? "ag-theme-alpine-dark" : "ag-theme-alpine"} >

                <AgGridReact

                    columnDefs={colDef}
                    rowData={items}
                    rowSelection="multiple"
                    animateRows={true}
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