/* eslint-disable no-unused-expressions */

import React from 'react';
import { Route } from 'react-router-dom';
import {renderWithProviders} from '../utils/test-utils';
import '@testing-library/jest-dom/extend-expect';
import config from 'react-global-configuration';
import {useSelector} from 'react-redux';
import EntityList from './EntityList';

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
jest.mock('ag-grid-react/lib/agGridReact');


const mockState = {
  theme: {themeType: false},
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


describe('EntityList Component', () => {

  beforeEach(() => {
    useSelector.mockImplementation(callback => {
      return callback(mockState);
    });
  });
  afterEach(() => {
    useSelector.mockClear();
  });

  test('Test EntityList.js exists', async () => {
    const {findByText} = renderWithProviders(
        <Route path="/list/:entityName">
          <EntityList />
        </Route>,
        {
          route: '/list/munt'
        }
    );
    await findByText("ERROR: Entity 'Munt' missing from API Schema");
  });


  test('Test EntityList.js New Entity button exists', async () => {

    const {findByTitle} = renderWithProviders(
        <Route path="/list/:entityName">
          <EntityList/>
        </Route>,
        {
          route: '/list/TestEntity'
        }
    );
    await findByTitle('New TestEntity');
  });

});



