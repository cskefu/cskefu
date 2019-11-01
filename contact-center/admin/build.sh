#! /bin/bash 
###########################################
#
###########################################

# constants
baseDir=$(cd `dirname "$0"`;pwd)
appHome=$baseDir/..
registryName=dockerhub.qingcloud.com
imagename=chatopera/contact-center
# functions

# main 
[ -z "${BASH_SOURCE[0]}" -o "${BASH_SOURCE[0]}" = "$0" ] || return
# build
cd $appHome
PACKAGE_VERSION=`git rev-parse --short HEAD`

set -x
$baseDir/package.sh

if [ ! $? -eq 0 ]; then
    exit 1
fi

docker build --build-arg VCS_REF=$PACKAGE_VERSION \
    --no-cache \
    --force-rm=true --tag $registryName/$imagename:$PACKAGE_VERSION .