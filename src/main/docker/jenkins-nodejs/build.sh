#!/usr/bin/env sh
if [ -n "$(docker ps)" ]; then
    docker build -t jenkins/jenkins:lts-jdk17-node18 .
else
    echo "A valid Docker installation is required to use this script"
fi
