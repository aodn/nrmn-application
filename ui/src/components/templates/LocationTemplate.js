import {Box} from '@material-ui/core';

import React from 'react';
import {PropTypes} from 'prop-types';

const LocationTemplate = ({properties}) => {
  const el = {};
  properties.map((e) => {
    el[e.name] = e.content;
  });

  return (
    <Box spacing={2} flexDirection="column">
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
