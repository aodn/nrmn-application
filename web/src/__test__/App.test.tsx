// @ts-ignore
import React from 'react';
import {render} from '@testing-library/react';
import {describe, test, expect} from '@jest/globals';
import App from '../App';

describe('<App/>', () => {
  test('Title is in ENV and in the page', () => {
    const {getAllByText} = render(<App />);
    const linkElement = getAllByText('National Reef Monitoring Network');
    expect(linkElement[0]);
  });
});
