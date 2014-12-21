/*
 * WriterThread.java
 *
 * Created on October 1, 2002, 1:05 PM
 */

package com.cboe.lwt.interProcess.ipc;

import junit.framework.Assert;

import com.cboe.lwt.interProcess.InterProcessConnection;


/**
 *
 * @author  dotyl
 */
public class WriterThread extends Thread
{
    InterProcessConnection ipc;
    int                    bytesToWrite;

    public WriterThread( InterProcessConnection p_ipc, int p_bytesToWrite )
    {
        super( "writer" );
        bytesToWrite = p_bytesToWrite;
        ipc = p_ipc;
    }

    public void run()
    {
        try
        {
            ipc.connect( 2000 );
            for ( int i = 0; i < bytesToWrite; ++i )
            {
                ipc.write( (byte)( i % 128 ) );
            }
            ipc.flush();

            ipc.disconnect();
        }
        catch( Exception ex )
        {
            ex.printStackTrace();
            Assert.fail();
        }
    }


    public void runInThisThread()
    {
        run();
    }
};

    
    
