/*
 * Created on Feb 2, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cboe.lwt.interProcess;

import java.io.IOException;

/**
 * @author dotyl
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class IpcException extends IOException
{
    private InterProcessConnection ipc;
    
    public IpcException( String p_message, InterProcessConnection p_ipc )
    {
        super( p_message );
        ipc = p_ipc;
    }
    
    public IpcException( String p_message, InterProcessConnection p_connection, Throwable p_cause )
    {
        this( p_message, p_connection );
        initCause( p_cause );
    }
    
    public InterProcessConnection getIpc()
    {
        return ipc;
    }
}
