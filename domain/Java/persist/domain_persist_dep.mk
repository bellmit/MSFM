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
include $(SESSIONMANAGEMENTSERVICE)/build/SessionManagementService.mk
include $(SERVER)/build/server.mk
include $(EVENT)/build/event.mk
include $(CMI)/build/cmi.mk

#######################################################################
# Defines the build classpath for this build component.
# Only those jars that are needed to build this component should be 
# referenced.
# 
#######################################################################
DOMAIN_PERSIST_CLASSPATH =$(DOMAIN_PERSIST_CLASS):$(CMI_IDL_JAR):$(DOMAIN_IMPLS_BUILD_JAR):$(DOMAIN_INTERFACES_BUILD_JAR):$(SERVER_INTERFACES_BUILD_JAR):$(EVENT_INTERFACES_BUILD_JAR):$(EVENT_IDL_BUILD_JAR):$(DOMAIN_IDL_BUILD_JAR):$(SERVER_IDL_BUILD_JAR):$(COMMONFACILITIES_BUILD_JAR):$(FFJAR):$(FFPERSISTJAR):$(FFIMPLJAR):$(OBJWAVEJAR):$(DSJAR):$(SESSIONMANAGEMENTSERVICE_BUILD_JAR):$(CONCURJAR):$(LDAPJAR):$(LDAPJDKJAR):$(LGJAR):$(SSJAR):$(SMJAR):$(OMGSERVICECLASSESJAR):$(OMGBASECLASSESJAR):$(OMGSERVICEJAR):$(JUNITJAR):$(JUNIT37JAR):$(JGLCLASSES):$(IVIDLCLASSESJAR):$(SERVER_EXTENSIONS_BUILD_JAR):$(SERVER_COMMON_BUILD_JAR)

#######################################################################
# The dependencies for this component.  This is intended to be used 
# in the dependency list in Makefile targets.
# 
#######################################################################
DOMAIN_PERSIST_DEPENDS=$(filter %.jar,$(subst :, ,$(DOMAIN_PERSIST_CLASSPATH)))



