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
include $(COMMONFACILITIES)/build/CommonFacilities.mk
include $(CMI)/build/cmi.mk
include $(EVENT)/build/event.mk
include $(SERVER)/build/server.mk

#######################################################################
# Defines the build classpath for this build component.
# Only those jars that are needed to build this component should be 
# referenced.
# 
#######################################################################
DOMAIN_INTERFACES_CLASSPATH =$(DOMAIN_INTERFACES_CLASS):$(CMI_IDL_JAR):$(DOMAIN_IDL_BUILD_JAR):$(EVENT_IDL_BUILD_JAR):$(EVENT_INTERFACES_BUILD_JAR):$(SERVER_IDL_BUILD_JAR):$(COMMONFACILITIES_BUILD_JAR):$(FFJAR):$(FFPERSISTJAR):$(OBJWAVEJAR):$(MSJARSET):$(SSINTJAR):$(IVIDLCLASSESJAR):$(LGINTCLASSESJAR):$(CONCURJAR)


#######################################################################
# The dependencies for this component.  This is intended to be used 
# in the dependency list in Makefile targets.
# 
#######################################################################
DOMAIN_INTERFACES_DEPENDS=$(filter %.jar,$(subst :, ,$(DOMAIN_INTERFACES_CLASSPATH)))


