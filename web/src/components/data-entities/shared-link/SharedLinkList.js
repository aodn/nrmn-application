import {LinearProgress, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography} from '@mui/material';
import {Box} from '@mui/system';
import SharedLinkAdd from './SharedLinkAdd';
import React, {useEffect, useState} from 'react';
import {getSharedLinks, deleteSharedLink} from '../../../api/api';
import PropTypes from 'prop-types';

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
  const [deleting, setDeleting] = useState();

  useEffect(() => {
    if (data) return;

    getSharedLinks().then((res) => {
      setData(res.data);
      //document.getElementById('endpoint').value = endpoints[0];
      const defaultDate = new Date();
      defaultDate.setDate(defaultDate.getDate() + 1);
      const defaultDateString = defaultDate.toISOString().split('T')[0];
      const expires = document.getElementById('expires');
      expires.value = defaultDateString;
      expires.min = defaultDateString;
    });
  }, [data, setData]);

  useEffect(() => {
    if (!deleting) return;
    deleteSharedLink(deleting).then(() => {
      setData(null);
      setDeleting(null);
    });
  }, [deleting]);

  const header = (
    <Box m={1} ml={2}>
      <Typography variant="h6">Endpoint Links</Typography>
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
    targetUrl: 'Shared Link',
    createdBy: 'Created By',
    created: 'Created',
    expires: 'Expires',
    linkId: ''
  };

  const keys = Object.keys(columns);

  return (
    <>
      {header}
      <SharedLinkAdd disabled/>
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
