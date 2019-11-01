#! /bin/bash 
###########################################
#
###########################################

# constants
baseDir=$(cd `dirname "$0"`;pwd)
# functions

# main 
[ -z "${BASH_SOURCE[0]}" -o "${BASH_SOURCE[0]}" = "$0" ] || return
cd $baseDir/../..
if [ -f docker-compose.yml ]; then
    docker-compose up redis
else
    echo "Invalid docker compose."
    exit 1
fi