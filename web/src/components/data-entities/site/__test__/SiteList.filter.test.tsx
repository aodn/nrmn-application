// @ts-ignore
import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import { render, waitFor, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import { describe, beforeAll, afterEach } from '@jest/globals';
import { Router } from 'react-router-dom';
import * as axiosInstance from '../../../../api/api';
import { AxiosResponse } from 'axios';
import SiteList from '../SiteList';
import { createMemoryHistory } from 'history';
import stateFilterHandler from '../../../../common/state-event-handler/StateFilterHandler';

jest.setTimeout(30000);

describe('<SiteList/> filter testing', () => {
  let mockGetEntity;
  let mockGetFiltersForId;
  let mockResetStateFilters;
  const columns = ['site.siteCode','site.siteName','site.locationName','site.state','site.country','site.latitude','site.longitude','site.isActive'];

  beforeAll(() => {
    mockGetEntity = jest.spyOn(axiosInstance, 'getResult');
    mockGetFiltersForId = jest.spyOn(stateFilterHandler, 'getFiltersForId');
    mockResetStateFilters = jest.spyOn(stateFilterHandler, 'resetStateFilters');

    // silence errors caused by not setting an AG Grid licence
    jest.spyOn(console, 'error').mockImplementation(() => {});
  });

  afterEach(() => {
    mockGetEntity.mockReset();
    mockGetFiltersForId.mockReset();
  });

  test('Render necessary fields and no filter restored', async () => {
    const canned = require('./SiteList.filter.data.json');

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
    const {container, rerender} = render(<Router location={history.location} navigator={history}><SiteList/></Router>);

    // Data loaded due to mock object being called once
    await waitFor(() => expect(mockGetEntity).toHaveBeenCalledTimes(1), {timeout: 10000})
      .then(() => {
        // verify default columns exist
        columns.forEach(x => {
          expect(container.querySelector('[col-id="' + x + '"]')).toBeInTheDocument();
        });
      })
      .finally(() => {
        // Refresh the dom tree
        rerender(<Router location={history.location} navigator={history}><SiteList/></Router>);

        expect(screen.getByText('Lhohaesf')).toBeInTheDocument();
        expect(screen.getByText('Silesv')).toBeInTheDocument();
        expect(screen.getByText('Janeng 2')).toBeInTheDocument();

        // Restore filter not called if you pass the resetFilter false to the component
        expect(mockGetFiltersForId).toBeCalledTimes(0);
        expect(mockResetStateFilters).toBeCalledTimes(1);
      });
  });

  test('Render necessary fields with filter restored', async () => {
    const canned = require('./SiteList.filter.data.json');

    // Filter set will cause some items disappeared
    mockGetFiltersForId.mockImplementation((id) => {
      return '{"site.siteName":{"filterType":"text","type":"contains","filter":"Channel"}}';
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
    const {container, rerender} = render(<Router location={history.location} navigator={history}><SiteList/></Router>);

    // Data loaded due to mock object being called once
    await waitFor(() => expect(mockGetEntity).toHaveBeenCalledTimes(1), {timeout: 10000})
      .then(() => {
        // verify default columns exist
        columns.forEach(x => {
          expect(container.querySelector('[col-id="' + x + '"]')).toBeInTheDocument();
        });
      })
      .finally(() => {
        // Refresh the dom tree
        rerender(<Router location={history.location} navigator={history}><SiteList/></Router>);

        // Filter is done on server site, we just need filter restore correctly
        expect(mockGetFiltersForId).toBeCalledTimes(1);
        expect(mockResetStateFilters).toBeCalledTimes(0);
      });
  });
});