#! /bin/bash 
###########################################
#
###########################################

# constants
baseDir=$(cd `dirname "$0"`;pwd)
registryName=dockerhub.qingcloud.com
imagename=chatopera/contact-center
PACKAGE_VERSION=w4l

# functions

# main 
[ -z "${BASH_SOURCE[0]}" -o "${BASH_SOURCE[0]}" = "$0" ] || return
docker push $registryName/$imagename:$PACKAGE_VERSION