
# This is a unix makefile based on GNU make (gmake). It will work 
# under Windows if you install the CYGWIN package so that you have
# a unix shell, rm, and mkdir installed. Of course you must also
# have gmake itself installed!
#
# To use this Makefile the following environment variables must exist:
# JAVA_HOME = the directory above where your Java binaries live
# CLASSPATH = your standard java classpath
#
# Usage:
#
#  make everything     -- do this the first time
#  make                -- incremental compile
#  make jar            -- build webmacro.jar
#  make doc            -- create javadocs
#

SOURCES=*.java */*.java
JFLAGS=-O

include make.defs

jar:    
	$(JAR) -cvf webmacro.jar $(TARGETS)

api:    
	$(MKDIR) api

doc:    api
	$(JAVADOC) -protected -d api -author -version -use $(SOURCES)
