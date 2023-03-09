#!/bin/bash

set -e

JENKINS_HOME=../docker/volumes/jenkins-home

mvn clean install -Pskip || { echo "Build failed"; exit 1; }

echo "Installing plugin in $JENKINS_HOME"

rm -rf $JENKINS_HOME/plugins/data-tables-api-plugin*
cp -fv target/data-tables-api.hpi $JENKINS_HOME/plugins/data-tables-api.jpi

CURRENT_UID="$(id -u):$(id -g)"
export CURRENT_UID
IS_RUNNING=$(docker-compose ps -q jenkins-controller)
if [[ "$IS_RUNNING" != "" ]]; then
    docker-compose restart
    echo "Restarting Jenkins (docker compose with user ID ${CURRENT_UID}) ..."
fi
