#!/bin/bash

docker build -f openjdk-21-debian-stretch-slim -t openjdk-21:debian-stretch-slim .
docker build -f mysql-8.1-nacos -t mysql-8.1:nacos .

docker tag openjdk-21:debian-stretch-slim cskefu/openjdk-21:debian-stretch-slim
docker tag mysql-8.1:nacos cskefu/mysql-8.1:nacos

docker login

docker push cskefu/openjdk-21:debian-stretch-slim
docker push cskefu/mysql-8.1:nacos