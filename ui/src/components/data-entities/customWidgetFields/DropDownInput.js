import React from 'react';

import {useDispatch, useSelector} from 'react-redux';
import {useEffect} from 'react';
import Autocomplete from '@material-ui/lab/Autocomplete';
import {Typography, CircularProgress, TextField} from '@material-ui/core';
import {selectedItemsRequested, setField} from '../middleware/entities';
import {PropTypes} from 'prop-types';

const DropDownInput = (props) => {
  const {entity, idKey, valueKey, route, entityList, values, optional} = props.uiSchema;
  const name = idKey ?? entity;

  const formValue = useSelector((state) => state.form.data[name]);
  const formOptions = useSelector((state) => state.form.options[entityList])?.map((i) => {
    return {id: i[idKey], label: i[valueKey]};
  });

  // use either the supplied values or options retreived from an endpoint
  const options = values ?? formOptions;
  const errors = useSelector((state) => state.form.errors);
  const dispatch = useDispatch();

  useEffect(() => {
    if (route) dispatch(selectedItemsRequested([route]));
  }, []);

  const error = errors.find((e) => e.property === name);
  return options ? (
    <>
      <Typography variant="subtitle2">{props.schema.title}</Typography>
      <Autocomplete
        disableClearable={optional ? !optional : true}
        filterSelectedOptions
        options={options}
        getOptionLabel={(o) => o.label}
        getOptionSelected={(o, v) => (v ? o.id === v.id : null)}
        value={options.find((o) => o.id === formValue) ?? null}
        onChange={(_, o) => dispatch(setField({newValue: o?.id, entity: name}))}
        renderInput={(params) => <TextField {...params} variant="outlined" error={error} helperText={error?.message} />}
      />
    </>
  ) : (
    <>
      <Typography variant="subtitle2">{props.schema.title}</Typography>
      <CircularProgress size={30} />
    </>
  );
};

DropDownInput.propTypes = {
  name: PropTypes.string,
  multiple: PropTypes.bool,
  schema: PropTypes.object,
  uiSchema: PropTypes.object
};

export default DropDownInput;
