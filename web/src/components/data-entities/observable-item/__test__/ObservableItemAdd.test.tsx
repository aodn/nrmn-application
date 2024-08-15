// @ts-ignore
import React from 'react';
import '@testing-library/jest-dom';
import ObservableItemAdd from '../ObservableItemAdd';
import { render, waitFor, screen, fireEvent } from '@testing-library/react';
import * as axiosInstance from '../../../../api/api';
import { afterEach, beforeAll } from '@jest/globals';
import { AxiosResponse } from 'axios';
import userEvent from '@testing-library/user-event';
import {AuthContext} from '../../../../contexts/auth-context';
import { AppConstants } from '../../../../common/constants';
import { createMemoryHistory } from 'history';
import { Router } from 'react-router-dom';

const speciesList = require('./cannedData/ObservableItemAdd.species.json');
const taxonomyList = require('./cannedData/ObservableItemAdd.taxonomy.json');

describe('<ObservableItemAdd/>',  ()=>{
  let mockSearch: any;
  let mockGetResult: any;

  beforeAll(() => {
    mockSearch = jest.spyOn(axiosInstance, 'search');
    mockGetResult = jest.spyOn(axiosInstance,'getResult');
  });

  afterEach(() => {
    mockSearch.mockReset();
    mockGetResult.mockReset();
  });

  /**
   * When user add new observable items by doing a search WORM data, the species name will not fill in by default
   * and the box should show error, even the user click the save button, the server will check and return error
   * message
   */
  test('Renders error on missing ObservableItem name', async () => {

    mockGetResult.mockImplementation((url: string) => {
      const raw = {
        config: undefined,
        data: taxonomyList,
        headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
        status: 200,
        statusText: url
      };

      return new Promise<AxiosResponse>((resolve => {
        resolve(raw as any);
      }));
    });

    mockSearch.mockImplementation((url: string) => {

      const raw = {
        config: undefined,
        data: speciesList,
        headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
        status: 200,
        statusText: url
      };

      return new Promise<AxiosResponse>((resolve => {
        resolve(raw as any);
      }));
    });

    const history = createMemoryHistory({initialEntries:[{state: {resetFilters: true}}]});
    render(
      <Router location={history.location} navigator={history}>
        <AuthContext.Provider value={{auth : {roles: [AppConstants.ROLES.ADMIN]}}}>
          <ObservableItemAdd />
        </AuthContext.Provider>
      </Router>
    );

    /*
      The observable item add contains a component call <SpeciesSearch/>, we want to check the button rendered
      so that we can click it
     */
    const v = 'step';
    const searchField = screen.getByTestId('worm-search-field').querySelector('input');

    // This page include the WORM search component, this seach-button locates in that component
    const button = screen.getByTestId('search-button');

    await waitFor(() => expect(searchField),{ timeout: 10000})
      .then(() => {
        // Need 3 chars in order to do search
        // Trigger screen refresh by typing the string
        if(searchField) {
          userEvent.type(searchField, v);
        }
      });

    await waitFor(() => expect(searchField?.value).toBe(v), { timeout: 10000})
      .then(() => {
        fireEvent.click(button);
      });

    // After click the button, issue search then fill values on the text box, we target
    // a row with the species (Stephanauge bulbosa)
    const targetSpecies = 'Stephanauge bulbosa';
    await waitFor(() => expect(screen.getByText(targetSpecies)), { timeout: 10000})
      .then(() => {
        // The species appear because the rest function called.
        expect(mockSearch).toHaveBeenCalledTimes(1);
        // Now we can click on an item of the species where it is valid
        const cell = screen.getByText(targetSpecies);
        fireEvent.click(cell);
      });

    // After click, we expect alert box shown because the observable item name field will not fill by default
    await waitFor(() => expect(screen.getByTestId('alert-field-missing')), { timeout: 10000})
      .then(() => {
        const errorMessageBox = screen.getByTestId('observable-item-name-text').querySelector('p');
        expect(errorMessageBox).toHaveTextContent('Species Name Required.');
      });
  });
});