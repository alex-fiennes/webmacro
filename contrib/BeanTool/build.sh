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

cp=ant/ant.jar:ant/jaxp.jar:ant/parser.jar
cp=$cp:$JAVA_HOME/lib/tools.jar

$JAVACMD -classpath $cp:$CLASSPATH org.apache.tools.ant.Main "$@"
