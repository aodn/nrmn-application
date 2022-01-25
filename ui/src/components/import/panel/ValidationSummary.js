import React from 'react';
import Typography from '@material-ui/core/Typography';
import ArrowDropDownIcon from '@material-ui/icons/ArrowDropDown';
import ArrowRightIcon from '@material-ui/icons/ArrowRight';
import TreeItem from '@material-ui/lab/TreeItem';
import TreeView from '@material-ui/lab/TreeView';
import {PropTypes} from 'prop-types';

import {measurements, extendedMeasurements} from '../../../constants';

const ValidationSummary = (props) => {
  const mm = measurements.concat(extendedMeasurements);
  return (
    <TreeView defaultCollapseIcon={<ArrowDropDownIcon />} defaultExpandIcon={<ArrowRightIcon />}>
      {props.data.map((m) => (
        <TreeItem
          key={m.key}
          nodeId={m.key}
          label={
            <Typography variant="body2">
              {m.message} {m.description.length > 1 ? '(' + m.description.length + ')' : ''}
            </Typography>
          }
        >
          {m.description.map((d) => {
            const mmHeader = mm.find((m) => m.field === d.columnName);
            let label = mmHeader ? `${(d.isInvertSize ? mmHeader.invertSize : mmHeader.fishSize)}cm` : d.columnName;
            return (
              <TreeItem
                nodeId={`${m.key}-${d.columnName}`}
                key={`${m.key}-${d.columnName}`}
                onClick={() => props.onItemClick(d)}
                label={
                  <Typography variant="body2">
                    {d.value ? (
                      <span>
                        {d.rowNumbers ? `${d.rowNumbers[0]}: ` : ''}
                        <b>{label}</b> {d.value}
                      </span>
                    ) : d.label ? (
                      <b>{label} is empty</b>
                    ) : d.columnNames ? (
                      <b>
                        Check Column{d.columnNames.length > 1 ? 's' : ''} {d.columnNames.join(', ')}
                      </b>
                    ) : (
                      <b>Rows {d.rowNumbers.join(', ')}</b>
                    )}
                  </Typography>
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
