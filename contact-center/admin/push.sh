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
PACKAGE_VERSION=`git rev-parse --short HEAD`

if [ -d ../private ]; then
    registryPrefix=dockerhub.qingcloud.com/
fi

docker push $registryPrefix$imagename:$PACKAGE_VERSION
docker push $registryPrefix$imagename:develop