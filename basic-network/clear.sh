#!/bin/bash
#
# Copyright IBM Corp All Rights Reserved
#
# SPDX-License-Identifier: Apache-2.0
#
# Exit on first error, print all commands.
set -e

# Shut down the Docker containers for the system tests.
docker-compose -f docker-compose.yml kill && docker-compose -f docker-compose.yml down

CONTAINER_IDS=$(docker ps -a | grep "dev\|none\|test-vp\|peer[0-9]-" | awk '{print $1}')
if [ -z "$CONTAINER_IDS" -o "$CONTAINER_IDS" = " " ]; then
    echo "---- No containers available for deletion ----"
else
    docker rm -f $CONTAINER_IDS
fi

# remove the local state
rm -f ~/.hfc-key-store/*

# remove chaincode docker images
DOCKER_IMAGE_IDS=$(docker images | grep "dev\|none\|test-vp\|peer[0-9]-" | awk '{print $3}')
if [ -z "$DOCKER_IMAGE_IDS" -o "$DOCKER_IMAGE_IDS" = " " ]; then
    echo "---- No images available for deletion ----"
else
    docker rmi -f $DOCKER_IMAGE_IDS
fi

# Your system is now clean
