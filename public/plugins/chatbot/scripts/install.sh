#! /bin/bash
###########################################
# Install Plugin
# Copyright (2019) 北京华夏春松科技有限公司
###########################################

# constants
## for windows platform
export MSYS=winsymlinks:nativestrict

baseDir=$(cd `dirname "$0"`;pwd)
rootDir=$(cd -P $baseDir/..;pwd)
upperDir=$(cd -P $rootDir/..;pwd)
COSINEE_BASEDIR=$(cd -P $upperDir/../..;pwd)
pluginName=$(basename $rootDir)

# functions

# main 
[ -z "${BASH_SOURCE[0]}" -o "${BASH_SOURCE[0]}" = "$0" ] || return
cd $rootDir/..
echo "[plugins] path" `pwd`

if [ -d $COSINEE_BASEDIR ]; then
    PLUGINS_DIR=$COSINEE_BASEDIR/contact-center/app/src/main/java/com/chatopera/cc/plugins
    echo "[plugin] link" $rootDir "as" $pluginName "..."
    if [ ! -d $PLUGINS_DIR ]; then
        mkdir -p $PLUGINS_DIR
    fi

    cd $PLUGINS_DIR
    pwd
    if [ -e $pluginName ]; then
        rm -rf $pluginName
    fi

    echo "[plugin] link source codes"
    ln -s $rootDir/classes $pluginName

    # Install channel views
    if [ -d $rootDir/views/channel/$pluginName ]; then
        echo "[plugin] unlink views for channel"
        VIEW_ADMIN_CHANNEL=$COSINEE_BASEDIR/contact-center/app/src/main/resources/templates/admin/channel

        if [ -d $VIEW_ADMIN_CHANNEL/$pluginName ]; then
            rm -rf $VIEW_ADMIN_CHANNEL/$pluginName
        fi

        cd $VIEW_ADMIN_CHANNEL
        ln -s $rootDir/views/channel/$pluginName .
    fi

    # Install apps view
    if [ -d $rootDir/views/apps/$pluginName ]; then
        echo "[plugin] unlink views for apps"
        VIEW_ADMIN_APPS=$COSINEE_BASEDIR/contact-center/app/src/main/resources/templates/apps

        if [ -d $VIEW_ADMIN_APPS/$pluginName ]; then
            rm -rf $VIEW_ADMIN_APPS/$pluginName
        fi

        cd $VIEW_ADMIN_APPS
        ln -s $rootDir/views/apps/$pluginName .
    fi    

    echo "[plugin] install done."
else
    echo "[error] not found cosinee dir."
    exit 2
fi