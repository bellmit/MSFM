package com.cboe.lwt.byteUtil;

import java.io.IOException;


public interface ByteWriter
{
    /**
     * Writes bytes to the destination
     *
     * @param p_block the storage to write from
     * @param p_startOfBlock the offset in p_block at which to start copying
     * @param p_blockLength the number of bytes to copy
     *
     * @throws IOException on failure
     */
    void write( byte[] p_block, 
                int p_startOfBlock, 
                int p_blockLength ) throws IOException;
                
    /**
     * Writes a byte to the destination
     *
     * @param p_byte the byte to write
     *
     * @throws IOException on failure
     */
    void write( byte p_byte ) throws IOException;
    
    
    /**
     * Flushes all bytes to the IPC (blocks until all bytes are processed)
     *
     * @throws IOException on failure
     */
     void flush() throws IOException;
    
}





     
     

