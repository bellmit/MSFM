/*
 * Created on Jan 21, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cboe.lwt.queue;

/**
 * @author dotyl
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class QueueException extends Exception
{
    public QueueException( String p_name )
    {
        super( p_name );
    }
    
    
    public QueueException( String p_name, Throwable p_cause )
    {
        super( p_name, p_cause );
    }
}
