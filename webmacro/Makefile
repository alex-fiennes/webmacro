
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
#  make            -- same as "make project"
#  make (PROJECT)  -- all of: build jar, doc, web, src 
#
#  make build      -- incrementally compile the sources
#  make jar        -- create project jar file
#  make doc        -- create project javadoc
#  make web        -- copy website into project
#  make src        -- copy sources into project src.zip
#
#  make clean      -- erase the compiled sources only
#  make distclean  -- erase the project and the compiled sources

# set the name of your project, will become the build directory
PROJECT = webmacro

# which packages are part of this project
PACKAGES = org.webmacro \
           org.webmacro.engine \
           org.webmacro.resource \
           org.webmacro.util \
           org.webmacro.directive \
           org.webmacro.parser \
           org.webmacro.extractor \
           org.webmacro.servlet 

# where do the root files for the website live?
WEBROOT = webroot

# files that you want copied with any material from this project
LEGAL_FILES = $(WEBROOT)/CREDITS $(WEBROOT)/LICENSE 

# file taht you want copied into the jar file in addition to classes
JAR_FILES = $(LEGAL_FILS) WebMacro.defaults

# files which you want included in the packaged distribution
PROJECT_FILES = $(WEBROOT)/README.html $(WEBROOT)/RELEASE-NOTES.html

# files which are required to build the project
BUILD_FILES = Makefile $(LEGAL_FILES)

# extra stuff to be copied into a release
EXTRAS = contrib examples

TEXT_FILES = LICENSE CREDITS *.html */*.html examples/* 
		
# general java configuration
JAVA_HOME = /usr/java/jdk1.3
JAVAC_FLAGS = -O -g
JAVADOC_FLAGS = -protected -use -version -author 

#
# Probably leave the rest of it alone!
#

VERSION ?= $(shell date +"%d-%m-%Y")
PROJECT_ZIP = $(PROJECT).$(VERSION).zip
PROJECT_TGZ = $(PROJECT).$(VERSION).tgz

NEWLINE_FILES = $(foreach file, $(TEXT_FILES), $(PROJECT)/$(file))

SOURCES = $(foreach dir, $(subst .,/,$(PACKAGES)), $(dir)/*.java) 

TARGETS = $(SOURCES:.java=.class)
SOURCEFILES = $(wildcard $(SOURCES))

DOC_DIR=$(PROJECT)/api
WEB_DIR=$(PROJECT)/webroot

DIST_FILES=$(foreach file, \
     $(notdir $(PROJECT_FILES) $(LEGAL_FILES)), $(PROJECT)/$(file))
DIRECTORIES=$(DOC_DIR) $(WEB_DIR)

JARFILE = $(PROJECT)/$(PROJECT).jar
SRC_ZIP = $(PROJECT)/src.zip

UNIXBIN = /usr/bin
MKDIR = $(UNIXBIN)/mkdir
RMFILE = $(UNIXBIN)/rm -rf
TOUCH = $(UNIXBIN)/touch
CP = cp -r

FLIPM = flip -m
FLIPU = flip -u
UNCVS = find . -name 'CVS' | xargs rm -rf 
TGZ = tar -cvzf 
ZIP = zip -r

JAVABIN = $(JAVA_HOME)/bin
JAVAC = $(JAVABIN)/javac
JAR = $(JAVABIN)/jar
JAVADOC = $(JAVABIN)/javadoc

%.class:%.java
	$(JAVAC) $(JAVAC_FLAGS) $<

all: $(PROJECT)

$(PROJECT): build doc jar web src $(DIST_FILES)

release: $(PROJECT_TGZ) $(PROJECT_ZIP)

build: .build
.build: $(SOURCEFILES) 
	@echo compiling: $(notdir $(filter %.java, $?))
	$(JAVAC) $(JAVAC_FLAGS) $(filter %.java, $?) 
	@$(TOUCH) .build

doc:    .doc
.doc: $(DOC_DIR) $(SOURCEFILES)
	@echo building javadoc in $(DOC_DIR)
	@$(JAVADOC) $(JAVADOC_FLAGS) -d $(DOC_DIR) $(SOURCEFILES)
	@$(TOUCH) .doc

jar: $(JARFILE) 
$(JARFILE): $(DIRECTORIES) $(JAR_FILES) build
	@echo building jar file: $(JARFILE)
	@$(TOUCH) $(JARFILE)
	@$(JAR) uvf $(JARFILE) $(TARGETS) $(JAR_FILES) 

web: $(WEB_DIR) $(LEGAL_FILES) 
	@echo copying website
	@$(CP) $(WEBROOT)/* $(WEB_DIR)
	@$(CP) $(LEGAL_FILES) $(WEB_DIR)

src: $(SRC_ZIP)
$(SRC_ZIP): $(SOURCEFILES) $(BUILD_FILES) $(DIRECTORIES)
	@echo creating $(SRC_ZIP)
	@$(TOUCH) $(SRC_ZIP)
	@$(JAR) uvf $(SRC_ZIP) $(filter %.java, $?) $(BUILD_FILES) 

$(DIST_FILES): $(DIRECTORIES) $(PROJECT_FILES) $(LEGAL_FILES)
	@echo copying files to directory: $(PROJECT)
	@$(CP) $(LEGAL_FILES) $(PROJECT_FILES) $(PROJECT)

$(DIRECTORIES): 
	@echo creating build directory: $(PROJECT)
	@$(MKDIR) $(PROJECT) $(DIRECTORIES)

extras: $(EXTRAS)
	$(CP) $(EXTRAS) $(PROJECT)
	cd $(PROJECT); $(UNCVS)
        
$(PROJECT_ZIP): all extras
	$(FLIPM) $(NEWLINE_FILES)
	$(ZIP) $(PROJECT_ZIP) $(PROJECT)

$(PROJECT_TGZ): all extras
	$(FLIPU) $(NEWLINE_FILES)
	$(TGZ) $(PROJECT_TGZ) $(PROJECT)

clean: 
	-$(RMFILE) $(TARGETS) .build 

distclean: clean
	-$(RMFILE) $(PROJECT) .doc

