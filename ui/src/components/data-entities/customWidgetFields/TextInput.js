import React from 'react';
import {useDispatch, useSelector} from 'react-redux';

import {TextField, Typography} from '@material-ui/core';
import {PropTypes} from 'prop-types';
import {setField} from '../middleware/entities';

const TextInput = ({name, schema, uiSchema}) => {
  const dispatch = useDispatch();
  const formData = useSelector((state) => state.form.formData);
  const errors = useSelector((state) => state.form.errors);
  const readOnly = uiSchema['ui:field'] === 'readonly';

  const fieldError = errors.find((e) => e.property === name);
  return (
    <>
      <Typography variant="subtitle2">{schema.title}</Typography>
      {readOnly ? (
        <Typography>{formData[name]}</Typography>
      ) : (
        <TextField
          color="primary"
          inputProps={{
            readOnly: readOnly
          }}
          error={fieldError}
          helperText={fieldError?.message}
          value={formData[name] ?? ''}
          onChange={(event) => {
            const newValue = event.target.value;
            dispatch(setField({newValue, entity: name}));
          }}
        />
      )}
    </>
  );
};

TextInput.propTypes = {
  name: PropTypes.string,
  title: PropTypes.string,
  schema: PropTypes.object,
  uiSchema: PropTypes.object,
  formData: PropTypes.string
};

export default TextInput;
