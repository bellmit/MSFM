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
CLIENT_INSTRUMENTATIONTRANSLATOR_JAVA        =$(CLIENT_JAVA)/instrumentationTranslator
CLIENT_INSTRUMENTATIONTRANSLATOR_CLASS       =$(CLIENT_CLASSES)/instrumentationTranslator
CLIENT_INSTRUMENTATIONTRANSLATOR_JARNAME     =client_instrumentationTranslator
CLIENT_INSTRUMENTATIONTRANSLATOR_JAR         =$(CLIENT_REL)/$(CLIENT_INSTRUMENTATIONTRANSLATOR_JARNAME).$(JAR_EXT)
CLIENT_INSTRUMENTATIONTRANSLATOR_VERSIONSRC  =$(CLIENT_INSTRUMENTATIONTRANSLATOR_JAVA)/$(CLIENT_INSTRUMENTATIONTRANSLATOR_JARNAME).$(VERSION_EXT)

ifdef BUILDCHECKOUT
    CLIENT_INSTRUMENTATIONTRANSLATOR_BUILD_JAR = $(CLIENT_INSTRUMENTATIONTRANSLATOR_JAR)
else
    CLIENT_INSTRUMENTATIONTRANSLATOR_BUILD_JAR = $(CLIENT_INSTRUMENTATIONTRANSLATOR_CLASS)/$(CLIENT_INSTRUMENTATIONTRANSLATOR_JARNAME).$(JAR_EXT)
endif
