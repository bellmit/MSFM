/*
* MeteredReaderThread.java
*
* Created on October 1, 2002, 1:04 PM
*/

package com.cboe.lwt.interProcess.ipc;


import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import junit.framework.Assert;

import com.cboe.lwt.testUtils.SystemUtils;


/**
*
* @author  dotyl
*/
public class MeteredReaderThread extends Thread
{
    ServerSocket svrSock;
    int          blocksToRead;
    int          blockSize;
    int          connectionsToAccept;

    public MeteredReaderThread( int p_port, int p_receiveBufSize, int p_blocksToRead, int p_blockSize, int p_connectionsToAccept )
    {
        super( "reader" );

        blocksToRead        = p_blocksToRead;
        blockSize           = p_blockSize;
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
            long startTime = System.currentTimeMillis();

            byte[] recv     = new byte[blockSize];
            int reads       = 0;
            int bytesRead   = 0;
            int bytesToRead = blocksToRead * blockSize;

            for ( int j = 0; j < connectionsToAccept; ++j )
            {
                Socket inSock = svrSock.accept();
                InputStream in = inSock.getInputStream();

                while (  bytesRead < bytesToRead )
                {
                    bytesRead += in.read( recv );
                    ++reads;
                }
               
                inSock.close();
            }
            
            svrSock.close();

            long endTime = System.currentTimeMillis();

            long byteTp  = SystemUtils.getThroughput( blocksToRead * blockSize * connectionsToAccept, startTime, endTime );
            long blockTp = SystemUtils.getThroughput( blocksToRead * connectionsToAccept, startTime, endTime );

            System.out.println( "    - Bytes Read  (" + ( blocksToRead * blockSize * connectionsToAccept ) + ") in " + blockSize + "-byte Blocks Across " + connectionsToAccept + " Connections: Byte Throughput (" + byteTp + ")" );
            System.out.println( "    - blocks Read (" + ( blocksToRead * connectionsToAccept ) + ") Across " + connectionsToAccept + " Connections: Block Throughput (" + blockTp + ")" );
            System.out.println("Average bytes per block : " + ( bytesToRead / reads ) );
            
        }
        catch( Exception ex )
        {
            ex.printStackTrace();
            Assert.fail();
            System.out.flush();
        }
    }
};

