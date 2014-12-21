README FILE FoundationFramework v1.0

Purpose:
Describe the contents of the example 
Describe how to run the example

fILE and Directory REQUIREMENTS:


The list of java jar files necessary for Running the example:
   \FoundationFramework\classes  
	contains 	Logging.jar
			omgcoss.jar
			objectwave.jar 
                 
INPUT fILES
   \FoundationFramework\
	ccm.ini  - properties for the Comm Path
	ReleaseNotes v1.0 - release info for v1.0
	runit - runs the example trade server
	testit - runs the trade server test
	put - runs the putit command

   \FoundationFramework\logs
	directory to hold logging output

   \FoundationFramework\java\com\cboe\infrastructureServices\foundationFramework\examples
	some example java files - loaded in the example


   \FoundationFramework\classes\*
	contains the foundationFramework class files 
	TradeService.properties - properties for the trade server example
	LoggingService.properties - properties for the logging service
	StandardMessageCatalog - message mapping properties for logging service

   \FoundationFramework\idl\
	contains 3 idl files: commPath, adminService, exampleService
	make - generate idl output

   \FoundationFramework\designs
	rose mdl files

OrbixWeb files needed:
   OrbixWeb31.classes

Steps to running the Example trade server:

1.	Modify Properties Files to point to valid directories
	In the file: LoggingService.properties  - modify the following lines to point to valid directories
	 
LoggingService.loggingAgent.FileAgent1.absFilePath=d:\\FoundationFramework\\logs\\FileAgent1.log
	Note - the above directory "logs" may have to be created
LoggingService.stdMsgCatAbsFilePath=d:\\FoundationFramework\\classes\\StandardMessageCatalog.properties

	In the file: TradeService.properties  - modify the following lines to point to the LoggingService.properties file
TradeServer.logServiceFileName=d:\\FoundationFramework\\classes\\LoggingService.properties	

	
2.	To Register the server
	To run: put
	
3.	THE EXAMPLE TRADE SERVER.
	What it does: Starts the Foundation Framework services such as Logging, Security, Trader, Orb.
	To Run: runit 

4.	THE EXAMPLE TRADE SERVER CLIENT
	What it does: Tests the trader service
	To Run: testit
 	   
5.	THE ADMIN SERVICE TEST
	What it does: Tests the administration functions such as callbacks, and property manipulation. Uses the Trader.
	To Run: testadmin TradeServer 


