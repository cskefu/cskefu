#! /bin/bash 
###########################################
#
###########################################

# constants
baseDir=$(cd `dirname "$0"`;pwd)
JREBEL_HOME=~/java/jrebel

if [ -f $JREBEL_HOME/lib/libjrebel64.dylib ]; then
    echo "jrebel is available."
else
    echo "jrebel is unavailable."
    echo "Please setup jrebel with https://github.com/Samurais/chatopera.io/issues/652"
    exit 1
fi

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
    mvn spring-boot:run -Dproject.rootdir=$baseDir/../app
         \ -Drun.jvmArguments="-agentpath:$JREBEL_HOME/lib/libjrebel64.dylib"
}


# main 
[ -z "${BASH_SOURCE[0]}" -o "${BASH_SOURCE[0]}" = "$0" ] || return
start
