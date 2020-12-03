import React from 'react';
import { render } from '@testing-library/react';
import App from './App';
import { Provider } from 'react-redux';
import store from './components/store';

test('Title is in ENV and in the page', () => {
  const { getByText } = render(
      <Provider store={store}>
        <App />
      </Provider>);
  const regex = new RegExp(`${process.env.REACT_APP_SITE_TITLE}`, 'i');
  const linkElement = getByText(regex);
  expect(linkElement).toBeInTheDocument();
});
