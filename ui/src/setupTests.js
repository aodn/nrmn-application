import '@testing-library/jest-dom/extend-expect';
import React from 'react';
import { render as rtlRender } from '@testing-library/react';
import { createStore } from 'redux';
import { Provider } from 'react-redux';
import store  from './components/store';

function render(
    ui,
    {
      initialState,
      //store = createStore(store, initialState),
      ...renderOptions
    } = {}
) {

  var Wrapper = ({ children }) => {
    return <Provider store={store}>{children}</Provider>;
  };
  return rtlRender(ui, { wrapper: Wrapper, ...renderOptions });
}

export * from '@testing-library/react';
export { render };

