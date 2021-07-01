const CracoEslintWebpackPlugin = require('craco-eslint-webpack-plugin');

module.exports = {
  plugins: [
    {
      plugin: CracoEslintWebpackPlugin,
      options: {
        skipPreflightCheck: true,
        eslintOptions: {
          files: 'src/**/*.{js,jsx,ts,tsx}',
          lintDirtyModulesOnly: false
        }
      }
    }
  ]
};
