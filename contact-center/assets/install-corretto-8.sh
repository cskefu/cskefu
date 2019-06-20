#! /bin/bash 
###########################################
#
###########################################

# constants
baseDir=$(cd `dirname "$0"`;pwd)
JDKNAME=java-1.8.0-amazon-corretto-jdk.deb
# functions

# main 
[ -z "${BASH_SOURCE[0]}" -o "${BASH_SOURCE[0]}" = "$0" ] || return
apt-get update && apt-get install -y --no-install-recommends publicsuffix ca-certificates wget java-common
cd /tmp
wget --no-check-certificate -O $JDKNAME https://d3pxv6yz143wms.cloudfront.net/8.212.04.2/java-1.8.0-amazon-corretto-jdk_8.212.04-2_amd64.deb
dpkg --install $JDKNAME
rm -rf $JDKNAME
java -version

export JAVA_HOME=/usr/lib/jvm/java-1.8.0-amazon-corretto
export PATH=$PATH:$JAVA_HOME/bin
rm -rf $0