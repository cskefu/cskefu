#! /bin/bash 
###########################################
# Create standalone SQL file to setup db
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

if [ ! -e tmp ]; then
    mkdir tmp
fi

cat config/sql/001.mysql-create-db.sql > tmp/db-setup.sql
echo "" >> tmp/db-setup.sql
cat config/sql/002.mysql-create-schemas.sql >> tmp/db-setup.sql

echo "Setup Script created in" `pwd`/tmp/db-setup.sql