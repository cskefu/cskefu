#!/usr/bin/env bash

# 启动服务
# 1. 启动rasa server
python -m rasa run --port 5005 -m models/20200319-162558.tar.gz --endpoints configs/endpoints.yml --credentials configs/credentials.yml --debug
# 2. 启动action server
python -m rasa run actions --port 5055 --actions actions --debug
# 3. 启动server.py
python server.py

