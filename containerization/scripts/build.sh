#!/bin/bash

docker build -f openjdk-21-debian-stretch-slim -t openjdk-21:debian-stretch-slim .

docker tag openjdk-21:debian-stretch-slim cskefu/openjdk-21:debian-stretch-slim

docker login

docker push cskefu/openjdk-21:debian-stretch-slim
