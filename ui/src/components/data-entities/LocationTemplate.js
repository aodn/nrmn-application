import {Box} from '@material-ui/core';

import React from 'react';
import {PropTypes} from 'prop-types';

const LocationTemplate = ({properties, title}) => {
  const el = {};
  properties.map((e) => {
    el[e.name] = e.content;
  });

  return (
    <Box width={400} spacing={2} flexDirection="column">
      <h1>{title}</h1>
      {el['locationName']}
      {el['isActive']}
    </Box>
  );
};

LocationTemplate.propTypes = {
  properties: PropTypes.any,
  title: PropTypes.string
};

export default LocationTemplate;
