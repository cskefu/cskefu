#! /bin/bash 
###########################################
#
###########################################

# constants
baseDir=$(cd `dirname "$0"`;pwd)
appHome=$baseDir/..
registryName=dockerhub.qingcloud.com
imagename=chatopera/contact-center
TIMESTAMP=`date "+%Y%m%d.%H%M%S"`

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
    --build-arg APPLICATION_BUILD_DATESTR=$TIMESTAMP \
    --build-arg APPLICATION_CUSTOMER_ENTITY=$APPLICATION_CUSTOMER_ENTITY \
    --no-cache \
    --force-rm=true --tag $registryName/$imagename:$PACKAGE_VERSION .

if [ $? -eq 0 ]; then
    docker tag $registryName/$imagename:$PACKAGE_VERSION $registryName/$imagename:develop
else 
    echo "Build contact-center failure."
    exit 1
fi