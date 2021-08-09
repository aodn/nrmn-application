import {makeStyles, Typography} from '@material-ui/core';
import {ArrowDropDown as ArrowDropDownIcon, ArrowRight as ArrowRightIcon} from '@material-ui/icons';
import TreeItem from '@material-ui/lab/TreeItem';
import TreeView from '@material-ui/lab/TreeView';
import React from 'react';
import {PropTypes} from 'prop-types';

import {measurements, extendedMeasurements} from '../../../constants';

const useTreeItemStyles = makeStyles((theme) => ({
  content: {
    color: theme.palette.text.secondary,
    borderTopRightRadius: theme.spacing(2),
    borderBottomRightRadius: theme.spacing(2),
    fontWeight: theme.typography.fontWeightMedium
  },
  group: {
    marginLeft: 0,
    '& $content': {
      paddingLeft: theme.spacing(2)
    }
  },
  label: {
    fontWeight: 'inherit',
    color: 'inherit'
  },
  labelRoot: {
    display: 'flex',
    alignItems: 'center',
    padding: theme.spacing(0.5, 0)
  },
  labelIcon: {
    marginRight: theme.spacing(1)
  },
  labelText: {
    fontWeight: 'inherit',
    flexGrow: 1
  }
}));

const ValidationSummary = (props) => {
  const classes = useTreeItemStyles();
  const mm = measurements.concat(extendedMeasurements);
  let i = 0;
  return (
    <TreeView defaultCollapseIcon={<ArrowDropDownIcon />} defaultExpandIcon={<ArrowRightIcon />}>
      {props.data.map((m) => (
        <TreeItem
          nodeId={m.key}
          label={
            <div className={classes.labelRoot}>
              <Typography variant="body2" className={classes.labelText}>
                {m.message}
              </Typography>
              <Typography variant="caption" color="inherit">
                {`(${m.description.length})`}
              </Typography>
            </div>
          }
          key={m.key}
        >
          {m.description.map((d) => {
            const mmHeader = mm.find((m) => m.field === d.columnName);
            let label = mmHeader ? `${mmHeader.fishSize}/${mmHeader.invertSize}` : d.columnName;
            return (
              <TreeItem
                nodeId={`${i++}`}
                key={i++}
                onClick={() => props.onItemClick(d)}
                className={classes.labelText}
                label={
                  <div className={classes.labelRoot}>
                    <Typography key={i++} variant="body2" className={classes.labelText}>
                      {d.value ? (
                        <div>
                          <b>{label}</b> {d.value}
                        </div>
                      ) : d.label ? (
                        <b>{label} is empty</b>
                      ) : d.columnNames ? (
                        <b>
                          Check Column{d.columnNames.length > 1 ? 's' : ''} {d.columnNames.join(', ')}
                        </b>
                      ) : (
                        <b>Region contains invalid values</b>
                      )}
                    </Typography>
                  </div>
                }
              />
            );
          })}
        </TreeItem>
      ))}
    </TreeView>
  );
};

export default ValidationSummary;

ValidationSummary.propTypes = {
  data: PropTypes.array,
  onItemClick: PropTypes.func
};
