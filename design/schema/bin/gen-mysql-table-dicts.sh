#! /bin/bash 
###########################################
#
###########################################

# constants
baseDir=$(cd `dirname "$0"`;pwd)
cwdDir=$PWD
export PYTHONUNBUFFERED=1
export PATH=/opt/miniconda3/envs/venv-py3/bin:$PATH
export TS=$(date +%Y%m%d%H%M%S)
export DATE=`date "+%Y%m%d"`
export DATE_WITH_TIME=`date "+%Y%m%d-%H%M%S"` #add %3N as we want millisecond too
php=/c/devel/php/php.exe

# functions

# main 
[ -z "${BASH_SOURCE[0]}" -o "${BASH_SOURCE[0]}" = "$0" ] || return
cd $baseDir/..

if [ ! -f .env ]; then
    echo "Copy sample.env to .env, modify it at first."
    exit 1
fi

source .env

# generate index.html
rm -rf tmp
mkdir tmp
cd tmp

$php ../app/generator.php -H$DB_HOST \
    -P$DB_PORT \
    -d$DATABASE \
    -u$DB_USER \
    -p$DB_PASS \
    -v$PRODUCT_VERSION \
    -s$DOWNLOAD_SQL \
    -m$DOWNLOAD_MODEL_PDF

# generate beautiful html
rm -rf docs
mkdir docs
cd docs

pandoc ../index.html \
    -o index.html \
    -f html \
    --template ../../assets/standalone.html \
    --toc --toc-depth=2

cp index.html $baseDir/../../../docs/mysql-dicts.html

echo "Generated doc index.html in" `pwd`

# compress files
cd ..
DATABASE_DICTS_ZIP=$DATABASE.dicts.$TS.zip
if [ -f $DATABASE_DICTS_ZIP ]; then
   rm -rf $DATABASE_DICTS_ZIP
fi

zip $DATABASE_DICTS_ZIP -r docs
echo "Compress with zip file" `pwd`/$DATABASE_DICTS_ZIP