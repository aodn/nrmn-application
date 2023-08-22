// @ts-ignore
import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import ObservableItemEdit from '../ObservableItemEdit';
import { render, waitFor, screen, fireEvent } from '@testing-library/react';
import * as axiosInstance from '../../../../api/api';
import { afterEach, beforeAll } from '@jest/globals';
import { AxiosResponse } from 'axios';
import userEvent from '@testing-library/user-event';
import {AuthContext} from '../../../../contexts/auth-context';
import { AppConstants } from '../../../../common/constants';
import { createMemoryHistory } from 'history';
import { Router } from 'react-router-dom';

const observableItem = require('./cannedData/ObservableItemEdit.item.json');
const taxonomyList = require('./cannedData/ObservableItemEdit.taxonomy.json');

describe('<ObservableItemEdit/>',  ()=>{
  let mockGetResult;
  let mockEntityEdit;

  beforeAll(() => {
    mockGetResult = jest.spyOn(axiosInstance,'getResult');
    mockEntityEdit = jest.spyOn(axiosInstance,'entityEdit');
  });

  afterEach(() => {
    mockGetResult.mockReset();
    mockEntityEdit.mockReset();
  });

  /**
   * Edit species name is not allowed after 72 hours, the page should display error correctly and
   * when all items correct after save, the errors should disappear.
   */
  test('Test update species name', async() => {
    mockGetResult.mockImplementation((url) => {
      const raw = {
        config: undefined,
        data: url.includes('taxonomyDetail') ? taxonomyList : observableItem,
        headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
        status: 200,
        statusText: url
      };

      return new Promise<AxiosResponse>((resolve => {
        resolve(raw);
      }));
    });

    mockEntityEdit.mockImplementation((url) => {
      const raw = {
        config: undefined,
        data: [{
          'entity':'au.org.aodn.nrmn.restapi.dto.observableitem.ObservableItemDto',
          'property':'observableItemName',
          'invalidValue':null,
          'message':'Species Name editing not allowed more than 72 hours after creation.'
        }],
        headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
        status: 400,
        statusText: url
      };

      return new Promise<AxiosResponse>((resolve => {
        resolve(raw);
      }));
    });

    const history = createMemoryHistory({ initialEntries: [{ state: { resetFilters: true } }] });
    render(
      <Router location={history.location} navigator={history}>
        <AuthContext.Provider value={{ auth: { roles: [AppConstants.ROLES.ADMIN] } }}>
          <ObservableItemEdit />
        </AuthContext.Provider>
      </Router>
    );

    // Wait the content update where species name is not empty
    const v = 'species not exist';
    const textField = screen.getByTestId('observable-item-name-text').querySelector('input');
    const button = screen.getByTestId('observable-item-save-btn');

    await waitFor(() => expect(textField.value).toBe('Pinnidae spp.'), { timeout: 10000})
      .then(() => {
        // Then we change to species name that isn't exit, mainly the update api call result in error.
        userEvent.type(textField,v);
      });

    await waitFor(() => expect(textField.value).toBe(v), { timeout: 10000})
      .then(() => {
        fireEvent.click(button);
      });

    await waitFor(() => expect(mockEntityEdit).toHaveBeenCalledTimes(1), { timeout: 10000})
      .then(() => {
        expect(screen.getByTestId('alert-field-error')).toBeInTheDocument();
      });
  });
});