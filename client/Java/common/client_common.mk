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
CLIENT_COMMON_JAVA        =$(CLIENT_JAVA)/common
CLIENT_COMMON_CLASS       =$(CLIENT_CLASSES)/common
CLIENT_COMMON_JARNAME     =client_common
CLIENT_COMMON_JAR         =$(CLIENT_REL)/$(CLIENT_COMMON_JARNAME).$(JAR_EXT)
CLIENT_COMMON_VERSIONSRC  =$(CLIENT_COMMON_JAVA)/$(CLIENT_COMMON_JARNAME).$(VERSION_EXT)

ifdef BUILDCHECKOUT
    CLIENT_COMMON_BUILD_JAR = $(CLIENT_COMMON_JAR)
else
    CLIENT_COMMON_BUILD_JAR = $(CLIENT_COMMON_CLASS)/$(CLIENT_COMMON_JARNAME).$(JAR_EXT)
endif
