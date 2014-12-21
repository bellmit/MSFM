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
CLIENT_INTERNALTRANSLATOR_JAVA        =$(CLIENT_JAVA)/internalTranslator
CLIENT_INTERNALTRANSLATOR_CLASS       =$(CLIENT_CLASSES)/internalTranslator
CLIENT_INTERNALTRANSLATOR_JARNAME     =client_internalTranslator
CLIENT_INTERNALTRANSLATOR_JAR         =$(CLIENT_REL)/$(CLIENT_INTERNALTRANSLATOR_JARNAME).$(JAR_EXT)
CLIENT_INTERNALTRANSLATOR_VERSIONSRC  =$(CLIENT_INTERNALTRANSLATOR_JAVA)/$(CLIENT_INTERNALTRANSLATOR_JARNAME).$(VERSION_EXT)

ifdef BUILDCHECKOUT
    CLIENT_INTERNALTRANSLATOR_BUILD_JAR = $(CLIENT_INTERNALTRANSLATOR_JAR)
else
    CLIENT_INTERNALTRANSLATOR_BUILD_JAR = $(CLIENT_INTERNALTRANSLATOR_CLASS)/$(CLIENT_INTERNALTRANSLATOR_JARNAME).$(JAR_EXT)
endif
