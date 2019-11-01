#! /bin/bash 
###########################################
#
###########################################

# constants
baseDir=$(cd `dirname "$0"`;pwd)
appHome=$baseDir/..
imagename=chatopera/contact-center
PACKAGE_VERSION=1.0.0
# functions

# main 
[ -z "${BASH_SOURCE[0]}" -o "${BASH_SOURCE[0]}" = "$0" ] || return
# build
cd $appHome
set -x
docker build --build-arg VCS_REF=`git rev-parse --short HEAD` --force-rm=true --tag $imagename:$PACKAGE_VERSION .
docker tag $imagename:$PACKAGE_VERSION $imagename:develop
