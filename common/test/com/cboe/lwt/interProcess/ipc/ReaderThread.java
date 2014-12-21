/*
 * ReaderThread.java
 *
 * Created on October 1, 2002, 1:04 PM
 */

package com.cboe.lwt.interProcess.ipc;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import junit.framework.Assert;


/**
 *
 * @author  dotyl
 */
public class ReaderThread extends Thread
{
    ServerSocket svrSock;
    int          bytesToRead;
    int          connectionsToAccept;

    public ReaderThread( int p_port, int p_receiveBufSize, int p_bytesToRead, int p_connectionsToAccept )
    {
        super( "reader" );

        bytesToRead         = p_bytesToRead;
        connectionsToAccept = p_connectionsToAccept;

        try
        {
            svrSock = new ServerSocket( p_port );
            svrSock.setReceiveBufferSize( p_receiveBufSize );
            svrSock.setReuseAddress( true );
        }
        catch( Exception ex )
        {
            ex.printStackTrace();
            Assert.fail();
        }
    }       
    public void run()
    {
        try
        {
            for ( int j = 0; j < connectionsToAccept; ++j )
            {
                Socket inSock = svrSock.accept();
                InputStream in = inSock.getInputStream();

                for ( int i = 0; i < bytesToRead; ++i )
                {
                    junit.framework.Assert.assertEquals( i % 128, in.read() );
                }

                inSock.close();
            }
            
            svrSock.close();
        }
        catch( Exception ex )
        {
            ex.printStackTrace();
            Assert.fail();
            System.out.flush();
        }
    }
};
