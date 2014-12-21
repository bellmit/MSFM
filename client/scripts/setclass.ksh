# Used by gui/release/trader/bin/startGui.ksh
# and simulator/release/simulator/CAS.ksh

VOB=$1

CLASSPATH=.

CLASSPATH=$CLASSPATH:$VOB/tools/java/jars/xml4j.jar
CLASSPATH=$CLASSPATH:$VOB/CommonFacilities/release/CommonFacilities.jar
CLASSPATH=$CLASSPATH:$VOB/infrastructure/release/ffimpl.jar
CLASSPATH=$CLASSPATH:$VOB/InfraVerity/release/jars/js.jar
CLASSPATH=$CLASSPATH:$VOB/InfraVerity/release/jars/jstools.jar
CLASSPATH=$CLASSPATH:$VOB/infrastructure/release/infrastructure.jar
CLASSPATH=$CLASSPATH:$VOB/InfraVerity/release/jars/InfraVerityIDLClasses.jar
CLASSPATH=$CLASSPATH:$VOB/FoundationFramework/release/jars/FoundationFramework.jar
CLASSPATH=$CLASSPATH:$VOB/SystemManagement/release/jars/SystemManagement.jar
CLASSPATH=$CLASSPATH:$VOB/SystemManagement/release/jars/SystemManagementIDL.jar
CLASSPATH=$CLASSPATH:$VOB/SystemManagement/release/jars/SystemManagementIDLClasses.jar
CLASSPATH=$CLASSPATH:$VOB/DirectoryService/release/jars/DirectoryService.jar
CLASSPATH=$CLASSPATH:$VOB/idlbase/idlcompiler/release/jars/IDLCompiler.jar
CLASSPATH=$CLASSPATH:$VOB/MessagingSystem/release/jars/MessagingSystem.jar
CLASSPATH=$CLASSPATH:$VOB/MessagingSystem/release/jars/MessagingSystemIDL.jar
CLASSPATH=$CLASSPATH:$VOB/idlbase/omg/release/jars/OMGBaseClasses.jar
CLASSPATH=$CLASSPATH:$VOB/idlbase/omg/release/jars/OMGServiceClasses.jar
CLASSPATH=$CLASSPATH:$VOB/tools/java/classes/concurrency.jar
CLASSPATH=$CLASSPATH:$VOB/tools/java/jars/jndi.jar
CLASSPATH=$CLASSPATH:$VOB/tools/java/jars/providerutil.jar
CLASSPATH=$CLASSPATH:$VOB/tools/netscape/jars/ldap.jar
CLASSPATH=$CLASSPATH:$VOB/LoggingService/release/jars/LoggingService.jar
CLASSPATH=$CLASSPATH:$VOB/SessionManagementService/release/SessionManagementService.jar
CLASSPATH=$CLASSPATH:$VOB/tools/iaik/jars/iaik_jce_full.jar
CLASSPATH=$CLASSPATH:$VOB/tools/java/jars/jawall.jar
CLASSPATH=$CLASSPATH:$VOB/tools/java/jms1.0.2b/lib/jms.jar
CLASSPATH=$CLASSPATH:$VOB/tools/jgl/classes
CLASSPATH=$CLASSPATH:$VOB/SecurityService/release/jars/SecurityService.jar
CLASSPATH=$CLASSPATH:$VOB/SecurityService/release/jars/SecurityServiceIDL.jar
CLASSPATH=$CLASSPATH:$VOB/SecurityService/release/jars/SecurityServiceIDLClasses.jar
CLASSPATH=$CLASSPATH:$VOB/tools/junit/classes/junit.jar
CLASSPATH=$CLASSPATH:$ORBIXWEB_HOME/classes
CLASSPATH=$CLASSPATH:$VOB/domain/release/domain_interfaces.jar
CLASSPATH=$CLASSPATH:$VOB/domain/release/domain_xml.jar
CLASSPATH=$CLASSPATH:$VOB/domain/release/domain_impls.jar
CLASSPATH=$CLASSPATH:$VOB/domain/event/release/event_impls.jar
CLASSPATH=$CLASSPATH:$VOB/domain/event/release/event_interfaces.jar
CLASSPATH=$CLASSPATH:$VOB/server/applappl/release/applappl.jar
CLASSPATH=$CLASSPATH:$VOB/server/message/release/message.jar
CLASSPATH=$CLASSPATH:$VOB/server/release/server_interfaces.jar
CLASSPATH=$CLASSPATH:$VOB/server/release/server_interceptors.jar
CLASSPATH=$CLASSPATH:$VOB/server/release/server_impls.jar
CLASSPATH=$CLASSPATH:$VOB/server/release/server_common.jar
CLASSPATH=$CLASSPATH:$VOB/server/release/server_proxies.jar
CLASSPATH=$CLASSPATH:$VOB/server/release/server_status.jar
CLASSPATH=$CLASSPATH:$VOB/server/eis/release/eis_impls.jar
CLASSPATH=$CLASSPATH:$VOB/server/eis/release/eis_interfaces.jar
CLASSPATH=$CLASSPATH:$VOB/cmi/release/jars/cmi_idl.jar
CLASSPATH=$CLASSPATH:$VOB/cmi/release/jars/cmi_idl_java.jar
CLASSPATH=$CLASSPATH:$VOB/client/release/client_interfaces.jar
CLASSPATH=$CLASSPATH:$VOB/client/release/client_interceptors.jar
CLASSPATH=$CLASSPATH:$VOB/client/release/client_impls.jar
CLASSPATH=$CLASSPATH:$VOB/client/release/client_internal_idl.jar
CLASSPATH=$CLASSPATH:$VOB/client/release/client_translator.jar
CLASSPATH=$CLASSPATH:$VOB/sysAdminClient/release/sysAdminClient_interfaces.jar
CLASSPATH=$CLASSPATH:$VOB/sysAdminClient/release/sysAdminClient_interceptors.jar
CLASSPATH=$CLASSPATH:$VOB/sysAdminClient/release/sysAdminClient_impls.jar
CLASSPATH=$CLASSPATH:$VOB/sysAdminClient/release/sysAdminClient_proxies.jar
CLASSPATH=$CLASSPATH:$VOB/simulator/release/simulator_impls.jar
CLASSPATH=$CLASSPATH:$VOB/simulator/release/simulator_interfaces.jar
CLASSPATH=$CLASSPATH:$VOB/common/release/jars/common.jar
CLASSPATH=$CLASSPATH:$VOB/Rollout/release/jars/Rollout.jar

CLASSPATH=$CLASSPATH:$VOB/client/release/client_idl.jar
CLASSPATH=$CLASSPATH:$VOB/domain/release/domain_idl.jar
CLASSPATH=$CLASSPATH:$VOB/domain/event/release/event_idl.jar
CLASSPATH=$CLASSPATH:$VOB/server/release/server_idl.jar
CLASSPATH=$CLASSPATH:$VOB/sysAdminClient/release/sysAdminClient_idl.jar

CLASSPATH=$CLASSPATH:$VOB/LoggingService/idl/LoggingService.idl
CLASSPATH=$CLASSPATH:$VOB/LoggingService/release/jars/LoggingServiceIDLClasses.jar
CLASSPATH=$CLASSPATH:$VOB/objectwave/release/objectwave.jar
CLASSPATH=$CLASSPATH:$VOB/tools/XMLParser/JWSDP-1.3/jaxb/lib/jaxb-api.jar
CLASSPATH=$CLASSPATH:$VOB/tools/XMLParser/JWSDP-1.3/jaxb/lib/jaxb-impls.jar
CLASSPATH=$CLASSPATH:$VOB/tools/XMLParser/JWSDP-1.3/jaxb/lib/jaxb-libs.jar
CLASSPATH=$CLASSPATH:$VOB/tools/XMLParser/JWSDP-1.3/jaxb/lib/jaxb-xjc.jar
CLASSPATH=$CLASSPATH:$VOB/tools/XMLParser/JWSDP-1.3/jwsdp-shared/lib/jax-qname.jar
CLASSPATH=$CLASSPATH:$VOB/jcache/release/jcache_impls.jar
CLASSPATH=$CLASSPATH:$VOB/tpt_tools/jcache/run_dir/jcache-1.0-dev-3.jar
CLASSPATH=$CLASSPATH:$VOB/tpt_tools/ehcache/run_dir/ehcache-1.2.4.jar
CLASSPATH=$CLASSPATH:$VOB/tpt_tools/commons-logging/run_dir/commons-logging.jar


CLASSPATH=$CLASSPATH:$VOB/tools/release/jars/CBOEUtility.jar
LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$VOB/tools/release/Solaris

CLASSPATH=$CLASSPATH:$VOB/tools/talarian/ss65/lib/ss.jar

export CLASSPATH
export LD_LIBRARY_PATH
