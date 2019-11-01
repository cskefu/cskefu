#! /bin/bash 
###########################################
#
###########################################

# constants
baseDir=$(cd `dirname "$0"`;pwd)
registryName=dockerhub.qingcloud.com
imagename=chatopera/contact-center
PACKAGE_VERSION=w4l

# functions

# main 
[ -z "${BASH_SOURCE[0]}" -o "${BASH_SOURCE[0]}" = "$0" ] || return
cd $baseDir/
docker run -it --rm \
    -p 9035:8035 \
    -p 9036:8036 \
    -v $PWD/data:/data \
    -v $PWD/logs:/logs \
    -e "JAVA_OPTS=-Xmx12288m -Xms2048m -XX:PermSize=256m -XX:MaxPermSize=1024m -Djava.net.preferIPv4Stack=true" \
    -e SERVER_PORT=8035 \
    -e SERVER_LOG_PATH=/logs \
    -e SERVER_LOG_LEVEL=INFO \
    -e WEB_UPLOAD_PATH=/data \
    -e SPRING_FREEMARKER_CACHE=true \
    -e SPRING_DATA_ELASTICSEARCH_PROPERTIES_PATH_DATA=/data \
    -e SPRING_DATASOURCE_DRIVER_CLASS_NAME=com.mysql.jdbc.Driver \
    -e "SPRING_DATASOURCE_URL=jdbc:mysql://mysql:8037/contactcenter?useUnicode=true&characterEncoding=UTF-8" \
    -e SPRING_DATASOURCE_USERNAME=root \
    -e SPRING_DATASOURCE_PASSWORD=123456 \
    -e MANAGEMENT_SECURITY_ENABLED=false \
    -e SPRING_REDIS_DATABASE=2 \
    -e SPRING_REDIS_HOST=redis \
    -e SPRING_REDIS_PORT=8041 \
    -e CSKEFU_CALLOUT_WATCH_INTERVAL=60000 \
    -e SPRING_DATA_ELASTICSEARCH_CLUSTER_NAME=elasticsearch \
    -e SPRING_DATA_ELASTICSEARCH_CLUSTER_NODES=elasticsearch:8040 \
    -e SPRING_DATA_ELASTICSEARCH_LOCAL=false \
    -e SPRING_DATA_ELASTICSEARCH_REPOSITORIES_ENABLED=true \
    $registryName/$imagename:$PACKAGE_VERSION
