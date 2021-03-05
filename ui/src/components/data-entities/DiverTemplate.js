import {Box} from '@material-ui/core';

import React from 'react';
import {PropTypes} from 'prop-types';

const DiverTemplate = ({properties, title}) => {
  const el = {};
  properties.map((e) => {
    el[e.name] = e.content;
  });

  return (
    <Box width={400} spacing={2} flexDirection="column">
      <h1>{title}</h1>
      {el['initials']}
      {el['fullName']}
    </Box>
  );
};

DiverTemplate.propTypes = {
  properties: PropTypes.any,
  title: PropTypes.string
};

export default DiverTemplate;
