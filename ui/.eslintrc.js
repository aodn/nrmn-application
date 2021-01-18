const currentEnv = process.env.NODE_ENV;
 const onlyDuringDev =  (currentEnv == 'development') ? 0 : 2
module.exports = exports = {
    "parser": "babel-eslint",
    "env": {
        "node": true,
        "jest": true,
        "es6": true,
        "browser": true
    },
    "extends": [
        "eslint:recommended",
        "plugin:react/recommended",
        "prettier"
    ],
    "globals": {
        "Atomics": "readonly",
        "SharedArrayBuffer": "readonly",
        "ReactClass": false,
        "axios" : true
    },
    "parserOptions": {
        "ecmaFeatures": {
            "jsx": true
        },
        "ecmaVersion": 2018,
        "sourceType": "module"
    },
    "plugins": [
        "react",
        "react-hooks",
        "no-console-log"
    ],
    "settings": {
        "react": {
            "createClass": "createReactClass",
            "pragma": "React",
            "version": "detect"
        }
    },
    "rules": {
        "no-process-env": 0,
        "react/prop-types": [
            2
        ],
        "react/function-component-definition": [
            2,
            {
                "namedComponents": "arrow-function"
            }
        ],
        "react-hooks/rules-of-hooks": 2,
        "react-hooks/exhaustive-deps": 0,
        "semi": [
            2,
            "always"
        ],
        "quotes": [
            2,
            "single",
            {
                "avoidEscape": true,
                "allowTemplateLiterals": true
            }
        ],
        "no-console-log/no-console-log": onlyDuringDev,
        "no-empty": 2,
        "no-trailing-spaces": 2,
        "no-unused-vars": 0,
        "no-alert": 2,
        "no-debugger": onlyDuringDev
    }
}
