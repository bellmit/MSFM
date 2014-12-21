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
CLIENT_INTERCEPTORS_JAVA        =$(CLIENT_JAVA)/interceptors
CLIENT_INTERCEPTORS_CLASS       =$(CLIENT_CLASSES)/interceptors
CLIENT_INTERCEPTORS_JARNAME     =client_interceptors
CLIENT_INTERCEPTORS_JAR         =$(CLIENT_REL)/$(CLIENT_INTERCEPTORS_JARNAME).$(JAR_EXT)
CLIENT_INTERCEPTORS_VERSIONSRC  =$(CLIENT_INTERCEPTORS_JAVA)/$(CLIENT_INTERCEPTORS_JARNAME).$(VERSION_EXT)

ifdef BUILDCHECKOUT
    CLIENT_INTERCEPTORS_BUILD_JAR = $(CLIENT_INTERCEPTORS_JAR)
else
    CLIENT_INTERCEPTORS_BUILD_JAR = $(CLIENT_INTERCEPTORS_CLASS)/$(CLIENT_INTERCEPTORS_JARNAME).$(JAR_EXT)
endif
