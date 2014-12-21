#######################################################################
# All macros defined in this file should be globally unique across
# all release and build components
#
# This makefile makes reference to macros defined elsewhere.  Please
# define the following when including this file
#
#######################################################################

#######################################################################
# include external component definitions
#
#######################################################################
#for now they are all defined globally
include /vobs/dte/sbtcommon/build/defs/sbt.mk
include $(CMI)/build/cmi.mk
include $(SERVER)/build/server.mk
include $(DOMAIN)/build/domain.mk

#######################################################################
# Defines the build classpath for this build component.
# Only those jars that are needed to build this component should be 
# referenced.
# 
#######################################################################
EVENT_IDL_CLASSPATH =$(EVENT_IDL_CLASS):$(CMI_IDL_JAR):$(DOMAIN_IDL_BUILD_JAR):$(SERVER_IDL_BUILD_JAR):$(IVIDLCLASSESJAR):$(OMGBASECLASSESJAR):$(OMGSERVICECLASSESJAR):$(MSIMPJAR)

#######################################################################
# The dependencies for this component.  This is intended to be used 
# in the dependency list in Makefile targets.
# 
#######################################################################
EVENT_IDL_DEPENDS=$(filter %.jar,$(subst :, ,$(EVENT_IDL_CLASSPATH)))


