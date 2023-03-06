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
    mockSearch.mockReset();
    mockGetEntity.mockReset();
  });

  test('UI show error on end of flow when backend report error', async () => {
    const locationList = require('./SpeciesCorrect.speciesLocation.json');
    const speciesNotolabrusList = require('./SpeciesCorrect.speciesNotolabrus.json');
    const speciesNotolabrusTetricusResult = require('./SpeciesCorrect.speciesNotolabrusTetricusResult.json');

    // Override function so that it return the data we set.
    mockGetEntity.mockImplementation((url) => {
      const raw = {
        config: undefined,
        data: locationList,
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
        data: speciesNotolabrusList,
        headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
        status: 200,
        statusText: url
      };

      return new Promise<AxiosResponse>((resolve => {
        resolve(raw);
      }));
    });

    mockSearchSpeciesSummary.mockImplementation((url) => {
      const raw = {
        config: undefined,
        data: speciesNotolabrusTetricusResult,
        headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
        status: 200,
        statusText: url
      };

      return new Promise<AxiosResponse>((resolve => {
        resolve(raw);
      }));
    });

    const history = createMemoryHistory({initialEntries:[{state: {resetFilters: true}}]});
    render(<Router location={history.location} navigator={history}><SpeciesCorrect/></Router>);

    let correctFromAutoComplete;
    let correctFromInput;

    const targetSpeciesName = 'Notolabrus tetricus/fucicola hybrid';

    await waitFor(() => expect(screen.getByTestId('species-correction-from')).toBeInTheDocument(), {timeout: 10000})
      .then(() => {
        correctFromAutoComplete = screen.getByTestId('species-correction-from');
        correctFromInput = within(correctFromAutoComplete).getByRole('combobox');
        correctFromAutoComplete.focus();

        // The search button is enabled if you fill in the value in species box
        // which will cause even fire on the auto complete
        fireEvent.keyUp(correctFromInput, { target: {value: 'Notol'}});
      });

    await waitFor(() => expect(mockSearch).toHaveBeenCalledTimes(1), {timeout: 10000})
      // Make sure the option list contains our target
      .then(() => {
        // Trigger screen refresh by typing the string
        userEvent.type(correctFromInput, 'Notol');
      });

    await waitFor(() => expect(screen.getByText(targetSpeciesName)).toBeInTheDocument())
      .then(() => {
        // The search disabled by default
        expect(screen.getByTestId('species-correction-search-button')).toBeDisabled();

        // Now the options loaded with our target value at position 1, we select it by arrow down and enter
        correctFromAutoComplete.focus();
        fireEvent.keyDown(correctFromAutoComplete, { key: 'ArrowDown' });
        fireEvent.keyDown(correctFromAutoComplete, { key: 'Enter' });

        expect(correctFromInput.value).toEqual(targetSpeciesName);
      });

    // The search button enabled by now
    await waitFor(() => expect(screen.getByTestId('species-correction-search-button')).not.toBeDisabled(), { timeout: 10000})
      .then(() => {
        fireEvent.click(screen.getByTestId('species-correction-search-button'));
      });

    // Search result loaded and the common name of species appear
    let correctToAutoComplete;
    let correctToInput;

    await waitFor(() => expect(mockSearchSpeciesSummary).toHaveBeenCalledTimes(1), { timeout: 10000})
      .then(() => {
        expect(screen.getByText('Blue-throat/Purple wrasse hybrid')).toBeInTheDocument();
        userEvent.click(screen.getByText('Blue-throat/Purple wrasse hybrid').closest('tr'));
      });

    await waitFor(() => expect(screen.getByTestId('species-correction-to')).toBeInTheDocument(), { timeout: 10000})
      .then(() => {
        correctToAutoComplete = screen.getByTestId('species-correction-to');
        correctToInput = within(correctToAutoComplete).getByRole('combobox');
        correctToAutoComplete.focus();

        // The search button is enabled if you fill in the value in species box
        // which will cause even fire on the auto complete
        fireEvent.keyUp(correctToInput, { target: {value: 'Notol'}});
      });

    await waitFor(() => expect(mockSearch).toHaveBeenCalledTimes(2), {timeout: 10000})
      // Make sure the option list contains our target
      .then(() => {
        // Trigger screen refresh by typing the string
        userEvent.type(correctToInput, 'Notol');
      });

    const correctToTargetSpecies = 'Notolabrus tetricus';
    await waitFor(() => expect(screen.getByText(correctToTargetSpecies)).toBeInTheDocument())
      .then(() => {
        // Now the options loaded with our target value at position 2, we select it by arrow down x2 and enter
        correctToAutoComplete.focus();
        fireEvent.keyDown(correctToAutoComplete, { key: 'ArrowDown' });
        fireEvent.keyDown(correctToAutoComplete, { key: 'ArrowDown' });
        fireEvent.keyDown(correctToAutoComplete, { key: 'Enter' });

        // Now we have selected the species that we want to change to.
        expect(correctToInput.value).toEqual(correctToTargetSpecies);
      });

  });
});