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
include $(EIS)/build/eis.mk
include $(SERVER)/build/server.mk
include $(EVENT)/build/event.mk
include $(MESSAGE)/build/message.mk
include $(CONNECTIONSERVER)/build/connectionServer.mk
include $(APPLAPPL)/build/applappl.mk
include $(CLIENT)/build/client.mk
include $(CMI)/build/cmi.mk
include $(INFRAVERITY)/build/InfraVerity.mk
include $(LOGGINGSERVICE)/build/LoggingService.mk


#######################################################################
# Defines the build classpath for this build component.
# Only those jars that are needed to build this component should be 
# referenced.
# 
#######################################################################
DOMAIN_TEST_CLASSPATH=$(DOMAIN_TEST_CLASS):$(SERVER_INTERCEPTORS_BUILD_JAR):$(SERVER_EXTENSIONS_IMPLS_BUILD_JAR):$(SERVER_EXTENSIONS_BUILD_JAR):$(SERVER_IMPLS_BUILD_JAR):$(EIS_IMPLS_BUILD_JAR):$(SERVER_COMMON_BUILD_JAR):$(SERVER_INTERFACES_BUILD_JAR):$(SERVER_IDL_BUILD_JAR):$(MESSAGE_BUILD_JAR):$(SERVER_PROXIES_BUILD_JAR):$(CONNECTIONSERVER_BUILD_JAR):$(APPLAPPL_BUILD_JAR):$(EIS_IMPLS_BUILD_JAR):$(EIS_INTERFACES_BUILD_JAR):$(EVENT_IMPLS_BUILD_JAR):$(EVENT_INTERFACES_BUILD_JAR):$(EVENT_IDL_BUILD_JAR):$(CLIENT_INTERFACES_BUILD_JAR):$(CMI_IDL_JAR):$(CLIENT_IMPLS_BUILD_JAR):$(DOMAIN_INTERFACES_BUILD_JAR):$(DOMAIN_IDL_BUILD_JAR):$(DOMAIN_IMPLS_BUILD_JAR):$(DOMAIN_PERSIST_BUILD_JAR):$(COMMONFACILITIES_BUILD_JAR):$(FFJAR):$(FFIMPLJAR):$(FFPERSISTJAR):$(MSIMPJAR):$(JUNIT_TPT):$(JUNIT37JAR):$(OBJWAVEJAR):$(JGLCLASSES):$(OMGBASECLASSESJAR):$(OMGSERVICECLASSESJAR):$(ROJAR):$(SSINTJAR):$(CONCURJAR):$(CMJAR):$(HAPSJAR):$(IVIDLCLASSESJAR):$(SSJAR):$(LOGGINGSERVICE_IDL_BUILD_JAR):$(LOGGINGSERVICE_BUILD_JAR):$(INFRAVERITY_IMPLS_BUILD_JAR):$(CFNSTOCKJAR):$(MSG_CODEC):/vobs/tpt/tpt_tools/mockito/run_dir/mockito-all.jar


#######################################################################
# The dependencies for this component.  This is intended to be used 
# in the dependency list in Makefile targets.
# 
#######################################################################
DOMAIN_TEST_DEPENDS=$(filter %.jar,$(subst :, ,$(DOMAIN_TEST_CLASSPATH)))

