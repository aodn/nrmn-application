// @ts-ignore
import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import { render, waitFor, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import { describe, beforeAll, afterEach } from '@jest/globals';
import userEvent from '@testing-library/user-event';
import CustomAutoCompleteInput, {ERROR_TYPE} from '../CustomAutoCompleteInput';

jest.setTimeout(10000);

describe('<CustomAutoCompleteInput/> options behavior', () => {

  let onChangeFunction;

  const cannedOptions = ['item1', 'item2', 'item3'];

  beforeAll(() => {
    onChangeFunction = jest.fn();
  });

  afterEach(() => {
    onChangeFunction.mockReset();
  });

  test('Input appears in options, no warning created', async () => {
    let i;
    onChangeFunction.mockImplementation((t) => i = t);

    const {rerender} = render(<CustomAutoCompleteInput
      label={'test label'}
      options={cannedOptions}
      formData={null}
      field={'testField'}
      errors={null}
      onChange={onChangeFunction}
      warnLevelOnNewValue={ERROR_TYPE.WARNING}
    />);

    userEvent.type(screen.getByRole('combobox'), 'item1');
    expect(screen.queryByText('New "test label" will be created')).toBeNull();

    // Pretended you move away from the text box by clicking the label hence onBlur trigger
    userEvent.click(screen.getByText('test label'));
    expect(onChangeFunction).toBeCalledTimes(1);
    expect(i === 'item1').toBeTruthy();
  });

  test('Input do not appears in options, created warning', async () => {
    let i;
    onChangeFunction.mockImplementation((t) => i = t);

    const {rerender} = render(<CustomAutoCompleteInput
      label={'test label'}
      options={cannedOptions}
      formData={null}
      field={'testField'}
      errors={null}
      onChange={onChangeFunction}
      warnLevelOnNewValue={ERROR_TYPE.WARNING}
    />);

    userEvent.type(screen.getByRole('combobox'), 'item10');
    expect(screen.getByText('New "test label" will be created').textContent)
      .toEqual('New "test label" will be created');

    // Pretended you move away from the text box by clicking the label hence onBlur trigger
    userEvent.click(screen.getByText('test label'));
    expect(onChangeFunction).toBeCalledTimes(1);
    expect(i === 'item10').toBeTruthy();
  });

  test('Input do not appears in options, no warning created due to default options', async () => {
    let i;
    onChangeFunction.mockImplementation((t) => i = t);

    const {rerender} = render(<CustomAutoCompleteInput
      label={'test label'}
      options={cannedOptions}
      formData={null}
      field={'testField'}
      errors={null}
      onChange={onChangeFunction}
    />);

    userEvent.type(screen.getByRole('combobox'), 'item11');
    expect(screen.queryByText('New "test label" will be created')).toBeNull();

    // Pretended you move away from the text box by clicking the label hence onBlur trigger
    userEvent.click(screen.getByText('test label'));
    expect(onChangeFunction).toBeCalledTimes(1);
    expect(i === 'item11').toBeTruthy();
  });
});