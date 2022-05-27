// @ts-ignore
import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import { render, waitFor, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import { describe, beforeAll, afterEach } from '@jest/globals';
import { Router } from 'react-router-dom';
import * as axiosInstance from '../../../../api/api';
import { AxiosResponse } from 'axios';
import LocationList from '../LocationList';
import { createMemoryHistory } from 'history';
import stateFilterHandler from '../../../../common/state-event-handler/StateFilterHandler';

describe('<LocationList/>', () => {
  let mockGetEntity;
  let mockGetFiltersForId;
  const columns = ['Location Name','Status','Eco Regions','Countries','Areas'];

  beforeAll(() => {
    mockGetEntity = jest.spyOn(axiosInstance, 'getEntity');
    mockGetFiltersForId = jest.spyOn(stateFilterHandler, 'getFiltersForId');

    // silence errors caused by not setting an AG Grid licence
    jest.spyOn(console, 'error').mockImplementation(() => {});
  });

  afterEach(() => {
    mockGetEntity.mockReset();
    mockGetFiltersForId.mockReset();
  });

  test('Render necessary fields and no filter restored', async () => {
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
    const {rerender} = render(<Router location={history.location} navigator={history}><LocationList/></Router>);

    // Data loaded due to mock object being called once
    await waitFor(() => expect(mockGetEntity).toHaveBeenCalledTimes(1), {timeout: 10000})
      .then(() => {
        // verify default columns exist
        columns.forEach(x => {
          expect(screen.queryAllByText(x).length).toBeGreaterThanOrEqual(1);
        });
      })
      .finally(() => {
        // Refresh the dom tree
        rerender(<Router location={history.location} navigator={history}><LocationList/></Router>);

        expect(screen.getByText('Antarctica')).toBeInTheDocument();
        expect(screen.getByText('Interest Bay')).toBeInTheDocument();
        expect(screen.getByText('Argentinian Gulfs')).toBeInTheDocument();

        // Restore filter not called if you pass the resetFilter false to the component
        expect(mockGetFiltersForId).toBeCalledTimes(0);
      });
  });

  test('Render necessary fields with filter restored', async () => {
    const canned = require('./sample1.json');

    // Filter set will cause some items disappeared
    mockGetFiltersForId.mockImplementation((id) => {
      return '{"locationName":{"filterType":"text","type":"contains","filter":"Interest Bay"}}';
    });

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

    const history = createMemoryHistory({initialEntries:[{state: {resetFilters: false}}]});
    const {rerender} = render(<Router location={history.location} navigator={history}><LocationList/></Router>);

    // Data loaded due to mock object being called once
    await waitFor(() => expect(mockGetEntity).toHaveBeenCalledTimes(1), {timeout: 10000})
      .then(() => {
        // verify default columns exist
        columns.forEach(x => {
          expect(screen.queryAllByText(x).length).toBeGreaterThanOrEqual(1);
        });
      })
      .finally(() => {
        // Refresh the dom tree
        rerender(<Router location={history.location} navigator={history}><LocationList/></Router>);

        // Restore filter called
        expect(mockGetFiltersForId).toBeCalledTimes(1);

        expect(screen.getByText('Interest Bay')).toBeInTheDocument();
        screen.findByText('Antarctica').then(i => expect(i).toBe({}));
        screen.findByText('Argentinian Gulfs').then(i => expect(i).toBe({}));
      });
  });
});