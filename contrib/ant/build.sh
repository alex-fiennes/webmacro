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
if [ -z "$ANT_HOME" ]
then
    ANT_HOME=./ant
fi

JAVACMD=$JAVA_HOME/bin/java

cp=$ANT_HOME/ant.jar:$ANT_HOME/parser.jar:$ANT_HOME/jaxp.jar:$JAVA_HOME/lib/tools.jar

$JAVACMD -cp $cp:$CLASSPATH org.apache.tools.ant.Main -f build.xml "$@"
