#!/bin/ksh
CP=`echo lib/*.jar | sed -e 's/ /:/g'`
CP=$CP:build/WEB-INF/classes
./build.sh && {
    java -Djava.compiler=none -cp $CP org.webmacro.util.SimpleHTMLJava12SyntaxHighlighter $*
}
