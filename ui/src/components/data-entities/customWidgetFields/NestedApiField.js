import React from "react";

import { useDispatch, useSelector } from "react-redux";
import { useEffect } from 'react';
import Autocomplete from '@material-ui/lab/Autocomplete';
import TextField from '@material-ui/core/TextField';
import pluralize from 'pluralize';
import {Typography} from "@material-ui/core";
import {selectedItemsRequested, setNestedField} from "../middleware/entities";


const NestedApiField = (props) => {

  let editItemValues = useSelector(state => state.form.editItem);
  let form = useSelector(state => state.form);
  const dispatch = useDispatch();

  console.log(form);

  const entity = props.name ;
  const pluralEntity = pluralize(entity);
  //const entities =  pluralEntity.charAt(0).toLowerCase() + pluralEntity.slice(1);

// todo all entities human readable label ?
  let itemsList = (editItemValues[pluralEntity]) ?editItemValues[pluralEntity][pluralEntity] : [];
  if (itemsList.length > 0) {
    itemsList = itemsList.map((item) => ({
      ...item,
      label: item[entity + "Name"]
    }));
  }

  let selectedItems = (editItemValues[entity]) ? [editItemValues[entity]].filter(Boolean) : [];
  if (selectedItems.length > 0) {
    selectedItems = selectedItems.map( (item) => ({
      ...item,
      label: item[entity + "Name"]
    }));
  }

  useEffect(() => {
    let urls = [pluralEntity];
    if (editItemValues._links) {
      urls.push(editItemValues._links[entity].href);
    }
    dispatch(selectedItemsRequested(urls));

  }, []);

  return (itemsList.length > 0) ? (
    <>
       <Typography variant={"h5"}>{entity}</Typography>
       <Autocomplete
        id={"select-auto-"+ entity}
        options={itemsList}
        multiple={props.multiple}
        getOptionLabel={(option) => option.label}
        defaultValue={(props.multiple) ? selectedItems : selectedItems[0]}
        filterSelectedOptions
        onChange={(event, newValues) => dispatch(setNestedField({newValues, entity}))}
        renderInput={(params) => <TextField {...params} label={"Select " + entity} variant="outlined" />}
      />
    </>
  ) : (<>
    <Typography variant={"h5"}>{pluralEntity}</Typography>
    <div>add item</div></>)

};

export default NestedApiField;

