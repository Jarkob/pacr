#!/usr/bin/env bash

# fail on first error
set -e

cd "/home/pacr/"

ls -lah

if [[ -d .docker ]]; then
    echo "Docker folder found"
else
    echo "Docker folder does not exist"
    exit 1
fi

if [[ -f Dockerfile ]]; then
    echo "Dockerfile found"
else
    echo "Dockerfile not found"
    exit 1
fi

cp Dockerfile .docker

cd .docker

docker build -t pacr-backend:latest .

# Clean up old images
docker image prune --filter "until=10m" --filter "label=pacr-backend" -f
docker stop pacr-backend || echo "Server not started yet."
docker rm pacr-backend || echo "Server really not started yet."
docker run -t -d --restart always --name pacr-backend -p 8080:8080 pacr-backend