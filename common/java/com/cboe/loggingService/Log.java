

package com.cboe.loggingService;

import com.cboe.common.log.LogMessageId;
import com.cboe.common.log.Logger;


/**
   <P>
   General lightweight logging class used by the logging service for debugging
   and tracing. Output is sent to the command console by default or a file.
   Debugging and tracing mode and output file are set via system properties
   or programmatically.
   <H3>
   Overview
   </H3>
   <P>
   A service running in verbose mode outputs to the command console more information
   than would be done under normal deployment conditions. A service will generally
   be run in verbose mode under the following conditions:
   <UL>
   <LI>
   The service is being developed or enhanced and some new functionality is
   being tested.
   <LI>
   The service has been reconfigured and the administrator wants to verify that
   the configuration is valid.
   <LI>
   The service has been running under an existing configuration and a problem
   has either been detected or suspected.
   </UL>
   <P>
   The goal in incorporating tracing and debugging should be the simple mantra
   "<U>minimal but meaningful</U>". Too much tracing and / or debugging will
   both slow execution and provide so much useless information that the embedded
   useful information is lost.
   <P>
   Trace and debug information should be generated at appropriate points in
   the execution of services when the service is running in verbose mode, but
   should introduce negligible performance overhead when the service is running
   with verbose mode turned off.
   <H3>
   Trace
   </H3>
   <P>
   Trace information allows the administrator to determine a path of execution
   both through methods and within methods to isolate a problem. Good places
   to put trace statements are as follows:
   <UL>
   <LI>
   At method entry points.
   </UL>
   <PRE>public boolean myMethod( int someArg ) throws SomeException {
   Log.traceEntry( this, "myMethod" );
   
   </PRE>
   <UL>
   <LI>
   At method exit points.
   </UL>
   <PRE>  Log.traceExit( this, "myMethod" );
   }
   
   </PRE>
   <UL>
   <LI>
   At strategic points within a method so the path of execution through a method
   may be traced.
   </UL>
   <PRE>if( somethingIsTrue ) {
   Log.trace( this, "Something is true." );
   
   doSomething();
   }
   else if( somethingElseIsTrue ) {
   Log.trace( this, "Something else is true." );
   
   doSomethingElse();
   }
   
   </PRE>
   <H3>
   Debug
   </H3>
   <P>
   Debug information allows the administrator to detect and diagnose a problem
   and typically includes exceptions and key data, for example input or output
   information to / from a key method. Good places to put debug statements are
   as follows:
   <UL>
   <LI>
   At method entry points.
   </UL>
   <PRE>public boolean myMethod( int someArg ) throws SomeException {
   Log.debug( this, "someArg", someArg );
   
   </PRE>
   <UL>
   <LI>
   At method exit points.
   </UL>
   <PRE>  Log.debug( this, "aResult", aResult );
   
   </PRE>
   <UL>
   <LI>
   At strategic points within a method so intermediate results of a method may
   be seen.
   </UL>
   <PRE>  Log.debug( this, "someIntermediateResult", someIntermediateResult );
   
   </PRE>
   <H3>
   When Not to Use Trace and Debug
   </H3>
   <UL>
   <LI>
   When you should really be using the distributed logging service, for example
   when:
   <UL>
   <LI>
   Logging must be active during normal operation in deployment.
   <LI>
   Log messages must contain more information than the simple arguments provided
   in the trace and debug methods.
   <LI>
   Log messages may need go to a destination other than the command console.
   <LI>
   Logging behavior may need to be turned on / off or otherwise changed at runtime.
   <LI>
   Log messages have legal value, for example in the case of non-repudiation
   and auditing.
   </UL>
   <LI>
   Methods that are accessors or "pass throughs" that simply delegate to another
   method.
   <LI>
   Execution paths that incorporate extremely tight loops that execute thousands
   of times and need to be very fast.
   <LI>
   Where it would clearly not be of any use.
   </UL>
   <P>
   <H3>
   How to Incorporate Tracing and Debugging
   </H3>
   <P>
   The best time is when the service is being written. Otherwise if you have
   any current System.out.println() statements either active or commented out,
   then you should change these to be either trace or debug statements depending
   on what is being printed. Similarly, if during development or debugging you
   feel the urge to put in a System.out.println() statement, clearly some useful
   trace or debug information is being output to the command console at this
   point and should be put in the form of a trace or debug call that may be
   turned off for deployment or on again at some later point if necessary.
   <P>
   <H3>
   How to Turn Tracing and / or Debugging On / Off
   </H3>
   <P>
   Tracing and debugging are both off by default and may be turned on separately
   using system properties provided when you execute your service from the command
   line. Similarly, the output file for the trace and debug information&nbsp;may
   be set via a system property as shown below. Note that if the output file
   contains directories that don't exist then they will be created.
   <P>
   java -D<B>TraceOn</B> -D<B>DebugOn</B>
   -D<B>LightweightLogFile</B>="someDir\MyDebugAndTraceOutput.log" MyClass
   <PRE>
   </PRE>
   <P>
   Tracing and debugging may also be turned on or the output file may be
   changed&nbsp;programmatically, although the preferred approach is to use
   system properties as outlined above.
   <PRE>Log.setTraceOn( true );
   Log.setDebugOn( true );
   Log.setOutputStream( newOutputStream );
   
   
   </PRE>
   <P>
   @see  LoggingServiceImpl
   
   @author David Houlding
   @version 4.0
   @deprecated Use com.cboe.common.log.Logger.
 */
public class Log {
	
	/**
	   The name of the system property used to turn debugging on.
	 */
	public static final String DEBUG_ON_PROPERTY = "DebugOn";
	
	/**
	   The name of the system property used to turn tracing on.
	 */
	public static final String TRACE_ON_PROPERTY = "TraceOn";
	
	/**
	   The path for the file to which debug and trace messages will be redirected.
	 */
	public static final String LIGHTWEIGHT_LOG_FILE_PROPERTY = "LightweightLogFile";

    /**
       The directory into which log files should be archived
     */
    public static final String LIGHTWEIGHT_LOG_ARCHIVE_PROPERTY = "LightweightLogArchive";
	
    /**
        The name of the property used to turn echo on
      */
    public static final String LIGHTWEIGHT_LOG_CONSOLE_ECHO_PROPERTY = "ConsoleEchoOn";

	/**
	   True if debugging mode is currently on.
	 */
	static boolean debugOn = ( System.getProperty( DEBUG_ON_PROPERTY ) != null );
	
	/**
	   True if tracing mode is currently on.
	 */
	static boolean traceOn = ( System.getProperty(  TRACE_ON_PROPERTY ) != null );
	
	
    /**
       Set up a flag to show that we have been initialized. In order to maintain compatibility with 
       previous versions, I can't insist people call init. Since all methods are static, I can't
       rely on a default constructor. Therefore, I will have to have all methods test whether initialization
       has been done.
     */
    private static boolean initialized = false;

	private static final String DEFAULT_LOGGER = Logger.getDefaultLoggerName();

	/**
	   If debugging is active, send a debug message to the command console.
	   @param source The object from which the debug message originates.
	   @param message The object and/or message to be used for debugging.
	   @roseuid 36DEF3DC0039
	 */
	public static void debug(java.lang.Object source, java.lang.Object message) {
		if ( debugOn ) {
			String messageString = "null";
			if( message != null ) {
				messageString = message.toString();
			}
			LogMessageId id = Logger.createLogMessageId( DEFAULT_LOGGER,
												messageString,
												getClassName( source ),
												"" );
			Logger.debug( id, null );
		}
    }
	
	/**
	   If tracing is on, output a message to the command console with information that would help an analyst trace the path of execution.
	   @param source The object from which the trace message originates.
	   @param message The message to be used for tracing.
	   @roseuid 36DEF3F900DB
	 */
	public static void trace(java.lang.Object source, java.lang.String message) {
		if ( traceOn ) {
			Logger.debug( getClassName( source ) + ": " + message );
		}
    }
	
	/**
	   Returns the class name for the given object.
	   @param source The object for which to get the class name. May be null in the case of static singleton classes.
	   @return The fully qualified name of the class for the given source object.
	   @roseuid 36DEF4E8020B
	 */
	private static String getClassName(java.lang.Object source) {
        String name = "";
        if( source != null ) {
            name = source.getClass().getName();
        }
        return name;
    }
	
	/**
	   Turn debugging mode on or off.
	   @param debugOn True if debugging is to be turned on, false if it is to be turned off.
	   @roseuid 36E5819E0332
	 * @deprecated
	 */
	public static synchronized void setDebugOn(boolean debugOn) {
        if ( !initialized )
        {
            init();
        }
        Log.debugOn = debugOn;
    }
	
	/**
	   Turn tracing mode on or off.
	   @param traceOn True if tracing is to be turned on, false if it is to be turned off.
	   @roseuid 36E581CE0287
	 * @deprecated
	 */
	public static synchronized void setTraceOn(boolean traceOn) {
        if ( !initialized )
        {
            init();
        }
        Log.traceOn = traceOn;
    }
	
	/**
	   Generate a debug message for the given exception.
	   @param source The object from which the debug call originates.
	   @param exception The exception to be used for debugging.
	   @roseuid 36E584D9011F
	 */
	public static void debugException(java.lang.Object source, java.lang.Throwable exception) {
		if ( debugOn ) {
			LogMessageId id = Logger.createLogMessageId( DEFAULT_LOGGER, "",
												getClassName( source ), "" );
			Logger.debug( id, null, exception );
		}
    }
	
	/**
	   If tracing is on, output a message to the command console indicating that a method has been entered.
	   @param source The object from which the trace message originates.
	   @param methodName The name of the method entered.
	   @roseuid 379C637801CD
	 */
	public static void traceEntry(java.lang.Object source, java.lang.String methodName) {
		if ( traceOn ) {
			LogMessageId id = Logger.createLogMessageId( DEFAULT_LOGGER, "",
												getClassName( source ), methodName );
			Logger.traceEntry( id, null );
		}
    }
	
	/**
	   If tracing is off, output a message to the command console indicating that a method has been entered.
	   @param source The object from which the trace message originates.
	   @param methodName The name of the method exited.
	   @roseuid 379C63D00364
	 */
	public static void traceExit(java.lang.Object source, java.lang.String methodName) {
		if ( traceOn ) {
			LogMessageId id = Logger.createLogMessageId( DEFAULT_LOGGER, "",
												getClassName( source ), methodName );
			Logger.traceExit( id, null );
		}
    }
	
	/**
	   If debugging is active, output a message to the command console with the given debug information.
	   @param source The object from which the debug message originates.
	   @param argumentName The name of the argument for which a debug message is being created.
	   @param argumentValue The value of the argument for which a debug message is being created.
	   @roseuid 379C6490019D
	 */
	public static void debug(java.lang.Object source, java.lang.String argumentName, java.lang.Object argumentValue) {
		if ( debugOn ) {
			String messageString = "null";
			if( argumentName != null && argumentValue != null ) {
				messageString = argumentName + " = " + argumentValue.toString();
			}
			LogMessageId id = Logger.createLogMessageId( DEFAULT_LOGGER, messageString,
												getClassName( source ), "" );
			Logger.debug( id, null );
		}
    }
	
	/**
	   This class is a singleton and therefore never needs to be constructed.
	   @roseuid 379C6C15028B
	 */
	private Log() {}
	
	/**
	   @return True if debugging is currently active.
	   @roseuid 379C7BE800F6
	 * @deprecated
	 */
	public static synchronized boolean isDebugOn() {
        return debugOn;
    }
	
	/**
	   @return True if tracing is currently active.
	   @roseuid 379C7C020220
	 * @deprecated
	 */
	public static synchronized boolean isTraceOn() {
        return traceOn;
    }
	
	
	/**
	   If debugging is active, output a message to the command console with the given debug information.
	   @param source The object from which the debug message originates.
	   @param argumentName The name of the argument for which a debug message is being created.
	   @param argumentValue The value of the argument for which a debug message is being created.
	   @roseuid 37A5E527010E
	 */
	public static void debug(java.lang.Object source, java.lang.String argumentName, int argumentValue) {
		if ( debugOn ) {
			LogMessageId id = Logger.createLogMessageId( DEFAULT_LOGGER,
												argumentName + " = " + argumentValue,
												getClassName( source ), "" );
			Logger.debug( id, null );
		}
    }
	
	/**
	   If debugging is active, output a message to the command console with the given debug information.
	   @param source The object from which the debug message originates.
	   @param argumentName The name of the argument for which a debug message is being created.
	   @param argumentValue The value of the argument for which a debug message is being created.
	   @roseuid 37A70B590174
	 */
	public static void debug(java.lang.Object source, java.lang.String argumentName, boolean argumentValue) {
		if ( debugOn ) {
			LogMessageId id = Logger.createLogMessageId( DEFAULT_LOGGER,
												argumentName + " = " + argumentValue,
												getClassName( source ), "" );
			Logger.debug( id, null );
		}
    }
	
	/**
	   If debugging is active, output a message to the command console with the given debug information.
	   @param source The object from which the debug message originates.
	   @param argumentName The name of the argument for which a debug message is being created.
	   @param argumentValue The value of the argument for which a debug message is being created.
	   @roseuid 37A9D396012A
	 */
	public static void debug(java.lang.Object source, java.lang.String argumentName, float argumentValue) {
		if ( debugOn ) {
			LogMessageId id = Logger.createLogMessageId( DEFAULT_LOGGER,
												argumentName + " = " + argumentValue,
												getClassName( source ), "" );
			Logger.debug( id, null );
		}
    }
	
	/**
	   If debugging is active, output a message to the command console with the given debug information.
	   @param source The object from which the debug message originates.
	   @param argumentName The name of the argument for which a debug message is being created.
	   @param argumentValue The value of the argument for which a debug message is being created.
	   @roseuid 37A9D3CF0095
	 */
	public static void debug(java.lang.Object source, java.lang.String argumentName, double argumentValue) {
		if ( debugOn ) {
			LogMessageId id = Logger.createLogMessageId( DEFAULT_LOGGER,
												argumentName + " = " + argumentValue,
												getClassName( source ), "" );
			Logger.debug( id, null );
		}
    }
	
	/**
	   If debugging is active, output a message to the command console with the given debug information.
	   @param source The object from which the debug message originates.
	   @param argumentName The name of the argument for which a debug message is being created.
	   @param argumentValue The value of the argument for which a debug message is being created.
	   @roseuid 37A9D3FB02CA
	 */
	public static void debug(java.lang.Object source, java.lang.String argumentName, short argumentValue) {
		if ( debugOn ) {
			LogMessageId id = Logger.createLogMessageId( DEFAULT_LOGGER,
												argumentName + " = " + argumentValue,
												getClassName( source ), "" );
			Logger.debug( id, null );
		}
    }
	
	/**
	   If debugging is active, output a message to the command console with the given debug information.
	   @param source The object from which the debug message originates.
	   @param argumentName The name of the argument for which a debug message is being created.
	   @param argumentValue The value of the argument for which a debug message is being created.
	   @roseuid 37A9D42A02BD
	 */
	public static void debug(java.lang.Object source, java.lang.String argumentName, long argumentValue) {
		if ( debugOn ) {
			LogMessageId id = Logger.createLogMessageId( DEFAULT_LOGGER,
												argumentName + " = " + argumentValue,
												getClassName( source ), "" );
			Logger.debug( id, null );
		}
    }
	
	/**
	   If debugging is active, output a message to the command console with the given debug information.
	   @param source The object from which the debug message originates.
	   @param argumentName The name of the argument for which a debug message is being created.
	   @param argumentValue The value of the argument for which a debug message is being created.
	   @roseuid 37A9D4570087
	 */
	public static void debug(java.lang.Object source, java.lang.String argumentName, byte argumentValue) {
		if ( debugOn ) {
			LogMessageId id = Logger.createLogMessageId( DEFAULT_LOGGER,
												argumentName + " = " + argumentValue,
												getClassName( source ), "" );
			Logger.debug( id, null );
		}
    }
	
	/**
	   If debugging is active, output a message to the command console with the given debug information.
	   @param source The object from which the debug message originates.
	   @param argumentName The name of the argument for which a debug message is being created.
	   @param argumentValue The value of the argument for which a debug message is being created.
	   @roseuid 37A9D48802AE
	 */
	public static void debug(java.lang.Object source, java.lang.String argumentName, char argumentValue) {
		if ( debugOn ) {
			LogMessageId id = Logger.createLogMessageId( DEFAULT_LOGGER,
												argumentName + " = " + argumentValue,
												getClassName( source ), "" );
			Logger.debug( id, null );
		}
    }
	
	/**
	 * Describe <code>log</code> method here.
	 *
	 * @param source a <code>java.lang.Object</code> value
	 * @param message a <code>java.lang.Object</code> value
	 */
	public static void log(java.lang.Object source, java.lang.Object message) {
        String messageString = "null";
        if( message != null ) {
            messageString = message.toString();
        }

	   LogMessageId id = Logger.createLogMessageId( DEFAULT_LOGGER, messageString,
										   getClassName( source ), "" );
	   Logger.sysNotify( id, null );
    }
	/**
	 * Describe <code>logException</code> method here.
	 *
	 * @param source a <code>java.lang.Object</code> value
	 * @param exception a <code>java.lang.Throwable</code> value
	 */
	public static void logException(java.lang.Object source, java.lang.Throwable exception) {
		LogMessageId id = Logger.createLogMessageId( DEFAULT_LOGGER, "",
											getClassName( source ), "" );
		Logger.sysWarn( id, null, exception );
    }

    private static synchronized void init( java.util.Properties properties ) {
        debugOn = ( properties.getProperty( DEBUG_ON_PROPERTY ) != null );
        traceOn = ( properties.getProperty(  TRACE_ON_PROPERTY ) != null );
        initialized = true;
    }
    // the init method with void argument list will use System.properties to:
    //   if a property named LOG_PROPERTY_FILE exists, treat it as the name of a property file
    //   else, get properties directly from System.properties
    private static synchronized void init()  {
        init( System.getProperties() );
    }
}
