const CracoEslintWebpackPlugin = require('craco-eslint-webpack-plugin');

module.exports = {
  plugins: [
    {
      plugin: CracoEslintWebpackPlugin,
      options: {
        // See the options description below
        skipPreflightCheck: true,
        eslintOptions: {
          files: 'src/**/*.{js,jsx,ts,tsx}',
          lintDirtyModulesOnly: false,
          failOnWarning: true
          // ...
        }
      }
    }
  ]
};
