###########################################################
# To use this include you must define the following macros
#
#   JAVADIR - the location of the source to build
#   CLASSDIR - the location to put the output classes
#   MYCLASSPATH - the classpath to use for the build
#   RELDIR - the place to checkin the final jar
#   JARNAME - the name of the jar without the extension
#   PRODUCT - the name of the product you are building (used by manifest)

# The following is optional if you want to use versioning
#
#   VERSIONSRC - the source of the file containing version information
#
#
# The following are needed but probably defined in global 
# place and not defined directly in the calling Makefile
#
#   JAVAC - the name of the the java compiler
#   JAR - the name of the the jar utility
#   JFLAGS - the flags to pass to the compiler
#   JARFLAGS - the flags to pass to the jar utility
#   JARMFLAGS - the flags to pass to the jar utility with manifest
#   IDLCOMJAR - The jar containing the idl compiler
#   JGLCLASSES - location JGL classes used by the IDL compiler
###########################################################

# Includes defines for other functionality
include $(INCREMENT_VERSION_MK)

export JAVA_COMPILER = sunwjit

GREP=/usr/bin/egrep

ifdef JARNAME
TMP_JAR = $(CLASSDIR)/$(JARNAME).$(JAR_EXT)
endif

ifdef JAVA_JARNAME
TMP_JAVA_JAR=$(CLASSDIR)/$(JAVA_JARNAME).$(JAR_EXT)
endif

ifdef VERSIONSRC
BUILD_VERSION=$(CLASSDIR)/$(VERSION_DEST)/$(notdir $(VERSIONSRC))
endif

ifdef JAR_JAVA_COMPILE_DIR
PACKAGE_DIR=$(JAR_JAVA_COMPILE_DIR)
else
PACKAGE_DIR=com
endif

MANIFEST = "Name: com/cboe/\nImplementation-Title: $(PRODUCT)\nImplementation-Vendor: CBOE\nImplementation-Version: $(IMPL_VERSION2)\nSpecification-Title: $(PRODUCT)\nSpecification-Vendor: CBOE\nSpecification-Version: $(IMPL_VERSION2)\n"
MANIFEST_FILE=$(JAVADIR)/$(addsuffix .mf,$(JARNAME) )


ifdef TMP_JAR
RELEASE_JAR=$(addprefix $(RELDIR)/, $(notdir $(TMP_JAR)))
endif

ifdef TMP_JAVA_JAR
RELEASE_JAVA_JAR=$(addprefix $(RELDIR)/, $(notdir $(TMP_JAVA_JAR)))
endif


ifdef BUILDCHECKOUT
    BUILD_JAR=$(RELEASE_JAR)
    BUILD_JAVA_JAR=$(RELEASE_JAVA_JAR)
else
    BUILD_JAR=$(TMP_JAR)
    BUILD_JAVA_JAR=$(TMP_JAVA_JAR)
endif



JAVA_COMPILE=$(addprefix $(CLASSDIR)/, $(addsuffix .compile,$(JARNAME) ))

#By default the JAVA_DIRS are assumed to be the single JAVADIR, but makefiles can override to specify multiples
JAVA_DIRS=$(JAVADIR)

ifndef THECOMPILEDEPENDENCIES
	THECOMPILEDEPENDENCIES:=$(filter %.jar,$(subst :, ,$(MYCLASSPATH)))
endif

#Boy is this a hack.  The dir is set to the current directory.
#Later on in the compile-java function it will be used in
#a foreach command
ifndef FIND_SOURCE_CMD
dir=.
FIND_SOURCE_CMD=find $(dir) -name "*.java" -print
endif

ifndef NOSOURCE
	THEJAVASOURCE:=$(shell $(FIND_SOURCE_CMD))
endif


DATE=`date`
COMMENT="jar built by clearmake on $(DATE)"


##########################################################
#
define display-java-version
	$(JAVA) -version
endef


##########################################################
#
define verify-classpath
    /usr/bin/echo $(MYCLASSPATH) | /usr/bin/egrep "(::|:$|^:)" > /dev/null; \
	if [ $$? -eq 0 ]; then \
		/usr/bin/echo "CLASSPATH has macros that expanded to nothing!"; \
		exit 1; \
	fi
endef


##########################################################
#
define compile-java
	$(verify-classpath)
    if [ ! -d $(CLASSDIR) ]; then \
        mkdir $(CLASSDIR); \
    fi;
	CLEANING=0; \
	$(clean-classes)
	echo Building the $(JAVA_DIRS) classes... and fast! ; \
	$(display-java-version); \
	echo $(JAVAC) $(JFLAGS) -d $(CLASSDIR) -classpath $(MYCLASSPATH); \
	time $(JAVAC) $(JFLAGS) -d $(CLASSDIR) -classpath $(MYCLASSPATH) $(foreach dir, $(JAVA_DIRS), `$(FIND_SOURCE_CMD)` )
endef



##########################################################
#
#This one is used for jarring everything in the CLASSDIR
define jar-all
ifdef BUILDCHECKOUT
	RELEASE_JAR=$(RELEASE_JAR);\
	CLEANING=0;\
	$(uninstall)
endif
	echo "Jarring everything...."; \
	/usr/bin/rm -f $(TMP_JAR)
	cd $(CLASSDIR); \
    if [ -f $(MANIFEST_FILE) ]; then \
        time $(JAR) $(JARMFLAGS) $(MANIFEST_FILE) $(TMP_JAR)  *; \
    else \
        time $(JAR) $(JARFLAGS) $(TMP_JAR)  *; \
    fi
ifdef BUILDCHECKOUT
	RELEASE_JAR=$(RELEASE_JAR);\
	TMP_JAR=$(TMP_JAR);\
	$(stage-install)
endif
endef

##########################################################
#
#This one is used to jar just classes and java files
define jar-java-classes
ifdef BUILDCHECKOUT
	RELEASE_JAR=$(RELEASE_JAVA_JAR);\
	CLEANING=0;\
	$(uninstall)
endif
	echo "Jarring all classes and java files...."; \
	/usr/bin/rm -f $(TMP_JAVA_JAR); \
	touch $(TMP_JAVA_JAR); \
	$(foreach dir2, $(JAVA_DIRS), if [ -d $(dir2)/$(PACKAGE_DIR) ];then cd $(dir2); $(JAR) -uf $(TMP_JAVA_JAR)  -C $(dir2) `$(FIND_SOURCE_CMD)`;  fi;) \
	cd $(CLASSDIR);\
    if [ -f $(MANIFEST_FILE) ]; then \
	    time $(JAR) -umf $(MANIFEST_FILE) $(TMP_JAVA_JAR) $(PACKAGE_DIR) $(CTCONFIGSPEC) $(BUILD_VERSION); \
    else \
	    time $(JAR) -uf $(TMP_JAVA_JAR) $(PACKAGE_DIR) $(CTCONFIGSPEC) $(BUILD_VERSION); \
    fi
ifdef BUILDCHECKOUT
	RELEASE_JAR=$(RELEASE_JAVA_JAR);\
	TMP_JAR=$(TMP_JAVA_JAR);\
	$(stage-install)
endif
endef


##########################################################
#
#This one is used to jar just classes
define jar-classes
ifdef BUILDCHECKOUT
	RELEASE_JAR=$(RELEASE_JAR);\
    CLEANING=0;\
	$(uninstall)
endif
	echo "Jarring all class files...."; \
	cd $(CLASSDIR); \
    if [ -f $(MANIFEST_FILE) ]; then \
	    time $(JAR) $(JARMFLAGS) $(MANIFEST_FILE) $(TMP_JAR) $(PACKAGE_DIR) $(CTCONFIGSPEC) $(BUILD_VERSION); \
    else \
	    time $(JAR) $(JARFLAGS) $(TMP_JAR) $(PACKAGE_DIR) $(CTCONFIGSPEC) $(BUILD_VERSION); \
    fi
ifdef BUILDCHECKOUT
	RELEASE_JAR=$(RELEASE_JAR);\
	TMP_JAR=$(TMP_JAR);\
	$(stage-install)
endif
endef



##########################################################
#
#This one is used to jar just classes and java files
define jar-java
ifdef BUILDCHECKOUT
	RELEASE_JAR=$(RELEASE_JAVA_JAR);\
	CLEANING=0;\
	$(uninstall)
endif
	echo "Jarring all java files...."; \
	/usr/bin/rm -f $(TMP_JAVA_JAR); \
	touch $(TMP_JAVA_JAR); \
	$(foreach dir2, $(JAVA_DIRS), cd $(dir2); $(JAR) -uf $(TMP_JAVA_JAR)  -C $(dir2) `$(FIND_SOURCE_CMD)`;) \
	cd $(CLASSDIR);\
    if [ -f $(MANIFEST_FILE) ]; then \
	    time $(JAR) -umf $(MANIFEST_FILE) $(TMP_JAVA_JAR) $(CTCONFIGSPEC) $(BUILD_VERSION); \
    else \
	    time $(JAR) -uf $(TMP_JAVA_JAR) $(CTCONFIGSPEC) $(BUILD_VERSION); \
    fi
ifdef BUILDCHECKOUT
	RELEASE_JAR=$(RELEASE_JAVA_JAR);\
	TMP_JAR=$(TMP_JAVA_JAR);\
	$(stage-install)
endif
endef


##########################################################
#
# Before calling this function define the following ENV vars
# RELEASE_JAR  - the jar to checkout
# TMP_JAR  - the jar to 
define stage-install
ifndef NOCHECKOUT
	$(CT) lsco -cview $$RELEASE_JAR | $(GREP) $$RELEASE_JAR >/dev/null 2>&1; \
	if [ $$? -ne 0 ]; then \
		$(CT) co -unres  -c $(COMMENT) $$RELEASE_JAR ;\
	fi;\
	/usr/bin/mv $$TMP_JAR $$RELEASE_JAR
else
	/usr/bin/mv $$TMP_JAR $$RELEASE_JAR
endif
endef




##########################################################
# The following defines are for conditionally building
# the install function based on which jars are being 
# used. (gnu make does not support nested conditionals)
#
define ci-release-jar
ifdef RELEASE_JAR
	$(CT) lsco -cview $(RELEASE_JAR) | $(GREP) $(RELEASE_JAR) >/dev/null 2>&1; \
	if [ $$? -eq 0 ]; then \
		$(CT) ci -nc $(RELEASE_JAR); \
	fi
endif
endef

define co-unres-release-jar
ifdef RELEASE_JAR
	$(CT) lsco -cview $(RELEASE_JAR) | $(GREP) $(RELEASE_JAR) >/dev/null 2>&1; \
	if [ $$? -ne 0 ]; then \
	    $(CT) co -unres -c $(COMMENT) $(RELEASE_JAR) ;\
	fi
endif
endef

define co-release-jar
ifdef RELEASE_JAR
	$(CT) lsco -cview $(RELEASE_JAR) | $(GREP) $(RELEASE_JAR) >/dev/null 2>&1; \
	if [ $$? -ne 0 ]; then \
	    $(CT) co -c $(COMMENT) $(RELEASE_JAR) ;\
	fi
endif
endef

define copy-release-jar
ifdef RELEASE_JAR
	/usr/bin/cp $(TMP_JAR) $(RELEASE_JAR)
endif
endef

define ci-release-java-jar
ifdef RELEASE_JAVA_JAR
	$(CT) lsco -cview $(RELEASE_JAVA_JAR) | $(GREP) $(RELEASE_JAVA_JAR) >/dev/null 2>&1; \
	if [ $$? -eq 0 ]; then \
		$(CT) ci -nc $(RELEASE_JAVA_JAR); \
	fi
endif
endef

define co-release-java-jar
ifdef RELEASE_JAVA_JAR
	$(CT) lsco -cview $(RELEASE_JAVA_JAR) | $(GREP) $(RELEASE_JAVA_JAR) >/dev/null 2>&1; \
	if [ $$? -ne 0 ]; then \
	    $(CT) co -c $(COMMENT) $(RELEASE_JAVA_JAR); \
	fi
endif
endef

define copy-release-java-jar
ifdef RELEASE_JAVA_JAR
	/usr/bin/cp $(TMP_JAVA_JAR) $(RELEASE_JAVA_JAR)
endif
endef


define install
ifdef BUILDCHECKOUT
	$(ci-release-jar)

	VERSIONSRC=$(VERSIONSRC); \
	CLASSESOUT=$(CLASSDIR); \
	$(install-version);

	$(ci-release-java-jar)
else
	$(co-release-jar)
	$(copy-release-jar)
	$(ci-release-jar)

	$(co-release-java-jar)
	$(copy-release-java-jar)
	$(ci-release-java-jar)
endif #BUILDCHECKOUT
endef #install


##########################################################
#
#
define uninstall
	$(CT) lsco -cview $$RELEASE_JAR | $(GREP) $$RELEASE_JAR >/dev/null 2>&1; \
	if [ $$? -eq 0 ]; then \
	    if [ $$CLEANING -eq 1 ]; then \
		    $(CT) unco -rm $$RELEASE_JAR; \
        else \
            /usr/bin/rm -f $$RELEASE_JAR; \
            CURVER=`$(CT) ls $$RELEASE_JAR | /usr/bin/nawk '{print $$3}'`; \
            LATESTVER=`echo $$CURVER | sed "s/[0-9]*$$/LATEST/"`; \
            $(CT) find $$RELEASE_JAR@@ -version "version($$CURVER)&& version($$LATESTVER)" -print | grep $$RELEASE_JAR > /dev/null 2>&1; \
	        if [ $$? -eq 1 ]; then \
		        $(CT) unco -rm $$RELEASE_JAR; \
            fi; \
        fi; \
	fi
endef


##########################################################
#
#
define clean-classes
	if [ "$(CLASSDIR)" = "" ];then \
		echo "The CLASSDIR macro is undefined, cannot clean"; \
		exit 1; \
	fi; \
	echo Cleaning up classes...; \
	/usr/bin/rm -rf $(CLASSDIR)/*; \
	$(CT) lsco -cview $(VERSIONSRC) | $(GREP) $(VERSIONSRC) >/dev/null 2>&1; \
	if [ $$? -eq 0 ]; then \
		VERSIONSRC=$(VERSIONSRC); \
		$(uninstall-version); \
	fi
endef

##########################################################
#
#
define clean-java
	if [ "$(JAVADIR)" = "" ];then \
		echo "The JAVADIR macro is undefined, cannot clean"; \
		exit 1; \
	fi;
	echo Cleaning up java...
	/usr/bin/rm -rf $(JAVADIR)/*;
endef


##########################################################
#
#
define clean-manifest
	if [ -f $(MANIFEST_FILE) ]; then \
	    /usr/bin/rm -f $(MANIFEST_FILE); \
	fi
endef


#if this target is hit assume that "all" is the default
default_target: all

gen_clean:: gen_uninstall
	@CLEANING=1; \
	$(clean-classes); \
    $(clean-manifest)

gen_stage_install::
	@RELEASE_JAR=$(RELEASE_JAR);\
	TMP_JAR=$(TMP_JAR);\
	$(stage-install)

ifndef BUILDCHECKOUT
gen_install:: $(RELEASE_JAR) $(RELEASE_JAVA_JAR)
	@$(ci-release-jar)
	@$(ci-release-java-jar)

$(RELEASE_JAR): $(BUILD_JAR)
	@$(co-unres-release-jar)
	@$(copy-release-jar)

$(RELEASE_JAVA_JAR): $(BUILD_JAVA_JAR)
	@$(co-release-java-jar)
	@$(copy-release-java-jar)

else

gen_install::
	@$(install)

endif #BUILDCHECKOUT

gen_uninstall::
ifdef RELEASE_JAR
	@RELEASE_JAR=$(RELEASE_JAR);\
	CLEANING=1;\
	$(uninstall)
endif
ifdef RELEASE_JAVA_JAR
	@RELEASE_JAR=$(RELEASE_JAVA_JAR);\
	CLEANING=1;\
	$(uninstall)
endif

.DEPENDENCY_IGNORED_FOR_REUSE: %.version

#This target is to create the version file
#Script changes will be ignored
#We will explicitly depend on the increment version file
#so if it changes we still rebuild (even ignoring script diffs)
.NO_CMP_SCRIPT:$(BUILD_VERSION)
$(BUILD_VERSION):: $(INCREMENT_VERSION_MK)  $(JAVA_COMPILE)
	@VERSIONSRC=$(VERSIONSRC); \
	CLASSESOUT=$(CLASSDIR); \
	$(build-version); \
	/usr/bin/touch $(BUILD_VERSION)

#This target is to create a manifest file
#Script changes will be ignored
.NO_CMP_SCRIPT:$(MANIFEST_FILE)
$(MANIFEST_FILE):: $(JAVA_COMPILE)
	/usr/bin/echo $(MANIFEST) > $(MANIFEST_FILE)


$(JAVA_COMPILE):: $(THECOMPILEDEPENDENCIES) $(THEJAVASOURCE) $(BUILD_ALL)
	@$(compile-java)
	@touch $(JAVA_COMPILE)
	@$(CT) $(CTCONFIGFLAGS) > $(CLASSDIR)/$(CTCONFIGSPEC)
	@cat $(BUILD_ALL) > /dev/null

#This is a generic target to execute a command specified in the COMMAND macro.  The command will be executed with the specified MYCLASSPATH
#for example: clearmake -C gnu COMMAND="javap com.cboe.util.Price" exec
exec:
	@CLASSPATH=$(MYCLASSPATH); export CLASSPATH;$(COMMAND)

compile_package:
	$(JAVAC) $(JFLAGS) -d $(CLASSDIR) -classpath $(MYCLASSPATH) *.java

compile_package_recurse:
	$(JAVAC) $(JFLAGS) -d $(CLASSDIR) -classpath $(MYCLASSPATH) `$(FIND_SOURCE_CMD)`

classpath:
	@echo $(MYCLASSPATH)
	@$(verify-classpath)

%.class : %.java
	$(JAVAC) $(JFLAGS) -d $(CLASSDIR) -classpath $(MYCLASSPATH) $<


