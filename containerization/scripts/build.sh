#!/bin/bash

docker build -f openjdk-21-debian-stretch-slim -t openjdk-21:debian-stretch-slim .

docker tag openjdk-21:debian-stretch-slim cskefu/openjdk-21:debian-stretch-slim


docker build -f sentinel-dashboard-1.8.6 -t sentinel-dashboard:1.8.6 .

docker tag sentinel-dashboard:1.8.6 cskefu/sentinel-dashboard:1.8.6

# docker login

docker push cskefu/openjdk-21:debian-stretch-slim
docker push cskefu/sentinel-dashboard:1.8.6
