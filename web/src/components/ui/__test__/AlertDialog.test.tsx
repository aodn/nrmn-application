import AlertDialog from '../AlertDialog';
import {render, fireEvent} from '@testing-library/react';
import {describe, it, test, expect} from '@jest/globals';
import '@testing-library/jest-dom';

const testLabel = 'Alert Sample Text';

describe('<AlertDialog/>', () => {
  it('does not render when not open', async () => {
    const dialog = render(<AlertDialog open={false} text={testLabel} />);
    expect(dialog.queryByText(testLabel)).toBeNull();
  });

  it('displays when open', async () => {
    const dialog = render(<AlertDialog open text={testLabel} />);
    expect(dialog.queryByText(testLabel)).toBeDefined();
  });

  test('onConfirm prop', (done) => {
    const submitButtonLabel = 'Submit Button Test Text';
    const dialog = render(<AlertDialog open action={submitButtonLabel} onConfirm={() => done()} />);
    fireEvent.click(dialog.getByText(submitButtonLabel));
  });

  test('onClose prop', (done) => {
    const dialog = render(<AlertDialog open action="" onClose={() => done()} />);
    fireEvent.click(dialog.getByText('Cancel'));
  });
});
