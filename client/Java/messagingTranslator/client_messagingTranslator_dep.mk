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

#######################################################################
# Defines the build classpath for this build component.
# Only those jars that are needed to build this component should be 
# referenced.
# 
#######################################################################
CLIENT_MESSAGINGTRANSLATOR_CLASSPATH=$(CLIENT_MESSAGINGTRANSLATOR_CLASS):$(CLIENT_COMMON_BUILD_JAR):$(CLIENT_COMMONTRANSLATOR_BUILD_JAR):$(CLIENT_INTERFACES_BUILD_JAR):$(DOMAIN_INTERFACES_BUILD_JAR):$(CMJAR):$(IVJAR):$(IVIDLCLASSESJAR):$(LGINTCLASSESJAR):$(LGJAR):$(IUJAR):$(MSIMPJAR):$(OMGBASECLASSESJAR):$(OMGSERVICECLASSESJAR):$(SMARTSOCKJAR):$(MSIMPJAR):$(COMMONFACILITIES_BUILD_JAR):$(JMSJAR)

#######################################################################
# The dependencies for this component.  This is intended to be used 
# in the dependency list in Makefile targets.
# 
#######################################################################
CLIENT_MESSAGINGTRANSLATOR_DEPENDS=$(filter %.jar,$(subst :, ,$(CLIENT_MESSAGINGTRANSLATOR_CLASSPATH)))

