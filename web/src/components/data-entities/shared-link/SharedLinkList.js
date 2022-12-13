import {Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography} from '@mui/material';
import {Box} from '@mui/system';
import React, {useEffect, useState} from 'react';
import {getSharedLinks} from '../../../api/api';

const SharedLinkList = () => {
  const [data, setData] = useState([]);

  useEffect(() => {
    getSharedLinks().then((data) => setData(data));
  });

  return (
    <>
      <Box m={1} ml={2}>
        <Typography variant="h6">Share Endpoints</Typography>
      </Box>
      <Box m={1} border={1} borderColor="divider">
        <TableContainer>
          <Table>
            <TableHead>
              <TableRow>
                {Object.keys(data).map((v, i) => (
                  <TableCell key={i}>{v}</TableCell>
                ))}
              </TableRow>
            </TableHead>
            <TableBody>
              {Object.keys(data).map((v, i) => (
                <TableRow key={i}>
                  <TableCell>{v}</TableCell>
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
