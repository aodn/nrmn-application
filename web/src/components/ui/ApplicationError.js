import React, {useState} from 'react';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import ArrowForwardIosSharpIcon from '@mui/icons-material/ArrowForwardIosSharp';
import Accordion from '@mui/material/Accordion';
import AccordionSummary from '@mui/material/AccordionSummary';
import AccordionDetails from '@mui/material/AccordionDetails';
import Alert from '@mui/material/Alert';
import {PropTypes} from 'prop-types';

const ApplicationError = ({error}) => {
  const [errorToggled, setErrorToggled] = useState(false);

  return (
    <Box m={10}>
      <Box py={3}>
        <Alert severity="error" variant="filled">
          The server may be experiencing problems. Please wait a moment and try again.
          <br />
          If this problem persists, please contact info@aodn.org.au.
        </Alert>
        <Accordion disableGutters elevation={0} square expanded={errorToggled} onChange={() => setErrorToggled(!errorToggled)}>
          <AccordionSummary expandIcon={<ArrowForwardIosSharpIcon sx={{fontSize: '0.9rem'}} />}>
            <p>{error?.message}</p>
          </AccordionSummary>
          <AccordionDetails>
            <code>{error?.response?.data}</code>
          </AccordionDetails>
        </Accordion>
      </Box>
      <Button variant="outlined" onClick={() => window.location.reload()}>
        Refresh Page
      </Button>
    </Box>
  );
};

export default ApplicationError;

ApplicationError.propTypes = {
  error: PropTypes.object
};
