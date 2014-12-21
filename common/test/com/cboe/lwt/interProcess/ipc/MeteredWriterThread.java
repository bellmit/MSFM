/*
* MeteredWriterThread.java
*
* Created on October 1, 2002, 1:05 PM
*/

package com.cboe.lwt.interProcess.ipc;

import junit.framework.Assert;

import com.cboe.lwt.interProcess.InterProcessConnection;
import com.cboe.lwt.testUtils.SystemUtils;


/**
*
* @author  dotyl
*/
public class MeteredWriterThread extends Thread
{
    InterProcessConnection ipc;
    int                    blocksToWrite;
    int                    blockSize;

    public MeteredWriterThread( InterProcessConnection p_ipc, int p_blocksToWrite, int p_blockSize )
    {
        super( "perf writer" );
        blocksToWrite = p_blocksToWrite;
        blockSize     = p_blockSize;

        ipc = p_ipc;
    }

    public void run()
    {
        try
        {
            ipc.connect( 2000 );
            byte[] sendBuff = new byte[ blockSize ];
            for ( int h = 0; h < blockSize; ++h )
            {
                sendBuff[h] = (byte)( h % 256 );
            }

            long startTime = System.currentTimeMillis();

            for ( int i = 0; i < blocksToWrite; ++i )
            {
                ipc.write( sendBuff, 0, sendBuff.length );
            }
            ipc.flush();

            long endTime = System.currentTimeMillis();

            ipc.disconnect();

            long byteTp  = SystemUtils.getThroughput( blocksToWrite * blockSize, startTime, endTime );
            long blockTp = SystemUtils.getThroughput( blocksToWrite, startTime, endTime );

            System.out.println( "    - Bytes Written  (" + ( blocksToWrite * blockSize ) + ") in " + blockSize + "-byte Blocks: Byte Throughput (" + byteTp + ")" );
            System.out.println( "    - blocks Written (" + ( blocksToWrite ) + " : Block Throughput (" + blockTp + ")" );
        }
        catch( Exception ex )
        {
            ex.printStackTrace();
            Assert.fail();
        }
    }

};
