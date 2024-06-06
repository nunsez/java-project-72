#!/bin/sh

set -eux

cd -P -- "$(dirname -- "$0")"

docker build --file ./Dockerfile --tag page-analyzer ../../
