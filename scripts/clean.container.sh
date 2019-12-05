#! /bin/bash 
###########################################
#
###########################################

# constants
baseDir=$(cd `dirname "$0"`;pwd)
# functions

# main 
[ -z "${BASH_SOURCE[0]}" -o "${BASH_SOURCE[0]}" = "$0" ] || return
cd $baseDir/..

CONTAINER=$1

if [[ -z $CONTAINER ]]; then
    echo "No container specified"
    exit 1
fi

set -x
if [[ "$(docker ps -aq -f name=^/${CONTAINER}$ 2> /dev/null)" == "" ]]; then
    CONTAINER="$(docker-compose ps $CONTAINER 2> /dev/null | awk '{if (NR==3) print $1 fi}')"
    if [[ -z $CONTAINER ]]; then
        echo "Container \"$1\" does not exist, exiting."
        exit 1
    fi
fi

log=$(docker inspect -f '{{.LogPath}}' $CONTAINER 2> /dev/null)
truncate -s 0 $log