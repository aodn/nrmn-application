#!/usr/bin/env bash

# builds the API and webapp

WEBAPP_DIR=./nrmn-ui
API_DIR=./api

# set caching folder
#yarn config set cache-folder /home/builder/.cache/yarn

# install node packages
yarn --cwd $WEBAPP_DIR install

# build storybook
yarn --cwd $WEBAPP_DIR ci-build-storybook

# build the react app
yarn --cwd $WEBAPP_DIR test-build

# build the api
#mvn -f $API_DIR clean package
