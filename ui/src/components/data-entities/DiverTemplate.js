import {Box} from '@material-ui/core';

import React from 'react';
import {PropTypes} from 'prop-types';

const DiverTemplate = ({properties}) => {
  const el = {};
  properties.map((e) => {
    el[e.name] = e.content;
  });

  return (
    <Box flexDirection="column">
      <Box width={200}>{el['initials']}</Box>
      <Box>{el['fullName']}</Box>
    </Box>
  );
};

DiverTemplate.propTypes = {
  properties: PropTypes.any,
  title: PropTypes.string
};

export default DiverTemplate;
