import React from 'react';
import {Box, Grid, TextField, Typography} from '@material-ui/core';
import {PropTypes} from 'prop-types';

const CustomArrayInput = ({schema, uiSchema, formData, error, onChange}) => {
  const formValue = formData ? formData.join(',') : '';
  const value = typeof formValue === 'number' || typeof formValue === 'boolean' ? formValue.toString() : formValue;
  const readOnlyInput = uiSchema['ui:field'] === 'readonly';
  const readOnlyModify = uiSchema['ui:readonly'] === true;

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
          <Typography variant="subtitle2">{schema.title}:</Typography>
        </Grid>
        <Grid item xs={7}>
          <Box pt="1px">{splitField(value)}</Box>
        </Grid>
      </Grid>
    </>
  );

  const rwField = (
    <>
      <Typography variant="subtitle2">{schema.title}</Typography>
      <TextField
        color="primary"
        inputProps={{
          readOnly: readOnlyModify,
          style: readOnlyModify ? {color: '#545454'} : null
        }}
        error={error}
        disabled={readOnlyModify}
        helperText={error?.message}
        value={value}
        onChange={(event) => onChange(event.target.value.split(','))}
      />
    </>
  );

  return readOnlyInput ? roField : rwField;
};

CustomArrayInput.propTypes = {
  schema: PropTypes.object,
  uiSchema: PropTypes.object,
  formData: PropTypes.string,
  errors: PropTypes.array,
  onChange: PropTypes.func
};

export default CustomArrayInput;
