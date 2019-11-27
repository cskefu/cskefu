#! /bin/bash 
###########################################
#
###########################################

# constants
baseDir=$(cd `dirname "$0"`;pwd)
source $baseDir/utils.sh
MYSQL_WRITEMODE_IP=`parse_host ${SPRING_DATASOURCE_URL}`
MYSQL_WRITEMODE_PORT=`parse_port ${SPRING_DATASOURCE_URL}`
CONTACT_CENTER_DB=`parse_dbname ${SPRING_DATASOURCE_URL}`
CONTACT_CENTER_WAR=/opt/chatopera/contact-center.war
MYSQL_SCRIPT_NAME=cosinee-MySQL-slim.sql

# functions
function import_db(){
    if [ ! -f $1 ]; then exit 1; fi
    println "run MySQL DB initialize script ..."
    mysql -u ${SPRING_DATASOURCE_USERNAME} \
        -h ${MYSQL_WRITEMODE_IP} \
        -P ${MYSQL_WRITEMODE_PORT} -p${SPRING_DATASOURCE_PASSWORD} \
        < $1

    # verify status
    if [ ! $? -eq 0 ]; then
        exit 1
    fi
}

function init_db(){
    println "extract SQL script ..."
    if [ -f $CONTACT_CENTER_WAR ]; then
        cd /tmp
        if [ -d ROOT ]; then 
            rm -rf ROOT
        fi

        unzip -q $CONTACT_CENTER_WAR -d ROOT
        if [ -f /tmp/ROOT/$MYSQL_SCRIPT_NAME ]; then
            println "start to import database ..."
            import_db /tmp/ROOT/$MYSQL_SCRIPT_NAME
            # verify status
            if [ ! $? -eq 0 ]; then
                exit 1
            fi
            rm -rf /tmp/ROOT
        else
            println "SQL script not exist."
            exit 1
        fi
    else
        println "War file not exist."
    fi
}

# main 
[ -z "${BASH_SOURCE[0]}" -o "${BASH_SOURCE[0]}" = "$0" ] || return
println "[setup] connecting to $MYSQL_WRITEMODE_IP:$MYSQL_WRITEMODE_PORT/$CONTACT_CENTER_DB with $SPRING_DATASOURCE_USERNAME/****"

## wait for database connection ...
while ! mysqladmin --user=${SPRING_DATASOURCE_USERNAME} --password=${SPRING_DATASOURCE_PASSWORD} --host=${MYSQL_WRITEMODE_IP} --port=${MYSQL_WRITEMODE_PORT} ping --silent &> /dev/null ; do
    echo "Waiting for database connection..."
    sleep 2
done

# check if database exist, if not, create it.
mysqlshow -h ${MYSQL_WRITEMODE_IP} \
    -P ${MYSQL_WRITEMODE_PORT} \
    --user=${SPRING_DATASOURCE_USERNAME} \
    --password=${SPRING_DATASOURCE_PASSWORD} \
    ${CONTACT_CENTER_DB} > /dev/null 2>&1 && exit 0;

# not exist
init_db

# verify status
if [ ! $? -eq 0 ]; then
    println "DB status check failed."
    exit 1
fi
