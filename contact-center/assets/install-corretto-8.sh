#! /bin/bash 
###########################################
#
###########################################

# constants
baseDir=$(cd `dirname "$0"`;pwd)
JDKNAME=java-1.8.0-amazon-corretto-jdk.deb
## fast download with internal addr
## replace JDKURL when builing outside chatopera machines, java-1.8.0-amazon-corretto-jdk_8.212.04-2_amd64.deb can be download from Amazon Official Site.
## https://docs.aws.amazon.com/corretto/latest/corretto-8-ug/downloads-list.html
JDKURL=http://192.168.2.217:30080/vendors/java/java-1.8.0-amazon-corretto-jdk_8.212.04-2_amd64.deb

# functions

# main 
[ -z "${BASH_SOURCE[0]}" -o "${BASH_SOURCE[0]}" = "$0" ] || return
apt-get update && apt-get install -y --no-install-recommends publicsuffix ca-certificates wget java-common
cd /tmp
wget --no-check-certificate -O $JDKNAME $JDKURL
dpkg --install $JDKNAME
rm -rf $JDKNAME
java -version

export JAVA_HOME=/usr/lib/jvm/java-1.8.0-amazon-corretto
export PATH=$PATH:$JAVA_HOME/bin
rm -rf $0