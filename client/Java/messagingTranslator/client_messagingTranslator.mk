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
CLIENT_MESSAGINGTRANSLATOR_JAVA        =$(CLIENT_JAVA)/messagingTranslator
CLIENT_MESSAGINGTRANSLATOR_CLASS       =$(CLIENT_CLASSES)/messagingTranslator
CLIENT_MESSAGINGTRANSLATOR_JARNAME     =client_messagingTranslator
CLIENT_MESSAGINGTRANSLATOR_JAR         =$(CLIENT_REL)/$(CLIENT_MESSAGINGTRANSLATOR_JARNAME).$(JAR_EXT)
CLIENT_MESSAGINGTRANSLATOR_VERSIONSRC  =$(CLIENT_MESSAGINGTRANSLATOR_JAVA)/$(CLIENT_MESSAGINGTRANSLATOR_JARNAME).$(VERSION_EXT)

ifdef BUILDCHECKOUT
    CLIENT_MESSAGINGTRANSLATOR_BUILD_JAR = $(CLIENT_MESSAGINGTRANSLATOR_JAR)
else
    CLIENT_MESSAGINGTRANSLATOR_BUILD_JAR = $(CLIENT_MESSAGINGTRANSLATOR_CLASS)/$(CLIENT_MESSAGINGTRANSLATOR_JARNAME).$(JAR_EXT)
endif
