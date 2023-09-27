#! /bin/bash 
###########################################
#
###########################################

# constants
baseDir=$(cd `dirname "$0"`;pwd)
appHome=$baseDir/../../

# main 
[ -z "${BASH_SOURCE[0]}" -o "${BASH_SOURCE[0]}" = "$0" ] || return
# build
cd $appHome
TIMESTAMP=`date "+%Y%m%d.%H%M%S"`
PACKAGE_VERSION=`git rev-parse --short HEAD`
APPLICATION_CUSTOMER_ENTITY=${APPLICATION_CUSTOMER_ENTITY:-"OpenSource Community"}

set -ex

cd $baseDir/../../cskefu-backend
mvn -DskipTests clean deploy -N && mvn -DskipTests clean deploy
