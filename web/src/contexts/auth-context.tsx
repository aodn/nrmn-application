import React from 'react';

interface Authenticate {
  auth: {
    features?: Array<string>,
    roles?: Array<string>,
  },
  setAuth?: React.Dispatch<Authenticate>;
}

export const AuthContext = React.createContext<Authenticate>({
  auth: {},
  setAuth: undefined
});
