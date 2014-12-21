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
include $(DOMAIN_PERSIST)/domain_persist.mk
include $(EIS)/build/eis.mk
include $(SERVER)/build/server.mk
include $(EVENT)/build/event.mk
include $(MESSAGE)/build/message.mk
include $(CONNECTIONSERVER)/build/connectionServer.mk
include $(APPLAPPL)/build/applappl.mk
include $(SYSADMINCLIENT)/build/sysAdminClient.mk
include $(CMI)/build/cmi.mk
include $(INFRAVERITY)/build/InfraVerity.mk
include $(LOGGINGSERVICE)/build/LoggingService.mk


#######################################################################
# Defines the build classpath for this build component.
# Only those jars that are needed to build this component should be 
# referenced.
# 
#######################################################################

CLIENT_TEST_CLASSPATH=$(CLIENT_TEST_CLASS):$(CLIENT_IMPLS_BUILD_JAR):$(SYSADMINCLIENT_IMPLS_BUILD_JAR):$(SYSADMINCLIENT_INTERCEPTORS_BUILD_JAR):$(SYSADMINCLIENT_INTERFACES_BUILD_JAR):$(SYSADMINCLIENT_PROXIES_BUILD_JAR):$(CMI_IDL_JAR):$(DOMAIN_IMPLS_BUILD_JAR):$(DOMAIN_IDL_BUILD_JAR):$(DOMAIN_INTERFACES_JAR):$(DOMAIN_PERSIST_BUILD_JAR):$(FFJAR):$(FFIMPLJAR):$(FFPERSISTJAR):$(MSIMPJAR):$(JUNIT_TPT):$(JUNIT37JAR):$(OBJWAVEJAR):$(JGLCLASSES):$(OMGBASECLASSESJAR):$(OMGSERVICECLASSESJAR):$(ROJAR):$(SSINTJAR):$(CONCURJAR):$(CMJAR):$(HAPSJAR):$(IVIDLCLASSESJAR):$(SSJAR):$(LOGGINGSERVICE_IDL_BUILD_JAR):$(LOGGINGSERVICE_BUILD_JAR):$(INFRAVERITY_IMPLS_BUILD_JAR):$(CFNSTOCKJAR):$(MSG_CODEC):$(SERVER_INTERFACES_BUILD_JAR):$(SERVER_IDL_BUILD_JAR):$(SERVER_PROXIES_BUILD_JAR):/vobs/tpt/tpt_tools/mockito/run_dir/mockito-all.jar


#######################################################################
# The dependencies for this component.  This is intended to be used 
# in the dependency list in Makefile targets.
# 
#######################################################################
CLIENT_TEST_DEPENDS=$(filter %.jar,$(subst :, ,$(CLIENT_TEST_CLASSPATH)))

