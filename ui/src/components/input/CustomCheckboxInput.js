import React from 'react';
import {Checkbox, FormControlLabel, Typography} from '@material-ui/core';
import {PropTypes} from 'prop-types';

const CustomCheckboxInput = ({label, formData, onChange}) => {
  return (
    <FormControlLabel
      style={{marginTop: '25px', marginLeft: '5px'}}
      control={<Checkbox checked={formData} onChange={(event) => onChange(event.target.checked)} />}
      label={<Typography variant="subtitle2">{label}</Typography>}
    />
  );
};

CustomCheckboxInput.propTypes = {
  label: PropTypes.string,
  formData: PropTypes.boolean,
  onChange: PropTypes.func
};

export default CustomCheckboxInput;
