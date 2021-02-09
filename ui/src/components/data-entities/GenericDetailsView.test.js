/* eslint-disable no-unused-expressions */

import React from 'react';
import GenericDetailsView from "./GenericDetailsView";
import { Route } from 'react-router-dom';
import {renderWithProviders} from '../utils/test-utils';
import '@testing-library/jest-dom/extend-expect';
import { screen } from '@testing-library/react';
import config from 'react-global-configuration';
import {useSelector} from 'react-redux';
import {waitForElement} from "@testing-library/dom";

const testSchema = {
  TestEntity: {
    required: [
      'password',
      'username'
    ],
    type: 'object',
    properties: {
      username: {
        type: 'string'
      },
      password: {
        type: 'string'
      }
    }
  }
};

config.set({api: testSchema});


const mockState = {
  form: {
    entities: [],
    editItem: {},
    entitySaved: false,
    errors: []
  }
};

jest.mock('react-redux', () => {
  const ActualReactRedux = require.requireActual('react-redux');
  return {
    ...ActualReactRedux,
    useSelector: jest.fn().mockImplementation(() => {
      return function(){ return {};};
    }),
    useDispatch: jest.fn().mockImplementation(() => {
      return function(){ return {};};
    }),
    useEffect: jest.fn().mockImplementation(() => {
      return function(){ return {};};
    }),
  };
});


describe('GenericForm.js Component', () => {

  beforeEach(() => {
    useSelector.mockImplementation(callback => {
      return callback(mockState);
    });
  });
  afterEach(() => {
    useSelector.mockClear();
  });

  test('Test GenericDetailsView header exists', async () => {
    const {queryByText} = renderWithProviders(
        <Route path="/edit/:entityName">
          <GenericDetailsView/>
        </Route>,
        {
          route: '/edit/TestEntity'
        }
    );
    await expect(queryByText('Details for TestEntity')).not.toBe(null);

  });


  test('Test there is no submit button', async () => {

    const {queryByText} = renderWithProviders(
        <Route path="/edit/:entityName">
          <GenericDetailsView/>
        </Route>,
        {
          route: '/edit/TestEntity'
        }
    );
    await expect(queryByText('Submit', { exact: false })).toBe(null);

  });

});



