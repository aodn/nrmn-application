import React from 'react';

import {useDispatch, useSelector} from 'react-redux';
import {Link, useParams} from 'react-router-dom';
import {useEffect} from 'react';
import NestedApiFieldDetails from './customWidgetFields/NestedApiFieldDetails';
import config from 'react-global-configuration';
import Grid from '@material-ui/core/Grid';
import {itemRequested} from './middleware/entities';
import Button from '@material-ui/core/Button';
import {makeStyles} from '@material-ui/core/styles';
import BaseForm from '../BaseForm';
import ObjectListViewTemplate from './ObjectListViewTemplate';
import {PropTypes} from 'prop-types';
import LinkButton from './LinkButton';
import _ from 'lodash';

const useStyles = makeStyles(() => ({
  buttons: {
    '& > *': {
      marginTop: 20
    }
  }
}));

const GenericDetailsView = (props) => {
  const {id} = useParams();
  const dispatch = useDispatch();
  const classes = useStyles();

  const editItem = useSelector((state) => state.form.editItem);

  const schemaDefinition = config.get('api') || {};

  useEffect(() => {
    dispatch(itemRequested(props.entity.entityListName + '/' + id));
  }, []);

  const entityDef = schemaDefinition[props.entity.entityName];
  let fullTitle = 'Details for ' + props.entity.entityName;
  const entitySchema = {title: fullTitle, ...entityDef};
  const JSSchema = {components: {schemas: schemaDefinition}, ...entitySchema};
  const uiSchema = {
    'ui:widget': 'string'
  };

  for (const key in entitySchema.properties) {
    const item = entitySchema.properties[key];
    if (item.type === 'string' && item.format === 'uri') {
      uiSchema[key] = {'ui:field': 'relationship'};
    }
    if (item.type === 'object') {
      uiSchema[key] = {'ui:field': 'objects'};
    }
  }

  const inputDisplay = (elem) => {
    const value = elem.formData?.toString();
    return (
      <span>
        <b>{elem.schema.title}: </b> {value ? value : ' -- '}
      </span>
    );
  };

  const objectDisplay = (elem) => {
    let items = [];
    if (elem.formData) {
      if (!Array.isArray(elem.formData)) {
        if (elem.formData.label) {
          items.push(<Grid item>{elem.formData.label}</Grid>);
        } else {
          for (let key of Object.keys(elem.formData)) {
            items.push(
              <Grid key={_.uniqueId('dataObjectDetails-')} item>
                <b>{key}: </b>
                {elem.formData[key]}
              </Grid>
            );
          }
        }
      } else {
        elem.formData.map((item) => items.push(<Grid item>{item}</Grid>));
      }
    } else {
      items.push(
        <Grid key={_.uniqueId('dataObjectDetails-')} item>
          --
        </Grid>
      );
    }
    return ObjectListViewTemplate({name: elem.name, items: items});
  };

  const fields = {
    relationship: NestedApiFieldDetails,
    objects: objectDisplay,
    ArrayField: objectDisplay,
    BooleanField: inputDisplay,
    NumberField: inputDisplay,
    StringField: inputDisplay
  };

  const submitButton = () => {
    let linkLabel = `Edit ` + props.entity.entityName;
    let link = '/edit/' + props.entity.entityListName + '/' + id;
    return (
      <div className={classes.buttons}>
        <Button type={'submit'} component={Link} to={link} color="secondary" aria-label={linkLabel} variant={'contained'}>
          {linkLabel}
        </Button>
      </div>
    );
  };

  const formContent = () => {
    return <BaseForm schema={JSSchema} uiSchema={uiSchema} fields={fields} formData={editItem} submitButton={submitButton()} />;
  };

  return (
    <Grid container direction="row" justify="center" alignItems="center">
      <Grid item>
        <Grid container alignItems="flex-end" justify="space-around" direction="column">
          <LinkButton to={'/list/' + props.entity.entityListName} title={'List ' + props.entity.name} size={'small'} />
          <Grid item>{formContent()}</Grid>
        </Grid>
      </Grid>
    </Grid>
  );
};

GenericDetailsView.propTypes = {
  entity: PropTypes.object
};

export default GenericDetailsView;
