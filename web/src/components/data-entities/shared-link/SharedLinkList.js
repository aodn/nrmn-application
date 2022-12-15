import {LinearProgress, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography} from '@mui/material';
import {Box} from '@mui/system';
import React, {useEffect, useState} from 'react';
import {createSharedLink, getSharedLinks} from '../../../api/api';

const endpoints = [
  'ep_m1',
  'ep_m2_cryptic_fish',
  'ep_m2_inverts',
  'ep_observable_items',
  'ep_rarity_abundance',
  'ep_rarity_extents',
  'ep_rarity_range',
  'ep_site_list',
  'ep_survey_list',
  'ep_rarity_frequency'
];

const SharedLinkList = () => {
  const [data, setData] = useState();
  const [posting, setPosting] = useState(false);

  useEffect(() => {
    if (data) return;

    getSharedLinks().then((res) => {
      setData(res.data);

      // FIXME: useState
      document.getElementById('endpoint').value = endpoints[0];
      const defaultDate = new Date();
      defaultDate.setDate(defaultDate.getDate() + 1);
      const defaultDateString = defaultDate.toISOString().split('T')[0];
      document.getElementById('expires').value = defaultDateString;
    });

  }, [data, setData]);

  useEffect(() => {
    if (!posting) return;
    const sharedLinkDto = {
      content: document.getElementById('endpoint').value,
      description: document.getElementById('description').value ?? '',
      expires: document.getElementById('expires').value
    };
    createSharedLink(sharedLinkDto).then((res) => setData(null));
  }, [posting]);

  const header = (
    <Box m={1} ml={2}>
      <Typography variant="h6">Endpoint Links</Typography>
    </Box>
  );

  // default date is 1 day from now formatted as yyyy-mm-dd

  const newLinkForm = (
    <Box m={1} border={1} p={1} borderColor="divider" flexDirection={'row'} display={'flex'}>
      <Box m={1} flexDirection={'column'} display={'flex'}>
        <label htmlFor="endpoint">Endpoint</label>
        <select id="endpoint" disabled={posting}>
          {endpoints.map((value) => (
            <option key={value}>{value}</option>
          ))}
        </select>
      </Box>
      <Box m={1} flexDirection={'column'} display={'flex'}>
        <label htmlFor="description">Description</label>
        <input id="description" disabled={posting} />
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
    description: 'Description',
    content: 'Content',
    targetUrl: 'Target URL',
    copyButton: '',
    createdBy: 'Created By',
    created: 'Created',
    expires: 'Expires'
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
                <TableRow key={row['publicId']}>
                  {keys.map((key) => (
                    <TableCell key={i}>
                      {key == 'targetUrl' ? (
                        <>
                          <a href={row[key]}>{row[key].substring(0, 20)}...</a>
                          <button onClick={() => navigator.clipboard.writeText(row[key])}>Copy</button>
                        </>
                      ) : (
                        row[key]
                      )}
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
