import React from 'react';
import Typography from '@mui/material/Typography';
import ArrowDropDownIcon from '@mui/icons-material/ArrowDropDown';
import ArrowRightIcon from '@mui/icons-material/ArrowRight';
import {TreeItem} from '@mui/x-tree-view';
import {SimpleTreeView} from '@mui/x-tree-view';
import {PropTypes} from 'prop-types';

import {allMeasurements} from '../../../common/constants';

const ValidationSummary = ({data, onItemClick}) => {
  const isContiguous = (sorted) => {
    const first = sorted[0];
    const last = sorted[sorted.length - 1];
    return last - first + 1 === sorted.length;
  };

  const labelForDescription = (d) => {
    const mmHeader = allMeasurements.find((m) => d.columnName && m.field === d.columnName.replace('measurements.', ''));
    return mmHeader ? `${d.isInvertSizing ? mmHeader.invertSize : mmHeader.fishSize}cm` : d.columnName;
  };

  return (
    <SimpleTreeView slots={{
      collapseIcon: ArrowDropDownIcon,
      expandIcon: ArrowRightIcon
    }} >
      {['BLOCKING', 'WARNING', 'DUPLICATE', 'INFO'].map((level) => (
        <div key={level}>
          <Typography variant="button">{data[level] ? level : 'No ' + level + '✔'}</Typography>
          {data[level]?.map((m) => (
              <TreeItem
                itemId={m.id}
                key={m.id}
                label={
                  <Typography variant="body2">
                    {m.message + ' ' + (m.message.includes('easurements') ? labelForDescription(m.description[0]) : '') + (m.description?.length > 1 ? ' (' + m.description.length + ')' : '')}
                  </Typography>
                }
              >
                {m.description?.map((d) => {
                  const label = labelForDescription(d);
                  return (
                    <TreeItem
                      itemId={d.id}
                      key={d.id}
                      onClick={() => onItemClick(d, level === 'DUPLICATE')}
                      label={
                        <Typography variant="body2">
                          {d.value ? (
                            <span>
                              <b>{label}</b> {d.value} {(d.rowNumbers?.length > 1) && `(${d.rowNumbers.length})`}
                            </span>
                          ) : d.label ? (
                            <b>{label} is empty</b>
                          ) : d.columnNames ? (
                            <b>
                              Check Column{d.columnNames.length > 1 ? 's' : ''} {d.columnNames.join(', ')}
                            </b>
                          ) : (
                            <b>
                              Rows{' '}
                              {isContiguous(d.rowNumbers)
                                ? `[${d.rowNumbers[0]}-${d.rowNumbers[d.rowNumbers.length - 1]}]`
                                : d.rowNumbers.join(', ')}
                            </b>
                          )}
                        </Typography>
                      }
                    />
                  );
                })}
              </TreeItem>
          ))}
        </div>
      ))}
    </SimpleTreeView>
  );
};

export default ValidationSummary;

ValidationSummary.propTypes = {
  data: PropTypes.object,
  onItemClick: PropTypes.func
};
