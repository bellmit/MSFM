@echo off
REM ---------------------------------------------------------------------------------------
REM   There should be nothing that needs to be changed in here!
REM ---------------------------------------------------------------------------------------

set OLDCP=%CLASSPATH%
set OLDPATH=%PATH%

call setEnvironment
echo "time started : 'date'"

if not exist %DRIVE_LETTER%:\ClientSetup\APIClient.jar (
echo Cannot find API Client Jar File %DRIVE_LETTER%:\ClientSetup\APIClient.jar. Cannot continue.
goto end
)
echo Using API Client Jar: %DRIVE_LETTER%:\ClientSetup\APIClient.jar

set CLASSPATH=%DRIVE_LETTER%:\ClientSetup\APIClient.jar;%CLASSPATH%

echo Using DLL Files From: %DRIVE_LETTER%:\gui\release\admin\lib
set PATH=%DRIVE_LETTER%:\gui\release\admin\lib;%PATH%

REM ---------------------------------------------------------------------------------------

set JAVA_FLAGS= -Xmx192m -Xms192m -XX:NewRatio=4 -XX:SurvivorRatio=2 -server -Djava.security.policy=java.policy


REM ---------------------------------------------------------------------------------------


if not exist "%JAVA_HOME%" (
echo Cannot find "JAVA_HOME" directory %JAVA_HOME% - please set "JAVA_HOME" to the correct location. Cannot continue.
goto end
)

"%JAVA_HOME%\bin\java" %JAVA_FLAGS% com.cboe.itg.apiclient.test.TestClass


:end
set CLASSPATH=%OLDCP%
set PATH=%OLDPATH%

