#!/bin/ksh
DIR=`dirname $0`
export TOMCAT_HOME=/opt/jakarta-tomcat-3.2.1/
$TOMCAT_HOME/bin/tomcat.sh run -config $DIR/conf/server.xml
