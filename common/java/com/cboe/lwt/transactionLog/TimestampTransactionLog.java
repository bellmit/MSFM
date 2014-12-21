/*
 * Created on Apr 11, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cboe.lwt.transactionLog;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.cboe.lwt.byteUtil.ByteIterator;


/**
 * @author dotyl
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TimestampTransactionLog implements TransactionLog
{
    public  static final String DEFAULT_HEADER_FORMAT_STRING = "\nHH:mm:ss |";
    
    private BufferedTransactionLog bufferedLog;    
    private SimpleDateFormat   timeFmt;

    private ByteIterator headerIter;


    public TimestampTransactionLog( LogDestination p_dest,
                                    String         p_headerFormatString,
                                    int            p_flushInterval_MS )
    {
        bufferedLog   = new BufferedTransactionLog( p_dest, p_flushInterval_MS );
        
        timeFmt = new SimpleDateFormat( p_headerFormatString );
        
        headerIter  = ByteIterator.getInstance();
    }

    
    /* 
     * NOTE: this method is synchronized on bufferedLog to synch with its flushthread
     */
    public void write( final ByteIterator p_data, int p_length )
        throws IOException
    {
        int roomInCurrentBuffer = bufferedLog.getRemainingSpaceInCurrentBuffer();
        int totalWriteLength = p_length;
        
        String timeStr = timeFmt.format( new Date() ); 
        int headerLength = timeStr.length();
        totalWriteLength += headerLength + 1;
        headerIter.rebase( timeStr.getBytes() );
        
        if ( totalWriteLength >= roomInCurrentBuffer )
        {
            bufferedLog.flush();
        }
        
        synchronized ( bufferedLog ) 
        {
            bufferedLog.write( headerIter, headerLength );
            bufferedLog.write( p_data, p_length );
        }
    }


    /* Delegate method
     * @see com.cboe.lwt.transactionLog.FileTransactionLog#cancel()
     */
    public void dispose()
    {
        bufferedLog.dispose();
    }
    

    /* Delegate method
     * @see com.cboe.lwt.transactionLog.FileTransactionLog#flush()
     * 
     * NOTE: this method is synchronized in bufferedLog.flush()
     */
    public void flush()
    {
        bufferedLog.flush();
    }

};
