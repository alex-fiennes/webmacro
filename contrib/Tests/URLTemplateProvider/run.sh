#!/usr/bin/ksh
CP=`echo lib/*.jar | sed -e 's/ /:/g'`
JSHOME=/opt/java-lib/3rdParty
WM=./ant-build/
DIR=`dirname $0`
CONF=$DIR/conf/
SCRIPTS=$DIR/scripts/
#
# Swap the commenting out of the next two lines to test loading from jar on
# classpath
#
# TEMPLATES=$DIR
TEMPLATES=$DIR/templates.jar
java -cp $CP:$WM:$TEMPLATES:$CONF:$JSHOME/js.jar:$JSHOME/jstools.jar \
    org.mozilla.javascript.tools.shell.Main "$SCRIPTS/$1"
