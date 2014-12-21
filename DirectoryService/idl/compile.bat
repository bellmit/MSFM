set PKG_NAME=org.omg

IF "%1"=="help" GOTO HELP
IF "%1"=="build" GOTO BUILD
IF "%1"=="clean" GOTO CLEAN
IF "%1"=="commands" GOTO COMMANDS
IF "%1"=="generate" GOTO GENERATE

:BUILD
idl -jP%PKG_NAME% -jO ..\java CosTrading.idl
idl -jP%PKG_NAME% -jO ..\java -jPCosTrading=%PKG_NAME% CosTradingRepos.idl


:GENERATE
@echo.
@echo @echo.> put.bat
@echo @echo Generated putit command >> put.bat
@echo @echo. >> put.bat
@echo call ..\..\bin\putit -j %SRV_NAME% %PKG_NAME%.javaserver1 >> put.bat
@echo call ..\..\bin\chmodit %SRV_NAME% i+all >> put.bat
@echo call ..\..\bin\chmodit %SRV_NAME% l+all >> put.bat

@echo ..\..\bin\owjava -echo %PKG_NAME%.javaclient1 %%1 > javaclient1.bat
@echo ..\..\bin\owjava -echo %PKG_NAME%.javaserver1 %%1 > javaserver1.bat



:COMMANDS
	echo.
	echo To run the OrbixWeb demo : 
	echo.
	echo Step 1 - Run the OrbixWeb Java Daemon (orbixdj) by typing
	echo          [ start orbixdj.bat ]
	echo.
	echo Step 2 - Register the server with the Daemon by typing
	echo          [ put.bat ]
	echo.
	echo Step 3 - Run the OrbixWeb client by typing
	echo          [ javaclient1 hostname ] to run using the Orbix protocol
	goto END	

:HELP
        echo.
	echo Executes the OrbixWeb IDL compiler and builds all java source files.
	echo.
	echo COMPILE [build] [clean] [commands] [generate] [help]
	echo.
	echo    build      Compiles the java source files.
	echo    clean      Removes all generated files.
	echo    commands   Display instructions on executing the demo.
        echo    generate   Generate batch files to register the server and
        echo               run the client.
	echo    help       Display these instructions.	
	echo.
	GOTO END

:CLEAN
	del javaclient1.bat 
	del javaserver1.bat 
	del put.bat

	IF NOT EXIST %WINDIR%\command\deltree.exe GOTO WINNT

:WIN95
	deltree /y java_output
	deltree /y ..\..\classes\%PKG_NAME%
	GOTO END

:WINNT
	rd /S/Q java_output
	rd /S/Q ..\..\classes\%PKG_NAME%
	GOTO END	

:END
	set PKG_NAME=
	set SRV_NAME=
	