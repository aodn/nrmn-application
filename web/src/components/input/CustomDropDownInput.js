import React from 'react';
import Autocomplete from '@mui/material/Autocomplete';
import {Typography, CircularProgress, TextField} from '@mui/material';
import {PropTypes} from 'prop-types';

const CustomDropDownInput = ({label, field, errors, optional, options, formData, onChange}) => {
  const error = errors?.find((f) => f.property === field);

  if (!options) {
    return (
      <>
        <Typography variant="subtitle2">{label}</Typography>
        <CircularProgress size={30} />
      </>
    );
  }

  return (
    <>
      <Typography variant="subtitle2">{label}</Typography>
      <Autocomplete
        disableClearable={optional ? !optional : true}
        filterSelectedOptions
        options={options}
        getOptionLabel={(o) => o.label}
        value={options.find((o) => o.id === formData) ?? null}
        onChange={(_, o) => onChange(o?.id)}
        renderInput={(params) => (
          <TextField {...params} size="small" variant="outlined" error={error ? true : false} helperText={error?.message} />
        )}
      />
    </>
  );
};

CustomDropDownInput.propTypes = {
  label: PropTypes.string,
  field: PropTypes.string,
  errors: PropTypes.array,
  uiSchema: PropTypes.object,
  formData: PropTypes.any,
  onChange: PropTypes.func,
  options: PropTypes.array,
  optional: PropTypes.bool
};

export default CustomDropDownInput;
