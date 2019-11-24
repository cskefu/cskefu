#! /bin/bash 
###########################################
# Contact Center Start
###########################################

# constants
baseDir=$(cd `dirname "$0"`;pwd)

# functions

# main 
[ -z "${BASH_SOURCE[0]}" -o "${BASH_SOURCE[0]}" = "$0" ] || return
cd $baseDir
./mysql.setup.db.sh
./mysql.upgrade.db.sh

if [ $? -eq 0 ]; then
    java -jar contact-center.war
else
    echo "Fail to resolve mysql database instance."
    exit 1
fi
