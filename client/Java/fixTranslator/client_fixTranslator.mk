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
CLIENT_FIXTRANSLATOR_JAVA        =$(CLIENT_JAVA)/fixTranslator
CLIENT_FIXTRANSLATOR_CLASS       =$(CLIENT_CLASSES)/fixTranslator
CLIENT_FIXTRANSLATOR_JARNAME     =client_fixTranslator
CLIENT_FIXTRANSLATOR_JAR         =$(CLIENT_REL)/$(CLIENT_FIXTRANSLATOR_JARNAME).$(JAR_EXT)
CLIENT_FIXTRANSLATOR_VERSIONSRC  =$(CLIENT_FIXTRANSLATOR_JAVA)/$(CLIENT_FIXTRANSLATOR_JARNAME).$(VERSION_EXT)

ifdef BUILDCHECKOUT
    CLIENT_FIXTRANSLATOR_BUILD_JAR = $(CLIENT_FIXTRANSLATOR_JAR)
else
    CLIENT_FIXTRANSLATOR_BUILD_JAR = $(CLIENT_FIXTRANSLATOR_CLASS)/$(CLIENT_FIXTRANSLATOR_JARNAME).$(JAR_EXT)
endif
