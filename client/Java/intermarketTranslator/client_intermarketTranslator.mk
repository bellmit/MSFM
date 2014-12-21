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
CLIENT_INTERMARKETTRANSLATOR_JAVA        =$(CLIENT_JAVA)/intermarketTranslator
CLIENT_INTERMARKETTRANSLATOR_CLASS       =$(CLIENT_CLASSES)/intermarketTranslator
CLIENT_INTERMARKETTRANSLATOR_JARNAME     =client_intermarketTranslator
CLIENT_INTERMARKETTRANSLATOR_JAR         =$(CLIENT_REL)/$(CLIENT_INTERMARKETTRANSLATOR_JARNAME).$(JAR_EXT)
CLIENT_INTERMARKETTRANSLATOR_VERSIONSRC  =$(CLIENT_INTERMARKETTRANSLATOR_JAVA)/$(CLIENT_INTERMARKETTRANSLATOR_JARNAME).$(VERSION_EXT)

ifdef BUILDCHECKOUT
    CLIENT_INTERMARKETTRANSLATOR_BUILD_JAR = $(CLIENT_INTERMARKETTRANSLATOR_JAR)
else
    CLIENT_INTERMARKETTRANSLATOR_BUILD_JAR = $(CLIENT_INTERMARKETTRANSLATOR_CLASS)/$(CLIENT_INTERMARKETTRANSLATOR_JARNAME).$(JAR_EXT)
endif
