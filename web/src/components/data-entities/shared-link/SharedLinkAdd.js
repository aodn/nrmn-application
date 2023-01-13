import React, {useEffect, useReducer, useState} from 'react';
import {Box} from '@mui/system';
import {createSharedLink} from '../../../api/api';
import PropTypes from 'prop-types';

const all_endpoints = [
  'EP_M1_ALL',
  'EP_M1_AUSTRALIA',
  'EP_M1_TAS',
  'EP_M1_NSW',
  'EP_M1_VIC',
  'EP_M1_WA',
  'EP_M1_SA',
  'EP_M1_NT',
  'EP_M1_QLD',
  'EP_M2_CRYPTIC_FISH_ALL',
  'EP_M2_CRYPTIC_FISH_AUSTRALIA',
  'EP_M2_CRYPTIC_FISH_TAS',
  'EP_M2_CRYPTIC_FISH_NSW',
  'EP_M2_CRYPTIC_FISH_VIC',
  'EP_M2_CRYPTIC_FISH_WA',
  'EP_M2_CRYPTIC_FISH_SA',
  'EP_M2_CRYPTIC_FISH_NT',
  'EP_M2_CRYPTIC_FISH_QLD',
  'EP_M2_INVERTS_ALL',
  'EP_M2_INVERTS_AUSTRALIA',
  'EP_M2_INVERTS_TAS',
  'EP_M2_INVERTS_NSW',
  'EP_M2_INVERTS_VIC',
  'EP_M2_INVERTS_WA',
  'EP_M2_INVERTS_SA',
  'EP_M2_INVERTS_NT',
  'EP_M2_INVERTS_QLD',
  'EP_OBSERVABLE_ITEMS',
  'EP_RARITY_ABUNDANCE',
  'EP_RARITY_RANGE',
  'EP_RARITY_EXTENTS',
  'EP_SITE_LIST',
  'EP_SURVEY_LIST',
  'EP_M0_OFF_TRANSECT_SIGHTINGS',
  'EP_M3',
  'EP_M4_MACROCYSTIS_COUNT',
  'EP_M5_LIMPET_QUADRATS',
  'EP_M7_LOBSTER_COUNT',
  'EP_M11_OFF_TRANSECT_MEASUREMENT',
  'EP_M12_DEBRIS',
  'EP_M13_PQ_SCORES',
  'EP_SPECIES_SURVEY_OBSERVATION'
];

const SharedLinkAdd = (props) => {
  const [posting, setPosting] = useState(false);
  const [disabled, setDisabled] = useState(props.disabled);

  const getDefaultExpiry = () => {
    const defaultDate = new Date();
    defaultDate.setDate(defaultDate.getDate() + 1);
    return defaultDate.toISOString().split('T')[0];
  };

  const getInitial = () => ({available: [...all_endpoints.sort()], generate: [], expires: getDefaultExpiry(), recipient: ''});

  useEffect(() => {
    if (!posting) return;
    setDisabled(true);
    const sharedLinkDto = {
      content: endpoints.generate,
      recipient: endpoints.recipient,
      expires: endpoints.expires
    };
    createSharedLink(sharedLinkDto).then(() => dispatch({verb: 'reset'}));
  }, [posting, setDisabled, endpoints]);

  const reducer = (state, action) => {
    var newState = {};
    switch (action.verb) {
      case 'reset': {
        newState = getInitial();
        break;
      }
      case 'add': {
        const available = state.available.filter((value) => !action.values.includes(value));
        const generate = [...state.generate, ...action.values];
        generate.sort();
        newState = {...state, available, generate};
        break;
      }
      case 'remove': {
        const generate = state.generate.filter((value) => !action.values.includes(value));
        const available = [...state.available, ...action.values];
        available.sort();
        newState = {...state, available, generate};
        break;
      }
      case 'setRecipient': {
        newState = {...state, recipient: action.value};
        break;
      }
      case 'setExpires': {
        newState = {...state, expires: action.value};
        break;
      }
    }
    const valid = newState.generate.length > 0 && newState.recipient.length > 2 && newState.expires.length === 10;
    return {...newState, valid};
  };

  const [endpoints, dispatch] = useReducer(reducer, getInitial());

  return (
    <Box m={1} border={1} p={1} borderColor="divider" flexDirection={'row'} display={'flex'}>
      <Box m={1} flexDirection={'column'} display={'flex'}>
        <label htmlFor="endpoint">Available Endpoints</label>
        <select size={all_endpoints.length / 4} style={{width: '275px', height: '100%'}} id="available" disabled={disabled} multiple>
          {endpoints.available.map((value) => (
            <option
              onDoubleClick={(e) => {
                dispatch({verb: 'add', values: [e.target.value]});
              }}
              key={value}
            >
              {value}
            </option>
          ))}
        </select>
      </Box>
      <Box m={1} flexDirection={'column'} display={'flex'} justifyContent={'center'}>
        <button
          style={{height: '50px'}}
          disabled={disabled}
          onClick={() => {
            const selected = document.querySelectorAll('#available option:checked');
            const values = Array.from(selected).map((el) => el.value);
            dispatch({verb: 'add', values});
          }}
        >
          {'>>'}
        </button>
        <button
          style={{marginTop: '5px', height: '50px'}}
          disabled={disabled}
          onClick={() => {
            const selected = document.querySelectorAll('#endpoint option:checked');
            const values = Array.from(selected).map((el) => el.value);
            dispatch({verb: 'remove', values});
          }}
        >
          {'<<'}
        </button>
      </Box>
      <Box m={1} flexDirection={'column'} display={'flex'}>
        <label htmlFor="endpoint">Endpoint Links To Generate</label>
        <select size={all_endpoints.length / 4} style={{width: '275px', height: '100%'}} id="endpoint" disabled={disabled} multiple>
          {endpoints.generate.map((value) => (
            <option
              onDoubleClick={(e) => {
                dispatch({verb: 'remove', values: [e.target.value]});
              }}
              key={value}
            >
              {value}
            </option>
          ))}
        </select>
      </Box>
      <Box m={1} width={'80%'} flexDirection={'column'} display={'flex'}>
        <label htmlFor="recipient">Recipient</label>
        <input
          id="recipient"
          value={endpoints.recipient}
          onChange={(e) => dispatch({verb: 'setRecipient', value: e.target.value})}
          disabled={disabled}
        />
        <label style={{marginTop: '10px'}} htmlFor="expires">
          Expiry
        </label>
        <input
          id="expires"
          min={getDefaultExpiry()}
          type="date"
          value={endpoints.expires}
          onChange={(e) => dispatch({verb: 'setExpires', value: e.target.value})}
          disabled={disabled}
        />
        <button style={{marginTop: 'auto'}} disabled={disabled || !endpoints.valid} onClick={() => setPosting(true)}>
          Generate Links
        </button>
      </Box>
    </Box>
  );
};

SharedLinkAdd.propTypes = {
  disabled: PropTypes.bool
};

export default SharedLinkAdd;
