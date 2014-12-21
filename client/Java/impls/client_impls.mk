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
CLIENT_IMPLS_JAVA        =$(CLIENT_JAVA)/impls
CLIENT_IMPLS_CLASS       =$(CLIENT_CLASSES)/impls
CLIENT_IMPLS_JARNAME     =client_impls
CLIENT_IMPLS_JAR         =$(CLIENT_REL)/$(CLIENT_IMPLS_JARNAME).$(JAR_EXT)
CLIENT_IMPLS_VERSIONSRC  =$(CLIENT_IMPLS_JAVA)/$(CLIENT_IMPLS_JARNAME).$(VERSION_EXT)

ifdef BUILDCHECKOUT
    CLIENT_IMPLS_BUILD_JAR = $(CLIENT_IMPLS_JAR)
else
    CLIENT_IMPLS_BUILD_JAR = $(CLIENT_IMPLS_CLASS)/$(CLIENT_IMPLS_JARNAME).$(JAR_EXT)
endif
