// @ts-ignore
import React from 'react';
import { render, waitFor, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import {describe} from '@jest/globals';
import {BrowserRouter} from 'react-router-dom';
import * as axiosInstance from '../../../api/api';
import {AxiosResponse} from 'axios';
import DataSheetView from '../DataSheetView';

describe('<DataSheetView/>', () => {

  let mockGetDataJob;

  beforeAll(() => {
    mockGetDataJob = jest.spyOn(axiosInstance, 'getDataJob');
  });

  afterEach(() => {
    mockGetDataJob.mockRestore();
  });

  test('Non extend data column correct', async () => {
    const canned = require('./job16.json');
    const ingest = (res) => {};

    // Override function so that it return the data we set.
    mockGetDataJob.mockImplementation((url) => {
      console.log('Load json');
      const raw = {
        config: undefined,
        data: canned,
        headers: {'Content-Type': 'application/json', 'Accept': 'application/json'},
        status: 200,
        statusText: url
      };

      return new Promise<AxiosResponse>((resolve => {
        resolve(raw);
      }));
    });

    // Need to wrap with a Router otherwise the useLocation() error shows, the result will auto set to screen object
    const {rerender} = render(<BrowserRouter><DataSheetView onIngest={i => ingest(i)} isAdmin={false}/></BrowserRouter>);

    const columns = [
      'ID','Diver','Buddy','Site No.','Site Name','Latitude','Longitude','Date','Vis','Direction','Time',
      'P-Qs','Depth','Method','Block','Code','Species','Common Name','Total','Inverts','2.5','5',
      '7.5','10','12.5','15','20','25','30','35','40','50','62.5','75','87.5','100','112.5','125','137.5','150','162.5','175','187.5','200','250','300','350','400'];

    const extendedColumns = ['450','500','550','600','650','700','750','800','850','900','950','1000','isInvertSizing'];

    await waitFor(() => screen.findByText('user_noextend.xlsx'))
      .then(() => {

        // verify columns exist
        columns.forEach(x => {
          console.log('Verify column: ' + x);
          expect(screen.queryAllByText(x).length).toBeGreaterThanOrEqual(1);
        });

        // non extend job and hence you will not have the following column
        extendedColumns.forEach(x => {
          console.log('Verify column: ' + x);
          expect(screen.queryAllByText(x).length).toEqual(0);
        });
      })
      .finally(() => {
        // Data loaded, need refresh to trigger HTML update
        rerender(<BrowserRouter><DataSheetView onIngest={i => ingest(i)} isAdmin={false}/></BrowserRouter>);

        expect(screen.getByText('Shell substrate')).toBeInTheDocument();
        expect(screen.getByText('Blacklip abalone')).toBeInTheDocument();
        expect(screen.getByText('Strapweed')).toBeInTheDocument();
      });
  });
});