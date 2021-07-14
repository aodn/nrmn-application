import React, {useEffect} from 'react';
import {useDispatch, useSelector} from 'react-redux';
import {CircularProgress, TextField, Typography} from '@material-ui/core';
import Autocomplete from '@material-ui/lab/Autocomplete';
import {selectedItemsRequested, selectedListItemsRequested, setField} from '../middleware/entities';
import {PropTypes} from 'prop-types';

const AutoCompleteInput = ({schema, uiSchema, name}) => {
  const dispatch = useDispatch();

  const value = useSelector((state) => state.form.data[name]);
  const errors = useSelector((state) => state.form.errors);
  // Custom endpoints will use an entity parameter, generics will use route
  const options = useSelector((state) => state.form.options[uiSchema.entity || uiSchema.route]);

  useEffect(() => {
    if(uiSchema.listOnly){  // If payload is coming from a custom endpoint
      // Pass in key to select the appropriate list from the combination endpoint
      dispatch(selectedListItemsRequested({'route': [uiSchema.route], 'key': uiSchema.entity}));
    } else {
      dispatch(selectedItemsRequested([uiSchema.route]));
    }
  }, [dispatch, uiSchema.route, uiSchema.listOnly, uiSchema.entity]);

  const error = errors.find((e) => e.property === name);
  return options ? (
    <>
      <Typography variant="subtitle2">{schema.title}</Typography>
      <Autocomplete
        options={options}
        freeSolo
        defaultValue={value}
        onBlur={(e) => {
          dispatch(setField({newValue: e.target.value, entity: name}));
        }}
        renderInput={(params) => <TextField {...params} color="primary" variant="outlined" error={error} helperText={error?.message} />}
      />
    </>
  ) : (
    <>
      <Typography variant="subtitle2">{schema.title}</Typography>
      <CircularProgress size={30} />
    </>
  );
};

AutoCompleteInput.propTypes = {
  name: PropTypes.string,
  uiSchema: PropTypes.object,
  schema: PropTypes.object
};

export default AutoCompleteInput;
