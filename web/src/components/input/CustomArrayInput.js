import React from 'react';
import {TextField, Typography} from '@mui/material';
import {PropTypes} from 'prop-types';

const CustomArrayInput = ({label, values, onChange}) => {
  return (
    <>
      <Typography variant="subtitle2">{label}</Typography>
      <TextField
        fullWidth
        color="primary"
        size="small"
        value={values?.join(',')}
        onChange={(event) => onChange(event.target.value.split(','))}
      />
    </>
  );
};

CustomArrayInput.propTypes = {
  label: PropTypes.string,
  values: PropTypes.array,
  onChange: PropTypes.func
};

export default CustomArrayInput;
