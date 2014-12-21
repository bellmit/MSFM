/*
 * ConsoleLogger.java
 *
 * Created on June 26, 2002, 11:18 AM
 */

package com.cboe.lwt.eventLog;

import java.io.PrintStream;

import com.cboe.lwt.byteUtil.*;



/**
 * Version of the logger that serializes to standard out
 * @author  dotyl
 */
public class ConsoleLogger extends Logger
{
    PrintStream out;
    
    public ConsoleLogger()
    {
        super( SEV_TRACE, false );
        out = System.out;
    }
    
    public ConsoleLogger( int p_severityFilter )
    {
        super( p_severityFilter, false );
        out = System.out;
    }
    
    public ConsoleLogger( int p_severityFilter, PrintStream p_out )
    {
        super( p_severityFilter, false );
        
        out = p_out;
    }
    
    /**
     * @param p_msg the message to log
     * @param p_severity the severity of the message
     */    
    public synchronized void log( String p_msg, int p_severity )
    {
        StringBuffer sb = new StringBuffer();
        
        appendLogInfo( sb, p_msg, p_severity );
        
        out.println( sb.toString() );
    }

    /**
     * @param p_msg the message to log
     * @param p_report an exception correlated in some way to the logged message
     * @param p_severity the severity of the message
     */    
    public synchronized void log( String p_msg, Throwable p_report, int p_severity )
    {
        StringBuffer sb = new StringBuffer();

        appendLogInfo( sb, p_msg, p_severity  );
        appendThrowableInfo( sb, p_report );
        
        out.println( sb.toString() );
    }

    /** @param p_msgs the messages to log
     * @param p_severity the severity of the message
     *
     */
    public synchronized void log( String p_msg, ByteIterator p_debugIter, int p_severity )
    {
        StringBuffer sb = new StringBuffer();

        appendLogInfo( sb, p_msg, p_severity  );
        appendIteratorInfo( sb, p_debugIter );
        
        out.println( sb.toString() );
    }

    /** @param p_msg the messages to log
     * @param p_report an exception correlated in some way to the logged message
     * @param p_severity the severity of the message
     *
     */
    public synchronized void log( String p_msg, ByteIterator p_debugIter, Throwable p_report, int p_severity )
    {
        StringBuffer sb = new StringBuffer();

        appendLogInfo( sb, p_msg, p_severity  );
        appendIteratorInfo( sb, p_debugIter );
        appendThrowableInfo( sb, p_report );
        
        out.println( sb.toString() );
    }
    
}
