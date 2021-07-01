module.exports = exports = {
  parser: 'babel-eslint',
  env: {
    node: true,
    jest: true,
    es6: true,
    browser: true
  },
  extends: ['eslint:recommended', 'plugin:react/recommended', 'prettier'],
  globals: {
    Atomics: 'readonly',
    SharedArrayBuffer: 'readonly',
    ReactClass: false,
    axios: true
  },
  parserOptions: {
    ecmaFeatures: {
      jsx: true
    },
    ecmaVersion: 2018,
    sourceType: 'module'
  },
  plugins: ['react', 'react-hooks', 'no-console-log'],
  settings: {
    react: {
      createClass: 'createReactClass',
      pragma: 'React',
      version: 'detect'
    }
  },
  rules: {
    'no-process-env': 'off',
    'react/prop-types': ['error'],
    'react/function-component-definition': [
      'error',
      {
        namedComponents: 'arrow-function'
      }
    ],
    'react/prefer-stateless-function': ['error', {ignorePureComponents: true}],
    'react-hooks/rules-of-hooks': 'error',
    'react-hooks/exhaustive-deps': 'warn',
    semi: ['error', 'always'],
    quotes: [
      'error',
      'single',
      {
        avoidEscape: true,
        allowTemplateLiterals: true
      }
    ],
    'no-console-log/no-console-log': 'warn',
    'no-empty': 'warn',
    'no-trailing-spaces': 'warn',
    'no-unused-vars': 'warn',
    'no-alert': 'error',
    'no-debugger': 'warn'
  }
};
