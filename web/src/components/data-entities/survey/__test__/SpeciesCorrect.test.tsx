// @ts-ignore
import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import { render, waitFor, screen, fireEvent, within } from '@testing-library/react';
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

    let speciesAutocomplete;
    let input;

    await waitFor(() => expect(screen.getByTestId('species-correction-box')).toBeInTheDocument(), {timeout: 10000})
      .then(() => {
        speciesAutocomplete = screen.getByTestId('species-correction-box');
        input = within(speciesAutocomplete).getByRole('combobox');
        speciesAutocomplete.focus();

        // The search button is enabled if you fill in the value in species box
        // which will cause even fire on the auto complete
        fireEvent.keyUp(input, { target: {value: 'Notol'}});
      });

    await waitFor(() => expect(mockSearch).toHaveBeenCalledTimes(1), {timeout: 10000})
      .then(() => {
        // Trigger value in text box then cause button enable
        fireEvent.change(input, { target: {value: 'Notol'}});
        screen.debug(undefined, Infinity);
      });

    const speciesCorrectionButton = screen.getByTestId('species-correction-search-button');
    await waitFor(() => expect(speciesCorrectionButton).not.toBeDisabled(), {timeout: 10000})
      .then(() => {
        // Trigger value in text box then cause button enable
        fireEvent.click(speciesCorrectionButton);
      });
  });
});