import React from 'react';
import Box from '@material-ui/core/Box';
import {PropTypes} from 'prop-types';

const AppContent = ({children}) => {
  return (
    <Box display="flex" flexDirection="column" height="100vh" width="100%">
      {children}
    </Box>
  );
};

export default AppContent;

AppContent.propTypes = {
  children: PropTypes.node.isRequired
};
