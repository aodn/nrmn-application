import {LinearProgress, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography} from '@mui/material';
import {Box} from '@mui/system';
import React, {useEffect, useState} from 'react';
import {getSharedLinks} from '../../../api/api';

const SharedLinkList = () => {
  const [data, setData] = useState();

  useEffect(() => {
    if (data) return;
    getSharedLinks().then((res) => {
      setData(res.data);
    });
  }, [data, setData]);

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
