import React, {useEffect} from 'react';
import {useDispatch, useSelector} from 'react-redux';
import {CircularProgress, TextField, Typography} from '@material-ui/core';
import Autocomplete from '@material-ui/lab/Autocomplete';
import {selectedItemsRequested, setField} from '../middleware/entities';
import {PropTypes} from 'prop-types';

const AutoCompleteInput = ({schema, uiSchema, name}) => {
  const dispatch = useDispatch();

  const value = useSelector((state) => state.form.data[name]);
  const options = useSelector((state) => state.form.options[uiSchema.route]);

  useEffect(() => {
    dispatch(selectedItemsRequested([uiSchema.route]));
  }, []);

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
        renderInput={(params) => <TextField {...params} color="primary" variant="outlined" />}
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
