#######################################################################
# All macros defined in this file should be globally unique across
# all release and build components
#
# This makefile makes reference to macros defined elsewhere.  Please
# define the following when including this file
#   DOMAIN_JAVA = the directory location for the java files
#   DOMAIN_CLASSES = the directory location for the class files
#   DOMAIN_REL = the directory location for the jar files
#
#######################################################################

#######################################################################
# Defines specific jars and locations for this build component 
# 
#######################################################################
DOMAIN_PERSIST_JAVA            =$(DOMAIN_JAVA)/persist
DOMAIN_PERSIST_CLASS           =$(DOMAIN_CLASSES)/persist
DOMAIN_PERSIST_JARNAME         =domain_persist
DOMAIN_PERSIST_JAR             =$(DOMAIN_REL)/$(DOMAIN_PERSIST_JARNAME).$(JAR_EXT)
DOMAIN_PERSIST_BUILD_JAR       =$(DOMAIN_CLASSES)/$(DOMAIN_PERSIST_JARNAME).$(JAR_EXT)
DOMAIN_PERSIST_VERSIONSRC      =$(DOMAIN_PERSIST_JAVA)/$(DOMAIN_PERSIST_JARNAME).$(VERSION_EXT)

ifdef BUILDCHECKOUT
    DOMAIN_PERSIST_BUILD_JAR = $(DOMAIN_PERSIST_JAR)
else
    DOMAIN_PERSIST_BUILD_JAR = $(DOMAIN_PERSIST_CLASS)/$(DOMAIN_PERSIST_JARNAME).$(JAR_EXT)
endif
