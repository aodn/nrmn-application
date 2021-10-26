import React from 'react';
import {CircularProgress, TextField, Typography} from '@material-ui/core';
import Autocomplete from '@material-ui/lab/Autocomplete';
import {PropTypes} from 'prop-types';

const CustomAutoCompleteInput = ({label, options, onChange, formData, error}) => {
  return options ? (
    <>
      <Typography variant="subtitle2">{label}</Typography>
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
      <Typography variant="subtitle2">{label}</Typography>
      <CircularProgress size={30} />
    </>
  );
};

CustomAutoCompleteInput.propTypes = {
  label: PropTypes.string,
  onChange: PropTypes.func,
  error: PropTypes.object,
  formData: PropTypes.string,
  options: PropTypes.array
};

export default CustomAutoCompleteInput;
