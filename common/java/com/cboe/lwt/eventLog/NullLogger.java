/*
 * NullLogger.java
 *
 * Created on June 26, 2002, 11:18 AM
 */

package com.cboe.lwt.eventLog;

import com.cboe.lwt.byteUtil.*;



/**
 *
 * @author  dotyl
 */
public class NullLogger extends Logger
{
    public NullLogger()
    {
        super( SEV_TRACE, false );
    }
    
    public void log( String p_msg, int p_severity )
    {
    }
    
    public void log( String p_msg, Throwable p_report, int p_severity )
    {
    }
    
    /** @param p_msgs the messages to log
     * @param p_severity the severity of the message
     *
     */
    public void log(String p_msg, ByteIterator p_debugIter, int p_severity)
    {
    }
    
    /** @param p_msgs the messages to log
     * @param p_report an exception correlated in some way to the logged message
     * @param p_severity the severity of the message
     *
     */
    public void log(String p_msg, ByteIterator p_debugIter, Throwable p_report, int p_severity)
    {
    }
    
}
