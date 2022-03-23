#!/usr/bin/env bash

set -euxo pipefail

main() {
  git fetch --prune origin "+refs/tags/*:refs/tags/*"
  OLD_VERSION=$(git tag -l '*.*.*' --sort=-version:refname | head -n 1)
  BUMP_OUTPUT=$(bump2version --current-version $OLD_VERSION --list --tag --commit  --allow-dirty patch)
  echo BUMP_OUTPUT: $BUMP_OUTPUT
  NEW_VERSION=$(echo $BUMP_OUTPUT | grep -oP 'new_version=\K.*$')
  echo NEW_VERSION: $NEW_VERSION
  git push origin tag $NEW_VERSION

  exit 0
}

main "$@"
