#######################################################################
# All macros defined in this file should be globally unique across
# all release and build components
#
# This makefile makes reference to macros defined elsewhere.  Please
# define the following when including this file
#   EVENT_JAVA = the directory location for the java files
#   EVENT_CLASSES = the directory location for the class files
#   EVENT_REL = the directory location for the jar files
#
#######################################################################

#######################################################################
# Defines specific jars and locations for this build component 
# 
#######################################################################
EVENT_TEST_JAVA            =$(EVENT_JAVA)/test
EVENT_TEST_CLASS           =$(EVENT_CLASSES)/test
EVENT_TEST_JARNAME         =event_test
EVENT_TEST_JAR             =$(EVENT_REL)/$(EVENT_TEST_JARNAME).$(JAR_EXT)
EVENT_TEST_VERSIONSRC      =$(EVENT_TEST_JAVA)/$(EVENT_TEST_JARNAME).$(VERSION_EXT)

ifdef BUILDCHECKOUT
    EVENT_TEST_BUILD_JAR = $(EVENT_TEST_JAR)
else
    EVENT_TEST_BUILD_JAR = $(EVENT_TEST_CLASS)/$(EVENT_TEST_JARNAME).$(JAR_EXT)
endif
