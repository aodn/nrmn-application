import React from "react";

import { useDispatch, useSelector } from "react-redux";
import { useEffect } from 'react';
import Autocomplete from '@material-ui/lab/Autocomplete';
import TextField from '@material-ui/core/TextField';
import pluralize from 'pluralize';
import {Typography} from "@material-ui/core";
import {selectedItemsRequested, setNestedField} from "../middleware/entities";
import {markupProjectionQuery} from "../../utils/helpers";


const NestedApiFieldDetails = (props) => {
  let editItemValues = useSelector(state => state.form.editItem);
  let form = useSelector(state => state.form);
  const dispatch = useDispatch();

  const entity = props.name ;
  const pluralEntity = pluralize(entity);

  let itemsList = (editItemValues[pluralEntity]) ? editItemValues[pluralEntity][pluralEntity] : [];
  let selectedItems = (editItemValues[entity + "Selected"]) ? [editItemValues[entity + "Selected"]].filter(Boolean) : [];

  useEffect(() => {
    if (editItemValues._links) {
      let urls = [markupProjectionQuery(pluralEntity)];
      urls.push(markupProjectionQuery(editItemValues._links[entity].href));
      dispatch(selectedItemsRequested(urls));
    }
  }, [editItemValues]);

  return (itemsList.length > 0) ? (<> <span><b>{props.name}:</b> {selectedItems[0].label}</span></>) :
      (<><span><b>{props.name}:</b>Loading... </span></>)


};

export default NestedApiFieldDetails;

