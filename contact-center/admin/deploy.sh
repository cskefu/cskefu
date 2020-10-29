#! /bin/bash 
###########################################
#
###########################################

# constants
baseDir=$(cd `dirname "$0"`;pwd)
REPO_ID_SNP=chatopera-snapshots
REPO_URL_SNP=https://nexus.chatopera.com/repository/maven-snapshots/
REPO_ID_REL=chatopera-releases
REPO_URL_REL=https://nexus.chatopera.com/repository/maven-releases/

# functions

# main 
[ -z "${BASH_SOURCE[0]}" -o "${BASH_SOURCE[0]}" = "$0" ] || return
cd $baseDir/../app
mvn clean jar:jar
PACKAGE_VERSION=$(grep --max-count=1 '<version>' pom.xml | awk -F '>' '{ print $2 }' | awk -F '<' '{ print $1 }')
if [[ $PACKAGE_VERSION == *SNAPSHOT ]]; then
    echo "Deploy as snapshot package ..."
    mvn deploy:deploy-file \
        -Dmaven.test.skip=true \
        -Dfile=./target/contact-center.jar \
        -DgroupId=com.chatopera.cc \
        -DartifactId=cc-core \
        -Dversion=$PACKAGE_VERSION \
        -Dpackaging=jar \
        -DgeneratePom=true \
        -DrepositoryId=$REPO_ID_SNP \
        -Durl=$REPO_URL_SNP
    if [ ! $? -eq 0 ]; then
        exit 1
    fi
else
    echo "Deploy as release package ..."
    mvn deploy:deploy-file \
        -Dmaven.test.skip=true \
        -Dfile=./target/contact-center.jar \
        -DgroupId=com.chatopera.cc \
        -DartifactId=cc-core \
        -Dversion=$PACKAGE_VERSION \
        -Dpackaging=jar \
        -DgeneratePom=true \
        -DrepositoryId=$REPO_ID_REL \
        -Durl=$REPO_URL_REL
    if [ ! $? -eq 0 ]; then
        exit 1
    fi
fi
