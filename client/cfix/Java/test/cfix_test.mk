#######################################################################
# All macros defined in this file should be globally unique across
# all release and build components
#
# This makefile makes reference to macros defined elsewhere.  Please
# define the following when including this file
#   CFIX_JAVA = the directory location for the java files
#   CFIX_CLASSES = the directory location for the class files
#   CFIX_REL = the directory location for the jar files
#
#######################################################################

#######################################################################
# Defines specific jars and locations for this build component 
# 
#######################################################################
CFIX_TEST_JAVA        =$(CFIX_JAVA)/test
CFIX_TEST_CLASS       =$(CFIX_CLASSES)/test
CFIX_TEST_JARNAME     =cfix_test
CFIX_TEST_JAR         =$(CFIX_REL)/$(CFIX_TEST_JARNAME).$(JAR_EXT)
CFIX_TEST_VERSIONSRC  =$(CFIX_TEST_JAVA)/$(CFIX_TEST_JARNAME).$(VERSION_EXT)

ifdef BUILDCHECKOUT
    CFIX_TEST_BUILD_JAR = $(CFIX_TEST_JAR)
else
    CFIX_TEST_BUILD_JAR = $(CFIX_TEST_CLASS)/$(CFIX_TEST_JARNAME).$(JAR_EXT)
endif
