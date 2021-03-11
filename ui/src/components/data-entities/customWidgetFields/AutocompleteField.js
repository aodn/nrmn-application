import React, {useEffect} from 'react';
import {useDispatch, useSelector} from 'react-redux';
import {CircularProgress, TextField, Typography} from '@material-ui/core';
import Autocomplete from '@material-ui/lab/Autocomplete';
import {selectedItemsRequested, setField} from '../middleware/entities';
import {PropTypes} from 'prop-types';

const AutocompleteField = (props) => {
  let formData = useSelector((state) => state.form.formData);
  let formOptions = useSelector((state) => state.form.formOptions);
  const dispatch = useDispatch();

  const entity = props.name;

  let route = props.uiSchema.route;
  let itemsList = formOptions[route] ?? [];
  let selectedItem = formData[entity];

  useEffect(() => {
    dispatch(selectedItemsRequested([route]));
  }, []);

  return itemsList.length > 0 ? (
    <>
      <Typography variant="subtitle2">{props.schema.title}</Typography>
      <Autocomplete
        options={itemsList}
        freeSolo
        defaultValue={selectedItem}
        onChange={(_, newValue) => {
          dispatch(setField({newValue: newValue, entity: entity}));
        }}
        renderInput={(params) => <TextField {...params} color="primary" variant="outlined" />}
      />
    </>
  ) : (
    <>
      <Typography variant="subtitle2">{props.schema.title}</Typography>
      <CircularProgress size={30} />
    </>
  );
};

AutocompleteField.propTypes = {
  name: PropTypes.string,
  multiple: PropTypes.bool,
  uiSchema: PropTypes.object,
  schema: PropTypes.object
};

export default AutocompleteField;
