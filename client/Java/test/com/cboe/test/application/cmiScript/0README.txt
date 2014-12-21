Usage notes for cmiScript

Sample script for invocation -----

#!/bin/ksh
# Usage: thisFile casName casPort < scriptFile

RUN_CMI_SCRIPT=com.cboe.test.application.cmiScript.RunScript
CMI_DIR=/vobs/dte/cmi/release/jars
CLIENT_DIR=/vobs/dte/client/release
OMG_DIR=/vobs/dte/idlbase/omg/release/jars
CLASSPATH=${CMI_DIR}/cmi_idl.jar:${CLIENT_DIR}/client_test.jar:${OMG_DIR}/OMGBaseClasses.jar:${OMG_DIR}/OMGServiceClasses.jar

CAS_HOST=$1
CAS_PORT=$2

java -classpath $CLASSPATH \
   -Dcom.sun.CORBA.transport.ORBTCPReadTimeouts=100:25000:300:20 \
   $RUN_CMI_SCRIPT $CAS_HOST $CAS_PORT

How to write a scriptFile -----

A little syntax:
- spaces separate words/values/symbols on the command line
- you can use "double quotes" to surround a value that contains spaces
- you can use \" inside double-quoted text to include " within the text
- you can continue a command over multiple lines by putting \ at the
  end of every line except the last one
- lines that start with # are ignored (comment lines)

Parameters:
Individual values in structures and in call interfaces have a name in the
IDL file. To specify a parameter and its value, use the name from IDL,
then a space, then the value. For example, to say that the userId field
of a UserLoginStruct should have the value PAT, you would say
   userId PAT

Symbolic names:
This program does not recognize symbolic names. You cannot use, for example,
the word PRIMARY for the loginMode in a UserAccess logon command; you must
use its value, the number 1.

Structures:
The struct command creates a structure described in IDL. The format of
the command is
struct <type> <name> = <parameter list>
For example, to create a cmiUser::UserLogonStruct you could use the command
   struct UserLogonStruct abc = userId PAT password HUSH version 8.0 loginMode 1

Calls:
Specify an IDL call as <interface> <method> <parameters>
Interface names and method names in this program match the names in the IDL.
For example, to log in with the original UserAccess interface, you might say
  UserAccess logon logonStruct abc sessionType 1 gmdTextMessage false

Callbacks:
This program creates callback objects automatically. You cannot create
the objects yourself in this scripting language, and you should not try
to specify callback objects for calls. Note the UserAccess example
above - it does not specify the clientListener parameter.

Services:
This program gets service objects automatically. If you log in with
UserAccess, the program will automatically get and make available service
objects like Quote, Administrator, MarketQuery. If you log in with a
later UserAccess object, it will get the corresponding service objects and
the service objects from all earlier UserAccess objects.

Waiting:
The wait command makes the program wait a specified number of milliseconds
before reading and executing the next command. For example, to make the
program wait 5 seconds (5000 milliseconds), you would say
   wait 5000

Special Structures:
Most structures must be defined with a struct command before they are used.
These few structures are exceptions - enter them and use them as single values,
just like strings or integers:
DateStruct: year-month-day, such as 2010-8-17
TimeStruct: hour:minute:second.fraction such as 8:30:29.0
DateTimeStruct: year-month-day.hour:minute:second.fraction
PriceStruct:
- $MKT or MKT for a MARKET price
- $-- or -- for NO_PRICE
- V$numeric or $numeric or numeric for a VALUED price
- L$numeric for a LIMIT price
- C$numeric for a CABINET price
("numeric" is one or more digits, with optional minus sign and decimal point)
