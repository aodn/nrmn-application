import React from 'react';
import {Box, Grid, TextField, Typography} from '@material-ui/core';
import {PropTypes} from 'prop-types';

const CustomTextInput = ({field, type, readOnlyInput, readOnlyModify, formData, errors, onChange, onBlur, label}) => {
  const value = typeof formData === 'number' || typeof formData === 'boolean' ? formData.toString() : formData ?? '';
  const error = errors?.find((f) => f.property === field);

  const splitField = (value) => {
    const displayValue = value.indexOf('\n') > -1 ? 'block' : 'inherit';
    if (value.length === 0) {
      return <Typography>---</Typography>;
    } else {
      return value.split('\n').map((val) => (
        <Typography key={val} variant="" display={displayValue}>
          {val}
        </Typography>
      ));
    }
  };

  const roField = (
    <>
      <Grid container alignItems="flex-start" direction="row" spacing={2}>
        <Grid item xs={5} style={{textAlign: 'right'}}>
          <Typography variant="subtitle2">{label}:</Typography>
        </Grid>
        <Grid item xs={7}>
          <Box pt="1px">{splitField(value)}</Box>
        </Grid>
      </Grid>
    </>
  );

  const rwField = (
    <>
      <Typography variant="subtitle2">{label}</Typography>
      <TextField
        fullWidth
        color="primary"
        type={type ?? 'string'}
        inputProps={{
          readOnly: readOnlyModify,
          style: readOnlyModify ? {color: '#545454'} : null
        }}
        error={error}
        disabled={readOnlyModify}
        helperText={error?.message}
        value={value}
        onChange={(event) => onChange(event.target.value)}
        onBlur={(e) => onBlur && onBlur(e)}
      />
    </>
  );

  return readOnlyInput ? roField : rwField;
};

CustomTextInput.propTypes = {
  field: PropTypes.string,
  type: PropTypes.string,
  formData: PropTypes.string,
  errors: PropTypes.array,
  onChange: PropTypes.func,
  label: PropTypes.string,
  onBlur: PropTypes.func,
  readOnlyModify: PropTypes.bool,
  readOnlyInput: PropTypes.bool
};

export default CustomTextInput;
