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
include $(EVENT)/build/event.mk
include $(CMI)/build/cmi.mk


#######################################################################
# Defines the build classpath for this build component.
# Only those jars that are needed to build this component should be 
# referenced.
# 
#######################################################################

CLIENT_TRANSLATOR_CLASSPATH=$(CLIENT_TRANSLATOR_CLASS):$(CMI_IDL_JAR):$(CLIENT_COMMON_BUILD_JAR):$(CLIENT_COMMONTRANSLATOR_BUILD_JAR):$(XMLPARSER):$(CLIENT_IMPLS_BUILD_JAR):$(CLIENT_INTERFACES_BUILD_JAR):$(EVENT_INTERFACES_BUILD_JAR):$(EVENT_IMPLS_BUILD_JAR):$(DOMAIN_IDL_BUILD_JAR):$(DOMAIN_INTERFACES_BUILD_JAR):$(DOMAIN_IMPLS_BUILD_JAR):$(JUNITJAR):$(JUNIT37JAR):$(CLIENT_INTERNAL_IDL_BUILD_JAR):$(CLIENT_IDL_BUILD_JAR):$(OBJWAVEJAR):$(JGLCLASSES):$(OMGBASECLASSESJAR):$(OMGSERVICEJAR):$(COMMONFACILITIES_BUILD_JAR):$(FFJAR):$(MSIMPJAR):$(LGJAR):$(LGINTJAR):$(CMJAR):$(IVIDLCLASSESJAR):$(SERVER_IDL_BUILD_JAR):$(EVENT_IDL_BUILD_JAR)


#######################################################################
# The dependencies for this component.  This is intended to be used 
# in the dependency list in Makefile targets.
# 
#######################################################################
CLIENT_TRANSLATOR_DEPENDS=$(filter %.jar,$(subst :, ,$(CLIENT_TRANSLATOR_CLASSPATH)))

