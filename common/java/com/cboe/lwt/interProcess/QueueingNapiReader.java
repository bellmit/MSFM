/*
 * Created on Feb 17, 2005
 */
package com.cboe.lwt.interProcess;

import java.io.IOException;

import com.cboe.lwt.interProcess.NapiReader;
import com.cboe.lwt.interProcess.TcpIpc;
import com.cboe.lwt.queue.InterThreadQueue;
import com.cboe.lwt.queue.QueueException;
import com.cboe.lwt.queue.QueueInterruptedException;
import com.cboe.lwt.thread.ThreadTask;

/**
 * @author dotyl
 */
public final class QueueingNapiReader 
    extends ThreadTask
{
    private InterThreadQueue outQueue;
    private TcpIpc           ipc;
    private NapiReader       napiIn;
 
    
    public QueueingNapiReader( String           p_name,
                               TcpIpc           p_ipc,
                               InterThreadQueue p_outQueue ) 
    {
        super( p_name );

        ipc      = p_ipc;
        napiIn   = new NapiReader( ipc );
        outQueue = p_outQueue;
    }
    

    protected final void doTask()
        throws IOException,
               QueueInterruptedException
    {
        try
        {
            while ( true )
            {
                outQueue.enqueue( napiIn.getBlock() );
            }
        }
        catch( QueueInterruptedException ex )
        {
            throw ex;
        }
        catch( QueueException ex )
        {
            IOException ex2 = new IOException( "QueueException" );
            ex2.initCause( ex );
            throw ex2;
        }
    }
    
}
