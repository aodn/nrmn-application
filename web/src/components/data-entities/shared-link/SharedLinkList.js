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
      <Typography variant="h6">Share Endpoints</Typography>
    </Box>
  );

  if (!data)
    return (
      <>
        {header}
        <LinearProgress />
      </>
    );

  if (data.length === 0)
    return (
      <>
        {header}
        <p>no links in the database. please create a link</p>
      </>
    );

  const columns = Object.keys(data[0]);
  return (
    <>
      {header}
      <Box m={1} border={1} borderColor="divider">
        <TableContainer>
          <Table>
            <TableHead>
              <TableRow>
                {columns.map((v, i) => (
                  <TableCell key={i}>{v}</TableCell>
                ))}
              </TableRow>
            </TableHead>
            <TableBody>
              {data.map((row, i) => (
                <TableRow key={i}>
                  {columns.map((column) => (
                    <TableCell key={i}>{row[column]}</TableCell>
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
