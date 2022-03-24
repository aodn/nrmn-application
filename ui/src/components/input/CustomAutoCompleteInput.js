import React from 'react';
import {CircularProgress, TextField, Typography} from '@mui/material';
import Autocomplete from '@mui/material/Autocomplete';
import {PropTypes} from 'prop-types';

const CustomAutoCompleteInput = ({label, field, options, onChange, formData, errors}) => {
  const error = errors?.find((f) => f.property === field);

  return options ? (
    <>
      <Typography variant="subtitle2">{label}</Typography>
      <Autocomplete
        options={options}
        freeSolo
        value={formData}
        onBlur={(e) => onChange(e.target.value)}
        renderInput={(params) => (
          <TextField {...params} size="small" color="primary" variant="outlined" error={error} helperText={error?.message} />
        )}
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
  field: PropTypes.string,
  onChange: PropTypes.func,
  errors: PropTypes.array,
  formData: PropTypes.string,
  options: PropTypes.array
};

export default CustomAutoCompleteInput;
