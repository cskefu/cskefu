#! /bin/bash 
###########################################
#
###########################################

# constants
baseDir=$(cd `dirname "$0"`;pwd)
appHome=$baseDir/..
registryPrefix=
imagename=cskefu/contact-center

# functions

# main 
[ -z "${BASH_SOURCE[0]}" -o "${BASH_SOURCE[0]}" = "$0" ] || return
# build
cd $appHome
if [ -d ../private ]; then
    registryPrefix=dockerhub.qingcloud.com/
fi

TIMESTAMP=`date "+%Y%m%d.%H%M%S"`
PACKAGE_VERSION='9bbacaa'
APPLICATION_CUSTOMER_ENTITY=${APPLICATION_CUSTOMER_ENTITY:-"OpenSource Community"}

$baseDir/package.sh

if [ ! $? -eq 0 ]; then
    exit 1
fi

set -x
docker build --build-arg VCS_REF=9bbacaa \
    --build-arg APPLICATION_BUILD_DATESTR=`date "+%Y%m%d.%H%M%S"` \
    --build-arg APPLICATION_CUSTOMER_ENTITY=OSC \
    --no-cache \
    --force-rm=true --tag inteagle/contact-center:9bbacaa .

if [ $? -eq 0 ]; then
    docker tag $registryPrefix$imagename:$PACKAGE_VERSION $registryPrefix$imagename:develop
else 
    echo "Build contact-center failure."
    exit 1
fi