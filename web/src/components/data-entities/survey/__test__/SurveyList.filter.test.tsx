// @ts-ignore
import React from 'react';
import '@testing-library/jest-dom';
import { render, waitFor, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import { describe, beforeAll, afterEach } from '@jest/globals';
import { Router } from 'react-router-dom';
import * as axiosInstance from '../../../../api/api';
import { AxiosResponse } from 'axios';
import SurveyList from '../SurveyList';
import { createMemoryHistory } from 'history';
import stateFilterHandler from '../../../../common/state-event-handler/StateFilterHandler';

jest.setTimeout(30000);

describe('<SurveyList/> filter testing', () => {
  let mockGetResult: any;
  let mockGetFiltersForId: any;
  let mockResetStateFilters: any;

  const columnIds = ['survey.surveyId', 'survey.surveyDate', 'survey.siteCode', 'survey.latitude',
    'survey.longitude', 'survey.siteName', 'survey.depth', 'survey.diverName', 'survey.method',
    'survey.programName', 'survey.locationName', 'survey.country', 'survey.state', 'survey.ecoregion',
    'survey.pqCatalogued'];

  beforeAll(() => {
    mockGetResult = jest.spyOn(axiosInstance, 'getResult');
    mockGetFiltersForId = jest.spyOn(stateFilterHandler, 'getFiltersForId');
    mockResetStateFilters = jest.spyOn(stateFilterHandler, 'resetStateFilters');

    // silence errors caused by not setting an AG Grid licence
    jest.spyOn(console, 'error').mockImplementation(() => {});
  });

  afterEach(() => {
    mockGetResult.mockReset();
    mockGetFiltersForId.mockReset();
  });

  test('Render necessary fields and no filter restored', async () => {
    const canned = require('./cannedData/SurveyList.filter.data.json');

    // Override function so that it return the data we set.
    mockGetResult.mockImplementation((url: string) => {

      const raw = {
        config: undefined,
        data: canned,
        headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
        status: 200,
        statusText: url
      };

      return new Promise<AxiosResponse>((resolve => {
        resolve(raw as any);
      }));
    });

    const history = createMemoryHistory({initialEntries:[{state: {resetFilters: true}}]});
    const {rerender, container} = render(<Router location={history.location} navigator={history}><SurveyList/></Router>);

    // Data loaded due to mock object being called once
    await waitFor(() => expect(mockGetResult).toHaveBeenCalledTimes(1), {timeout: 10000})
      .then(() => {
        // verify default columns exist
        columnIds.forEach(x => {
          expect(container.querySelector('[col-id="' + x + '"]')).toBeInTheDocument();
        });
      })
      .finally(() => {
        // Refresh the dom tree
        rerender(<Router location={history.location} navigator={history}><SurveyList/></Router>);

        expect(screen.getByText('Apple')).toBeInTheDocument();
        expect(screen.getByText('Melon')).toBeInTheDocument();
        expect(screen.getByText('Orange')).toBeInTheDocument();

        // Restore filter not called if you pass the resetFilter false to the component
        expect(mockGetFiltersForId).toBeCalledTimes(0);
        expect(mockResetStateFilters).toBeCalledTimes(1);
      });
  });

  test('Render necessary fields with filter restored', async () => {
    const canned = require('./cannedData/SurveyList.filter.data.json');

    // Filter set will cause some items disappeared
    mockGetFiltersForId.mockImplementation((id: string) => {
      return '{"survey.siteName":{"filterType":"text","type":"contains","filter":"South East Apple"}}';
    });

    let requestURL = '';
    // Override function so that it return the data we set.
    mockGetResult.mockImplementation((url: string) => {
      requestURL = url;

      const raw = {
        config: undefined,
        data: canned,
        headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
        status: 200,
        statusText: url
      };

      return new Promise<AxiosResponse>((resolve => {
        resolve(raw as any);
      }));
    });

    const history = createMemoryHistory({initialEntries:[{state: {resetFilters: false}}]});
    const {rerender, container} = render(<Router location={history.location} navigator={history}><SurveyList/></Router>);

    // Data loaded due to mock object being called once
    await waitFor(() => expect(mockGetResult).toHaveBeenCalledTimes(1), {timeout: 10000})
      .then(() => {
        // verify default columns exist
        columnIds.forEach(x => {
          expect(container.querySelector('[col-id="' + x + '"]')).toBeInTheDocument();
        });
      })
      .finally(() => {
        // Refresh the dom tree
        rerender(<Router location={history.location} navigator={history}><SurveyList/></Router>);

        // Restore filter called
        expect(mockGetFiltersForId).toBeCalledTimes(1);
        expect(mockResetStateFilters).toBeCalledTimes(0);
      });

    // Once filter applied, ag grid will trigger another load.
    await waitFor(() => expect(mockGetResult).toHaveBeenCalledTimes(2), {timeout: 10000})
      .then(() => {
        // With the filter operation moved to backend, there is no need to validate filter values
        // just need to make sure it do send filter in the url
        expect(requestURL).toEqual(expect.stringContaining('&filters='));
      });
  });
});
