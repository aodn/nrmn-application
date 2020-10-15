#!/usr/bin/env bash

set -eu

skip_ui_yarn=false
skip_api_mvn=false

while [[ $# -gt 0 ]]
do
key="$1"

case $key in
    --skip-ui-yarn-test)
    skip_ui_yarn=true
    shift
    ;;
    --skip-api-mvn-test)
    skip_api_mvn=true
    shift
    ;;
esac
done
# builds the API and webapp

WEBAPP_DIR=./nrmn-ui
API_DIR=./api
NETWORK_TIMEOUT=1000000

# set caching folder
#yarn config set cache-folder /home/builder/.cache/yarn

# install node packages
yarn --cwd $WEBAPP_DIR --network-timeout $NETWORK_TIMEOUT --ignore-optional install

# build storybook
yarn --cwd $WEBAPP_DIR ci-build-storybook

# build the react app
export REACT_APP_VERSION=${BUILD_TAG-"no version"}
if "$skip_ui_yarn"; then
  echo "Skipping yarn ui tests"
  yarn --cwd $WEBAPP_DIR build
else
  echo "Building version" $REACT_APP_VERSION
  yarn --cwd $WEBAPP_DIR test-build
fi

# build the api
if "$skip_api_mvn"; then
  echo "Skipping Maven API tests"
  mvn -f $API_DIR -B clean package -DskipTests
else
  mvn -f $API_DIR -B clean package
fi
