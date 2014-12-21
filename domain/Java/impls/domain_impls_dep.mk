#######################################################################
# All macros defined in this file should be globally unique across
# all release and build components
#
# This makefile makes reference to macros defined elsewhere.  Please
# define the following when including this file
#   DOMAIN = the root directory location for the release component
#
#######################################################################


#######################################################################
# include external component definitions
#
#######################################################################
include $(COMMONFACILITIES)/build/CommonFacilities.mk
include $(EVENT)/build/event.mk
include $(SERVER)/build/server.mk
include $(CLIENT)/build/client.mk
include $(CMI)/build/cmi.mk
include $(SYSADMINCLIENT)/build/sysAdminClient.mk
include $(ICS)/build/ics.mk

#######################################################################
# Defines the build classpath for this build component.
# Only those jars that are needed to build this component should be 
# referenced.
# 
#######################################################################
DOMAIN_IMPLS_CLASSPATH =$(DOMAIN_IMPLS_CLASS):$(CMI_IDL_JAR):$(DOMAIN_XML_BUILD_JAR):$(DOMAIN_INTERFACES_BUILD_JAR):$(SERVER_INTERFACES_BUILD_JAR):$(EVENT_INTERFACES_BUILD_JAR):$(SYSADMINCLIENT_IDL_BUILD_JAR):$(ICS_IDL_BUILD_JAR):$(DOMAIN_IDL_BUILD_JAR):$(EVENT_IDL_BUILD_JAR):$(SERVER_IDL_BUILD_JAR):$(COMMONFACILITIES_BUILD_JAR):$(FFJAR):$(MSIMPJAR):$(OBJWAVEJAR):$(CMJAR):$(OMGBASECLASSESJAR):$(OMGSERVICECLASSESJAR):$(JUNITJAR):$(JUNIT37JAR):$(IVIDLCLASSESJAR):$(CONCURJAR):$(SMJAR):$(JAWJAR):$(XML4JAR):$(XML_PARSER_API):$(XERCES_IMPL):$(JAXB_API_JAR):$(JAXB_IMPL_JAR):$(JAXB_LIBS_JAR):$(JAXB_XJC_JAR):$(JWSDP_JAX_JAR):$(JWSDP_NAMESPACE_JAR):$(JWSDP_RELAXNG_JAR):$(JWSDP_XSD_JAR):$(LGINTCLASSESJAR):$(CUJAR):$(TPT_JARAMIKO):$(TPT_JSCH)


#######################################################################
# The dependencies for this component.  This is intended to be used 
# in the dependency list in Makefile targets.
# 
#######################################################################
DOMAIN_IMPLS_DEPENDS=$(filter %.jar,$(subst :, ,$(DOMAIN_IMPLS_CLASSPATH)))

