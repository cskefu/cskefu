#! /bin/bash 
###########################################
#
###########################################

# constants
baseDir=$(cd `dirname "$0"`;pwd)
# functions

# main
[ -z "${BASH_SOURCE[0]}" -o "${BASH_SOURCE[0]}" = "$0" ] || return
cd $baseDir/../app
mvn -DskipTests clean package
# take too long time with dev002 for uploading artifact, skip this operation
# $baseDir/deploy.app.sh

if [ ! $? -eq 0 ]; then
    exit 1
fi
