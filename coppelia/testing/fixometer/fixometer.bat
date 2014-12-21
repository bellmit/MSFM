set CLASSPATH=%CLASSPATH%;D:/dev/coppelia41f01/debug;../../classes/fixometer.jar;../../classes/coppelia.jar;/dev/Coppelia41f01/lib/jmf.jar;../../classes/classes12.zip

set RMI_SEC=-Djava.security.policy=d:/dev/coppelia41f01/rmi/policy.txt

java %RMI_SEC% com.javtech.fixometer.FIXometer server.ini
