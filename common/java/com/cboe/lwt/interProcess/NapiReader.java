/*
 * NapiReader.java
 *
 * Created on August 19, 2002, 3:25 PM
 */

package com.cboe.lwt.interProcess;

import java.io.IOException;

import com.cboe.lwt.byteUtil.ByteIterator;
import com.cboe.lwt.byteUtil.ByteReader;
import com.cboe.lwt.eventLog.Logger;


/**
 * Decodes NAPI input.  (reads and removes headers)
 *
 * @author  dotyl
 */
public class NapiReader 
{
    ByteReader in;
    
 
    /**
     * @param p_in the inputStream containing napi blocks

     */    
    public NapiReader( ByteReader p_in )
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
        int blockSize = NapiUtils.parseBlockSize( high, low ); // don't include napi length in size
        
        if ( blockSize > p_dest.length || blockSize <= 0 ) 
        {
            StringBuffer sb = new StringBuffer();
                
            sb.append( "Napi Reader Returned unreasonable length of " )
              .append( blockSize )
              .append( ".  High byte = " )
              .append( high )
              .append( ", Low byte = " )
              .append( low )
              .append( ", for a napi receive buffer of size ")
              .append( p_dest.length );
                  
            if ( ! Logger.getGlobal().getIsTerse() )
            {
                sb.append( "\n================\n" );
                in.read( p_dest, 
                         0, 
                         p_dest.length ); 
                
                sb.append( new String( p_dest ) );
                sb.append( "\n================" );
            }

            throw new IOException( sb.toString() );
        }
        
        int bytesReceived = in.read( p_dest, 
                                     0, 
                                     blockSize, 
                                     blockSize ); 
            
        assert ( bytesReceived == blockSize ) : "Napi error: Napi header advertised length (" + blockSize + "), but actul read length is (" + bytesReceived + ")";
        
        return bytesReceived;
    }
    
    
    /**
     * @returns ByteIterator containing only the read bytes
     * @throws IOException if the in's read operation throws it or if the in was closed
     * before a full block is retrieved
     */    
    public ByteIterator getBlock() 
        throws IOException
    {
        byte high     = in.read();
        byte low      = in.read();
        int blockSize = NapiUtils.parseBlockSize( high, low ); // don't include napi length in size
        
        ByteIterator result = ByteIterator.getInstance( blockSize );
        
        result.write( in, blockSize );
        
        return result.first();
    }

}
