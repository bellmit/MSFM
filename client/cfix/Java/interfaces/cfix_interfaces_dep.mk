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
include $(CLIENT)/build/client.mk
include $(CMI)/build/cmi.mk
include $(SERVER)/build/server.mk

#######################################################################
# Defines the build classpath for this build component.
# Only those jars that are needed to build this component should be 
# referenced.
# 
#######################################################################
CFIX_INTERFACES_CLASSPATH=$(CFIX_INTERFACES_CLASS):$(CLIENT_IMPLS_BUILD_JAR):$(CLIENT_INTERFACES_BUILD_JAR):$(CMI_IDL_JAR):$(DOMAIN_INTERFACES_BUILD_JAR):$(DOMAIN_IDL_BUILD_JAR):$(COMMONFACILITIES_BUILD_JAR):$(CMJAR):$(JUNITJAR):$(IVIDLCLASSESJAR)



#######################################################################
# The dependencies for this component.  This is intended to be used 
# in the dependency list in Makefile targets.
# 
#######################################################################
CFIX_INTERFACES_DEPENDS=$(filter %.jar,$(subst :, ,$(CFIX_INTERFACES_CLASSPATH)))

