import React from 'react';
import {PropTypes} from 'prop-types';

const TargetUrlComponent = ({value, max}) => {
  const [label, setLabel] = React.useState('Copy');
  const copy = () => {
    navigator.clipboard.writeText(value);
    setLabel('Copied!');
    setTimeout(() => setLabel('Copy'), 2000);
  };
  return (
    value && <>
      {value.substring('http') >= 0 ? <a href={value}>{value.substring(0, max && 20)}...</a> : value}
      <button style={{width: '60px'}} onClick={copy}>
        {label}
      </button>
    </>
  );
};

export default TargetUrlComponent;

TargetUrlComponent.propTypes = {
  value: PropTypes.string,
  max: PropTypes.number,
  disabled: PropTypes.bool
};
