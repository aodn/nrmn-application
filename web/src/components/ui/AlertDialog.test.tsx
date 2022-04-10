import React from 'react'
import AlertDialog from './AlertDialog';
import { render } from "@testing-library/react";
import { test, expect } from '@jest/globals';
import '@testing-library/jest-dom'

test('loads and displays greeting', async () => {
    const dialog = render(<AlertDialog text="text 123" />);
    expect(dialog.findAllByText("text 123")).toBeTruthy();
});
