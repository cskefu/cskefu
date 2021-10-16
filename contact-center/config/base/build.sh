#! /bin/bash 
###########################################
#
###########################################

# constants
baseDir=$(cd `dirname "$0"`;pwd)
appHome=$baseDir
# registryName=
imagename=chatopera/java-1.8.0-amazon-corretto-jdk
PACKAGE_VERSION=1.0.0
TIMESTAMP=`date "+%Y%m%d.%H%M%S"`

# functions

# main 
[ -z "${BASH_SOURCE[0]}" -o "${BASH_SOURCE[0]}" = "$0" ] || return
# build
cd $appHome

docker build --build-arg \
    --no-cache \
    --force-rm=true --tag $imagename:$PACKAGE_VERSION .

set -x
if [ $? -eq 0 ]; then
    docker tag $imagename:$PACKAGE_VERSION $imagename:develop
    docker push $imagename:$PACKAGE_VERSION 
    docker push $imagename:develop
else 
    echo "Build contact-center failure."
    exit 1
fi