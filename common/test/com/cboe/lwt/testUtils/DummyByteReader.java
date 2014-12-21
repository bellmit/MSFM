/**
 * DummyByteReader.java
 *
 * Created on August 20, 2002, 11:15 AM
 */

package com.cboe.lwt.testUtils;

import java.io.IOException;
import java.io.InterruptedIOException;

import com.cboe.lwt.byteUtil.ByteIterator;
import com.cboe.lwt.byteUtil.ByteReader;
import com.cboe.lwt.queue.InterThreadQueue;
import com.cboe.lwt.queue.QueueException;
import com.cboe.lwt.queue.QueueInterruptedException;


/**
 *
 * @author  dotyl
 */
public class DummyByteReader implements ByteReader
{
    InterThreadQueue impendingReads;
    
    ByteIterator currentRead;
    
    
    public DummyByteReader()
    {
        impendingReads = InterThreadQueue.getInstance( "Impending reads", 64, 4, 2000 );
        currentRead    = ByteIterator.getInstance();
    }
    
    
    public void addSimulatedRead( byte[] p_buff )
        throws QueueException
    {
        impendingReads.enqueue( p_buff );
        impendingReads.flush();
    }
    

    public synchronized byte read() 
        throws IOException
    {
        try
        {
            if ( currentRead.remaining() == 0 )
            { 
                byte[] readBuff = (byte[])impendingReads.dequeue();
                currentRead.rebase( readBuff );
                assert ( currentRead.remaining() > 0 ) : "Error: empty buffer set for read";
            }

            byte result = currentRead.get();
            currentRead.next();
            return result;
        }
        catch( QueueInterruptedException ex )
        {
            InterruptedIOException ex2 = new InterruptedIOException( "Read interrupted " );
            ex2.initCause( ex );
            throw ex2;
        }
    }

    public synchronized int read( byte[] p_dest )
        throws IOException
    {
        try
        {
            if ( currentRead.remaining() == 0 )
            { 
                currentRead.rebase( (byte[])impendingReads.dequeue( InterThreadQueue.INFINITE_TIMEOUT ) );
            }

            int copyLength = ( currentRead.remaining() > p_dest.length )
                           ? p_dest.length
                           : currentRead.remaining();

            currentRead.read( p_dest, 0, copyLength );
            return copyLength;
        }
        catch( QueueInterruptedException ex )
        {
            InterruptedIOException ex2 = new InterruptedIOException( "Read interrupted " );
            ex2.initCause( ex );
            throw ex2;
        }
    }

    public synchronized int read( byte[] p_dest, int p_start, int p_length )
        throws IOException
    {
        assert ( p_length <= p_dest.length ) : "Asked for illegal length for furnished buffer.  p_length = " + p_length + ", buffer.length = " + p_dest.length;

        try
        {
            if ( currentRead.remaining() == 0 )
            { 
                currentRead.rebase( (byte[])impendingReads.dequeue() );
            }

            int copyLength = ( currentRead.remaining() > p_length )
                           ? p_length
                           : currentRead.remaining();

            currentRead.read( p_dest, p_start, copyLength );
            return copyLength; 
        }
        catch( QueueInterruptedException ex )
        {
            InterruptedIOException ex2 = new InterruptedIOException( "Read interrupted " );
            ex2.initCause( ex );
            throw ex2;
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
     * @throws IOException on failure, including if there are no further bytes available
     *
     */
    public int read(byte[] p_dest, int p_destOffset, int p_minLength, int p_maxLength) 
        throws IOException
    {
        int length = read( p_dest, p_destOffset, p_maxLength );
        
        if ( length < p_minLength )
        {
            throw new IOException( "Bad read of min length : " + p_minLength + ", received only : " + length );
        }
        
        return length;
    }
    
};    

