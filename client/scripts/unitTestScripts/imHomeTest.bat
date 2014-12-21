@ECHO OFF

SET VOB=V:

SET RUN_DIR=V:\\client\\scripts\\unitTestScripts

call %RUN_DIR%\\imHometestsimclass %VOB%


set SERVER=ClientApplicationServer
set PROPERTY_FILE=%RUN_DIR%\\imHomeCAS.properties
set OPTIONS=-DApplicationDebugOn -DORB.IIOPTransport.OrbName=serviceTest%COMPUTERNAME% -DORB.IIOPTransport.PortNum=8904 -DORB.BindingSequence=Local:Iiop -DORB.FlowControl=none

set FRAMEWORK=com.cboe.infrastructureServices.foundationFramework.FoundationFramework
set CONFIGSERVICE=com.cboe.infrastructureServices.systemsManagementService.ConfigurationServiceFileImpl

if not "%1" == "" set PROPERTY_FILE=%1

echo .
echo Running CAS for server, %PREFIX%
echo .

cd v:\simulator\release\simulator

java  %OPTIONS% com.cboe.application.marketData.UnitTestIntermarketQueryHomeImpl %PROPERTY_FILE%

