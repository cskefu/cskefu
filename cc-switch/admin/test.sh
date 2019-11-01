#! /bin/bash 
###########################################
#
###########################################

# constants
baseDir=$(cd `dirname "$0"`;pwd)
export ACTIVEMQ_HOST=corsair
export REDIS_HOST=corsair

# functions

# main 
[ -z "${BASH_SOURCE[0]}" -o "${BASH_SOURCE[0]}" = "$0" ] || return
cd $baseDir/../app

# for x in {1..100}; do
#     echo $x
#     DEBUG=cc* ava --timeout=10hrs
#     sleep 5
# done

DEBUG=cc* ava --timeout=10hrs $*

