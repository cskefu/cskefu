#! /bin/bash 
###########################################
#
###########################################

# constants
export JAVA_HOME=/usr/lib/jvm/java-1.8.0-amazon-corretto
export PATH=$PATH:$JAVA_HOME/bin
baseDir=$(cd `dirname "$0"`;pwd)
MVNNAME=maven.tgz

# functions

# main 
[ -z "${BASH_SOURCE[0]}" -o "${BASH_SOURCE[0]}" = "$0" ] || return
cd /opt && wget -O $MVNNAME https://www-us.apache.org/dist/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz
tar xzf $MVNNAME
mv apache-maven-* maven
rm $MVNNAME

export MAVEN_HOME=/opt/maven
export PATH=$PATH:$MAVEN_HOME/bin
rm -rf $0