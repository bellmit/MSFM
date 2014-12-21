/*
 * TcpToFileThread.java
 *
 * Created on July 25, 2002, 11:33 AM
 */

package com.cboe.lwt.systemTest;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import com.cboe.lwt.eventLog.Logger;
import com.cboe.lwt.thread.ThreadTask;


/**
 *
 * @author  dotyl
 */
public class TcpToFileThread extends ThreadTask
{   
    private static final int ZERO_READ_RETRY_DELAY_MS = 500;
    
    
    int                 port;
    int                 sendBuffSize;
    int                 receiveBuffSize;
    int                 blockSize;
    String              logPathBase;
    long                targetFileLength;
    ServerSocketChannel serverSock;
    
    public TcpToFileThread( int                 p_port,
                            int                 p_receiveBuffSize,
                            int                 p_sendBuffSize,
                            int                 p_blockSize,
                            String              p_logPathBase,
                            long                p_targetFileLength )
    {
        super(   "Meter: Port = " + p_port
               + ", Log File Base = " + p_logPathBase );
        
        port             = p_port;
        receiveBuffSize  = p_receiveBuffSize;
        sendBuffSize     = p_sendBuffSize;
        blockSize        = p_blockSize;
        logPathBase      = p_logPathBase;
        targetFileLength = p_targetFileLength;    
    }
    
    
    public void doTask()
        throws IOException, 
               InterruptedException
    {
        serverSock = ServerSocketChannel.open();
        serverSock.configureBlocking( true );
        InetSocketAddress address = new InetSocketAddress( port ); 
        serverSock.socket().setReceiveBufferSize( receiveBuffSize );
        serverSock.socket().bind( address );

        Logger.info( "Port ready for connection : " + port );

        SocketChannel chan = serverSock.accept();

        Logger.info( "Client connection accepted on port : " + port );

        Socket sock = chan.socket();

        sock.setSendBufferSize( sendBuffSize );

        Logger.info( "Tcp send buffer size    : " + sock.getSendBufferSize() );
        Logger.info( "Tcp receive buffer size : " + sock.getReceiveBufferSize() );

        try
        {
            listen( chan );
        }
        catch ( IOException ex )
        {
            Logger.error( "Port terminated by IOException: " + port, ex );
        }
        
        chan.socket().close();
        
        serverSock.close();

        Logger.info( "Disconnected port : " + port );
    }

     
    private void listen( SocketChannel p_in )
        throws InterruptedException,
               IOException
    {
        int curFileSuffix = 0;
        File logFile;

        while ( true )
        {
            String newFileName = ( logPathBase + curFileSuffix ) + ".log";

// TBD- REPLACE THIS to not overwrite existing logs  (see also kludge below)
            newFileName = ( logPathBase + curFileSuffix ) + ".log";
            logFile = new File( newFileName );
            
            if ( logFile.exists() )  // then found an open filename 
            {
                logFile.delete();
            }
/* TBD- WITH THIS to not overwrite existing logs  (see also kludge below)
            while ( true )
            {
                newFileName = ( logPathBase + curFileSuffix ) + ".log";
                logFile = new File( newFileName );
                
                if ( ! logFile.exists() )  // then found an open filename 
                {
                    break;
                }
                ++curFileSuffix;
            }
*/

            logFile.createNewFile();

            FileOutputStream outStream = new FileOutputStream( logFile );

            FileChannel out = outStream.getChannel();  

            if ( out == null )
            {
                Logger.error( "File " + newFileName + " channel not created" );
                continue;
            }

            socketToFile( p_in, out );
            
            out.close();

            // TBD- kludge to allow round-the-clock testing without filling the disk
            // to un-kludge, uncomment the next line:
            // ++curFileSuffix;
        }
    }
    
    
    private void socketToFile( SocketChannel p_in, FileChannel p_out )
        throws InterruptedException,
               IOException
    {
        ByteBuffer xfer    = ByteBuffer.allocateDirect( blockSize );
        int        readLen = 0;
        int        len     = 0;

        while ( p_out.position() < targetFileLength ) 
        {
            readLen = p_in.read( xfer );

            while ( readLen <= 0 )
            {
                // write whatever we have in the buffer
                xfer.flip();
                p_out.write( xfer );

                if ( readLen < 0 ) // then the line is closed
                {
                    p_in.socket().close();
                    p_out.close();
                    throw new IOException( "Channel at EOF" );
                }

                // if here, then no bytes read on a still open channel... pause then retry
                Thread.sleep( ZERO_READ_RETRY_DELAY_MS );
                readLen = p_in.read( xfer );
            }

            len += readLen;
            if ( len + 256 < blockSize )
            {
                continue;
            }

            xfer.flip();
            p_out.write( xfer );
            xfer.compact();
            len = 0;
        }
        
    }
    
};

        

 
 
