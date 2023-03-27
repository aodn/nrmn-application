import {LinearProgress, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography} from '@mui/material';
import {Box} from '@mui/system';
import SharedLinkAdd from './SharedLinkAdd';
import React, {useEffect, useReducer} from 'react';
import {getSharedLinks, deleteSharedLink} from '../../../api/api';
import TargetUrlComponent from '../../input/TargetUrlComponent';

const SharedLinkList = () => {
  const [data, setData] = useReducer(
    (state, action) => {
      switch (action.verb) {
        case 'disabled':
          return {...state, disabled: action.data};
        case 'reset':
          return {...state, sort: {key: 'created', asc: false}, reset: true};
        case 'data':
          return {...state, data: action.data, reset: false};
        case 'sort': {
          const sortedData = [...state.data];
          const asc = state.sort.key === action.key ? !state.sort.asc : true;
          sortedData.sort((a, b) => {
            if (a[action.key] < b[action.key]) return asc ? -1 : 1;
            if (a[action.key] > b[action.key]) return asc ? 1 : -1;
            return 0;
          });
          return {...state, data: [...sortedData], sort: {key: action.key, asc}};
        }
      }
    },
    {data: null, sort: {key: 'created', asc: false}, reset: true}
  );

  useEffect(() => {
    document.title = 'Endpoint Links';
    if (data.reset) {
      getSharedLinks().then((res) => setData({verb: 'data', data: res.data}));
    }
  }, [data, setData]);

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
      <Box m={1} ml={2}>
        <Typography variant="h6">Endpoint Links</Typography>
      </Box>
      <SharedLinkAdd onPost={() => setData({verb: 'reset'})} />
      <Box m={1} border={1} borderColor="divider" sx={{backgroundColor: 'white'}}>
        {!data.data ? (
          <LinearProgress />
        ) : (
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  {keys.map((key) => (
                    <TableCell style={{cursor: 'pointer'}} key={key} onClick={() => setData({verb: 'sort', key})}>
                      {columns[key]} {data.sort.key === key && (data.sort.asc ? '▲' : '▼')}
                    </TableCell>
                  ))}
                </TableRow>
              </TableHead>
              <TableBody>
                {data.data.length < 1 && (
                  <TableRow>
                    <TableCell colSpan={columns.length}>
                      <i>No Links</i>
                    </TableCell>
                  </TableRow>
                )}
                {data.data.map((row, i) => (
                  <TableRow key={`${row['linkId']}`}>
                    {keys.map((key) => {
                      const disabled = data.disabled === row[key];
                      return (
                        <TableCell key={`${key}-${i}`}>
                          {(key === 'targetUrl' && (disabled ? <></> : <TargetUrlComponent value={row[key]} />)) ||
                            (key === 'linkId' && (
                              <button
                                style={{width: '80px'}}
                                disabled={disabled}
                                onClick={() => {
                                  setData({verb: 'disabled', data: row[key]});
                                  deleteSharedLink(row[key]).then(() => setData({verb: 'reset'}));
                                }}
                              >
                                {disabled ? 'Deleting...' : 'Delete'}
                              </button>
                            )) ||
                            row[key]}
                        </TableCell>
                      );
                    })}
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        )}
      </Box>
    </>
  );
};

export default SharedLinkList;
