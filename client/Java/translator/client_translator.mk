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
CLIENT_TRANSLATOR_JAVA        =$(CLIENT_JAVA)/translator
CLIENT_TRANSLATOR_CLASS       =$(CLIENT_CLASSES)/translator
CLIENT_TRANSLATOR_JARNAME     =client_translator
CLIENT_TRANSLATOR_JAR         =$(CLIENT_REL)/$(CLIENT_TRANSLATOR_JARNAME).$(JAR_EXT)
CLIENT_TRANSLATOR_VERSIONSRC  =$(CLIENT_TRANSLATOR_JAVA)/$(CLIENT_TRANSLATOR_JARNAME).$(VERSION_EXT)

ifdef BUILDCHECKOUT
    CLIENT_TRANSLATOR_BUILD_JAR = $(CLIENT_TRANSLATOR_JAR)
else
    CLIENT_TRANSLATOR_BUILD_JAR = $(CLIENT_TRANSLATOR_CLASS)/$(CLIENT_TRANSLATOR_JARNAME).$(JAR_EXT)
endif
