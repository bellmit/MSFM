/**
 * ByteReader.java
 *
 * Created on October 9, 2002, 12:37 PM
 */

package com.cboe.lwt.byteUtil;

import java.io.IOException;


/**
 *
 * @author  dotyl
 */
public interface ByteReader
{
    /**
     * Reads a byte from the source
     *
     * @return the byte read from the stream
     *
     * @throws IOException on failure
     */
    byte read() throws IOException;
    
    /**
     * Reads bytes from the source
     *
     * @param p_dest the destination buffer that will receive the read
     * @param p_destOffset the offset within the p_dest buffer to take the first byte
     * @param p_maxLength the number of bytes to attempt to copy 
     *
     * @return the number of bytes read
     *
     * @throws IOException on failure, including if there are no further bytes available
     */
    int read( byte[] p_dest, 
              int    p_destOffset,
              int    p_maxLength ) throws IOException;
    
    /**
     * Reads bytes from the source and block until a minimum number of bytes are read
     *
     * @param p_dest the destination buffer that will receive the read
     * @param p_destOffset the offset within the p_dest buffer to take the first byte
     * @param p_maxLength the number of bytes to attempt to copy 
     * @param p_minLength the minimum number of bytes to copy (will block until available)
     *
     * @return the number of bytes read
     *
     * @throws IOException on failure, including if there are no further bytes available
     */
    int read( byte[] p_dest, 
              int    p_destOffset,
              int    p_minLength,
              int    p_maxLength ) throws IOException;
    
}
