#######################################################################
# All macros defined in this file should be globally unique across
# all release and build components
#
# This makefile makes reference to macros defined elsewhere.  Please
# define the following when including this file
#   COMMONTEST_JAVA = the directory location for the java files
#   COMMONTEST_CLASSES = the directory location for the class files
#   COMMONTEST_REL = the directory location for the jar files
#
#######################################################################

#######################################################################
# Defines specific jars and locations for this build component
#
#######################################################################
COMMONTEST_UTIL_JAVA        =$(COMMONTEST_JAVA)/util
COMMONTEST_UTIL_CLASS       =$(COMMONTEST_CLASSES)/util
COMMONTEST_UTIL_JARNAME     =commontest
COMMONTEST_UTIL_JAR         =$(COMMONTEST_REL)/$(COMMONTEST_UTIL_JARNAME).$(JAR_EXT)

ifdef BUILDCHECKOUT
    COMMONTEST_UTIL_BUILD_JAR = $(COMMONTEST_UTIL_JAR)
else
    COMMONTEST_UTIL_BUILD_JAR = $(COMMONTEST_UTIL_CLASS)/$(COMMONTEST_UTIL_JARNAME).$(JAR_EXT)
endif

