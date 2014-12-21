#######################################################################
# All macros defined in this file should be globally unique across
# all release and build components
#
# This makefile makes reference to macros defined elsewhere.  Please
# define the following when including this file
#   CLIENT_JAVA = the directory location for the java files
#   CLIENT_CLASSES = the directory location for the class files
#   CLIENT_REL = the directory location for the jar files
#
#######################################################################

#######################################################################
# Defines specific jars and locations for this build component 
# 
#######################################################################
CLIENT_TEST_JAVA        =$(CLIENT_JAVA)/test
CLIENT_TEST_CLASS       =$(CLIENT_CLASSES)/test
CLIENT_TEST_JARNAME     =client_test
CLIENT_TEST_JAR         =$(CLIENT_REL)/$(CLIENT_TEST_JARNAME).$(JAR_EXT)
CLIENT_TEST_VERSIONSRC  =$(CLIENT_TEST_JAVA)/$(CLIENT_TEST_JARNAME).$(VERSION_EXT)

ifdef BUILDCHECKOUT
    CLIENT_TEST_BUILD_JAR = $(CLIENT_TEST_JAR)
else
    CLIENT_TEST_BUILD_JAR = $(CLIENT_TEST_CLASS)/$(CLIENT_TEST_JARNAME).$(JAR_EXT)
endif
