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
APP_WAR_EXTRACTED=/tmp/ROOT
UPGRADE_DB_SCRIPT_DIR=$APP_WAR_EXTRACTED/upgrade

# functions
function upgrade_db(){
    if [ ! -f $1 ]; then exit 1; fi
    println "[upgrade] run MySQL DB upgrade script" $1 "..."
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
        if [ -d $APP_WAR_EXTRACTED ]; then 
            rm -rf $APP_WAR_EXTRACTED
        fi

        unzip -q $CONTACT_CENTER_WAR -d ROOT
    else
        println "War file not exist."
    fi
}

# main 
[ -z "${BASH_SOURCE[0]}" -o "${BASH_SOURCE[0]}" = "$0" ] || return
println "[upgrade] connecting to $MYSQL_WRITEMODE_IP:$MYSQL_WRITEMODE_PORT/$CONTACT_CENTER_DB with $SPRING_DATASOURCE_USERNAME/****"
## check upgrade footprint
if [ -f /opt/chatopera/upgrade.his ]; then
    echo "[upgrade] upgrade has been done with previous start."
    exit 0
fi

## wait for database connection ...
while ! mysqladmin --user=${SPRING_DATASOURCE_USERNAME} --password=${SPRING_DATASOURCE_PASSWORD} --host=${MYSQL_WRITEMODE_IP} --port=${MYSQL_WRITEMODE_PORT} ping --silent &> /dev/null ; do
    echo "Waiting for database connection..."
    sleep 2
done

## check root dir
if [ ! -d $APP_WAR_EXTRACTED ]; then
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

## touch upgrade footprint
if [ ! -f /opt/chatopera/upgrade.his ]; then
    touch /opt/chatopera/upgrade.his
fi
