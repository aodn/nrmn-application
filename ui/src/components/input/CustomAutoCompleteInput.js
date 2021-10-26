import React, {useEffect, useState} from 'react';
import {CircularProgress, TextField, Typography} from '@material-ui/core';
import Autocomplete from '@material-ui/lab/Autocomplete';
import {PropTypes} from 'prop-types';
import {getResult} from '../../axios/api';

const CustomAutoCompleteInput = ({schema, uiSchema, onChange, formData, error}) => {
  const [options, setOptions] = useState([]);

  useEffect(() => {
    getResult(uiSchema.route).then((res) => setOptions(res.data._embedded[uiSchema.route].map((d) => d.name)));
  }, [uiSchema.route]);

  return options ? (
    <>
      <Typography variant="subtitle2">{schema.title}</Typography>
      <Autocomplete
        options={options}
        freeSolo
        value={formData}
        onBlur={(e) => onChange(e.target.value)}
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

CustomAutoCompleteInput.propTypes = {
  onChange: PropTypes.func,
  error: PropTypes.object,
  formData: PropTypes.string,
  uiSchema: PropTypes.object,
  schema: PropTypes.object
};

export default CustomAutoCompleteInput;
