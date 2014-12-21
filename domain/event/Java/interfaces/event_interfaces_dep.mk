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
include $(DOMAIN)/build/domain.mk
include $(ICS)/build/ics.mk

#######################################################################
# Defines the build classpath for this build component.
# Only those jars that are needed to build this component should be 
# referenced.
# 
#######################################################################
EVENT_INTERFACES_CLASSPATH =$(EVENT_INTERFACES_CLASS):$(CMI_IDL_JAR):$(EVENT_IDL_BUILD_JAR):$(DOMAIN_IDL_BUILD_JAR):$(ICS_IDL_BUILD_JAR):$(COMMONFACILITIES_BUILD_JAR):$(FFJAR):$(FFIMPLJAR):$(DSJAR):$(CONCURJAR):$(LDAPJAR):$(LDAPJDKJAR):$(LGJAR):$(SSJAR):$(SMJAR):$(JUNITJAR):$(OBJWAVEJAR):$(JGLCLASSES):$(MSJARSET):$(SSINTJAR):$(IVIDLCLASSESJAR):$(LGINTCLASSESJAR):$(MSG_CODEC)

#######################################################################
# The dependencies for this component.  This is intended to be used 
# in the dependency list in Makefile targets.
# 
#######################################################################
EVENT_INTERFACES_DEPENDS=$(filter %.jar,$(subst :, ,$(EVENT_INTERFACES_CLASSPATH)))


