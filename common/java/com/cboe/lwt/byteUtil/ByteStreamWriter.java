/*
 * Created on May 26, 2004
 *
 */
package com.cboe.lwt.byteUtil;

import java.io.IOException;
import java.io.OutputStream;


public class ByteStreamWriter implements ByteWriter
{
    OutputStream out;
    
    
    public ByteStreamWriter( OutputStream p_out )
    {
        out = p_out;
    }
    
        
    /**
     * Writes bytes to the destination
     *
     * @param p_block the storage to write from
     * @param p_startOfBlock the offset in p_block at which to start copying
     * @param p_blockLength the number of bytes to copy
     *
     * @throws IOException on failure
     */
    public void write( byte[] p_block, 
                       int    p_startOfBlock, 
                       int    p_blockLength ) 
        throws IOException
    {
        try
        {
            out.write( p_block,
                       p_startOfBlock,
                       p_blockLength );
        }
        catch ( RuntimeException ex )
        {
            ex.initCause( new Exception( "Writing length of " + p_blockLength + " to out stream" ) );
            throw ex; 
        }
    }
                
    /**
     * Writes a byte to the destination
     *
     * @param p_byte the byte to write
     *
     * @throws IOException on failure
     */
    public void write( byte p_byte ) 
        throws IOException
    {
        try
        {
            out.write( p_byte );
        }
        catch ( RuntimeException ex )
        {
            ex.initCause( new Exception( "Writing length of 1 to out stream" ) );
            throw ex; 
        }
    }
    
    
    /**
     * Flushes all bytes to the IPC (blocks until all bytes are processed)
     *
     * @throws IOException on failure
     */
     public void flush() 
         throws IOException
     {
         out.flush();
     }
}
