import React, {useEffect, useReducer} from 'react';
import {Box} from '@mui/system';
import {createSharedLink} from '../../../api/api';
import PropTypes from 'prop-types';

const all_endpoints = [
  {value: 'EP_M1_ALL', disabled: false},
  {value: 'EP_M1_AUSTRALIA', disabled: false},
  {value: 'EP_M1_TAS', disabled: false},
  {value: 'EP_M1_NSW', disabled: false},
  {value: 'EP_M1_VIC', disabled: false},
  {value: 'EP_M1_WA', disabled: false},
  {value: 'EP_M1_SA', disabled: false},
  {value: 'EP_M1_NT', disabled: false},
  {value: 'EP_M1_QLD', disabled: false},
  {value: 'EP_M2_CRYPTIC_FISH_ALL', disabled: false},
  {value: 'EP_M2_CRYPTIC_FISH_AUSTRALIA', disabled: false},
  {value: 'EP_M2_CRYPTIC_FISH_TAS', disabled: false},
  {value: 'EP_M2_CRYPTIC_FISH_NSW', disabled: false},
  {value: 'EP_M2_CRYPTIC_FISH_VIC', disabled: false},
  {value: 'EP_M2_CRYPTIC_FISH_WA', disabled: false},
  {value: 'EP_M2_CRYPTIC_FISH_SA', disabled: false},
  {value: 'EP_M2_CRYPTIC_FISH_NT', disabled: false},
  {value: 'EP_M2_CRYPTIC_FISH_QLD', disabled: false},
  {value: 'EP_SITE_LIST', disabled: false},
  {value: 'EP_M2_INVERTS_ALL', disabled: false},
  {value: 'EP_M2_INVERTS_AUSTRALIA', disabled: false},
  {value: 'EP_M2_INVERTS_TAS', disabled: false},
  {value: 'EP_M2_INVERTS_NSW', disabled: false},
  {value: 'EP_M2_INVERTS_VIC', disabled: false},
  {value: 'EP_M2_INVERTS_WA', disabled: false},
  {value: 'EP_M2_INVERTS_SA', disabled: false},
  {value: 'EP_M2_INVERTS_NT', disabled: false},
  {value: 'EP_M2_INVERTS_QLD', disabled: false},
  {value: 'EP_OBSERVABLE_ITEMS', disabled: true},
  {value: 'EP_RARITY_ABUNDANCE', disabled: true},
  {value: 'EP_RARITY_RANGE', disabled: true},
  {value: 'EP_RARITY_EXTENTS', disabled: true},
  {value: 'EP_SURVEY_LIST', disabled: true},
  {value: 'EP_M0_OFF_TRANSECT_SIGHTINGS', disabled: true},
  {value: 'EP_M3', disabled: true},
  {value: 'EP_M4_MACROCYSTIS_COUNT', disabled: true},
  {value: 'EP_M5_LIMPET_QUADRATS', disabled: true},
  {value: 'EP_M7_LOBSTER_COUNT', disabled: true},
  {value: 'EP_M11_OFF_TRANSECT_MEASUREMENT', disabled: true},
  {value: 'EP_M12_DEBRIS', disabled: true},
  {value: 'EP_M13_PQ_SCORES', disabled: true},
  {value: 'EP_SPECIES_SURVEY_OBSERVATION', disabled: true}
];

const SharedLinkAdd = ({onPost}) => {
  const getDefaultExpiry = () => {
    const defaultDate = new Date();
    defaultDate.setDate(defaultDate.getDate() + 1);
    return defaultDate.toISOString().split('T')[0];
  };

  const getInitial = () => {
    all_endpoints.sort((a, b) => a.value.localeCompare(b.value));
    return {available: [...all_endpoints], generate: [], expires: getDefaultExpiry(), recipient: ''};
  };

  const reducer = (state, action) => {
    var newState = {};
    switch (action.verb) {
      case 'reset': {
        newState = getInitial();
        break;
      }
      case 'posting': {
        newState = {...state, posting: true};
        break;
      }
      case 'add': {
        const available = state.available.filter(({value}) => !action.values.map((e) => e.value).includes(value));
        const generate = [...state.generate, ...action.values];
        generate.sort((a, b) => a.value.localeCompare(b.value));
        newState = {...state, available, generate};
        break;
      }
      case 'remove': {
        const generate = state.generate.filter(({value}) => !action.values.map((e) => e.value).includes(value));
        const available = [...state.available, ...action.values];
        available.sort((a, b) => a.value.localeCompare(b.value));
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

  useEffect(() => {
    if (!endpoints.posting) return;
    const sharedLinkDto = {
      content: endpoints.generate,
      recipient: endpoints.recipient,
      expires: endpoints.expires,
      endpoints: endpoints.generate.map((e) => e.value)
    };
    createSharedLink(sharedLinkDto).then(() => {
      dispatch({verb: 'reset'});
      onPost();
    });
  }, [endpoints, onPost]);

  return (
    <Box
      m={1}
      border={1}
      p={1}
      borderColor="divider"
      flexDirection="row"
      display="flex"
      justifyContent="center"
      sx={{backgroundColor: 'white'}}
    >
      <Box m={1} flexDirection="column" display="flex">
        <label htmlFor="endpoint">Available Endpoints</label>
        <select
          size={all_endpoints.length / 4}
          style={{width: '275px', height: '100%'}}
          id="available"
          disabled={endpoints.posting}
          multiple
        >
          {endpoints.available
            .sort((a, b) => a.value - b.value)
            .map((o) => (
              <option disabled={o.disabled} key={o.value} onDoubleClick={(e) => dispatch({verb: 'add', values: [e.target.value]})}>
                {o.value}
              </option>
            ))}
        </select>
      </Box>
      <Box m={1} flexDirection="column" display="flex" justifyContent="center">
        <button
          style={{height: '50px'}}
          disabled={endpoints.posting}
          onClick={() => {
            const selected = document.querySelectorAll('#available option:checked');
            const values = Array.from(selected).map((el) => el.value);
            dispatch({verb: 'add', values: all_endpoints.filter((e) => values.includes(e.value))});
          }}
        >
          {'>'}
        </button>
        <button
          style={{marginTop: '5px', height: '50px'}}
          disabled={endpoints.posting}
          onClick={() => {
            const selected = document.querySelectorAll('#endpoint option:checked');
            const values = Array.from(selected).map((el) => el.value);
            dispatch({verb: 'remove', values: all_endpoints.filter((e) => values.includes(e.value))});
          }}
        >
          {'<'}
        </button>
      </Box>
      <Box m={1} flexDirection="column" display="flex">
        <label htmlFor="endpoint">Endpoint Links To Generate</label>
        <select
          size={all_endpoints.length / 4}
          style={{width: '275px', height: '100%'}}
          id="endpoint"
          disabled={endpoints.posting}
          multiple
        >
          {endpoints.generate.map((o) => (
            <option
              onDoubleClick={(e) => {
                dispatch({verb: 'remove', values: [e.target.value]});
              }}
              disabled={o.disabled}
              key={o.value}
            >
              {o.value}
            </option>
          ))}
        </select>
      </Box>
      <Box m={1} width="275px" flexDirection="column" display="flex">
        <label htmlFor="recipient">Recipient</label>
        <input
          id="recipient"
          value={endpoints.recipient}
          onChange={(e) => dispatch({verb: 'setRecipient', value: e.target.value})}
          disabled={endpoints.posting}
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
          disabled={endpoints.posting}
        />
        <button style={{marginTop: 'auto'}} disabled={endpoints.posting || !endpoints.valid} onClick={() => dispatch({verb: 'posting'})}>
          {endpoints.posting ? 'Generating Links. Please Wait..' : 'Generate Links'}
        </button>
      </Box>
    </Box>
  );
};

export default SharedLinkAdd;

SharedLinkAdd.propTypes = {
  onPost: PropTypes.func.isRequired
};
