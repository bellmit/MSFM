/*
 * Created on Apr 11, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cboe.lwt.transactionLog;

import java.io.IOException;

import com.cboe.lwt.byteUtil.ByteIterator;


/**
 * @author dotyl
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class NewlineTransactionLog implements TransactionLog
{
    private BufferedTransactionLog bufferedLog;    

    private static final byte NEWLINE        = '\n';
    private static final int  NEWLINE_LENGTH = 1;


    public NewlineTransactionLog( LogDestination p_dest,
                                  int            p_flushInterval_MS )
    {
        bufferedLog = new BufferedTransactionLog( p_dest, p_flushInterval_MS );
    }

    
    /* 
     * NOTE: this method is synchronized on bufferedLog to synch with its flushthread
     */
    public synchronized void write( final ByteIterator p_data, int p_length )
        throws IOException
    {
        int roomInCurrentBuffer = bufferedLog.getRemainingSpaceInCurrentBuffer();
        int totalWriteLength = p_length + NEWLINE_LENGTH;
        
        if ( totalWriteLength >= roomInCurrentBuffer )
        {
            bufferedLog.flush();
        }
        
        synchronized ( bufferedLog ) 
        {
            bufferedLog.write( p_data, p_length );
            bufferedLog.write( NEWLINE );
        }
    }


    /* Delegate method
     * @see com.cboe.lwt.transactionLog.FileTransactionLog#cancel()
     */
    public synchronized void dispose()
    {
        bufferedLog.dispose();
    }
    

    /* Delegate method
     * @see com.cboe.lwt.transactionLog.FileTransactionLog#flush()
     * 
     * NOTE: this method is synchronized in bufferedLog.flush()
     */
    public synchronized void flush()
    {
        bufferedLog.flush();
    }

};
