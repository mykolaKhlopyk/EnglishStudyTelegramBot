#!/bin/bash

./gradlew assemble

docker-compose up --build --force-recreate