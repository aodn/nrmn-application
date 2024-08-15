import React from 'react';

interface Auth {
  features?: Array<string>,
  roles?: Array<string>,
}

interface AuthContextType {
  auth: Auth,
  setAuth?: React.Dispatch<React.SetStateAction<Auth>>;
}

export const AuthContext = React.createContext<AuthContextType>({
  auth: {},
  setAuth: undefined
});
