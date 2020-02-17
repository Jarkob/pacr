#!/usr/bin/env bash

# This is our Continous Deployment deploy script. You may use it as a baseline,
# but it is likely
#  a) Way too specific
#  b) Full of »totally best practices«™

# Fail if any command inside fails
set -e

function copyToServer() {
    echo "Copying $1 to $CD_URL at location $2"
    scp -P "$CD_PORT" "$1" "$CD_USER@$CD_URL:$2"
}

function executeOnServer() {
    echo "Executing on server: '$1'"
    ssh -p "$CD_PORT" "$CD_USER@$CD_URL" "$1"
}

DOCKER_SERVER_DIR="/home/pacr/.docker"

executeOnServer "rm -rf $DOCKER_SERVER_DIR"
executeOnServer "mkdir -p $DOCKER_SERVER_DIR"

copyToServer "webapp_backend/target/webapp_backend-0.1.jar" "$DOCKER_SERVER_DIR"
copyToServer "webapp_backend/ssh.key" "$DOCKER_SERVER_DIR"
copyToServer "webapp_backend/ssh.pub" "$DOCKER_SERVER_DIR"
copyToServer "webapp_backend/defaultAdminPasswordHash.txt" "$DOCKER_SERVER_DIR"

# Build and deploy it
executeOnServer "sudo /home/pacr/deploy_from_docker.sh"