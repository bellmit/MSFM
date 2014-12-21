@ECHO OFF

SET VOB=%1
REM SET CLASSPATH=.


SET CLASSPATH=%CLASSPATH%;%VOB%\tools\java\classes\concurrency.jar
SET CLASSPATH=%CLASSPATH%;%VOB%\MessagingSystem\release\jars\MessagingSystem.jar
SET CLASSPATH=%CLASSPATH%;%VOB%\javautil\release\jars\javautil.jar
SET CLASSPATH=%CLASSPATH%;%VOB%\CommonFacilities\release\CommonFacilities.jar
SET CLASSPATH=%CLASSPATH%;%VOB%\common\release\jars\common.jar
SET CLASSPATH=%CLASSPATH%;%VOB%\objectwave\release\objectwave.jar

SET CLASSPATH=%CLASSPATH%;%VOB%\InfraVerity\release\jars\logOnly.jar

SET CLASSPATH=%CLASSPATH%;%VOB%\infrastructure\release\infrastructure.jar
SET CLASSPATH=%CLASSPATH%;%VOB%\FoundationFramework\release\jars\FoundationFramework.jar
SET CLASSPATH=%CLASSPATH%;%VOB%\MessagingSystem\release\jars\OMGBase.jar
SET CLASSPATH=%CLASSPATH%;%VOB%\MessagingSystem\release\jars\OMGService.jar
SET CLASSPATH=%CLASSPATH%;%VOB%\LoggingService\release\jars\LoggingService.jar
SET CLASSPATH=%CLASSPATH%;%VOB%\InfraVerity\release\InfraVerityIDLClasses.jar
SET CLASSPATH=%CLASSPATH%;%VOB%\tools\jgl\jars\jgl.jar
SET CLASSPATH=%CLASSPATH%;%VOB%\tools\release\jars\CBOEUtility.jar

SET CLASSPATH=%CLASSPATH%;%VOB%\domain\release\domain_interfaces.jar
SET CLASSPATH=%CLASSPATH%;%VOB%\domain\release\domain_xml.jar
SET CLASSPATH=%CLASSPATH%;%VOB%\domain\release\domain_impls.jar
SET CLASSPATH=%CLASSPATH%;%VOB%\server\release\server_interfaces.jar
SET CLASSPATH=%CLASSPATH%;%VOB%\client\release\client_interfaces.jar
SET CLASSPATH=%CLASSPATH%;%VOB%\client\release\client_interceptors.jar
SET CLASSPATH=%CLASSPATH%;%VOB%\client\release\client_impls.jar
SET CLASSPATH=%CLASSPATH%;%VOB%\simulator\release\simulator_impls.jar
SET CLASSPATH=%CLASSPATH%;%VOB%\simulator\release\simulator_interfaces.jar

SET CLASSPATH=%CLASSPATH%;%VOB%\client\release\client_idl.jar
SET CLASSPATH=%CLASSPATH%;%VOB%\server\release\server_idl.jar
SET CLASSPATH=%CLASSPATH%;%VOB%\domain\release\domain_idl.jar
SET CLASSPATH=%CLASSPATH%;%VOB%\domain\event\release\event_idl.jar
SET CLASSPATH=%CLASSPATH%;%VOB%\sysAdminClient\release\sysAdminClient_idl.jar
SET CLASSPATH=%CLASSPATH%;%VOB%\LoggingService\release\jars\LoggingServiceIDL.jar

SET CLASSPATH=%CLASSPATH%;%VOB%\tools\XMLParser\JWSDP-1.3\jaxb\lib\jaxb-api.jar
SET CLASSPATH=%CLASSPATH%;%VOB%\tools\XMLParser\JWSDP-1.3\jaxb\lib\jaxb-impl.jar
SET CLASSPATH=%CLASSPATH%;%VOB%\tools\XMLParser\JWSDP-1.3\jaxb\lib\jaxb-libs.jar
SET CLASSPATH=%CLASSPATH%;%VOB%\tools\XMLParser\JWSDP-1.3\jaxb\lib\jaxb-xjc.jar

SET CLASSPATH=%CLASSPATH%;%VOB%\tools\XMLParser\JWSDP-1.3\jwsdp-shared\lib\xsdlib.jar
SET CLASSPATH=%CLASSPATH%;%VOB%\tools\XMLParser\JWSDP-1.3\jwsdp-shared\lib\jax-qname.jar
SET CLASSPATH=%CLASSPATH%;%VOB%\tools\XMLParser\JWSDP-1.3\jwsdp-shared\lib\relaxngDatatype.jar
SET CLASSPATH=%CLASSPATH%;%VOB%\tools\XMLParser\JWSDP-1.3\jwsdp-shared\lib\namespace.jar


@ECHO ON
