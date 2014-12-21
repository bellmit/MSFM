/*
 * NapiWriter.java
 *
 * Created on August 19, 2002, 3:25 PM
 */

package com.cboe.lwt.interProcess;

import java.io.IOException;

import com.cboe.lwt.byteUtil.ByteIterator;
import com.cboe.lwt.byteUtil.ByteWriter;


/**
 * Encodes NAPI blocks
 * 
 * Used only for simulators (not production code)
 *
 * @author  dotyl
 */
public class NapiWriter
{
    ByteWriter out;           // the output to receive the NAPI-encoded blocks
    byte[]     lengthBuff;
    
    
    /**
     * @param p_out ByteWriter that will receive the napi-blocked bytes
     */    
    public NapiWriter( ByteWriter p_out )
    {
        out         = p_out;
        lengthBuff = new byte[ NapiUtils.NUM_LENGTH_BYTES ]; 
    }
    
    
    /**
     * @param p_outBlock block to wrap with NAPI headers and write to the output stream
     * (starting at the current index)
     * @param p_length length of the block
     * @throws IOException
     */    
    public void write( ByteIterator p_outBlock, int p_length ) 
        throws IOException
    {
        assert ( p_length > 0 ) : "Illegal write length : " + p_length;
        
        byte length_LOW  = (byte)( p_length & 0x00FF );
        byte length_HIGH = (byte)( ( p_length >> 8 ) & 0x00FF );
        
        out.write( length_HIGH );
        out.write( length_LOW );
        
        p_outBlock.read( out, p_length ); 
    }
    
    
    public void flush()
        throws IOException
    {
        out.flush();
    }
    
}
