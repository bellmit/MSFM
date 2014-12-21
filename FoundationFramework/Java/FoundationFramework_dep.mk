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
include $(COMMON)/build/common.mk
include $(LOGGINGSERVICE)/build/LoggingService.mk
include $(MESSAGINGSYSTEM)/build/MessagingSystem.mk
include $(IDLBASE)/defs/idlbase.mk
include $(INFRAVERITY)/build/InfraVerity.mk
include $(CMI)/build/cmi.mk
include $(SECURITYSERVICE)/build/SecurityService.mk

#######################################################################
# Defines the build classpath for this build component.
# Only those jars that are needed to build this component should be 
# referenced.
# 
#######################################################################
FOUNDATIONFRAMEWORK_CLASSPATH=$(FOUNDATIONFRAMEWORK_CLASS):$(JAWJAR):$(OMGBASE_JAR):$(OMGSERVICES_JAR):$(CMI_IDL_JAR):$(MESSAGINGSYSTEM_IMPLS_BUILD_JAR):$(SECURITYSERVICE_IDL_BUILD_JAR):$(JUNIT37JAR):$(INFRAVERITY_IDL_BUILD_JAR):$(INFRAVERITY_IMPLS_BUILD_JAR):$(LOGGINGSERVICE_IDL_BUILD_JAR):$(LOGGINGSERVICE_BUILD_JAR):$(COMMON_BUILD_JAR):$(SECURITYSERVICE_BUILD_JAR):/vobs/tpt/tpt_tools/Infinispan/run_dir/target/infinispan-core.jar:/vobs/tpt/tpt_tools1/Infinispan/cboe-infinispan/target/lib/rhq-pluginAnnotations.jar


#######################################################################
# The dependencies for this component.  This is intended to be used 
# in the dependency list in Makefile targets.
# 
#######################################################################
FOUNDATIONFRAMEWORK_DEPENDS=$(filter %.jar,$(subst :, ,$(FOUNDATIONFRAMEWORK_CLASSPATH)))

