#! /bin/bash
###########################################
# Uninstall Plugin
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
    echo "[plugin] unlink" $pluginName "..."
    if [ ! -d $PLUGINS_DIR ]; then
        mkdir -p $PLUGINS_DIR
    fi

    cd $PLUGINS_DIR
    if [ -L $pluginName ]; then
        rm -rf $pluginName
    fi

    if [ -d $rootDir/views/channel/$pluginName ]; then
        echo "[plugin] unlink views for channel"
        VIEW_ADMIN_CHANNEL=$COSINEE_BASEDIR/contact-center/app/src/main/resources/templates/admin/channel

        if [ -L $VIEW_ADMIN_CHANNEL/$pluginName ]; then
            rm -rf $VIEW_ADMIN_CHANNEL/$pluginName
        fi
    fi

    # Install apps view
    if [ -d $rootDir/views/apps/$pluginName ]; then
        echo "[plugin] unlink views for apps"
        VIEW_ADMIN_APPS=$COSINEE_BASEDIR/contact-center/app/src/main/resources/templates/apps

        if [ -L $VIEW_ADMIN_APPS/$pluginName ]; then
            rm -rf $VIEW_ADMIN_APPS/$pluginName
        fi
    fi   

    echo "[plugin] uninstall done."
else
    echo "[error] not found cosinee dir."
    exit 2
fi