import React from 'react';
import {PropTypes} from 'prop-types';
import {allMeasurements} from '../../../common/constants';

const SurveyMeasurementHeader = ({column}) => {
  const header = allMeasurements[column.colId - 1];

  return (
    <div style={{width: 35, textAlign: 'center'}}>
      <div style={{color: '#c4d79b', borderBottom: '1px solid rgba(0, 0, 0, 0.12)'}}>{header.fishSize}</div>
      <div style={{color: '#da9694'}}>{header.invertSize}</div>
    </div>
  );
};

export default SurveyMeasurementHeader;

SurveyMeasurementHeader.propTypes = {
  column: PropTypes.any
};
