/*
 * DummyIpc.java
 *
 * Created on May 3, 2002, 3:54 PM
 */

package com.cboe.lwt.interProcess;

import java.io.IOException;

import com.cboe.lwt.byteUtil.ByteReader;
import com.cboe.lwt.byteUtil.ByteWriter;
import com.cboe.lwt.transactionLog.LogDestination;

/**
 *
 * @author  dotyl
 */
public class DummyIpc implements InterProcessConnection
{
    protected int        numberOfWrites;
    private   ByteWriter out;
    private   ByteReader in;
    private   boolean    isConnected;

        
    public DummyIpc()
    {
        numberOfWrites = 0;
        in  = null;
        out = null;
        isConnected = false;
    }

        
    public DummyIpc( ByteReader p_inDelegate, ByteWriter p_outDelegate )
    {
        numberOfWrites = 0;
        in  = p_inDelegate;
        out = p_outDelegate;
    }

    
    public void write(byte[] p_b, int p_off, int p_len)
        throws IpcException
    {
        ++numberOfWrites;
        if ( out != null )
        {
            try
            {
                out.write( p_b, p_off, p_len );
            }
            catch ( IOException ex )
            {
                throw new IpcException( "write", this, ex );
            }
        }
    }

    public void write( byte p_b )
        throws IpcException
    {
        ++numberOfWrites;
        if ( out != null )
        {
            try
            {
                out.write( p_b );
            }
            catch ( IOException ex )
            {
                throw new IpcException( "write", this, ex );
            }
        }
    }
    
    public int getNumberOfWrites()
    {
        return numberOfWrites;
    }

    
    public byte read()
        throws IpcException
    {
        if ( in != null )
        {
            try
            {
                return in.read();
            }
            catch ( IOException ex )
            {
                throw new IpcException( "read", this, ex );
            }

        }
        
        return (byte)'0';
    }

    public int read( byte[] p_dest, int p_offset, int p_length )
        throws IpcException
    {
        if ( in != null )
        {
            try
            {
                return in.read( p_dest, p_offset, p_length );
            }
            catch ( IOException ex )
            {
                throw new IpcException( "read", this, ex );
            }
        }
        
        return 0;
    }
    
    public void connectSpecific( int p_lowIndex, 
                                 int p_highIndex, 
                                 int p_connectRetryTimeout ) 
    {
        isConnected = true;
    }
    
    public void connect( int p_connectRetryTimeout ) 
    {
        isConnected = true;
    }
    
    public void asyncConnectPrimary( Runnable p_successMonitor )
    {
        isConnected = true;
        p_successMonitor.run();
    }
    
    public void asyncConnect( Runnable p_successMonitor )
    {
        isConnected = true;
        p_successMonitor.run();
    }
    
    public void disconnect()
    {
        isConnected = false;
    }

    public void flush() throws IpcException
    {
        if ( out != null )
        {
            try
            {
                out.flush();
            }
            catch ( IOException ex )
            {
                throw new IpcException( "flush", this, ex );
            }
        }
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
    public int read(byte[] p_dest, int p_destOffset, int p_maxLength, int p_minLength) 
        throws IpcException
    {
        if ( in != null )
        {
            try
            {
                return in.read( p_dest, p_destOffset, p_maxLength, p_minLength );  // counts on test cases being set up to receive correct min length
            }
            catch ( IOException ex )
            {
                throw new IpcException( "read", this, ex );
            }
        }
        
        return 0;
    }
    
    /** Clears any buffered but unsent data
     *
     */
    public void clear()
    {
        numberOfWrites = 0;
    }
    
    /** @returns true if the output is hung (blocked on a send and with the
     * same block since the last time isHung() is called) and false otherwise
     *
     */
    public boolean isHung()
    {
        return false;
    }
    

    public void disableInboundTransactionLogging()
    {
    }


    public void disableOutboundTransactionLogging()
    {
    }


    public void enableInboundTransactionLogging( LogDestination p_logDest, int p_logFlushInterval_MS )
    {
    }


    public void enableOutboundTransactionLogging( LogDestination p_logDest, int p_logFlushInterval_MS )
    {
    }
    
    public boolean isInboundTransactionLoggingEnabled()
    {
        return false;
    }

    public boolean isOutboundTransactionLoggingEnabled()
    {
        return false;
    }


    public boolean isConnected()
    {
        return isConnected;
    }

    public String getConnectionString()
    {
        return "DummyIpc";
    }


    public int getConnectionNumber()
    {
        return 0;
    }


    public void connect( int p_connectRetryTimeout,
                         int p_retryLoopsBeforeFailure )
    {
        connect( p_connectRetryTimeout );
    }
    
    
    public void connectSpecific( int p_lowIndex,
                                 int p_highIndex,
                                 int p_connectRetryTimeout,
                                 int p_retryLoopsBeforeFailure )
    {
        connectSpecific( p_lowIndex, p_highIndex, p_connectRetryTimeout );
    }
};
