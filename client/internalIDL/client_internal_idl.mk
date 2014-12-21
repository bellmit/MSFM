#######################################################################
# All macros defined in this file should be globally unique across
# all release and build components
#
# This makefile makes reference to macros defined elsewhere.  Please
# define the following when including this file
#   CLIENT_INTERNAL_IDL = the directory location for the idl files
#   CLIENT_REL = the directory location for the jar files
#
#######################################################################

#######################################################################
# Defines specific jars and locations for this build component 
# 
#######################################################################
CLIENT_INTERNAL_IDL_JAVA            =$(CLIENT_INTERNAL_IDL)/trc_output/java
CLIENT_INTERNAL_IDL_CLASS           =$(CLIENT_INTERNAL_IDL)/trc_output/classes
CLIENT_INTERNAL_IDL_JAVA_JARNAME    =client_internal_idl_java
CLIENT_INTERNAL_IDL_JARNAME         =client_internal_idl
CLIENT_INTERNAL_IDL_JAVA_JAR        =$(CLIENT_REL)/$(CLIENT_INTERNAL_IDL_JAVA_JARNAME).$(JAR_EXT)
CLIENT_INTERNAL_IDL_JAR             =$(CLIENT_REL)/$(CLIENT_INTERNAL_IDL_JARNAME).$(JAR_EXT)
CLIENT_INTERNAL_IDL_VERSIONSRC      =$(CLIENT_INTERNAL_IDL)/$(CLIENT_INTERNAL_IDL_JARNAME).$(VERSION_EXT)


ifdef BUILDCHECKOUT
    CLIENT_INTERNAL_IDL_JAVA_BUILD_JAR = $(CLIENT_INTERNAL_IDL_JAVA_JAR)
    CLIENT_INTERNAL_IDL_BUILD_JAR      = $(CLIENT_INTERNAL_IDL_JAR)
else
    CLIENT_INTERNAL_IDL_JAVA_BUILD_JAR = $(CLIENT_INTERNAL_IDL_CLASS)/$(CLIENT_INTERNAL_IDL_JAVA_JARNAME).$(JAR_EXT)
    CLIENT_INTERNAL_IDL_BUILD_JAR      = $(CLIENT_INTERNAL_IDL_CLASS)/$(CLIENT_INTERNAL_IDL_JARNAME).$(JAR_EXT)
endif


#######################################################################
# Defines the idl files to build
# 
#######################################################################

CLIENT_INTERNAL_IDL_FILES= \
	pcqs.ida \
      ProductConfigurationQueryService.ida \
      omt.ida \
	    par.ida\
	    activity.ida\
	    floorApplication.ida
