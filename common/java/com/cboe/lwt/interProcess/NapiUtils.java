/*
 * NapiUtils.java
 *
 * Created on August 19, 2002, 4:18 PM
 */

package com.cboe.lwt.interProcess;

import com.cboe.lwt.byteUtil.ByteIterator;

/**
 * Utility class for NAPI-related functionality (i.e. encoding/decoding lengths)
 *
 * @author  dotyl
 */
public class NapiUtils
{
    public static final int NUM_LENGTH_BYTES = 2;  // block starts after 2 bytes of binary length
    
    /** utility class: prevent instantiation */
    private NapiUtils() 
    {}

    /**
     * Given a the high and low bytes of the block size, compute the block length.
     *
     * @param p_high The high-order byte of the block size
     * @param p_low The low-order byte of the block size
     * @return An integer representation of the block size
     */
    public static int parseBlockSize( byte p_high, byte p_low )
    {
        int length = ( 0x00ff & p_high );
        length <<= 8;
        length |= ( 0x00ff & p_low );
        
        return length;
    }
    
    
    /**
     * Encode NAPI length for a specified block
     *
     * @param p_buffer The storage to use
     * @param p_blockOffset start of the block within p_buffer
     * @param p_napiLength length of block to record in NAPI header
     */    
    public static void setBlockSize( byte[] p_buffer, int p_blockOffset, int p_napiLength )
    {
        byte length_LOW  = (byte)( p_napiLength & 0x00FF );
        byte length_HIGH = (byte)( ( p_napiLength >> 8 ) & 0x00FF );
        
        p_buffer[ p_blockOffset ]   = length_HIGH;
        p_buffer[ ++p_blockOffset ] = length_LOW;
    }
    
    
    /**
     * Encode NAPI length for a specified block
     *
     * @param p_buffer The storage to use
     * @param p_blockOffset start of the block within p_buffer
     * @param p_napiLength length of block to record in NAPI header
     */    
    public static void setBlockSize( ByteIterator p_iter, int p_napiLength )
    {
        byte length_LOW  = (byte)( p_napiLength & 0x00FF );
        byte length_HIGH = (byte)( ( p_napiLength >> 8 ) & 0x00FF );
        
        p_iter.write( length_HIGH );
        p_iter.write( length_LOW );
    }

}
