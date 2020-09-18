import React from "react";

import Select from '@material-ui/core/Select';
import MenuItem from '@material-ui/core/MenuItem';
import InputLabel from '@material-ui/core/InputLabel';
import {  useDispatch, useSelector } from "react-redux";
import {  useEffect } from 'react';
import { selectRequested } from "../redux-form";

 const SelectWidget = (props) => {
        const {options} = props

    const items = useSelector(state => state.form.entities)
    const dispatch = useDispatch();
       useEffect(() => {
         dispatch(selectRequested(options.entity))
   },[])
   console.log("items:", items);
     return (items)?    (
     <>
        <InputLabel id={"select-" + options.id}>{options.id}</InputLabel>
        <Select
          labelId={"select-" + options.id}
          id={options.id}
          onChange={(event) => props.onChange(event.target.value)}
        >
           {items.map(it => (<MenuItem value={it.name}>{it.name}</MenuItem>))}
        </Select>
        </>): (<></>)
}

export default SelectWidget;