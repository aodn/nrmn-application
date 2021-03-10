import React from 'react';

import {useDispatch, useSelector} from 'react-redux';
import {useEffect} from 'react';
import Autocomplete from '@material-ui/lab/Autocomplete';
import {Typography, CircularProgress, TextField} from '@material-ui/core';
import {selectedItemsRequested, setField} from '../middleware/entities';
import {markupProjectionQuery} from '../../utils/helpers';
import {PropTypes} from 'prop-types';

const NestedApiField = (props) => {
  let editItemValues = useSelector((state) => state.form.formData);
  let formOptions = useSelector((state) => state.form.formOptions);
  const dispatch = useDispatch();
  const {entity, key, valueKey, route, entityList, values} = props.uiSchema;

  let items = values ?? formOptions[entityList] ?? [];
  const itemsList =
    values ??
    items.map((i) => {
      return {id: i[key], label: i[valueKey]};
    });
  const pluralEntity = entity;

  useEffect(() => {
    if (!values) {
      let urls = [markupProjectionQuery(pluralEntity)];
      if (editItemValues._links) {
        urls.push(markupProjectionQuery(editItemValues._links[entity].href));
      }
      dispatch(selectedItemsRequested([route]));
    }
  }, []);
  const id = editItemValues[key];
  const value = itemsList.find((i) => i.id === id);
  return itemsList.length > 0 ? (
    <>
      <Typography variant="subtitle2">{props.schema.title}</Typography>
      <Autocomplete
        id={'select-auto-' + key}
        options={itemsList}
        multiple={props.multiple || false}
        getOptionLabel={(option) => {
          return option.label;
        }}
        defaultValue={value}
        filterSelectedOptions
        onChange={(_, value) => dispatch(setField({newValue: value.id, entity: key}))}
        renderInput={(params) => <TextField {...params} variant="outlined" />}
      />
    </>
  ) : (
    <>
      <Typography variant="subtitle2">{props.schema.title}</Typography>
      <CircularProgress size={30} />
    </>
  );
};

NestedApiField.propTypes = {
  name: PropTypes.string,
  multiple: PropTypes.bool,
  schema: PropTypes.object,
  uiSchema: PropTypes.object
};

export default NestedApiField;
