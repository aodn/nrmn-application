import React from 'react';

import {Button, Box} from '@mui/material';

import LaunchIcon from '@mui/icons-material/Launch';
import IconButton from '@mui/material/IconButton';
import DeleteIcon from '@mui/icons-material/Close';
import Link from '@mui/material/Link';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import PropTypes from 'prop-types';

import { useState } from 'react';

import CustomSearchInput from '../../input/CustomSearchInput';

const SpeciesCorrectEdit = ({detail, onClick}) => {
  const [correction, setCorrection] = useState({});
  const [request, setRequest] = useState({});
  const [correctionLocations, setCorrectionLocations] = useState([]);

  return (
    <Box width="50%" borderLeft={1} borderColor="divider" style={{overflowX: 'hidden', overflowY: 'auto'}}>
      <Box mx={1}>
        <Typography variant="subtitle2">Current species name</Typography>
        <Box flexDirection={'row'} display={'flex'} alignItems={'center'}>
          <TextField fullWidth color="primary" size="small" value={detail.observableItemName} spellCheck={false} readOnly />
          <IconButton
            style={{marginLeft: 5, marginRight: 15}}
            onClick={() => window.open(`/reference/observableItem/${detail.observableItemId}`, '_blank').focus()}
          >
            <LaunchIcon />
          </IconButton>
        </Box>
      </Box>
      <Box m={1}>
        <Typography variant="subtitle2">Correct to</Typography>
        <Box flexDirection={'row'} display={'flex'} alignItems={'center'}>
          <CustomSearchInput
            fullWidth
            formData={correction?.newObservableItemName}
            exclude={detail.observableItemName}
            onChange={(t) => {
              if (t) {
                setCorrection({...request, newObservableItemId: t.id, newObservableItemName: t.species});
              } else {
                setCorrection({...request, newObservableItemId: null, newObservableItemName: null});
              }
            }}
          />
          <IconButton
            style={{marginLeft: 5, marginRight: 15}}
            disabled={!correction?.newObservableItemId}
            onClick={() => window.open(`/reference/observableItem/${correction.newObservableItemId}`, '_blank').focus()}
          >
            <LaunchIcon />
          </IconButton>
        </Box>
      </Box>
      <Box m={1}>
        <Button variant="contained" disabled={!correction?.newObservableItemName}>
          Submit Correction
        </Button>
      </Box>
      <Box m={1} key={detail.observableItemId}>
        {correctionLocations?.map((l) => {
          return (
            <Box key={l.locationId} borderBottom={1} borderColor="divider">
              <IconButton
                size="small"
                onClick={() => {
                  setCorrectionLocations([...correctionLocations.filter((c) => c.locationName !== l.locationName)]);
                }}
              >
                <DeleteIcon fontSize="inherit" />
              </IconButton>
              <Typography variant="caption" sx={{fontWeight: 'medium'}}>
                {l.locationName}{' '}
              </Typography>
              <Typography variant="caption">
                {l.surveyIds.map((l) => (
                  <Link key={l} onClick={() => window.open(`/data/survey/${l}`, '_blank').focus()} href="#">
                    {l}
                  </Link>
                ))}
              </Typography>
            </Box>
          );
        })}
      </Box>
    </Box>
  );
};

export default SpeciesCorrectEdit;

SpeciesCorrectEdit.propTypes = {
  detail: PropTypes.object,
  onClick: PropTypes.func
};
