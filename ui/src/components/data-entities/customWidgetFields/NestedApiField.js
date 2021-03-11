import React from 'react';

import {useDispatch, useSelector} from 'react-redux';
import {useEffect} from 'react';
import Autocomplete from '@material-ui/lab/Autocomplete';
import {Typography, CircularProgress, TextField} from '@material-ui/core';
import {selectedItemsRequested, setField} from '../middleware/entities';
import {markupProjectionQuery} from '../../utils/helpers';
import {PropTypes} from 'prop-types';

const NestedApiField = (props) => {
  let formData = useSelector((state) => state.form.formData);
  let formOptions = useSelector((state) => state.form.formOptions);
  const dispatch = useDispatch();
  const {entity, idKey, valueKey, route, entityList, values} = props.uiSchema;

  let items = values ?? formOptions[entityList] ?? [];

  let itemsList = [];
  if (values) {
    itemsList = [...values];
  } else {
    const formattedItems = items.map((i) => {
      return {id: i[idKey], label: i[valueKey]};
    });
    itemsList = [...formattedItems];
  }

  const pluralEntity = entity;

  useEffect(() => {
    if (!values) {
      let urls = [markupProjectionQuery(pluralEntity)];
      if (formData._links) {
        urls.push(markupProjectionQuery(formData._links[entity].href));
      }
      dispatch(selectedItemsRequested([route]));
    }
  }, []);

  const selectedValue = itemsList.find((o) => o.id === formData[idKey ?? entity]);
  return itemsList.length > 1 ? (
    <>
      <Typography variant="subtitle2">{props.schema.title}</Typography>
      <Autocomplete
        disableClearable
        id={'select-auto-' + idKey}
        options={itemsList}
        multiple={props.multiple || false}
        getOptionLabel={(o) => o.label}
        getOptionSelected={(o, v) => o.id === v.id}
        defaultValue={selectedValue}
        filterSelectedOptions
        onChange={(_, o) => {
          dispatch(setField({newValue: o.id, entity: idKey ?? entity}));
        }}
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
