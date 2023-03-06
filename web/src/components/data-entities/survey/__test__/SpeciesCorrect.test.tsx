// @ts-ignore
import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import { render, waitFor, screen, fireEvent, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import '@testing-library/jest-dom';
import { describe, beforeAll, afterEach } from '@jest/globals';
import { Router } from 'react-router-dom';
import * as axiosInstance from '../../../../api/api';
import { AxiosResponse } from 'axios';
import SpeciesCorrect from '../SpeciesCorrect';
import { createMemoryHistory } from 'history';

jest.setTimeout(30000);

describe('<SpeciesCorrect/> testing', () => {

  let mockSearchSpeciesSummary;
  let mockSearch;
  let mockGetEntity;

  beforeAll(() => {
    mockSearchSpeciesSummary = jest.spyOn(axiosInstance, 'searchSpeciesSummary');
    mockSearch = jest.spyOn(axiosInstance, 'search');
    mockGetEntity = jest.spyOn(axiosInstance,'getEntity');

    // silence errors caused by not setting an AG Grid licence
    jest.spyOn(console, 'error').mockImplementation(() => {});
  });

  afterEach(() => {
    mockSearchSpeciesSummary.mockReset();
  });

  test('UI show error on end of flow when backend report error', async () => {
    const canned1 = require('./SpeciesCorrect.specieslocation.json');
    const canned2 = require('./SpeciesCorrect.speciesdata.json');

    // Override function so that it return the data we set.
    mockGetEntity.mockImplementation((url) => {
      const raw = {
        config: undefined,
        data: canned1,
        headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
        status: 200,
        statusText: url
      };

      return new Promise<AxiosResponse>((resolve => {
        resolve(raw);
      }));
    });

    mockSearch.mockImplementation((url) => {
      const raw = {
        config: undefined,
        data: canned2,
        headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
        status: 200,
        statusText: url
      };

      return new Promise<AxiosResponse>((resolve => {
        resolve(raw);
      }));
    });

    const history = createMemoryHistory({initialEntries:[{state: {resetFilters: true}}]});
    const {rerender, container} = render(<Router location={history.location} navigator={history}><SpeciesCorrect/></Router>);

    let autocomplete;
    let input;

    await waitFor(() => expect(screen.getByTestId('species-correction-box')).toBeInTheDocument(), {timeout: 10000})
      .then(() => {
        autocomplete = screen.getByTestId('species-correction-box');
        input = within(autocomplete).getByRole('combobox');
        autocomplete.focus();

        // The search button is enabled if you fill in the value in species box
        // which will cause even fire on the auto complete
        fireEvent.keyUp(input, { target: {value: 'Notol'}});
      });

    await waitFor(() => expect(mockSearch).toHaveBeenCalledTimes(1), {timeout: 10000})
      // Make sure the option list contains our target
      .then(() => {
        // Trigger screen refresh by typing part of the string
        userEvent.type(input, 'Notola');
      })
      .then(() => waitFor(() => expect(screen.getByText('Notolabrus tetricus/fucicola hybrid')).toBeInTheDocument()))
      .then(() => {
        // Now the options loaded with our target value at position 1, we select it by arrow down and enter
        fireEvent.keyDown(autocomplete, { key: 'ArrowDown' });
        fireEvent.keyDown(autocomplete, { key: 'Enter' });
      });
  });
});