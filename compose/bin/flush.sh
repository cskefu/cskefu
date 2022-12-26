#! /bin/bash 
###########################################
#
###########################################

# constants
baseDir=$(cd `dirname "$0"`;pwd)
cwdDir=$PWD
export PYTHONUNBUFFERED=1
export PATH=/opt/miniconda3/envs/venv-py3/bin:$PATH
export TS=$(date +%Y%m%d%H%M%S)
export DATE=`date "+%Y%m%d"`
export DATE_WITH_TIME=`date "+%Y%m%d-%H%M%S"` #add %3N as we want millisecond too

# functions

# main 
[ -z "${BASH_SOURCE[0]}" -o "${BASH_SOURCE[0]}" = "$0" ] || return
cd $baseDir/..
echo "Remove docker containers and drop data in 5 seconds ..."
sleep 5

docker-compose down

echo "Clean up db ..."
rm -rf databases/redis/data
rm -rf databases/mysql/data
rm -rf databases/mongodb/data


echo "Pull docker images ..."
docker-compose pull

echo "Start services ..."
docker-compose up -d
