import React from 'react';
import {render} from '@testing-library/react';
import {Provider} from 'react-redux';
import Login from './login';
import store from '../store';

jest.mock('react-router-dom', () => {
  return {
    useLocation: () => {
      return {pathname: ''};
    }
  };
});

describe('Login', () => {
  test('renders', () => {
    render(
      <Provider store={store}>
        <Login />
      </Provider>
    );

    const inputs = document.getElementsByTagName('input');
    expect(inputs.length).toBe(2);
  });
});
