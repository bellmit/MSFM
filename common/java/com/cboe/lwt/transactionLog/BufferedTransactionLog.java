/*
 * Created on Jun 26, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cboe.lwt.transactionLog;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.cboe.lwt.byteUtil.ByteBufferWriter;
import com.cboe.lwt.byteUtil.ByteIterator;
import com.cboe.lwt.thread.ThreadTask;

/**
 * @author dotyl
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public final class BufferedTransactionLog implements TransactionLog
{
    private class FlushThread extends ThreadTask
    {
        private int autoFlush_MS;

        FlushThread( int p_autoFlush_MS )
        {
            super( "BufferedTransactionLog - Flush Thread" );
            autoFlush_MS = p_autoFlush_MS;
        }

        public void doTask()
            throws InterruptedException
        {
            while ( true )
            {
                Thread.sleep( autoFlush_MS );
                flush();
            }
        }
    };


    private FlushThread      flushThread = null;
    private LogDestination   dest;
    private ByteBuffer       writeBuffer;
    private ByteBufferWriter writer;
    private ByteIterator     writeIter = ByteIterator.getInstance();


    public BufferedTransactionLog( LogDestination p_dest, int p_flushInterval_MS )
    {
        if ( p_flushInterval_MS > 0 )
        {
            flushThread = new FlushThread( p_flushInterval_MS );
            flushThread.go();
        }

        dest = p_dest;
        writer = new ByteBufferWriter( null );
        setBuffer();
    }


    public final synchronized void write( final ByteIterator p_data, int p_length )
        throws IOException
    {
        findSpaceForWrite( p_length );

        // must write from writeIter so as not to modify the pointers inside p_data
        writeIter.rebase( p_data );

        writeIter.read( writer, p_length);
    }
    
    
    public final synchronized void write( byte p_data )
    {
        writer.write( p_data );
    }


    final int getRemainingSpaceInCurrentBuffer()
    {
        return writeBuffer.remaining();
    }


    private final void findSpaceForWrite( int p_length )
        throws IOException
    {
        if ( writeBuffer.capacity() < p_length )
        {
            StringBuffer sb = new StringBuffer( 128 );

            sb.append( "\n      Buffer too small to write length of " ).append( p_length )
              .append( "\n      Buffer capacity = " ).append( writeBuffer.capacity() )
              .append( "\n      Buffer position = " ).append( writeBuffer.position() )
              .append( "\n      Buffer limit    = " ).append( writeBuffer.limit() );

            throw new IOException( sb.toString() );
        }

        if ( writeBuffer.remaining() < p_length )
        {
            flush();

            if ( writeBuffer.remaining() < p_length )
            {
                StringBuffer sb = new StringBuffer( 128 );

                sb.append( "\n      Flush failed to clear enough space for length of " ).append( p_length )
                  .append( "\n      Buffer capacity = " ).append( writeBuffer.capacity() )
                  .append( "\n      Buffer position = " ).append( writeBuffer.position() )
                  .append( "\n      Buffer limit    = " ).append( writeBuffer.limit() );

                throw new IOException( sb.toString() );
            }
        }
    }


    public synchronized void flush()
    {
        int writeLength = writeBuffer.capacity() - writeBuffer.remaining();

        if ( writeLength > 0 )  // then there is something to flush
        {
            dest.write( writeBuffer );
            setBuffer();
        }

        dest.flush();
    }


    public synchronized void dispose()
    {
        if ( flushThread != null )
        {
            flushThread.signalKill();
        }

        dest.write( writeBuffer );
        writeBuffer = null;

        dest.flush();
    }


    private void setBuffer()
    {
        writeBuffer = dest.getFreeBuffer();
        writeBuffer.clear();
        writer.replaceBuffer( writeBuffer );
    }
    
}