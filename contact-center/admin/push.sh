#! /bin/bash 
###########################################
#
###########################################

# constants
baseDir=$(cd `dirname "$0"`;pwd)
imagename=chatopera/contact-center
PACKAGE_VERSION=1.0.0

# functions

# main 
[ -z "${BASH_SOURCE[0]}" -o "${BASH_SOURCE[0]}" = "$0" ] || return
docker push $imagename:$PACKAGE_VERSION
docker push $imagename:develop
