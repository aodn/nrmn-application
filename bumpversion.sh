#!/usr/bin/env bash

set -eux

RELEASE_BRANCH=master

_get_tagged_version() {
  # retrieve highest tag version
  version=$(git tag -l '*.*.*' --sort=-version:refname | head -n 1)
  echo "${version}"
}

_get_bumped_version() {
  local version="$1"; shift
  bump2version patch --current-version ${version} --allow-dirty --dry-run --list | grep -oP '^new_version=\K.*$'
}

_set_maven_version() {
  local version="$1"; shift
  # use Maven versions plugin to bump version
  mvn versions:set -DnewVersion="${version}" versions:commit
}

_update_git() {
  local version=$1; shift
  git add pom.xml '*/pom.xml'
  git commit -m "Bump version to ${version}"
  git tag -a -f -m "Bump version to ${version}" ${version}
  git push origin tag "${version}"
}

release() {
 git fetch --prune origin "+refs/tags/*:refs/tags/*"
 local OLD_VERSION=$(_get_tagged_version)
 local NEW_VERSION=$(_get_bumped_version "${OLD_VERSION}")

 _set_maven_version "${NEW_VERSION}"
 _update_git "${NEW_VERSION}"
}

main() {
  release

  exit 0
}

main "$@"
