import {PropTypes} from 'prop-types';

const DataSheetCell = (props) => {
  return props.value;
};

DataSheetCell.propTypes = {
  value: PropTypes.any
};

export default DataSheetCell;
