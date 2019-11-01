#! /bin/bash 
###########################################
#
###########################################

# constants
baseDir=$(cd `dirname "$0"`;pwd)

if [ -f $baseDir/localrc ]; then
    echo "Load localrc for environment variables ..."
    set -x
    source $baseDir/localrc
else
    echo $baseDir/localrc "not exist."
    echo "First, copy and modify the rc template."
    echo "cp " $baseDir/localrc.sample $baseDir/localrc
    exit 1
fi


# functions
function start(){
    cd $baseDir/../app
    mvn spring-boot:run
}


# main 
[ -z "${BASH_SOURCE[0]}" -o "${BASH_SOURCE[0]}" = "$0" ] || return
start
