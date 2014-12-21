package com.cboe.lwt.byteUtil;

import java.text.DecimalFormat;

import com.cboe.lwt.interProcess.NapiUtils;



/**
 * Utility class that holds common helper methods for handling byte arrays
 *
 * @author  dotyl
 */
public abstract class ByteArrayUtils
{
    // used by intToAscii/ascii to int
    public static final int  RADIX        = 10;  // the radix for base 10 conversion 
    public static final int  ASCII_ZERO   = '0'; 
    public static final int  ASCII_NINE   = '9'; 
    public static final byte NEGATIVE     = '-'; 
    
    // used in number writing/reading
    public static final int   MASK_TO_BYTE             = 0xFF;
    public static final long  MASK_LONG_TO_BYTE        = 0xFF;
    public static final long  MASK_LONG_AS_INT         = 0x00000000FFFFFFFFl;
    
    public static final int   SHORT_SIZE               = 2;
    public static final int   INT_SIZE                 = 4;
    public static final int   LONG_SIZE                = 8;
    
    public static final int   STRING_OVERHEAD_SIZE      = 2;

    public static final int   NUMBER_OF_BITS_IN_AN_INT = 32;
    public static final int   NUMBER_OF_BITS_IN_A_BYTE = 8;
    
    public static final byte  FALSE_BYTE               = (byte)0x00;
    public static final byte  TRUE_BYTE                = (byte)0x01;
    
    
    // prevent instantiation and subclassing
    private ByteArrayUtils()
    {
    }
    
    
    /** returns true iff p_a and p_b have the same bytes for p_length starting at the
     * specified offsets.
     *
     * @param p_a the first byte array
     * @param p_aOffset the offset in p_a of the start of the comparison
     * @param p_b the second byte array
     * @param p_bOffset the offset in p_b of the start of the comparison
     * @param p_length number of bytes to check
     */
    public static boolean isEqual( byte[] p_a, 
                                   int    p_aOffset,
                                   byte[] p_b, 
                                   int    p_bOffset,
                                   int    p_length )
    {
        for( int i = 0; i < p_length; ++i )
        {
            if ( p_a[ p_aOffset ] != p_b[ p_bOffset ] ) 
            {
                return false;
            }
        }
        
        return true;
    }
    
    
    /**
     * throws an assertion with an explanatory error message if the two byte arrays aren't equal
     *
     * @param p_a the first byte array
     * @param p_b the second byte array
     */
    public static void assertEqual( byte[] p_a, byte[] p_b )
    {
        assert ( p_a.length == p_b.length ) : "\nUnequal: \n" + getComparisonString( p_a, p_b );

        int largestLength = ( p_a.length > p_b.length ) 
                            ? p_a.length
                            : p_b.length;
        
        assertEqual( p_a, 0, p_b, 0, largestLength );
    }
    

    /**
     * throws an assertion with an explanatory error message if the two byte 
     * arrays aren't equal at least over the first p_length bytes
     *
     * @param p_a the first byte array
     * @param p_b the second byte array
     * @param p_length number of bytes to check
     */
    public static void assertEqual( byte[] p_a, byte[] p_b, int p_length )
    {
        assertEqual( p_a, 0, p_b, 0, p_length );
    }
    
    
    /** throws an assertion with an explanatory error message if the two byte
     * arrays aren't equal at least over the first p_length bytes
     *
     * @param p_a the first byte array
     * @param p_aOffset the offset in p_a of the start of the comparison
     * @param p_b the second byte array
     * @param p_bOffset the offset in p_b of the start of the comparison
     * @param p_length number of bytes to check
     */
    public static void assertEqual( byte[] p_a, 
                                    int    p_aOffset,
                                    byte[] p_b, 
                                    int    p_bOffset,
                                    int    p_length )
    {
        int aEndIndex = p_aOffset + p_length -1;  // -1 for length/index conversion
        int bEndIndex = p_bOffset + p_length -1;  // -1 for length/index conversion
        
        assert ( aEndIndex < p_a.length && bEndIndex < p_b.length ) 
               : "length is invalid for at least one array\n" 
                 + "aOffset = " + p_aOffset 
                 + ", bOffset = " + p_bOffset 
                 + ", length = " + p_length + "\n"
                 + getComparisonString( p_a, p_b );
        
        for( int i = 0; i < p_length; ++i )
        {
            assert ( p_a[ i + p_aOffset ] == p_b[ i + p_bOffset ] ) 
                    :   "\nUnequal: aOffset = " + p_aOffset 
                      + ", bOffset = " + p_bOffset 
                      + ", length = " + p_length 
                      + ")\n" + getComparisonString( p_a, p_b );
        }
    }
    
    
    private static DecimalFormat numFmt;
    
    static
    {
        numFmt = new DecimalFormat( " 000;-000" );
        numFmt.setParseIntegerOnly( true );
    }
    
    /**
     * @return a string representation (3 columns: 1-index 2-a[index] 3-b[index]) of the specified byte arrays
     *
     * @param p_a the first byte array
     * @param p_b the second byte array
     */
    public static String getDebugEbcdicString( byte[] p_a )
    {
        StringBuffer msg = new StringBuffer( p_a.length * 10 );

        ByteIterator eIter = ByteIterator.getInstance( p_a );
        ByteIterator aIter = ByteIterator.getInstance( p_a.length );
        
        EbcdicConverter.convertToAscii( eIter, aIter, p_a.length );
          
        eIter.first();
        aIter.first(); 
                       
        while ( eIter.isValid() )
        {
            msg.append( "i[" ).append( numFmt.format( eIter.getIndex() ) ).append( "] #(" );
            msg.append( numFmt.format( eIter.get() ) ).append( ") : rawChar(" );
            msg.append( (char)eIter.get() ).append( ") : hex(" );
            msg.append( numFmt.format( eIter.get() & 0xFF ) ).append( ") :  e->a(" );
            msg.append( (char)aIter.get() ).append( ")" );
            msg.append( "\n" ); 
            eIter.next();
            aIter.next();
        }
        
        return msg.toString();
    }
    
    
    /**
     * @return a string representation (3 columns: 1-index 2-a[index] 3-b[index]) of the specified byte arrays
     *
     * @param p_a the first byte array
     * @param p_b the second byte array
     */
    public static String getComparisonString( byte[] p_a, byte[] p_b )
    {
        String msg = "";
        boolean elementPresentInBothArrays = true;
        
        int largestLength = ( p_a.length > p_b.length ) 
                          ? p_a.length
                          : p_b.length;
                          
        for ( int i = 0; i < largestLength; i++ )
        {
            if ( i < p_a.length )
            {
                msg += "X[" + i + "] (" + (int)p_a[i] + ") : (" + (char)p_a[i] + ")    ";
            }
            else
            {
                msg += "X --      : (";
                elementPresentInBothArrays = false;
            }
            
            if ( i < p_b.length )
            {
                msg += "Y[" + i + "] (" + (int)p_b[i] + ") : (" + (char)p_b[i] + ")";
            }
            else
            {
                msg += "Y -- ";
                elementPresentInBothArrays = false;
            }
            
            if ( elementPresentInBothArrays && p_a[i] != p_b[i] ) // flag difference
            {
                msg += "   ---( != )--- ";
            }
            
            msg += "\n";
        }
        
        return msg;
    }
    
    
    /**
     * @return printable, loggable string (columnar) representation of the byte array
     */
    public static String getLogString( byte[] p_bytes )
    {
        return getLogString( p_bytes, 0, p_bytes.length, "" );
    }
    
    
    /**
     * @return printable, loggable string (columnar) representation of the 
     * specified subarray of bytes
     *
     * @param p_bytes array to convert to printable string
     * @param p_start start index of the subarray to use
     * @param p_end end index of the subarray to use
     */
    public static String getLogString( byte[] p_bytes, int p_start, int p_end )
    {
        return getLogString( p_bytes, p_start, p_end, "" );
    }
    
    
    
    
    /**
     * @return printable, loggable string (columnar) representation of the 
     * specified subarray of bytes
     *
     * @param p_bytes array to convert to printable string
     * @param p_start start index of the subarray to use
     * @param p_end end index of the subarray to use
     * @param p_prefix prefix to add at the start of each line of the result string
     */
    public static String getLogString( byte[] p_bytes, int p_start, int p_end, String p_prefix )
    {
        assert ( p_start >= 0 );
        assert ( p_end <= p_bytes.length );
        assert ( p_start <= p_end );
        
        String logString = "";
        
        for ( int i = p_start; i < p_end; i++ )
        {
            logString += p_prefix + "[" + i + "] (" + (int)p_bytes[i] + ") : (" + (char)p_bytes[i] + ")\n";
        }
        
        return logString;
    }
    
    
    /**
     * Converts an integer to a set of ascii encoded bytes
     *
     * Notes:
     *      - If the combination of p_offset and p_length will result in writes 
     *        outside p_dest, and ArrayIndexOutOfBoundsException is thrown
     *      - If the converted int is less than p_length bytes long, the number
     *        will be left padded with ascii '0'
     *      - If p_length is too short to hold the entire number, only the least
     *        signifigant numbers will be translated
     *
     * @param p_dest The destination of the conversion
     * @param p_offset the offset within p_dest to receive the first converted byte
     * @param p_length the fixed length of the conversion: if the array is too short to 
     * to receive the full number, then only the least significant digits will be
     * translated, if the converted number is less than p_length digits, then  
     * the number will be left-padded with ascii '0'
     * @param p_intToWrite
     * @return the precision of the number (the number of digits that are not pads)
     */    
    public static final int intToAscii( byte[] p_dest, 
                                        int    p_offset,
                                        int    p_length,
                                        int    p_intToWrite )
    {
        final int endIndex = p_offset  + p_length - 1;  // -1 for length/index conversion

        int remainder = 0;
        int length = 0;
        
        // iterate over the number backwards (starting at the least signifigant digit)
        for( int index = endIndex; 
             index >= p_offset; 
             --index ) 
        {
            if ( p_intToWrite > 0 ) 
            {
                ++length;
                remainder = p_intToWrite / RADIX;
                p_dest[ index ] = (byte)( p_intToWrite - ( remainder * RADIX ) + ASCII_ZERO );
                p_intToWrite = remainder;
            }
            else 
            {
                p_dest[ index ] = (byte)ASCII_ZERO;
            }
            
        }
        
        if ( remainder > 0 )
        {
            throw new RuntimeException( "conversion failed: remainder of " + remainder + " has not been converted" );
        }
        
        return length;
    }

    
    /**
     * Converts an integer to a set of ascii encoded bytes
     *
     * Notes:
     *      - If the combination of p_offset and p_length will result in writes 
     *        outside p_dest, and ArrayIndexOutOfBoundsException is thrown
     *      - If the converted int is less than p_length bytes long, the number
     *        will be left padded with ascii '0'
     *      - If p_length is too short to hold the entire number, only the least
     *        signifigant numbers will be translated
     *
     * @param p_dest The destination of the conversion (insertion will happen at current index)
     * @param p_length the fixed length of the conversion: if the arralength is too short to 
     * to receive the full number, an , if it is too long, 
     * the number will be left-padded with ascii '0'
     * @param p_intToWrite
     * @return the precision of the number (the number of digits that are not pads)
     */    
    public static final int intToAscii( ByteIterator p_dest,
                                        int          p_length,
                                        int          p_intToWrite )
    {
        ByteIterator markIter = p_dest.shallowCopy();
        markIter.prev();   // mark the spot before the first char (the for loop will stop BEFORE writing at the markIter)

        int remainder = 0;
        int length = 0;
        
        // iterate over the number backwards (starting at the least signifigant digit)
        // converting into ascii digits
        for( p_dest.next( p_length - 1 );  // -1 to start at last index, not past end
             ! p_dest.equals( markIter ); 
             p_dest.prev() ) 
        {
            if ( p_intToWrite > 0 ) 
            {
                ++length;
                remainder = p_intToWrite / RADIX;
                p_dest.set( (byte)( p_intToWrite - ( remainder * RADIX ) + ASCII_ZERO ) );
                p_intToWrite = remainder;
            }
            else
            {
                p_dest.set( (byte)ASCII_ZERO );
            }
        }
        
        // since we walked backwards to the position BEFORE the first char, 
        // now set the iterator to point AFTER the last price digit
        p_dest.next( p_length + 1 );
        
        if ( remainder > 0 )
        {
            throw new RuntimeException( "conversion failed: remainder of " + remainder + " has not been converted" );
        }

        return length;
    }
    
    
    /** returns an integer representation of the next p_length bytes
     * 
     * @param p_length The number of characters to convert
     * @return the integer representation of the next p_length bytes starting at 
     * the current position
     */
    public static  final int asciiToInt( ByteIterator p_iter,
                                         int          p_length )
    {
        assert ( p_iter.remaining() >= p_length ) : "bad length of " + p_length + ", when only " + p_iter.remaining() + " bytes left in iterator";

        
        int multiplier = 1;
        int result = 0;
        byte cur = 0;        
        
        p_iter.next( p_length - 1 ); // move to last byte of number  
                
        int i = 0;
        for ( ; i < p_length; ++i )
        {   
            cur = p_iter.get();
            if ( cur != NEGATIVE )
            {
                result += multiplier * asciiByteToInt( cur );
                multiplier *= RADIX;
                p_iter.prev();
            }
            else
            {
                result *= -1;
                break;
            }
        }
        
        p_iter.next( i + 1 );  // moves one past the end of the decoded number
        return result;
    }
    
    
    
    public static int asciiToInt( byte[] p_buff, int p_start, int p_length )
    {
        assert ( p_start + p_length <= p_buff.length ) : "Illegal byte range";
        
        int endIndex = p_start + p_length - 1;  // -1 for length/index transform
        
        int multiplier = 1;
        int result = 0;
        
        for ( int i = endIndex; i >= p_start; --i )
        {   
            if ( p_buff[ i ] != NEGATIVE )
            {
                result += multiplier * asciiByteToInt( p_buff[ i ] );
                multiplier *= RADIX;
            }
            else
            {
                result *= -1;
                break;
            }
        }
        
        return result;
    }   
        
        
    public static void writeAsciiString( ByteIterator p_dest,
                                         String       p_toWrite )
    {
        byte[] asciiToWrite = p_toWrite.getBytes();
        
        NapiUtils.setBlockSize( p_dest, asciiToWrite.length );
        
        p_dest.write( asciiToWrite, 0, asciiToWrite.length );
    }
    
    
    public static void writeAsciiString( ByteIterator p_dest,
                                         ByteIterator p_toWrite, 
                                         int          p_length )
    {
        NapiUtils.setBlockSize( p_dest,
                                p_length );

        p_toWrite.read( p_dest,
                        p_length );
    }
    
    
    public static void readAsciiStringInto( ByteIterator p_src, 
                                            ByteIterator p_dest )
    {
        byte high = p_src.read();
        byte low  = p_src.read();
            
        int strSize = NapiUtils.parseBlockSize( high, low );
        
        p_src.read( p_dest, strSize );
    }
    
    
    public static byte[] readAsciiString( ByteIterator p_src )
    {
        byte high = p_src.read();
        byte low  = p_src.read();
            
        int strSize = NapiUtils.parseBlockSize( high, low );
        
        byte[] dest = new byte[ strSize ];
        
        p_src.read( dest, 0, strSize );
        
        return dest;
    }
              
            
    public static void writeBoolean( ByteIterator p_dest,
                                     boolean      p_toWrite )
    {
        p_dest.write( p_toWrite ? TRUE_BYTE 
                                : FALSE_BYTE );
    }
    
    
    /**
     * Encode long to the specified buffer
     * 
     * @param p_dest The destination of the write (it will be positioned after 
     *        the final byte written after the operation
     * @param p_blockOffset
     * @param p_toWrite
     */
    public static void writeShort( ByteIterator p_dest,
                                   short        p_toWrite )
    {
        // unrolled loop of 2 iterations
        p_dest.write( (byte)( p_toWrite & MASK_TO_BYTE ) );
        p_toWrite >>= NUMBER_OF_BITS_IN_A_BYTE;
         
        p_dest.write( (byte)( p_toWrite & MASK_TO_BYTE ) );
    }
    
    
    /**
     * Encode long to the specified buffer
     * 
     * @param p_dest
     *            The destination of the write (it will be positioned after
     *            the final byte written after the operation
     * @param p_blockOffset
     * @param p_toWrite
     */
    public static void writeInt( ByteIterator p_dest,
                                 int          p_toWrite )
    {
        // unrolled loop of 4 iterations
        p_dest.write( (byte)( p_toWrite & MASK_TO_BYTE ) );
        p_toWrite >>= NUMBER_OF_BITS_IN_A_BYTE;

        p_dest.write( (byte)( p_toWrite & MASK_TO_BYTE ) );
        p_toWrite >>= NUMBER_OF_BITS_IN_A_BYTE;
 
        p_dest.write( (byte)( p_toWrite & MASK_TO_BYTE ) );
        p_toWrite >>= NUMBER_OF_BITS_IN_A_BYTE;

        p_dest.write( (byte)( p_toWrite & MASK_TO_BYTE ) );
    }
    
    
    /**
     * Encode long to the specified buffer
     * 
     * @param p_dest The destination of the write (it will be positioned after 
     *        the final byte written after the operation
     * @param p_blockOffset
     * @param p_toWrite
     */
    public static void writeLong( ByteIterator p_dest,
                                  long         p_toWrite )
    {
        // unrolled loop of 8 iterations
        p_dest.write( (byte)( p_toWrite & MASK_TO_BYTE ) );
        p_toWrite >>= NUMBER_OF_BITS_IN_A_BYTE;
        
        p_dest.write( (byte)( p_toWrite & MASK_TO_BYTE ) );
        p_toWrite >>= NUMBER_OF_BITS_IN_A_BYTE;
        
        p_dest.write( (byte)( p_toWrite & MASK_TO_BYTE ) );
        p_toWrite >>= NUMBER_OF_BITS_IN_A_BYTE;
        
        p_dest.write( (byte)( p_toWrite & MASK_TO_BYTE ) );
        p_toWrite >>= NUMBER_OF_BITS_IN_A_BYTE;
        
        p_dest.write( (byte)( p_toWrite & MASK_TO_BYTE ) );
        p_toWrite >>= NUMBER_OF_BITS_IN_A_BYTE;
        
        p_dest.write( (byte)( p_toWrite & MASK_TO_BYTE ) );
        p_toWrite >>= NUMBER_OF_BITS_IN_A_BYTE;
        
        p_dest.write( (byte)( p_toWrite & MASK_TO_BYTE ) );
        p_toWrite >>= NUMBER_OF_BITS_IN_A_BYTE;

        p_dest.write( (byte)( p_toWrite & MASK_TO_BYTE ) );
    }
    
    
    public static boolean readBoolean( ByteIterator p_src )
    {
        return p_src.read() == TRUE_BYTE ? true 
                                         : false;
    }

    
    public static short readShort( ByteIterator p_src )
    {
        // unrolled loop of 2 iterations
        short result = (short)( p_src.read() & MASK_TO_BYTE );
    
        result |= ( ( p_src.read() & MASK_TO_BYTE ) << NUMBER_OF_BITS_IN_A_BYTE );
    
        return result;
    }

    
    public static int readInt( ByteIterator p_src )
    {
        // unrolled loop of 4 iterations
        int result = ( p_src.read() & MASK_TO_BYTE );
        result |= ( ( p_src.read() & MASK_TO_BYTE ) << 8 );
        result |= ( ( p_src.read() & MASK_TO_BYTE ) << 16 );
        result |= ( ( p_src.read() & MASK_TO_BYTE ) << 24 );
    
        return result;
    }

    
    public static long readLong( ByteIterator p_src )
    {
        // unrolled loop of 8 iterations
        long result = ( p_src.read() & MASK_LONG_TO_BYTE );
        result   |= ( ( p_src.read() & MASK_LONG_TO_BYTE ) << 8 );
        result   |= ( ( p_src.read() & MASK_LONG_TO_BYTE ) << 16 );
        result   |= ( ( p_src.read() & MASK_LONG_TO_BYTE ) << 24 );
        result   |= ( ( p_src.read() & MASK_LONG_TO_BYTE ) << 32 );
        result   |= ( ( p_src.read() & MASK_LONG_TO_BYTE ) << 40 );
        result   |= ( ( p_src.read() & MASK_LONG_TO_BYTE ) << 48 );
        result   |= ( ( p_src.read() & MASK_LONG_TO_BYTE ) << 56 );
    
        return result;
    }


    ////////////////////////////////////////////////////////////////////////////
    // implementation
    
    
    private static int asciiByteToInt( byte p_byte )
    {
        assert( p_byte <= ASCII_NINE && p_byte >= ASCII_ZERO ) : "Byte [" + (char)p_byte + "] is non-numeric";
        
        return p_byte - ASCII_ZERO;
    }
    
 
}
