import '@testing-library/jest-dom/extend-expect';
import {render, screen} from '@testing-library/react';
import '@testing-library/jest-dom';
import {describe, beforeAll, afterEach} from '@jest/globals';
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
    onChangeFunction.mockImplementation((t) => (i = t));

    render(
      <div>
        <CustomAutoCompleteInput
          label={'test label'}
          options={cannedOptions}
          formData={null}
          field={'testField'}
          errors={null}
          onChange={onChangeFunction}
          warnLevelOnNewValue={ERROR_TYPE.WARNING}
        />
        <input />
      </div>
    );

    userEvent.click(screen.getByRole('combobox'));
    await userEvent.type(screen.getByRole('combobox'), 'item1');
    expect(screen.queryByText('New "test label" will be created')).toBeNull();
    await userEvent.tab();
    expect(onChangeFunction).toBeCalledTimes(1);
    expect(i === 'item1').toBeTruthy();
  });

  test('Input do not appears in options, created warning', async () => {
    let i;
    onChangeFunction.mockImplementation((t) => (i = t));

    render(
      <CustomAutoCompleteInput
        label={'test label'}
        options={cannedOptions}
        formData={null}
        field={'testField'}
        errors={null}
        onChange={onChangeFunction}
        warnLevelOnNewValue={ERROR_TYPE.WARNING}
      />
    );

    await userEvent.type(screen.getByRole('combobox'), 'item10');
    expect(screen.getByText('New "test label" will be created').textContent).toEqual('New "test label" will be created');
    await userEvent.tab();
    expect(onChangeFunction).toBeCalledTimes(1);
    expect(i === 'item10').toBeTruthy();
  });

  test('Input do not appears in options, no warning created due to warnLevelOnNewValue default value', async () => {
    let i;
    onChangeFunction.mockImplementation((t) => (i = t));

    render(
      <CustomAutoCompleteInput
        label={'test label'}
        options={cannedOptions}
        formData={null}
        field={'testField'}
        errors={null}
        onChange={onChangeFunction}
      />
    );

    await userEvent.type(screen.getByRole('combobox'), 'item11');
    expect(screen.queryByText('New "test label" will be created')).toBeNull();
    await userEvent.tab();
    expect(onChangeFunction).toBeCalledTimes(1);
    expect(i === 'item11').toBeTruthy();
  });
});
