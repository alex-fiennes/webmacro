
CLASSPATH=$(JAVAHOME)/lib/classes.zip:/local/java-lib/collections.jar:/local/java-lib/jsdk.jar:.

TARGETS = 	webmacro/webmacro.jar \
            	webmacro/src.zip \
		webmacro/examples \
		webmacro/docs \
		webmacro/IMPORTANT \
		webmacro/RELEASE-NOTES.html \
		webmacro/collections.jar \
		webmacro/README.html 

SOURCES =	org/webmacro/servlet/*.java \
		org/webmacro/engine/*.java \
		org/webmacro/resource/*.java \
		org/webmacro/broker/*.java \
		org/webmacro/util/*.java \
                org/webmacro/*.java

CLASSES =	org/webmacro/servlet/*.class \
		org/webmacro/engine/*.class \
		org/webmacro/resource/*.class \
		org/webmacro/broker/*.class \
		org/webmacro/util/*.class \
                org/webmacro/*.class

DOCS =		org/webmacro/webroot/*.html \
		org/webmacro/webroot/api \
		org/webmacro/webroot/mkdoc \
		org/webmacro/CHANGES \
		org/webmacro/webroot/COPYING

EXAMPLES =	org/webmacro/examples/*.wm \
		org/webmacro/examples/*.java \
                org/webmacro/examples/README \
		org/webmacro/examples/*.properties

snapshot: date-version dist

release: prompt-version dist

dist: version $(TARGETS) 
	flip -m webmacro/RELEASE-NOTES.html
	flip -m webmacro/README.html
	flip -m webmacro/examples/*.wm
	flip -m webmacro/examples/*.properties
	flip -m webmacro/examples/*.java
	flip -m webmacro/docs/License.html
	flip -m webmacro/docs/COPYING
	flip -m webmacro/IMPORTANT
	zip -r webmacro.`cat version`.zip webmacro
	flip -u webmacro/RELEASE-NOTES.html
	flip -u webmacro/README.html
	flip -u webmacro/examples/*.wm
	flip -u webmacro/examples/*.properties
	flip -u webmacro/examples/*.java
	flip -u webmacro/docs/License.html
	flip -u webmacro/docs/COPYING
	flip -u webmacro/IMPORTANT
	tar -czf webmacro.`cat version`.tgz webmacro


org/webmacro:
	cvs co org/webmacro

webmacro: org/webmacro
	cvs update org/webmacro
	mkdir webmacro

webmacro/webmacro.jar:	webmacro
	CLASSPATH=$(CLASSPATH) javac $(SOURCES)
	jar cf webmacro/webmacro.jar $(CLASSES)

webmacro/src.zip:	webmacro
	zip -r webmacro/src $(SOURCES)

webmacro/examples:	webmacro
	mkdir webmacro/examples
	cp $(EXAMPLES) webmacro/examples

webmacro/README.html: webmacro
	cp org/webmacro/webroot/README.html webmacro

webmacro/RELEASE-NOTES.html: webmacro
	cp org/webmacro/webroot/RELEASE-NOTES.html webmacro

webmacro/License.html: webmacro
	cp org/webmacro/webroot/License.html webmacro

webmacro/docs: 	webmacro
	mkdir webmacro/docs
	cp -pR $(DOCS) webmacro/docs
	CLASSPATH=$(CLASSPATH) webmacro/docs/mkdoc

webmacro/IMPORTANT: webmacro
	cp org/webmacro/webroot/IMPORTANT webmacro

webmacro/collections.jar:
	cp org/webmacro/lib/collections.jar webmacro

date-version:
	@date +%d-%m-%Y > version
	@echo snapshot version is: `cat version`

prompt-version: 
	@echo
	@echo -n "enter version id: " 
	@sh -c 'read VERSION && echo $$VERSION' > version
	@echo version is: `cat version`

clean:
	rm -rf org webmacro version *.zip *.tgz
