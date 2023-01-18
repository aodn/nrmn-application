import {LinearProgress, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography} from '@mui/material';
import {Box} from '@mui/system';
import SharedLinkAdd from './SharedLinkAdd';
import React, {useEffect, useReducer} from 'react';
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
  const [data, setData] = useReducer(
    (state, action) => {
      switch (action.verb) {
        case 'reset':
          return {data: null, sort: {key: null, asc: true}};
        case 'data':
          return {...state, data: action.data};
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
    {data: null, sort: {key: null, asc: false}}
  );

  useEffect(() => {
    if (data.data) return;
    getSharedLinks().then((res) => setData({verb: 'data', data: res.data}));
  }, [data, setData]);

  const header = (
    <Box m={1} ml={2}>
      <Typography variant="h6">Endpoint Links</Typography>
    </Box>
  );

  if (!data.data) {
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
      <SharedLinkAdd onPost={() => setData({verb: 'reset'})} />
      <Box m={1} border={1} borderColor="divider">
        <TableContainer>
          <Table>
            <TableHead>
              <TableRow>
                {keys.map((key) => (
                  <TableCell style={{cursor: 'pointer'}} key={key} onClick={() => setData({verb: 'sort', key})}>
                    {columns[key]} {data.sort.key === key && (data.sort.asc ? '▼' : '▲')}
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
                <TableRow key={`${row['publicId']}`}>
                  {keys.map((key) => (
                    <TableCell key={`${key}-${i}`}>
                      {(key === 'targetUrl' && <TargetUrlComponent value={row[key]} />) ||
                        (key === 'linkId' && (
                          <button onClick={() => deleteSharedLink(row[key]).then(() => setData({verb: 'reset'}))}>Delete</button>
                        )) ||
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
