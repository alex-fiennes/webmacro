#! /bin/sh

# $Id$

if [ -z "$JAVA_HOME" ]
then
	JAVACMD=`which java`
	if [ -z "$JAVACMD" ]
	then
		echo "Cannot find JAVA. Please set your PATH."
		exit 1
	fi
	JAVA_BINDIR=`dirname $JAVACMD`
	JAVA_HOME=$JAVA_BINDIR/..
fi

JAVACMD=$JAVA_HOME/bin/java

cp=ant/ant.jar:ant/parser.jar:ant/jaxp.jar:$JAVA_HOME/lib/tools.jar

$JAVACMD -cp $cp org.apache.tools.ant.Main "$@"
