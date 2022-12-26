#! /bin/bash 
###########################################
# Install All public plugins
# Copyright (2019-2023) 北京华夏春松科技有限公司
###########################################

# constants
baseDir=$(cd `dirname "$0"`;pwd)

# functions

# main 
[ -z "${BASH_SOURCE[0]}" -o "${BASH_SOURCE[0]}" = "$0" ] || return

echo "Remove old plugins ..."
set -x
rm -rf $baseDir/../../compose/contact-center/plugins/*.jar
set +x

cd $baseDir/..
echo "Inspect plugins -->" `pwd`
for x in `ls .`; do
    cd $baseDir/..
    if [ -d ./$x ] && [ $x != "bin" ] && [ -f $x/pom.xml ]; then
        cd $x
        echo "Package and Deploy Plugin" `pwd`
        mvn -DskipTests clean package deploy
    fi
done
