/*
 * TcpIpc.java
 *
 * Created on May 3, 2002, 3:08 PM
 */

package com.cboe.lwt.interProcess;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.cboe.lwt.byteUtil.ByteIterator;
import com.cboe.lwt.eventLog.Logger;
import com.cboe.lwt.transactionLog.LogDestination;
import com.cboe.lwt.transactionLog.ReplayTransactionLog;


/**
 * Represents an Inter-process connection using the TCP protocol
 *
 * @author  dotyl
 */
public class TcpIpc implements InterProcessConnection
{
    private static final int INITIAL_BUF_SIZE = 1024;
    

    private InetSocketAddress[] addresses;
    private int currentUrlIndex;
    
    private int sendBufferSize;
    private int receiveBufferSize;

    private Object        connectMonitor = new Object();
    private Object        readMonitor = new Object();
    private Object        writeMonitor = new Object();
    private Object        inboundLogMonitor = new Object();
    private Object        outboundLogMonitor = new Object();
 
    private SocketChannel channel;  // when this is null ==> disconnected
    
    private ByteBuffer    outBuff;
    private ByteBuffer    inBuff;

    private ReplayTransactionLog outboundTransactionLog = null;
    private ReplayTransactionLog inboundTransactionLog = null;
    
    private ByteIterator inLogIter = ByteIterator.getInstance();
    private byte[]       inLogBuf;
    
    private ByteIterator outLogIter = ByteIterator.getInstance();
    private byte[]       outLogBuf;
    
    private boolean isConnectable;

    private Object  hangTest                 = new Object();
    private int     numFlushesAtLastHungTest = 0;
    private int     numFlushesNow            = 0;
    private boolean flushPending             = false;
    private int     connectionNumber         = 0;


    /**
     * Creates an instance with the specified attributes
     *
     * @param p_urls Array of URLs to which this connection will be attempted
     * element 0 is the primary, and all subsequent elements are backups to
     * attempt in order
     * @param p_sendBufferSize Size in bytes of the TCP send buffer
     * @param p_receiveBufferSize Size in bytes of the TCP receive buffer
     */
    public static TcpIpc getInstance( URL[] p_urls, int p_sendBufferSize, int p_receiveBufferSize )
        throws IpcException
    {
        return new TcpIpc( p_urls, p_sendBufferSize, p_receiveBufferSize );
    }
    
    
    /**
     * Creates an instance with the specified attributes
     *
     * @param p_url Array of URLs to which this connection will be attempted
     * element 0 is the primary, and all subsequent elements are backups to
     * attempt in order
     * @param p_sendBufferSize Size in bytes of the send buffer
     * @param p_receiveBufferSize Size in bytes of the TCP receive buffer
     */
    public static TcpIpc getInstance( URL p_url, int p_sendBufferSize, int p_receiveBufferSize )
        throws IpcException
    {
        if ( p_url == null ) 
        {
            throw new IpcException( "URL is null", null );
        }
        
        URL[] connectTo = new URL[1];
        connectTo[0] = p_url;
        
        return new TcpIpc( connectTo, p_sendBufferSize, p_receiveBufferSize );
    }


    /**
     * Creates an instance with the specified attributes
     *
     * @param p_channel the existant channel this IPC should wrap
     * @param p_sendBufferSize Size in bytes of the TCP send buffer
     * @param p_receiveBufferSize Size in bytes of the TCP receive buffer
     */
    public static TcpIpc wrapExistingChannel( SocketChannel p_channel, 
                                              int           p_sendBufferSize, 
                                              int           p_receiveBufferSize )
        throws IpcException
    {
        return new TcpIpc( p_channel, p_sendBufferSize, p_receiveBufferSize );
    }


    /**
     * Creates an unconnected Ipc with the specified attributes
     *
     * @param p_urls Array of URLs to which this connection will be attempted
     * element 0 is the primary, and all subsequent elements are backups to
     * attempt in order
     * @param p_sendBufferSize Size in bytes of the TCP send buffer
     * @param p_receiveBufferSize Size in bytes of the TCP receive buffer
     */
    private TcpIpc( URL[] p_urls, int p_sendBufferSize, int p_receiveBufferSize )
        throws IpcException
    {
        // configure IPC

            isConnectable = true;
        
            initialize( p_sendBufferSize, p_receiveBufferSize );
            
        // set local URLs
        
            if ( p_urls.length == 0 ) 
            {
                throw new IpcException( "URL list is empty", this );
            }
    
            addresses = new InetSocketAddress[ p_urls.length ];
            
            for ( int i = 0; i < p_urls.length; ++i )
            {
                if ( p_urls[ i ] == null ) 
                {
                    throw new IpcException( "URL # " + i + " is NULL", this );
                }
                
                addresses[ i ] = new InetSocketAddress( p_urls[ i ].getHost(), 
                                                        p_urls[ i ].getPort() );
            }
    }


    private TcpIpc( SocketChannel p_channel, int p_sendBufferSize, int p_receiveBufferSize )
        throws IpcException
    {
        // configure IPC

            isConnectable = false;
        
            initialize( p_sendBufferSize, p_receiveBufferSize );

        // set local URLs
        
            InetSocketAddress sockAddr = (InetSocketAddress)p_channel.socket().getRemoteSocketAddress();
            
            addresses = new InetSocketAddress[1];
            addresses[0] = sockAddr; // used for logging

        // set channel / configure socket
        
            assert ( p_channel != null ) : "Programming Error: Cannot wrap a null socket";
            
            channel = p_channel;
            
            initSocket();
    }


    private void initialize( int p_sendBufferSize, int p_receiveBufferSize )
    {
        sendBufferSize = p_sendBufferSize;
        receiveBufferSize = p_receiveBufferSize;
        
        outBuff           = ByteBuffer.allocateDirect( sendBufferSize );
        inBuff            = ByteBuffer.allocateDirect( receiveBufferSize );
        inBuff.flip();  // marks all bytes as consumed... will cause readMore() on the first read operation

        currentUrlIndex = 0;
    }


    private void initSocket() 
        throws IpcException
    {
        assert ( channel != null ) : "Programming error : Internal TcpIpc : Must first configuree channel";

        if ( channel.socket() == null ) 
        {
            throw new IpcException( "No socket available for channel", this );
        }
        
        try
        {
            channel.socket().setSendBufferSize( sendBufferSize );
            channel.socket().setReceiveBufferSize( receiveBufferSize );
        }
        catch ( SocketException ex )
        {
            throw new IpcException( "Configuring Socket", this, ex );
        }
    }


    public void enableOutboundTransactionLogging( LogDestination p_logDest, int p_logFlushInterval_MS )
    {
        synchronized ( outboundLogMonitor )
        {
            if ( outboundTransactionLog != null )
            {
                outboundTransactionLog.dispose();
            }
            outboundTransactionLog = new ReplayTransactionLog( p_logDest, p_logFlushInterval_MS );  
                
            if ( outLogBuf == null )
            {
                outLogBuf = new byte[ INITIAL_BUF_SIZE ];
            }
        }
    }


    public void disableOutboundTransactionLogging()
    {
        synchronized ( outboundLogMonitor )
        {
            if ( outboundTransactionLog == null )
            {
                return;
            }
    
            outboundTransactionLog.dispose();
            outboundTransactionLog = null;
        }
    }


    public boolean isOutboundTransactionLoggingEnabled()
    {
        synchronized ( outboundLogMonitor )
        {
            return ( outboundTransactionLog != null );
        }
    }

    public void enableInboundTransactionLogging( LogDestination p_logDest, int p_logFlushInterval_MS )
    {
        synchronized ( inboundLogMonitor )
        {
            if ( inboundTransactionLog != null )
            {
                inboundTransactionLog.dispose();
            }
            inboundTransactionLog = new ReplayTransactionLog( p_logDest, p_logFlushInterval_MS );  
        
            if ( inLogBuf == null )
            {
                inLogBuf = new byte[ INITIAL_BUF_SIZE ];
            }
        }
    }


    public void disableInboundTransactionLogging()
    {
        synchronized ( inboundLogMonitor )
        {
            if ( inboundTransactionLog == null )
            {
                return;
            }

            inboundTransactionLog.dispose();
            inboundTransactionLog = null;
        }
    }

    public boolean isInboundTransactionLoggingEnabled()
    {
        synchronized ( inboundLogMonitor )
        {
            return ( inboundTransactionLog != null );
        }
    }

    /**
     * Writes bytes to the IPC
     *
     * @param p_block the storage to write from
     * @param p_startOfBlock the offset in p_block at which to start copying
     * @param p_blockLength the number of bytes to copy
     *
     * @throws IpcException on failure
     */
    public void write( byte[] p_block,
                       int p_startOfBlock,
                       int p_blockLength )
        throws IpcException
    {
        synchronized ( writeMonitor )
        {
            while ( outBuff.remaining() < p_blockLength )
            {
                flush();
            }
    
            outBuff.put( p_block, p_startOfBlock, p_blockLength );
        }
    }


    /**
     * Writes a byte to the IPC
     *
     * @param p_byte the byte to write
     *
     * @throws IpcException on failure
     */
    public void write( byte p_byte )
        throws IpcException
    {
        synchronized ( writeMonitor )
        {
            while ( outBuff.remaining() <= 0 )
            {
                flush();
            }
    
            outBuff.put( p_byte );
        }
    }
    
    
    public void writeDirect( ByteBuffer p_toWrite ) 
        throws IpcException
    {
        int writeLength = p_toWrite.position();
        if ( writeLength == 0 )
        {
            return;
        }
        
        synchronized ( writeMonitor )
        {
            flush();

            synchronized ( hangTest )
            {
                flushPending = true;
            }
    
            try
            {
                p_toWrite.flip();

                writeOutboundTransactionLogIfNecessary();

                int bytesWritten = channel.write( p_toWrite );
                if ( bytesWritten <= writeLength ) 
                {
                    outBuff.put( p_toWrite );
                }
            }
            catch( IOException ex )
            {
                throw new IpcException( "Flush - Writing to channel", this, ex );
            }
            finally
            {
                synchronized ( hangTest )
                {
                    ++numFlushesNow;
                    flushPending = false;
                }
            }
        }
    }


    /**
     * returns true if the socket is hung for writing (i.e.
     */
    public boolean isHung()
    {
        boolean result;

        synchronized ( hangTest )
        {
            result = ( ! isConnected() // IF not connected 
                       || ( flushPending                // OR waiting for a flush
                            && ( numFlushesNow == numFlushesAtLastHungTest ) ) ); // AND it's still the same one as last time we looked
            numFlushesAtLastHungTest = numFlushesNow;
        }

        return result;
    }


    /**
     * Flushes all bytes to the IPC (blocks until all bytes are processed)
     *
     * @throws IpcException on failure
     */
    public void flush()
        throws IpcException
    {
        synchronized ( writeMonitor )
        {
            if ( outBuff.position() == 0 )
            {
                return;
            }
            
            synchronized ( hangTest )
            {
                ++numFlushesNow;
                flushPending = true;
            }
    
            try
            {
                outBuff.flip();

                writeOutboundTransactionLogIfNecessary();

                channel.write( outBuff );
                outBuff.compact();
            }
            catch( IOException ex )
            {
                throw new IpcException( "Flush - Writing to channel", this, ex );
            }
            finally
            {
                synchronized ( hangTest )
                {
                    flushPending = false;
                }
            }
        }
    }


    private void writeOutboundTransactionLogIfNecessary() throws IOException
    {
        synchronized ( outboundLogMonitor )
        {
            if ( outboundTransactionLog != null )
            {
                int flushLength = outBuff.remaining();
             
                if ( flushLength > outLogBuf.length )
                {
                    outLogBuf = new byte[ 2 * flushLength ];
                }
                
                outBuff.mark();
                outBuff.get( outLogBuf, 0, flushLength );
                outBuff.reset();
                
                outLogIter.rebase( outLogBuf, 0, flushLength, 0 );
             
                outboundTransactionLog.write( outLogIter, flushLength );
            }
        }
    }


    /**
     * Reads a byte from the IPC
     *
     * @return the byte read from the stream
     *
     * @throws IpcException on failure
     */
    public byte read()
        throws IpcException
    {
        synchronized ( readMonitor )
        {
            if ( inBuff.remaining() < 1 )
            {
                readMore( 1 );
            }
    
            return inBuff.get();
        }
    }


    /**
     * Reads bytes from the IPC
     *
     * @param p_dest the destination buffer that will receive the read
     * @param p_destOffset the offset withing the buffer to take the first byte
     * @param p_maxLength the number of bytes to attempt to copy
     *
     * @return the number of bytes read
     *
     * @throws IpcException on failure
     */
    public int read( byte[] p_dest,
                     int    p_destOffset,
                     int    p_maxLength )
        throws IpcException
    {
        return read( p_dest, p_destOffset, p_maxLength, 1 );
    }


    /** Reads bytes from the source and block until a minimum number of bytes are read
     *
     * @param p_dest the destination buffer that will receive the read
     * @param p_destOffset the offset within the p_dest buffer to take the first byte
     * @param p_maxLength the number of bytes to attempt to copy
     * @param p_minLength the minimum number of bytes to copy (will block until available)
     *
     * @return the number of bytes read
     *
     * @throws IpcException on failure, including if there are no further bytes available
     *
     */
    public int read( byte[] p_dest,
                     int    p_destOffset,
                     int    p_maxLength,
                     int    p_minLength )
        throws IpcException
    {
        synchronized ( readMonitor )
        {
            if ( inBuff.remaining() < p_maxLength )
            {
                readMore( p_minLength ); // attempt a read if we don't have p_maxLength bytes in readableBytes
            }
    
            int copyLength = ( p_maxLength < inBuff.remaining() )
                             ? p_maxLength
                             : inBuff.remaining();
    
            inBuff.get( p_dest, p_destOffset, copyLength );
    
            return copyLength;
        }
    }


    private void readMore( int p_minLength )
        throws IpcException
    {
        if ( channel == null )
        {
            throw new IpcException( "Reading from unconnected channel",
                                    this );
        }

        if ( p_minLength > inBuff.capacity() )
        {
            throw new IpcException( "Minimum read length (" + p_minLength + ") would overflow input buffer of size " + inBuff.capacity(),
                                    this );
        }

        int totalLength = inBuff.remaining();

        inBuff.compact();

        while ( totalLength < p_minLength )
        {
            int readLen;
            
            try
            {
                readLen = channel.read(inBuff);
            }
            catch ( IOException ex )
            {
                throw new IpcException( getLogString( "Socket closed while reading" ),
                                        this,
                                        ex );
            }
            
            if ( readLen <= 0 )
            {
                throw new IpcException( getLogString( "Socket closed while reading" ),
                                        this );
            }

            // at this point, we've received data
            
            writeInboundTransactionLogIfNecessary();

            totalLength += readLen;
        }

        inBuff.flip();
    }


    private void writeInboundTransactionLogIfNecessary() throws IpcException
    {
        synchronized ( inboundLogMonitor )
        {
            if ( inboundTransactionLog != null )
            {
                ByteBuffer logTemp = inBuff.duplicate();
                logTemp.flip();
                int readLength = logTemp.remaining();
                
                if ( readLength > inLogBuf.length )
                {
                    inLogBuf = new byte[ 2 * readLength ];
                }
                
                logTemp.get( inLogBuf, 0, readLength );
                inLogIter.rebase( inLogBuf, 0, readLength, 0 );
                
                try
                {
                    inboundTransactionLog.write( inLogIter, readLength );
                }
                catch ( IOException ex )
                {
                    throw new IpcException( getLogString( "Write" ),
                                            this,
                                            ex );
                }
            }
        }
    }


    /**
     * Connects to the remote process over the primary URL (the firs in the list
     * of URLs supplied to the constructor)
     *
     * This primary URL will be retried until a connection succeeds.
     *
     * @param p_connectRetryTimeout the amount of time to wait before retrying the
     * URL after a failure to `
     */
    public void connectSpecific( int p_lowIndex, 
                                 int p_highIndex, 
                                 int p_connectRetryTimeout )
        throws IpcException
    {
        createConnectedChannel( p_lowIndex, 
                                p_highIndex, 
                                p_connectRetryTimeout, 
                                -1 );
    }


    /**
     * Connects to the remote process over the primary URL (the firs in the list
     * of URLs supplied to the constructor)
     *
     * This primary URL will be retried until a connection succeeds.
     *
     * @param p_connectRetryTimeout the amount of time to wait before retrying the
     * URL after a failure to `
     */
    public void connectSpecific( int p_lowIndex, 
                                 int p_highIndex, 
                                 int p_connectRetryTimeout,
                                 int p_retryLoopsBeforeFailure )
        throws IpcException
    {
        createConnectedChannel( p_lowIndex, 
                                p_highIndex, 
                                p_connectRetryTimeout, 
                                p_retryLoopsBeforeFailure );
    }


    /**
     * Connects to the remote process over any of the connection's URLs
     *
     * Connection will be attempted starting at element 0 of the URL array passed
     * to the constructor.  If that connection fails, all URLs will be
     * tried in order they appear in the array.  This array of URLs will be retried
     * until a connection succeeds.
     *
     * @param p_connectRetryTimeout the amount of time to wait before retrying the
     * URL after a failure to connect
     */
    public void connect( int p_connectRetryTimeout )
        throws IpcException
    {
        createConnectedChannel( 0, 
                                addresses.length - 1, 
                                p_connectRetryTimeout, 
                                -1 );
    }


    /**
     * Connects to the remote process over any of the connection's URLs
     *
     * Connection will be attempted starting at element 0 of the URL array passed
     * to the constructor.  If that connection fails, all URLs will be
     * tried in order they appear in the array.  This array of URLs will be retried
     * until a connection succeeds.
     *
     * @param p_connectRetryTimeout the amount of time to wait before retrying the
     * URL after a failure to connect
     */
    public void connect( int p_connectRetryTimeout,
                         int p_retryLoopsBeforeFailure )
        throws IpcException
    {
        createConnectedChannel( 0, 
                                addresses.length - 1, 
                                p_connectRetryTimeout, 
                                p_retryLoopsBeforeFailure );
    }
 
 
    // static to prevent confusion between connectPrimary and connect (connect primary provides an explicity p_connectTo array)
    void createConnectedChannel( int p_firstIndex,
                                 int p_lastIndex, 
                                 int p_connectRetryTimeout, 
                                 int p_retryLoopsBeforeFailure )
        throws IpcException
    {
        assert ( isConnectable ) : "Application Programming Error: Misuse of TcpIpc : Can't connect channels created by wrapExistingChannel()";
        
        currentUrlIndex = p_firstIndex;
        int remainingRetryLoops = p_retryLoopsBeforeFailure;
        
        synchronized ( connectMonitor )
        {
            do 
            {
                try
                {
                    channel = SocketChannel.open();
                    if ( channel == null )
                    {
                        throw new IOException( "Could not open Channel" );
                    }
    
                    channel.configureBlocking( true );
            
                    initSocket();
            
                    channel.connect( addresses[ currentUrlIndex ] );
                    
                    // connected
                    ++connectionNumber;
                    
                    StringBuffer sb = new StringBuffer();
                    sb.append( "\nSocket connected" )
                      .append( "\n    Tcp send buffer size : " ).append( channel.socket().getSendBufferSize() )
                      .append( "\n    Tcp receive buffer size : " ).append( channel.socket().getReceiveBufferSize() )
                      .append( "\n    Blocking is : " ).append( ( channel.isBlocking() )
                                                                ? "ENABLED"
                                                                : "DISABLED" );
                                                                
                    Logger.trace( getLogString( sb.toString() ) );
                }
                catch( IOException ex )
                {
                    Logger.info( getLogString( "[LAST ADDRESS] Couldn't connect" ) );
    
                    ++currentUrlIndex; // try next URL
                    
                    if ( currentUrlIndex > p_lastIndex )
                    { 
                        if ( --remainingRetryLoops == 0 )  // allows p_retryLoopsBeforeFailure <= 0 to result in infinite loops
                        {
                            --currentUrlIndex;
                            throw new IpcException( "Failed to connect within " + p_retryLoopsBeforeFailure + " connection loops",
                                                    this );
                        }
                        
                        currentUrlIndex = p_firstIndex; // start again at first URL                                  
                        try
                        {
                            connectMonitor.wait( p_connectRetryTimeout );  // both waits, and allows for cancellation
                        }
                        catch ( InterruptedException ex2 )
                        {
                            throw new InterruptedIpcException( getLogString( "Connect attempt interrupted" ),
                                                               this,
                                                               ex );
                        }
                    }
                }
            }
            while ( ! isConnected() );
            
            // clear buffers for use with new connection
            
            outBuff.clear();
            inBuff.clear();
            inBuff.flip();  // marks all bytes as consumed... will cause readMore() on the first read operation
        }
    }


    /**
     * Disconnects from the remote process
     *
     * @throws IpcException on failure
     */
    public void disconnect()
        throws IpcException
    {
        synchronized ( connectMonitor )
        {
            if ( channel == null )
            {
                return;  // still unconnected
            }
            
            Socket sock = channel.socket();
            if ( sock != null )
            {
                try
                {
                    if ( ! sock.isClosed() )
                    {    
                        sock.close();
                    }
                }
                catch ( IOException ex )
                {
                    throw new IpcException( "Close", this, ex );
                }
            }
            
            Logger.trace( getLogString( "Disconnected" ) );
        }
    }


    /* (non-Javadoc)
     * @see com.cboe.cfn.interProcess.InterProcessConnection#isConnected()
     */
    public boolean isConnected()
    {
        synchronized ( connectMonitor )
        {
            if ( channel == null )
            {
                return false;
            }
            
            if ( channel.socket() == null )
            {
                return false;
            }
            
            return channel.socket().isConnected();
        }
    }
    
        
    public void appendSystemStatus( StringBuffer p_sb )
    {
        p_sb.append( getLogString( "" ) );
    }


    private String getLogString( String p_msg )
    {
        if ( channel == null )
        {
            return getUnconnectedLogString( p_msg + "\n    NO CHANNEL" );
        }
        Socket sock = channel.socket();
        
        if ( sock == null )
        {
            return getUnconnectedLogString( p_msg + "\n    NO SOCKET" );
        }
        
        if ( ! sock.isConnected() )
        {
            return getUnconnectedLogString( p_msg + "\n    NOT CONNECTED" );
        }
        
        StringBuffer sb = new StringBuffer();
                
        sb.append( "\n" ).append( p_msg )
          .append( "\n    Remote Host : " ).append( sock.getInetAddress().toString() )
          .append( "\n    Remote Port : " ).append( sock.getPort() )
          .append( "\n    Local Port  : " ).append( sock.getLocalPort() );
                                                            
        return sb.toString();
    }


    private String getUnconnectedLogString( String p_msg ) 
    {
        StringBuffer sb = new StringBuffer();
        
        sb.append( "\n" ).append( p_msg )
          .append( "\n    Remote Host : " ).append( addresses[ currentUrlIndex ].getHostName() )
          .append( "\n    Remote Port : " ).append( addresses[ currentUrlIndex ].getPort() );
                                                            
        return sb.toString();
    }
        
    
    public String getConnectionString()
    {
        if (addresses[currentUrlIndex].isUnresolved())
        {
            return addresses[currentUrlIndex].getHostName() 
                    + ":" 
                    + addresses[currentUrlIndex].getPort();
        }

        return addresses[currentUrlIndex].getAddress().getHostAddress()
                + ":"
                + addresses[currentUrlIndex].getPort();
    }


    /* (non-Javadoc)
     * @see com.cboe.cfn.interProcess.InterProcessConnection#getConnectionNumber()
     */
    public int getConnectionNumber()
    {
        return connectionNumber;
    }

}
