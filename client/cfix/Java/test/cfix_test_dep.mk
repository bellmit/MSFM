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
include $(CLIENT)/build/client.mk
include $(CMI)/build/cmi.mk
include $(SERVER)/build/server.mk

#######################################################################
# Defines the build classpath for this build component.
# Only those jars that are needed to build this component should be 
# referenced.
# 
#######################################################################
CFIX_TEST_CLASSPATH=$(CFIX_TEST_CLASS):$(CFIX_INTERFACES_BUILD_JAR):$(CFIX_INTERCEPTORS_BUILD_JAR):$(CLIENT_IMPLS_BUILD_JAR):$(CLIENT_INTERFACES_BUILD_JAR):$(CMI_IDL_JAR):$(SERVER_INTERFACES_BUILD_JAR):$(SERVER_IDL_BUILD_JAR):$(DOMAIN_INTERFACES_BUILD_JAR):$(DOMAIN_IDL_BUILD_JAR):$(DOMAIN_IMPLS_BUILD_JAR):$(EVENT_INTERFACES_BUILD_JAR):$(COMMONFACILITIES_BUILD_JAR):$(FFJAR):$(MSIMPJAR):$(JUNIT38JAR):$(JUNITJAR):$(OBJWAVEJAR):$(JGLCLASSES):$(OMGBASECLASSESJAR):$(OMGSERVICEJAR):$(LGINTJAR):$(CMJAR):$(SMJAR):$(LGJAR):$(JAWJAR):$(XML4JAR):$(IVIDLCLASSESJAR):$(CLIENT_INTERNAL_IDL_BUILD_JAR)



#######################################################################
# The dependencies for this component.  This is intended to be used 
# in the dependency list in Makefile targets.
# 
#######################################################################
CFIX_TEST_DEPENDS=$(filter %.jar,$(subst :, ,$(CFIX_TEST_CLASSPATH)))



