#######################################################################
# All macros defined in this file should be globally unique across
# all release and build components
#
# This makefile makes reference to macros defined elsewhere.  Please
# define the following when including this file
#   CLIENT_IDL = the directory location for the idl files
#   CLIENT_REL = the directory location for the jar files
#
#######################################################################

#######################################################################
# Defines specific jars and locations for this build component 
# 
#######################################################################
CLIENT_IDL_JAVA            =$(CLIENT_IDL)/trc_output/java
CLIENT_IDL_CLASS           =$(CLIENT_IDL)/trc_output/classes
CLIENT_IDL_JAVA_JARNAME    =client_idl_java
CLIENT_IDL_JARNAME         =client_idl
CLIENT_IDL_JAVA_JAR        =$(CLIENT_REL)/$(CLIENT_IDL_JAVA_JARNAME).$(JAR_EXT)
CLIENT_IDL_JAR             =$(CLIENT_REL)/$(CLIENT_IDL_JARNAME).$(JAR_EXT)
CLIENT_IDL_VERSIONSRC      =$(CLIENT_IDL)/$(CLIENT_IDL_JARNAME).$(VERSION_EXT)

ifdef BUILDCHECKOUT
    CLIENT_IDL_JAVA_BUILD_JAR = $(CLIENT_IDL_JAVA_JAR)
    CLIENT_IDL_BUILD_JAR      = $(CLIENT_IDL_JAR)
else
    CLIENT_IDL_JAVA_BUILD_JAR = $(CLIENT_IDL_CLASS)/$(CLIENT_IDL_JAVA_JARNAME).$(JAR_EXT)
    CLIENT_IDL_BUILD_JAR      = $(CLIENT_IDL_CLASS)/$(CLIENT_IDL_JARNAME).$(JAR_EXT)
endif


#######################################################################
# Defines the idl files to build
# 
#######################################################################

CLIENT_IDL_FILES= \
	    clientCallbackServices.idn




