/*
 * Created on Feb 2, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cboe.lwt.interProcess;

/**
 * @author dotyl
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class InterruptedIpcException extends IpcException
{
    public InterruptedIpcException( String                 p_message, 
                                    InterProcessConnection p_ipc, 
                                    Throwable              p_cause )
    {
        super( p_message, p_ipc, p_cause );
    }
}
