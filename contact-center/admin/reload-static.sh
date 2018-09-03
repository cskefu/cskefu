#! /bin/bash 
###########################################
#
###########################################

# constants
baseDir=$(cd `dirname "$0"`;pwd)
TARGET=$baseDir/../app/target/classes
SRC=$baseDir/../app/src/main/resources
# functions
function copy(){
    echo $SRC/$1 "override" $TARGET/$1 "..."
    cd $SRC/$1
    tar cf - .|(cd $TARGET/$1;tar xf -)
}


# main 
[ -z "${BASH_SOURCE[0]}" -o "${BASH_SOURCE[0]}" = "$0" ] || return
copy templates
copy static
