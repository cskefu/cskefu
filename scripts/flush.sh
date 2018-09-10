#! /bin/bash 
###########################################
# 全新导入新版本服务
###########################################

# constants
baseDir=$(cd `dirname "$0"`;pwd)
# functions

# main 
[ -z "${BASH_SOURCE[0]}" -o "${BASH_SOURCE[0]}" = "$0" ] || return
echo "【警告】即将停掉服务，删除所有数据，重新拉取新的镜像，然后启动服务 ..."
echo ">> 5 秒钟后开始，如果不清楚风险请通过【Ctrl + C】取消操作 ..."
sleep 5

cd $baseDir/..
docker-compose down
rm -rf mysql/data/*
rm -rf elasticsearch/data/elasticsearch
./scripts/start.sh
