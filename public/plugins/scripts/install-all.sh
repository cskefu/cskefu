#! /bin/bash 
###########################################
# Install All public plugins
# Copyright (2019-2022) 北京华夏春松科技有限公司
###########################################

# constants
baseDir=$(cd `dirname "$0"`;pwd)
# functions

function install_plugin(){
    echo "Install plugin" $1 "..."
    cd $baseDir/../$1
    if [ -f ./scripts/install.sh ]; then
        ./scripts/install.sh
    else
        echo "[WARN] not exist command" $baseDir/../$1/scripts/install.sh
    fi
    cd $baseDir/..
}

# main 
[ -z "${BASH_SOURCE[0]}" -o "${BASH_SOURCE[0]}" = "$0" ] || return
cd $baseDir/..
echo "Inspect plugins -->" `pwd`
for x in `ls .`; do
    if [ $x != "scripts" ] && [ $x != "tmp" ] && [ -d ./$x ]; then
        install_plugin $x
        echo -e "\n"
    fi
done
