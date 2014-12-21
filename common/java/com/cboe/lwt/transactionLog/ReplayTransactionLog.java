/*
 * Created on Apr 11, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cboe.lwt.transactionLog;

import java.io.IOException;

import com.cboe.lwt.byteUtil.ByteArrayUtils;
import com.cboe.lwt.byteUtil.ByteIterator;
import com.cboe.lwt.interProcess.NapiUtils;


/**
 * @author dotyl
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public final class ReplayTransactionLog implements TransactionLog
{
    public static final int INDEX_LENGTH    = ByteArrayUtils.LONG_SIZE;
    public static final int OFFSET_SIZE     = 2; 
    public static final int HEADER_LENGTH   = OFFSET_SIZE + INDEX_LENGTH;
    public static final int FOOTER_LENGTH   = OFFSET_SIZE; 
    public static final int ENVELOPE_LENGTH = HEADER_LENGTH + FOOTER_LENGTH; 
    
    
    private BufferedTransactionLog bufferedLog;    

    private ByteIterator headerIter;


    public ReplayTransactionLog( LogDestination p_dest, int p_flushInterval_MS )
    {
        bufferedLog = new BufferedTransactionLog( p_dest, p_flushInterval_MS );
        
        headerIter = ByteIterator.getInstance( HEADER_LENGTH );
    }

    
    /* 
     * NOTE: this method is synchronized on bufferedLog to synch with its flushthread and other clients
     */
    public final void write( final ByteIterator p_data, int p_length )
        throws IOException
    {
        write ( p_data, p_length, System.currentTimeMillis() );
    }

    
    /* 
     * NOTE: this method is synchronized on bufferedLog to synch with its flushthread and other clients
     */
    public final void write( final ByteIterator p_data, 
                             int                p_length, 
                             long               p_index )
        throws IOException
    {
        synchronized ( bufferedLog ) 
        {
            int roomInCurrentBuffer = bufferedLog.getRemainingSpaceInCurrentBuffer();
        
            if ( ( p_length + ENVELOPE_LENGTH ) > roomInCurrentBuffer )
            {
                bufferedLog.flush();
            }
        
            
            // prepare the header
            
            // write leading offset to header iter
            headerIter.first();
            NapiUtils.setBlockSize( headerIter, p_length );
        
            // write epoch time to header iter
            ByteArrayUtils.writeLong( headerIter,
                                      p_index );
       
            
            // write to the log
            
            // write header iter to the buffered log   
            bufferedLog.write( headerIter.first(), HEADER_LENGTH );
        
            // write the message to the buffered log   
            bufferedLog.write( p_data, p_length );

            // write trailing ofset to the buffered log
            bufferedLog.write( headerIter.first(), FOOTER_LENGTH );
        }
    }


    /* Delegate method
     * @see com.cboe.lwt.transactionLog.FileTransactionLog#cancel()
     */
    public final void dispose()
    {
        bufferedLog.dispose();
    }
    

    /* Delegate method
     * @see com.cboe.lwt.transactionLog.FileTransactionLog#flush()
     * 
     * NOTE: this method is synchronized in bufferedLog.flush()
     */
    public final synchronized void flush()
    {
        bufferedLog.flush();
    }
        
 };
