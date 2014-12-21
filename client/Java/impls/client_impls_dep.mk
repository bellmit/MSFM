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
include $(SERVER)/build/server.mk
include $(EIS)/build/eis.mk
include $(EVENT)/build/event.mk
include $(CMI)/build/cmi.mk
include $(JCACHE)/build/jcache.mk

#######################################################################
# Defines the build classpath for this build component.
# Only those jars that are needed to build this component should be 
# referenced.
# 
#######################################################################

CLIENT_IMPLS_CLASSPATH=$(CLIENT_IMPLS_CLASS):$(CMI_IDL_JAR):$(CLIENT_INTERFACES_BUILD_JAR):$(CLIENT_INTERCEPTORS_BUILD_JAR):$(SERVER_INTERFACES_BUILD_JAR):$(EVENT_INTERFACES_BUILD_JAR):$(DOMAIN_INTERFACES_BUILD_JAR):$(EIS_INTERFACES_BUILD_JAR):$(DOMAIN_IMPLS_BUILD_JAR):$(EVENT_IMPLS_BUILD_JAR):$(DOMAIN_XML_BUILD_JAR):$(COMMONFACILITIES_BUILD_JAR):$(FFJAR):$(ROJAR):$(FFPERSISTJAR):$(MSIMPJAR):$(JUNIT45JAR):$(JUNIT38JAR):$(JUNIT37JAR):$(JUNITJAR):$(CLIENT_IDL_BUILD_JAR):$(CLIENT_INTERNAL_IDL_BUILD_JAR):$(DOMAIN_IDL_BUILD_JAR):$(EVENT_IDL_BUILD_JAR):$(SERVER_IDL_BUILD_JAR):$(JCACHE_IMPLS_BUILD_JAR):$(OBJWAVEJAR):$(JGLCLASSES):$(OMGBASECLASSESJAR):$(OMGSERVICEJAR):$(LGINTJAR):$(CMJAR):$(SMJAR):$(LGJAR):$(JAWJAR):$(XML4JAR):$(XML_PARSER_API):$(XERCES_IMPL):$(JAXB_API_JAR):$(JAXB_IMPL_JAR):$(JAXB_LIBS_JAR):$(JAXB_XJC_JAR):$(JWSDP_JAX_JAR):$(JWSDP_NAMESPACE_JAR):$(JWSDP_RELAXNG_JAR):$(JWSDP_XSD_JAR):$(IVIDLCLASSESJAR):$(TPT_COMMONS_LOGGING):$(TPT_JCACHE):$(TPT_EHCACHE):$(SERVER_PROXIES_BUILD_JAR):$(MSG_CODEC):$(SERVER_COMMON_BUILD_JAR):$(SERVER_IMPLS_BUILD_JAR):$(SSINTJAR):$(SSJAR)


#######################################################################
# The dependencies for this component.  This is intended to be used 
# in the dependency list in Makefile targets.
# 
#######################################################################
CLIENT_IMPLS_DEPENDS=$(filter %.jar,$(subst :, ,$(CLIENT_IMPLS_CLASSPATH)))

