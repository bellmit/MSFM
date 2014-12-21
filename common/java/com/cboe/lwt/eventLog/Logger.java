/*
 * Logger.java
 *
 * Created on June 26, 2002, 11:02 AM
 */

package com.cboe.lwt.eventLog;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.cboe.lwt.byteUtil.*;



/**
 * Root of the Log utility.  The Logger classes are built as a delegator to the
 * infrastructure when we integrate with it.
 *
 * @author  dotyl
 */
public abstract class Logger
{
    // TBD- getNextCategory( "SEV_TRACE" );
    public static final int  SEV_TRACE          = 0; // tracing: mostly for debugging
    public static final int  SEV_INFO           = 1; // informational: a noteworthy event has occured
    public static final int  SEV_WARNING        = 2; // warning: a low consequence error has occurred
    public static final int  SEV_ERROR          = 3; // error: an error which may compromise functionality has occurred
    public static final int  SEV_CRITICAL       = 4; // critical: an application fatal error has occurred
    
    public static final int  SEV_MIN            = SEV_TRACE;    // used to bound severity (low bound)
    public static final int  SEV_MAX            = SEV_CRITICAL; // used to bound severity (high bound)

    
    ////////////////////////////////////////////////////////////////////////////
    // modified singleton pattern
    
    private static Logger globalLogger   = null;  // modified singleton instance 
    
    
    /**
     * @return the global (modified singleton) instance of the logger
     */    
    public static synchronized Logger getGlobal()        
    {
        if ( globalLogger == null ) 
        {
            globalLogger = new ConsoleLogger( SEV_TRACE );
        }
        
        return globalLogger;
    }
    
    
    /**
     * set this instance to be the global instance (modified singleton pattern)
     */
    public synchronized void setGlobal()
    {
        globalLogger = this;
    }
    
    // END modified singleton pattern
    ////////////////////////////////////////////////////////////////////////////
    
    
    private int     severityFilter; // the level below which messages are discarded without being logged
    private boolean isTerse;        // if true, terse logging is enabled (messages may be terse based on originating code)
    
    
    /**
     * @param p_severityFilter How severe must a log message be in order to be logged
     * @param p_isTerse how detailed are logged messages
     */    
    protected Logger( int p_severityFilter, boolean p_isTerse )
    {
        severityFilter = p_severityFilter;
        isTerse        = p_isTerse;
    }
    
    /**
     * @return the current severity which must be met by a logged message in order
     * for logging of that message to occur
     */    
    public int getSeverityFilter()
    {
        return severityFilter;
    }

    /**
     * Sets the current severity which must be met by a logged message in order
     * for logging of that message to occur
     @return true if the method has succeeded, false - otherwise
     */    
    public synchronized boolean setSeverityFilter(int _severityFilter)
    {
        if (_severityFilter < SEV_MIN || _severityFilter > SEV_MAX ) return false;
        severityFilter = _severityFilter;
        return true;
    }
    
    /**
     * @return true iff terse mode is enabled
     */    
    public boolean getIsTerse()
    {
        return isTerse;
    }
    
    /**
     * @param p_isTerse new value for terse mode (true/false)
     */    
    public void setTerse( boolean p_isTerse )
    {
        isTerse = p_isTerse;
    }
    
    // string representation of severitys
    private static final String[] severityStrings = 
    {
        "TRACE",
        "INFO ", 
        "WARN ",
        "ERROR",
        "!CRIT"
    };
    
    /**
     * @param p_severity a severity enum
     * @return the string representation of p_severity
     */    
    public static String severityString( int p_severity )
    {
        assert( p_severity >= SEV_MIN && p_severity <= SEV_MAX ) : "Illegal severity enumeration";
        return severityStrings[ p_severity ];
    }

    
    /**
     * @param p_msg the message to log
     * @param p_severity the severity of the message
     */    
    public abstract void log( String p_msg, int p_severity );
    
    /**
     * @param p_msg the message to log
     * @param p_report an exception correlated in some way to the logged message
     * @param p_severity the severity of the message
     */    
    public abstract void log( String p_msg, Throwable p_report, int p_severity );

    
    /**
     * @param p_msg the messages to log
     * @param p_debugIter ByteIterator to write out
     * @param p_severity the severity of the message
     */    
    public abstract void log( String p_msg, ByteIterator p_debugIter, int p_severity );
    
    /**
     * @param p_msg the messages to log
     * @param p_debugIter ByteIterator to write out
     * @param p_report an exception correlated in some way to the logged message
     * @param p_severity the severity of the message
     */    
    public abstract void log( String p_msg, ByteIterator p_debugIter, Throwable p_report, int p_severity );
    
    
    public static boolean isTraceEnabled()
    {
        return getGlobal().getSeverityFilter() <= SEV_TRACE;
    }
    
    
    public static boolean isTerse()
    {
        return getGlobal().getIsTerse();
    }
    
    
    /** logs a message of severity level trace
     * @param p_msg the message to log
     */    
    public static void trace( String p_msg )
    {
        if ( SEV_TRACE >= getGlobal().severityFilter )
        {
            globalLogger.log( p_msg, SEV_TRACE );
        }
    }
    
    /** logs a message of severity level trace with an accompanying exception
     * @param p_msg the message to log
     * @param p_report the exception which is correlated to this log message
     */    
    public static void trace( String p_msg, Throwable p_report )
    {
        if ( SEV_TRACE >= getGlobal().severityFilter )
        {
            globalLogger.log( p_msg, p_report, SEV_TRACE );
        }
    }
    
    
    /** logs a message of severity level info
     * @param p_msg the message to log
     */    
    public static void info( String p_msg )
    {
        if ( SEV_INFO >= getGlobal().severityFilter )
        {
            globalLogger.log( p_msg, SEV_INFO );
        }
    }
    
    /** logs a message of severity level info with an accompanying exception
     * @param p_msg the message to log
     * @param p_report the exception which is correlated to this log message
     */    
    public static void info( String p_msg, Throwable p_report )
    {
        if ( SEV_INFO >= getGlobal().severityFilter )
        {
            globalLogger.log( p_msg, p_report, SEV_INFO );
        }
    }
    
    
    /** logs a message of severity level warning with an accompanying exception
     * @param p_msg the message to log
     * @param p_report the exception which is correlated to this log message
     */    
    public static void warning( String p_msg )
    {
        if ( SEV_WARNING >= getGlobal().severityFilter )
        {
            globalLogger.log( p_msg, SEV_WARNING );
        }
    }
    
    /** logs a message of severity level warning with an accompanying exception
     * @param p_msg the message to log
     * @param p_report the exception which is correlated to this log message
     */    
    public static void warning( String p_msg, Throwable p_report )
    {
        if ( SEV_WARNING >= getGlobal().severityFilter )
        {
            globalLogger.log( p_msg, p_report, SEV_WARNING );
        }
    }
    
    /** logs a message of severity level error with an accompanying exception
     * @param p_msg the message to log
     * @param p_report the exception which is correlated to this log message
     */    
    public static void error( String p_msg )
    {
        if ( SEV_ERROR >= getGlobal().severityFilter )
        {
            globalLogger.log( p_msg, SEV_ERROR );
        }
    }
    
    /** logs a message of severity level error with an accompanying exception
     * @param p_msg the message to log
     * @param p_report the exception which is correlated to this log message
     */    
    public static void error( String p_msg, Throwable p_report )
    {
        if ( SEV_ERROR >= getGlobal().severityFilter )
        {
            globalLogger.log( p_msg, p_report, SEV_ERROR );
        }
    }
    
    
    /** logs a message of severity level critical with an accompanying exception
     * @param p_msg the message to log
     * @param p_report the exception which is correlated to this log message
     */    
    public static void critical( String p_msg )
    {
        if ( SEV_CRITICAL >= getGlobal().severityFilter )
        {
            globalLogger.log( p_msg, SEV_CRITICAL );
        }
    }
    
    /** logs a message of severity level critical with an accompanying exception
     * @param p_msg the message to log
     * @param p_report the exception which is correlated to this log message
     */    
    public static void critical( String p_msg, Throwable p_report )
    {
        if ( SEV_CRITICAL >= getGlobal().severityFilter )
        {
            globalLogger.log( p_msg, p_report, SEV_CRITICAL );
        }
    }


    /**
     * appends all relevant information about p_report to p_sb
     * 
     * The information that will be appended is the exception's: message, stack trace,
     * and then recursively all exceptions that caused this exception with the same info
     * 
     * @param p_sb  string buffer that will be appended with p_report's info
     * @param p_report throwable that will be documented into p_sb
     */
    protected void appendThrowableInfo( StringBuffer p_sb, Throwable p_report )
    {
        p_sb.append( "\n-------- Exception --------\n" )
            .append( p_report.toString() )
            .append( "\n-------- Trace ------------\n" );

        StackTraceElement[] stackTrace = p_report.getStackTrace();
        
        for ( int i = 0; i < stackTrace.length; ++i )
        {
            p_sb.append( stackTrace[i].toString() )
                .append( '\n' );
        }
        
        if ( p_report.getCause() != null )
        {
            p_sb.append( "-------- Caused by ------------" );
            appendThrowableInfo( p_sb, p_report.getCause() );  // recursive call to describe entire exceotion stack
        }
    }


    protected void appendLogInfo( StringBuffer sb, String p_msg, int p_severity )
    {
        sb.append( timeFmt.format( new Date() ) )
          .append( "[" )
          .append( severityString( p_severity ) )
          .append( "] " )
          .append( p_msg );
    }


    protected void appendIteratorInfo( StringBuffer sb, ByteIterator p_debugIter )
    {
        sb.append( "Iterator :\n" );
        p_debugIter.appendDebugString( sb );
    }
    
    
    private static SimpleDateFormat timeFmt;
    
    static
    {
        timeFmt = new SimpleDateFormat( "HH:mm:ss | " );
    }
    
    

}
