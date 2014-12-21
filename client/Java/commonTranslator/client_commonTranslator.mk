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
CLIENT_COMMONTRANSLATOR_JAVA        =$(CLIENT_JAVA)/commonTranslator
CLIENT_COMMONTRANSLATOR_CLASS       =$(CLIENT_CLASSES)/commonTranslator
CLIENT_COMMONTRANSLATOR_JARNAME     =client_commonTranslator
CLIENT_COMMONTRANSLATOR_JAR         =$(CLIENT_REL)/$(CLIENT_COMMONTRANSLATOR_JARNAME).$(JAR_EXT)
CLIENT_COMMONTRANSLATOR_VERSIONSRC  =$(CLIENT_COMMONTRANSLATOR_JAVA)/$(CLIENT_COMMONTRANSLATOR_JARNAME).$(VERSION_EXT)

ifdef BUILDCHECKOUT
    CLIENT_COMMONTRANSLATOR_BUILD_JAR = $(CLIENT_COMMONTRANSLATOR_JAR)
else
    CLIENT_COMMONTRANSLATOR_BUILD_JAR = $(CLIENT_COMMONTRANSLATOR_CLASS)/$(CLIENT_COMMONTRANSLATOR_JARNAME).$(JAR_EXT)
endif
