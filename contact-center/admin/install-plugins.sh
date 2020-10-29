#! /bin/bash 
###########################################
#
###########################################

# constants
baseDir=$(cd `dirname "$0"`;pwd)
SCRIPT_PATH=$0
ts=`date +"%Y-%m-%d_%H-%M-%S"`
buildDir=/tmp/cc-build-$ts
# functions
function print_usage(){
    echo "Install contact-center plugin: $SCRIPT_PATH contact-center_jar_path plugin_path output_path"
}

# main 
[ -z "${BASH_SOURCE[0]}" -o "${BASH_SOURCE[0]}" = "$0" ] || return
if [ "$#" -ne 4 ]; then
    CONTACT_CENTER=$1
    CC_PLUGIN=$2
    OUTPUT_PATH=$3
    if [ ! -f $1 ]; then
        echo "contact center jar file not exist."
        print_usage
        exit 1
    fi

    if [ ! -f $2 ]; then
        echo "cc plugin jar file not exist."
        print_usage
        exit 2
    fi

    # create jar
    rm -rf $buildDir
    mkdir $buildDir
    unzip $CONTACT_CENTER -d $buildDir
    cp $CC_PLUGIN $buildDir/BOOT-INF/lib
    cd $buildDir
    jar -cvfM0 $3 .
    echo "Created new jar file as" $OUTPUT_PATH "successfully."
    echo "Build done, delete buildDir" $buildDir "in 3 seconds ..."
    sleep 3
    rm -rf $buildDir
else
    print_usage
fi
