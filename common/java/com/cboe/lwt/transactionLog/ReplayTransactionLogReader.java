/*
 * NapiReader.java
 *
 * Created on August 19, 2002, 3:25 PM
 */

package com.cboe.lwt.transactionLog;

import java.io.IOException;

import com.cboe.lwt.byteUtil.ByteReader;
import com.cboe.lwt.interProcess.NapiUtils;


/**
 * Decodes NAPI input.  (reads and removes headers)
 *
 * @author  dotyl
 */
public class ReplayTransactionLogReader 
{
    ByteReader in;
    
 
    /**
     * @param p_in the inputStream containing napi blocks
     * @param p_isHeaderIncludedInSize set to either of these values:
     *    NapiUtils.INCLUDE_NAPI_LENGTH_IN_LENGTH (true     )  --> napi length includes the 2 length bytes
     *    NapiUtils.DONT_INCLUDE_NAPI_LENGTH_IN_LENGTH (false) --> napi length does NOT include the 2 length bytes
     */    
    public ReplayTransactionLogReader( ByteReader p_in )
    {
        in = p_in;
    }
    
    
    /**
     * @param p_dest an allocated array to receive the next block (output param)
     * @returns the nubmer of bytes copied into p_dest
     * @throws IOException if the in's read operation throws it or if the in was closed
     * before a full block is retrieved
     */    
    public int getBlock( byte[] p_dest ) 
        throws IOException
    {
        byte high     = in.read();
        byte low      = in.read();
        int blockSize = getBlockSize( high, low, p_dest.length );
        
        int bytesReceived = in.read( p_dest, 
                                     0, 
                                     blockSize, 
                                     blockSize ); 
            
        assert ( bytesReceived == blockSize ) : "Error: Header advertised length (" + blockSize + "), but actul read length is (" + bytesReceived + ")";
        
        byte verificationHigh = in.read();
        byte verificationLow  = in.read();
        
        if ( verificationHigh != high 
          || verificationLow  != low )
        {
            throw new IOException( "Error: leading length header is : " 
                                   + blockSize 
                                   + ", but trailing length header is : " 
                                   + ( NapiUtils.parseBlockSize( verificationHigh, 
                                                                 verificationLow ) 
                                       - ReplayTransactionLog.INDEX_LENGTH 
                                     ) ); 
        }
        
        return bytesReceived;
    }


    private int getBlockSize( byte p_high, byte p_low, int p_maxLength )
        throws IOException
    {
        int blockSize = NapiUtils.parseBlockSize( p_high, p_low ) - ReplayTransactionLog.INDEX_LENGTH; 
        
        if ( blockSize > p_maxLength || blockSize <= 0 ) 
        {
            StringBuffer sb = new StringBuffer();
                
            sb.append( "Napi Reader Returned unreasonable length of " )
              .append( blockSize )
              .append( ".  High byte = " )
              .append( p_high )
              .append( ", Low byte = " )
              .append( p_low )
              .append( ", Max length for read = " )
              .append( p_maxLength );
                  
            throw new IOException( sb.toString() );
        }
        return blockSize;
    }
}
