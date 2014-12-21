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
include $(TEMPLATES)/templates.mk

#######################################################################
# Defines the build classpath for this build component.
# Only those jars that are needed to build this component should be 
# referenced.
# 
#######################################################################
CFIX_INTERCEPTORS_CLASSPATH=$(CFIX_INTERCEPTORS_CLASS):$(CMJAR):$(CFIX_INTERFACES_BUILD_JAR):$(CLIENT_INTERFACES_BUILD_JAR):$(CMI_IDL_JAR):$(DOMAIN_INTERFACES_BUILD_JAR):$(DOMAIN_IDL_BUILD_JAR):$(COMMONFACILITIES_BUILD_JAR):$(TEMPLATES):$(OBJWAVEJAR):$(FFIMPLJAR):$(FFJAR):$(JUNIT37JAR):$(IVIDLCLASSESJAR):$(CLIENT_INTERNAL_IDL_BUILD_JAR)

#######################################################################
# The dependencies for this component.  This is intended to be used 
# in the dependency list in Makefile targets.
# 
#######################################################################
CFIX_INTERCEPTORS_DEPENDS=$(filter %.jar,$(subst :, ,$(CFIX_INTERCEPTORS_CLASSPATH)))

