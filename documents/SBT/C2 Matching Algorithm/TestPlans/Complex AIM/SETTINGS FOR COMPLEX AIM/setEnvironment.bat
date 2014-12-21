REM --- CHECK THIS - IT NEEDS TO MATCH YOUR JAVA INSTALLATION LOCATION!! ---
set JAVA_HOME=C:\ClientSetup\jdk1.6.0_05
REM set JAVA_HOME=C:\sbt\CBOE\SBT\CBOEDIR_5.5.58.6.0\trader\jre
REM set JAVA_HOME=C:\Program Files\Java\jre1.5.0_06
echo Using Java Location: %JAVA_HOME%

REM --- CHECK THIS - BE SURE IT MATCHES YOUR VOB DRIVE LETTER!! ---
set DRIVE_LETTER=C
echo Using VOB Letter: %DRIVE_LETTER%



REM --- Create Short-Named Link to Jars - Should be no need to change this! ---

echo " Started time "
echo %time%>log.txt
subst K: /D
subst K: %DRIVE_LETTER%:\ClientSetup\Jars
set JAR_DIR=K:
echo Using CBOEdirect Jars From Location: %JAR_DIR%

REM --- THIS LIST SHOULD CONTAIN *ALL* JAR FILES IN THE "\ClientSetup\Jars" VOB Dir. ---
REM --- NEW JARS WILL HAVE TO BE ADDED AS NEEDED IF THE JAR FILE COLLECTION CHANGES ---

set CLASSPATH=%CLASSPATH%;%JAR_DIR%\ffimpl.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\domain_impls.jar
set CLASSPATH=%JAR_DIR%\CBOEUtility.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\CommonFacilities.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\FoundationFramework.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\InfraVerityIDLClasses.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\LoggingService.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\LoggingServiceIDL.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\MessagingSystem.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\OMGBaseClasses.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\OMGServiceClasses.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\SecurityService.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\SecurityServiceIDLClasses.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\activation.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\client_common.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\client_commonTranslator.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\client_idl.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\client_impls.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\client_instrumentationTranslator.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\client_interceptors.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\client_interfaces.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\client_intermarketTranslator.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\client_internalTranslator.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\client_messagingTranslator.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\client_translator.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\common.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\concurrency.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\domain_idl.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\domain_interfaces.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\domain_xml.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\event_idl.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\event_impls.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\event_interfaces.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\gui_admin.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\gui_common.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\gui_commonBusiness.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\idlClasses.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\itgProxy_idl.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\itgProxy_interfaces.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\itgProxy_impls.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\jaxb-api.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\jgl.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\jsch-0.1.40.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\mail.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\server_common.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\server_idl.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\server_impls.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\server_interfaces.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\server_proxies.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\ss.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\sysAdminClient_idl.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\sysAdminClient_interfaces.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\sysAdminClient_proxies.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\activemq-4.2.jar
REM set CLASSPATH=%CLASSPATH%;%JAR_DIR%\appia.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\appia_oe.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\cmi_idl.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\dependency.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\FIX.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\fix43.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\fix44.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\fixclient.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\itgLinkageRouter.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\jms.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\jython.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\server.jar
set CLASSPATH=%CLASSPATH%;%JAR_DIR%\grizzly-nio-framework.jar
