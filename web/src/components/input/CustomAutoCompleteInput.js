import React, {useReducer} from 'react';
import {CircularProgress, TextField, Typography} from '@mui/material';
import Autocomplete from '@mui/material/Autocomplete';
import {PropTypes} from 'prop-types';
import { makeStyles } from '@mui/styles';

const CustomAutoCompleteInput = ({label, field, options, onChange, formData, errors}) => {

  const ERROR_TYPE = {NORMAL: 0, WARNING: 1, ERROR: 2};

  const useStyles = value =>
    makeStyles(theme => ({
      root: {
        // Text in the text box color
        '& .MuiOutlinedInput-root.Mui-error': {
          color: acquireValidationColor(value.type),
          // The border color on alert
          '& fieldset': {
            borderColor: acquireValidationColor(value.type),
          },
        },
        // Helper text color
        '& .MuiFormHelperText-root.Mui-error' :{
          color: acquireValidationColor(value.type)
        },
      },
    }));

  const acquireValidationColor = state => {
    switch (state) {
      case ERROR_TYPE.WARNING:
        return 'DarkOrange';
      case ERROR_TYPE.ERROR:
        return 'red';
      default:
        return 'black';
    }
  };

  const errorReducer = (state, action) => {
    state.display = action.internalError !== undefined? action.internalError : state.display;

    if(state.display) {
      state.message = state.internalErrorMessage;
    }
    else {
      state.message = '';
    }
    return state;
  };

  const [validate, setValidate] = useReducer(errorReducer, {
    internalErrorMessage: 'New "' + label + '" will be created',
    display: false,
    type: ERROR_TYPE.WARNING,
    message: ''
  });

  const onInputChange = (event, value, reason) => {
    if(options.find(p => p.toLowerCase() === value.toLowerCase()) === undefined && value !== '') {
      setValidate({ internalError: true});
    }
    else {
      setValidate({ internalError: false});
    }
  };

  const classes = useStyles(validate)();

  return options ? (
    <>
      <Typography variant="subtitle2">{label}</Typography>
      <Autocomplete
        className={classes.root}
        options={options}
        freeSolo
        value={formData}
        onBlur={(e) => onChange(e.target.value)}
        onInputChange={(e, v, r) => onInputChange(e,v,r)}
        renderInput={(params) => (
          <TextField
            {...params}
            size="small"
            color="primary"
            variant="outlined"
            error={validate.display}
            helperText={validate.message}/>
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
