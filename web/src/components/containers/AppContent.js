import React from 'react';
import Box from '@mui/material/Box';
import {PropTypes} from 'prop-types';

const AppContent = ({children}) => {
  return (
    <Box display="flex" flexDirection="column" height="100vh" width="100%">
      <Box margin={3}></Box>
      {children}
    </Box>
  );
};

export default AppContent;

AppContent.propTypes = {
  children: PropTypes.node.isRequired
};
