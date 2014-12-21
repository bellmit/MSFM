/*
 * FileByteReader.java
 */

package com.cboe.lwt.byteUtil;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


/**
 * Represents an Inter-process connection using the TCP protocol
 *   
 * @author  jsimonis
 */
public class FileByteReader implements ByteReader
{ 
    private FileChannel in;
    private ByteBuffer  byteBuff;
    private FileInputStream inStream;

    public FileByteReader( File p_file, int p_buffSize ) 
        throws IOException
    {
        inStream = new FileInputStream(p_file);
        in = inStream.getChannel();  
        byteBuff = ByteBuffer.allocateDirect(p_buffSize);

        byteBuff.flip();  // marks all bytes as consumed... will cause readMore() on the first read operation
    }

    /**
     * Reads a byte from the File Channel
     *
     * @return the byte read from the stream
     *
     * @throws IOException on failure
     */
    public byte read() 
        throws IOException
    {
        if (byteBuff.remaining() < 1)
        {
            if ( ! readMore( 1 ) )
            {
                throw new EOFException( "End of file" );
            }
        }

        return byteBuff.get();
    }
    

    /**
     * Reads bytes from the File
     *
     * @param p_dest the destination buffer that will receive the read
     * @param p_destOffset the offset withing the buffer to take the first byte
     * @param p_maxLength the number of bytes to attempt to copy
     *
     * @return the number of bytes read
     *
     * @throws IOException on failure
     */
    public int read( byte[] p_dest, 
                     int    p_destOffset,
                     int    p_maxLength ) 
        throws IOException
    {
        return read( p_dest, p_destOffset, 1, p_maxLength );
    }
    /** Reads bytes from the source and block until a minimum number of bytes are read
     *
     * @param p_dest the destination buffer that will receive the read
     * @param p_destOffset the offset within the p_dest buffer to take the first byte
     * @param p_maxLength the number of bytes to attempt to copy
     * @param p_minLength the minimum number of bytes to copy (will block until available)
     *
     * @return the number of bytes read
     *
     * @throws IOException on failure, including if there are no further bytes available
     *
     */
    public int read( byte[] p_dest, 
                     int    p_destOffset, 
                     int    p_minLength, 
                     int    p_maxLength ) 
        throws IOException
    {
        assert ( p_minLength <= p_maxLength ) : "Min length must be <= max length";

        if ( byteBuff.remaining() < p_maxLength ) 
        {
            if ( ! readMore( p_minLength ) ) // attempt a read if we don't have p_maxLength bytes in readableBytes
            {// then minLength wasn't available before EOF
                 throw new EOFException( "End of file" );
            }
        }

        int copyLength = ( p_maxLength < byteBuff.remaining() ) 
                         ? p_maxLength 
                         : byteBuff.remaining();

        byteBuff.get( p_dest, p_destOffset, copyLength );        

        return copyLength;
    }
        

    
    
    public long fileSize() throws IOException
    {
        return in.size();
    }
    
    
    public long remaining() throws IOException
    {
        return in.size() - in.position();
    }


    
    private boolean readMore( int p_minLength )
        throws IOException
    {
        assert ( p_minLength < byteBuff.capacity() ) : "Minimum read length (" + p_minLength + ") would overflow the buffer with remaining capacity " + byteBuff.capacity();
        assert ( p_minLength > 0 ) : "zero length read";
        
        int totalLength = byteBuff.remaining();
        
        byteBuff.compact();
        
        while ( totalLength < p_minLength )
        {
            int readLen = in.read(byteBuff); 

            if ( readLen < 0 )
            {
                return false;
            }

            totalLength += readLen; 
        }
            
        byteBuff.flip();
        
        return true;
    }
    
    public void close()
        throws IOException
    {
        in.close();
    }
}
    
