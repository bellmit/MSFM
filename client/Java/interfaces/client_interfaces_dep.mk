#######################################################################
# All macros defined in this file should be globally unique across
# all release and build components
#
#######################################################################

#######################################################################
# include external component definitions
# These are references to components other than the current component
#
#######################################################################
include $(COMMONFACILITIES)/build/CommonFacilities.mk
include $(DOMAIN)/build/domain.mk
include $(EVENT)/build/event.mk
include $(SERVER)/build/server.mk
include $(CMI)/build/cmi.mk

#######################################################################
# Defines the build classpath for this build component.
# Only those jars that are needed to build this component should be 
# referenced.
# 
#######################################################################

CLIENT_INTERFACES_CLASSPATH=$(CLIENT_INTERFACES_CLASS):$(CMI_IDL_JAR):$(DOMAIN_INTERFACES_BUILD_JAR):$(EVENT_INTERFACES_BUILD_JAR):$(COMMONFACILITIES_BUILD_JAR):$(JUNITJAR):$(CLIENT_IDL_BUILD_JAR):$(CLIENT_INTERNAL_IDL_BUILD_JAR):$(DOMAIN_IDL_BUILD_JAR):$(IVIDLCLASSESJAR):$(SERVER_INTERFACES_BUILD_JAR):$(SERVER_IDL_BUILD_JAR):$(EVENT_IDL_BUILD_JAR):$(DOMAIN_IMPLS_BUILD_JAR)


#######################################################################
# The dependencies for this component.  This is intended to be used 
# in the dependency list in Makefile targets.
# 
#######################################################################
CLIENT_INTERFACES_DEPENDS=$(filter %.jar,$(subst :, ,$(CLIENT_INTERFACES_CLASSPATH)))

