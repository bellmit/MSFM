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
DOMAIN_TEST_JAVA        =$(DOMAIN_JAVA)/test
DOMAIN_TEST_CLASS       =$(DOMAIN_CLASSES)/test
DOMAIN_TEST_JARNAME     =domain_test
DOMAIN_TEST_JAR         =$(DOMAIN_REL)/$(DOMAIN_TEST_JARNAME).$(JAR_EXT)
DOMAIN_TEST_VERSIONSRC  =$(DOMAIN_TEST_JAVA)/$(DOMAIN_TEST_JARNAME).$(VERSION_EXT)

ifdef BUILDCHECKOUT
    DOMAIN_TEST_BUILD_JAR = $(DOMAIN_TEST_JAR)
else
    DOMAIN_TEST_BUILD_JAR = $(DOMAIN_TEST_CLASS)/$(DOMAIN_TEST_JARNAME).$(JAR_EXT)
endif
