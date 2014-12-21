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
include $(CMI)/build/cmi.mk
include $(DOMAIN)/build/domain.mk
include $(EVENT)/build/event.mk
include $(SERVER)/build/server.mk

#######################################################################
# Defines the build classpath for this build component.
# Only those jars that are needed to build this component should be 
# referenced.
# 
#######################################################################
CLIENT_INTERNAL_IDL_CLASSPATH =$(CLIENT_INTERNAL_IDL_CLASS):$(CMI_IDL_JAR):$(DOMAIN_IDL_BUILD_JAR):$(EVENT_IDL_BUILD_JAR):${SERVER_IDL_BUILD_JAR}:$(OMGBASECLASSESJAR):$(OMGSERVICECLASSESJAR):$(MSIMPJAR):$(SSINTJAR):$(IVIDLCLASSESJAR)


#######################################################################
# The dependencies for this component.  This is intended to be used 
# in the dependency list in Makefile targets.
# 
#######################################################################
CLIENT_INTERNAL_IDL_DEPENDS=$(filter %.jar,$(subst :, ,$(CLIENT_INTERNAL_IDL_CLASSPATH)))


