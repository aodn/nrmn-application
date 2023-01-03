import {LinearProgress, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography} from '@mui/material';
import {Box} from '@mui/system';
import React, {useEffect, useState} from 'react';
import {createSharedLink, getSharedLinks, deleteSharedLink} from '../../../api/api';
import PropTypes from 'prop-types';

const enabledEndpoints = ['EP_SITE_LIST'];

const endpoints = [
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

const TargetUrlComponent = ({value}) => (
  <>
    <a href={value}>{value.substring(0, 20)}...</a>
    <button onClick={() => navigator.clipboard.writeText(value)}>Copy</button>
  </>
);

TargetUrlComponent.propTypes = {
  value: PropTypes.string
};

const SharedLinkList = () => {
  const [data, setData] = useState();
  const [posting, setPosting] = useState(false);
  const [deleting, setDeleting] = useState();

  useEffect(() => {
    if (data) return;

    getSharedLinks().then((res) => {
      setData(res.data);
      document.getElementById('endpoint').value = endpoints[0];
      const defaultDate = new Date();
      defaultDate.setDate(defaultDate.getDate() + 1);
      const defaultDateString = defaultDate.toISOString().split('T')[0];
      document.getElementById('expires').value = defaultDateString;
      setPosting(false);
    });
  }, [data, setData, setPosting]);

  useEffect(() => {
    if (!deleting) return;
    deleteSharedLink(deleting).then(() => {
      setData(null);
      setDeleting(null);
    });
  }, [deleting]);

  useEffect(() => {
    if (!posting) return;
    const sharedLinkDto = {
      content: document.getElementById('endpoint').value,
      recipient: document.getElementById('recipient').value ?? '',
      expires: document.getElementById('expires').value
    };
    createSharedLink(sharedLinkDto).then(() => setData(null));
  }, [posting]);

  const header = (
    <Box m={1} ml={2}>
      <Typography variant="h6">Endpoint Links</Typography>
    </Box>
  );

  const newLinkForm = (
    <Box m={1} border={1} p={1} borderColor="divider" flexDirection={'row'} display={'flex'}>
      <Box m={1} flexDirection={'column'} display={'flex'}>
        <label htmlFor="endpoint">Endpoint</label>
        <select id="endpoint" disabled={posting}>
          {endpoints.filter(e => enabledEndpoints.includes(e)).map((value) => (
            <option key={value}>{value}</option>
          ))}
        </select>
      </Box>
      <Box m={1} flexDirection={'column'} display={'flex'}>
        <label htmlFor="recipient">Recipient</label>
        <input id="recipient" disabled={posting} />
      </Box>
      <Box m={1} flexDirection={'column'} display={'flex'}>
        <label htmlFor="expires">Expires</label>
        <input id="expires" type="date" disabled={posting} />
      </Box>
      <Box m={1} flexDirection={'column'} display={'flex'}>
        <button style={{marginTop: 'auto'}} disabled={posting} onClick={() => setPosting(true)}>
          Create New Shared Link
        </button>
      </Box>
    </Box>
  );

  if (!data) {
    return (
      <>
        {header}
        <LinearProgress />
      </>
    );
  }

  const columns = {
    recipient: 'Recipient',
    content: 'Content',
    targetUrl: 'Target URL',
    createdBy: 'Created By',
    created: 'Created',
    expires: 'Expires',
    linkId: ''
  };

  const keys = Object.keys(columns);

  return (
    <>
      {header}
      {newLinkForm}
      <Box m={1} border={1} borderColor="divider">
        <TableContainer>
          <Table>
            <TableHead>
              <TableRow>
                {keys.map((key) => (
                  <TableCell key={key}>{columns[key]}</TableCell>
                ))}
              </TableRow>
            </TableHead>
            <TableBody>
              {data.length < 1 && (
                <TableRow>
                  <TableCell colSpan={columns.length}>
                    <i>No Links</i>
                  </TableCell>
                </TableRow>
              )}
              {data.map((row, i) => (
                <TableRow key={`${row['publicId']}`}>
                  {keys.map((key) => (
                    <TableCell key={`${key}-${i}`}>
                      {(key === 'targetUrl' && <TargetUrlComponent value={row[key]} />) ||
                        (key === 'linkId' && <button onClick={() => setDeleting(row[key])}>Delete</button>) ||
                        row[key]}
                    </TableCell>
                  ))}
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </Box>
    </>
  );
};

export default SharedLinkList;
