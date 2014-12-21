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
include $(DOMAIN)/build/domain.mk
include $(SERVER)/build/server.mk
include $(CMI)/build/cmi.mk
include $(CLIENT)/build/client.mk
include $(EVENT)/build/event.mk
include $(ICS)/build/ics.mk

#######################################################################
# Defines the build classpath for this build component.
# Only those jars that are needed to build this component should be 
# referenced.
# 
#######################################################################
EVENT_TEST_CLASSPATH=$(EVENT_TEST_CLASS):$(CMI_IDL_JAR):$(EVENT_INTERFACES_BUILD_JAR):$(DOMAIN_INTERFACES_BUILD_JAR):$(DOMAIN_IMPLS_BUILD_JAR):$(DOMAIN_IDL_BUILD_JAR):$(EVENT_IDL_BUILD_JAR):$(SERVER_INTERFACES_BUILD_JAR):$(CLIENT_INTERFACES_BUILD_JAR):$(ICS_IDL_BUILD_JAR):$(COMMONFACILITIES_BUILD_JAR):$(FFJAR):$(OBJWAVEJAR):$(LGJAR):$(LGINTJAR):$(CMJAR):$(OMGBASECLASSESJAR):$(OMGSERVICECLASSESJAR):$(JUNITJAR):$(JUNITJAR2):$(IVIDLCLASSESJAR):$(SMJAR):$(JAWJAR):$(MSG_CODEC)


#######################################################################
# The dependencies for this component.  This is intended to be used 
# in the dependency list in Makefile targets.
# 
#######################################################################
EVENT_TEST_DEPENDS=$(filter %.jar,$(subst :, ,$(EVENT_TEST_CLASSPATH)))

