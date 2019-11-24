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
UPGRADE_DB_SCRIPT_DIR=/tmp/ROOT/upgrade

println "[upgrade] connecting to $MYSQL_WRITEMODE_IP:$MYSQL_WRITEMODE_PORT/$CONTACT_CENTER_DB with $SPRING_DATASOURCE_USERNAME/****"

# functions
function upgrade_db(){
    if [ ! -f $1 ]; then exit 1; fi
    println "run MySQL DB upgrade script" $1 "..."
    mysql -u ${SPRING_DATASOURCE_USERNAME} \
        -h ${MYSQL_WRITEMODE_IP} \
        -P ${MYSQL_WRITEMODE_PORT} -p${SPRING_DATASOURCE_PASSWORD} \
        < $1

    # verify status
    if [ ! $? -eq 0 ]; then
        exit 1
    fi
}

function extract_war(){
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

## check root dir
if [ ! -d /tmp/ROOT ]; then
    extract_war
fi

## run scripts
if [ -d $UPGRADE_DB_SCRIPT_DIR ]; then
    cd $UPGRADE_DB_SCRIPT_DIR
    for x in `find . -name "*.sql"|sort`; do
        echo "[run] " $x " ..."
        upgrade_db $x
        if [ ! $? -eq 0 ]; then
            echo "Failed result with" $x 
            exit 1
        fi
    done
fi