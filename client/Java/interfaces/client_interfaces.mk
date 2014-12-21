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
CLIENT_INTERFACES_JAVA        =$(CLIENT_JAVA)/interfaces
CLIENT_INTERFACES_CLASS       =$(CLIENT_CLASSES)/interfaces
CLIENT_INTERFACES_JARNAME     =client_interfaces
CLIENT_INTERFACES_JAR         =$(CLIENT_REL)/$(CLIENT_INTERFACES_JARNAME).$(JAR_EXT)
CLIENT_INTERFACES_VERSIONSRC  =$(CLIENT_INTERFACES_JAVA)/$(CLIENT_INTERFACES_JARNAME).$(VERSION_EXT)

ifdef BUILDCHECKOUT
    CLIENT_INTERFACES_BUILD_JAR = $(CLIENT_INTERFACES_JAR)
else
    CLIENT_INTERFACES_BUILD_JAR = $(CLIENT_INTERFACES_CLASS)/$(CLIENT_INTERFACES_JARNAME).$(JAR_EXT)
endif
