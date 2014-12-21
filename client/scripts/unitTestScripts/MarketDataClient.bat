@ECHO OFF

SET VOB=X:

SET CLASSPATH=.
set CLASSPATH=%CLASSPATH%;%VOB%\client\classes\impls;
set CLASSPATH=%CLASSPATH%;%VOB%\domain\classes\impls;

call setclasspath %VOB%

echo Classpath is %CLASSPATH%
set SERVER=com.cboe.application.test.MarketDataClient
set PROPERTY_FILE=TestAPIClient.properties
set OPTIONS=-DORB.LocatorDiscovery.Disable=true -DORB.IIOPTransport.PortNum=8311 -DORB.BindingSequence=Local:Iiop -DIOR_PATH=http://dev11cas:8003 -DIOR_FILE=/UserAccessV3.ior

set FRAMEWORK=com.cboe.infrastructureServices.foundationFramework.FoundationFramework
set CONFIGSERVICE=com.cboe.infrastructureServices.systemsManagementService.ConfigurationServiceFileImpl

if not "%1" == "" set PROPERTY_FILE=%1

echo .
echo Running MarketDataClient for server, %SERVER%
echo .

java %OPTIONS% %SERVER%

