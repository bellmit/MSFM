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

#######################################################################
# Defines the build classpath for this build component.
# Only those jars that are needed to build this component should be 
# referenced.
# 
#######################################################################
DOMAIN_XML_CLASSPATH =$(DOMAIN_XML_CLASS):$(CMI_IDL_JAR):$(DOMAIN_INTERFACES_BUILD_JAR):$(COMMONFACILITIES_BUILD_JAR):$(JAXB_API_JAR):$(JAXB_IMPL_JAR):$(JAXB_LIBS_JAR):$(JAXB_XJC_JAR):$(JWSDP_JAX_JAR):$(JWSDP_NAMESPACE_JAR):$(JWSDP_RELAXNG_JAR):$(JWSDP_XSD_JAR):$(XML_PARSER_API):$(XERCES_IMPL)


#######################################################################
# The dependencies for this component.  This is intended to be used 
# in the dependency list in Makefile targets.
# 
#######################################################################
DOMAIN_XML_DEPENDS=$(filter %.jar,$(subst :, ,$(DOMAIN_XML_CLASSPATH)))



