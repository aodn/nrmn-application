// @ts-ignore
import React from 'react';
import { render, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { describe, beforeAll, afterEach } from '@jest/globals';
import { Router } from 'react-router-dom';
import * as axiosInstance from '../../../../api/api';
import { AxiosResponse } from 'axios';
import LocationList from '../LocationList';
import { createMemoryHistory } from 'history';
import '@testing-library/jest-dom/extend-expect';

describe('<LocationList/>', () => {
  let mockGetEntity: jest.SpyInstance<Promise<any>, [entity?: any]>;
  const columns = ['Location Name','Status','Eco Regions','Countries','Area'];

  beforeAll(() => {
    mockGetEntity = jest.spyOn(axiosInstance, 'getResult');
    // silence errors caused by not setting an AG Grid licence
    jest.spyOn(console, 'error').mockImplementation(() => {});
  });

  afterEach(() => {
    mockGetEntity.mockClear();
  });

  test('Render necessary fields', async () => {
    const canned = require('./sample1.json');

    // Override function so that it return the data we set.
    mockGetEntity.mockImplementation((url) => {
      const raw = {
        config: undefined,
        data: canned,
        headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
        status: 200,
        statusText: url
      };

      return new Promise<AxiosResponse>((resolve => {
        resolve(raw);
      }));
    });

    const history = createMemoryHistory({initialEntries:[{state: {resetFilters: true}}]});
    const {rerender, queryAllByText, findByText, getByText} = render(<Router location={history.location} navigator={history}><LocationList/></Router>);

    await waitFor(() => findByText('Locations'))
      .then(() => {
        // verify default columns exist
        columns.forEach(x => {
          expect(queryAllByText(x).length).toBeGreaterThanOrEqual(1);
        });
      })
      .finally(() => {
        rerender(<Router location={history.location} navigator={history}><LocationList/></Router>);

        expect(getByText('Antarctica')).toBeInTheDocument();
        expect(getByText('Interest Bay')).toBeInTheDocument();
        expect(getByText('Argentinian Gulfs')).toBeInTheDocument();
      });
  });
});