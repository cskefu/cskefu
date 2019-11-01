#! /bin/bash 
###########################################
#
###########################################

# constants
baseDir=$(cd `dirname "$0"`;pwd)
DB_HOST=192.168.2.217
DB_PORT=7111
DB_USER=root
DB_PASS=123456
DATABASE=cosinee
PRODUCT_VERSION=3.9.0

# functions

# main 
[ -z "${BASH_SOURCE[0]}" -o "${BASH_SOURCE[0]}" = "$0" ] || return
cd $baseDir/docs
if [ ! -d $baseDir/tmp ]; then
    mkdir $baseDir/tmp
fi

set -x
cd $baseDir/tmp
php ../php/generator.php -H$DB_HOST \
    -P$DB_PORT \
    -d$DATABASE \
    -u$DB_USER \
    -p$DB_PASS \
    -v$PRODUCT_VERSION

cd $baseDir/docs
pandoc ../tmp/index.html \
    -o index.html \
    -f html \
    --template standalone.html \
    --toc --toc-depth=2