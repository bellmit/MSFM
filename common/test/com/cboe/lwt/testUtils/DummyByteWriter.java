/*
 * DummyOutputStream.java
 *
 * Created on August 20, 2002, 11:18 AM
 */

package com.cboe.lwt.testUtils;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.cboe.lwt.byteUtil.ByteWriter;
import com.cboe.lwt.queue.QueueException;
import com.cboe.lwt.eventLog.Logger;


/**
 *
 * @author  dotyl
 */
public class DummyByteWriter implements ByteWriter
{
    protected int             numberOfWrites;
    protected ByteBuffer      writeBuffer;
    protected DummyByteReader reader;

public DummyByteWriter()
    {
        numberOfWrites = 0;
        writeBuffer    = ByteBuffer.allocate( 2048 );
        reader         = null;
        writeBuffer.clear();
    }

    public DummyByteWriter( int p_writeBufferSize )
    {
        numberOfWrites = 0;
        writeBuffer    = ByteBuffer.allocate( p_writeBufferSize );
        reader         = null;
        writeBuffer.clear();
    }
    
    public void registerReader( DummyByteReader p_reader )
    {
        reader = p_reader;
    }
    
    public int getNumberOfWrites()
    {
        SystemUtils.pauseForOtherThreads();
        return numberOfWrites;
    }
    
    public byte[] getWrites()
    {
        SystemUtils.pauseForOtherThreads();

        writeBuffer.flip();
        int size = writeBuffer.remaining();
        
        byte[] result = new byte[ size ];
        writeBuffer.get( result, 0, size );
        writeBuffer.clear();
        return result;
    }
    
    public void clear()
    {
        numberOfWrites = 0;
        writeBuffer.clear();
    }
    

    private static byte[] readerBuff = new byte[ 2048 ];
    
    public void write( byte[] p_block, int p_srcOffset, int p_length )
        throws IOException
    {
        if ( writeBuffer.remaining() < p_length )
        {
            writeBuffer.clear();
        }
        assert ( p_srcOffset + p_length <= readerBuff.length ) : "dummy writer limits writes to size " + readerBuff.length;
        
        if ( writeBuffer.remaining() < p_length )
        {
            flush();
        }
        
        assert ( ( writeBuffer.remaining() >= p_length ) ) : "flush didn't clear buffer of size : " + writeBuffer.capacity() + " for write of size : " + p_length + ", remaining is : " + writeBuffer.remaining();
        ++numberOfWrites;
        writeBuffer.put( p_block, p_srcOffset, p_length );
    }


    public void write( byte p_byte )
    {
        ++numberOfWrites;
        
        if ( writeBuffer.remaining() < 1 )
        {
            writeBuffer.clear();
        }
        writeBuffer.put( p_byte );
    }
    
    public void flush()
        throws IOException
    {
        if ( reader != null )
        {
            try
            {
                writeBuffer.flip();
                int size = writeBuffer.remaining();
    
                byte[] temp = new byte[ size ];
                writeBuffer.get( temp, 0, size );
                reader.addSimulatedRead( temp );
            }
            catch( QueueException ex )
            {
                Logger.error( "QueueException thrown from addSimulatedRead", ex );
                throw new IOException( "QueueException thrown from addSimulatedRead" );
            }
        }

        writeBuffer.clear();
    }
    
}    
