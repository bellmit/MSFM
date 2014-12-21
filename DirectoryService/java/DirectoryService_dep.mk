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
include $(MESSAGINGSYSTEM)/build/MessagingSystem.mk
include $(IDLBASE)/defs/idlbase.mk
include $(LOGGINGSERVICE)/build/LoggingService.mk
include $(COMMON)/build/common.mk
include $(OBJECTWAVE)/build/objectwave.mk

#######################################################################
# Defines the build classpath for this build component.
# Only those jars that are needed to build this component should be 
# referenced.
# 
#######################################################################
DIRECTORYSERVICE_CLASSPATH=$(DIRECTORYSERVICE_CLASS):$(OMGBASE_JAR):$(OMGSERVICES_JAR):$(MESSAGINGSYSTEM_IMPLS_BUILD_JAR):$(LOGGINGSERVICE_BUILD_JAR):$(JAWJAR):$(COMMON_BUILD_JAR):$(OBJECTWAVE_BUILD_JAR):$(LDAPJDKJAR):$(LDAPJAR):$(JNDIJAR):$(FSCONJAR):$(JUNITJAR):$(JUNITJAR2):$(LOGGINGSERVICE_IDL_BUILD_JAR)

#######################################################################
# The dependencies for this component.  This is intended to be used 
# in the dependency list in Makefile targets.
# 
#######################################################################
DIRECTORYSERVICE_DEPENDS=$(filter %.jar,$(subst :, ,$(DIRECTORYSERVICE_CLASSPATH)))

