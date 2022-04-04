import LoginForm from './LoginForm';
import {rest} from 'msw';
import {setupServer} from 'msw/node';
import {render, fireEvent, waitFor} from '@testing-library/react';
import '@testing-library/jest-dom';

const UNAUTHORIZED = 'Unauthorised';
const TEST_USERNAME = 'test@example.com';
const TEST_PASSWORD = 'abc123';

const server = setupServer(
  rest.post('/api/auth/signin', (req, res, ctx) => {
    if (req.body?.username === TEST_USERNAME && req.body?.password === TEST_PASSWORD)
      return res(
        ctx.json({
          accessToken:
            'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6InRlc3RAZXhhbXBsZS5jb20iLCJpYXQiOjE1MTYyMzkwMjJ9.Rw_GkXqDon4ytYdX7JTbBzORJUjRuZ7fJ02EoFqxF5g'
        })
      );
    else return res(ctx.json({error: UNAUTHORIZED}));
  })
);

beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

test('login success', async () => {
  const {getByPlaceholderText, getByText} = render(<LoginForm />);
  const username = getByPlaceholderText('Email');
  const password = getByPlaceholderText('Password');
  fireEvent.change(username, {target: {value: TEST_USERNAME}});
  fireEvent.change(password, {target: {value: TEST_PASSWORD}});
  fireEvent.click(getByText('Submit'));
  await waitFor(() => {
    expect(getByText(TEST_USERNAME)).toBeInTheDocument();
  });
});

test('login failure', async () => {
  const {getByText} = render(<LoginForm />);
  fireEvent.click(getByText('Submit'));
  await waitFor(() => {
    expect(getByText(UNAUTHORIZED)).toBeInTheDocument();
  });
});
