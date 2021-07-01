import React from 'react';
import {render} from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import App from './App';
import {Provider} from 'react-redux';
import store from './components/store';

test('Title is in ENV and in the page', () => {
  const {getAllByText} = render(
    <Provider store={store}>
      <App />
    </Provider>
  );
  const linkElement = getAllByText('National Reef Monitoring Network');
  expect(linkElement[0]).toBeInTheDocument();
});
