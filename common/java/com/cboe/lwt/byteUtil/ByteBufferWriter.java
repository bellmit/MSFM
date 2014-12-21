package com.cboe.lwt.byteUtil;

import java.io.IOException;
import java.nio.ByteBuffer;


public class ByteBufferWriter implements ByteWriter
{
    ByteBuffer out;
    
    
    public ByteBufferWriter( ByteBuffer p_out )
    {
        out = p_out;
    }
    
        
    public void replaceBuffer( ByteBuffer p_newOut )
    {
        out = p_newOut;
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
    {
        try
        {
            out.put( p_block,
                     p_startOfBlock,
                     p_blockLength );
        }
        catch ( RuntimeException ex )
        {
            ex.initCause( new Exception( "Writing length of " + p_blockLength + " to ByteBuffer with remaining bytes = " + out.remaining() ) );
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
    {
        try
        {
            out.put( p_byte );
        }
        catch ( RuntimeException ex )
        {
            ex.initCause( new Exception( "Writing length of 1 to ByteBuffer with remaining bytes = " + out.remaining() ) );
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
         throw new IOException( "can't flush a byteBuffer... Programming error" );
     }
}
