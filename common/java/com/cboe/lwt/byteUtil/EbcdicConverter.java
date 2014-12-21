package com.cboe.lwt.byteUtil;


/**
 * Utility methods for dealing with EBCDIC/ASCII conversion.
 *
 * @author peterson
 */
public class EbcdicConverter
{
        
    /**
     * Converts the specified bytes from EBCDIC to ASCII
     *
     * @param p_inIter Iterator pointing to the first byte to be converted
     * @param p_outIter Iterator pointing to the byte which will receive first converted byte
     * @param p_copyLength How many bytes to convert
     */
    public static void convertToAscii( ByteIterator p_inIter, 
                                       ByteIterator p_outIter, 
                                       int          p_copyLength )
    {
        convert( p_inIter, p_outIter, p_copyLength, ebcdicToAsciiConversionTable);
    }
    
    
    /**
     * Converts the specified EBCDIC byte to ASCII
     *
     * @param p_in the EBCDIC byte to be converted
     * @returns the ASCII representation of p_in
     */
    public static byte convertToAscii( byte p_in )
    {
        return ebcdicToAsciiConversionTable[ 0xFF & p_in ];
    }
    
    
    /**
     * Converts the specified bytes to ascii to EBCDIC
     *
     * @param p_inIter Iterator pointing to the first byte to be converted
     * @param p_outIter Iterator pointing to the byte which will receive first converted byte
     * @param p_copyLength How many bytes to convert
     */
    public static void convertFromAscii( ByteIterator p_inIter, 
                                         ByteIterator p_outIter, 
                                         int          p_copyLength )
    {
        convert( p_inIter, p_outIter, p_copyLength, asciiToEbcdicConversionTable );
    }
    
    
    /**
     * Converts the specified byte to ascii to EBCDIC
     *
     * @param p_in the ASCII byte to be converted
     * @returns the EBCDIC representation of p_in
     */
    public static byte convertFromAscii( byte p_in )
    {
        return asciiToEbcdicConversionTable[ 0xFF & p_in ];
    }

    
    /** Private method to perform the conversion from one format to
     * another depending on the passed arguments.
     * @param p_inIter The iterator containing the bytes to convert
     * @param p_outIter The iterator to hold the converted bytes
     * @param p_copyLength The number of bytes to convert
     * @param p_ConversionTable The mapping table that determines the conversion
     */
    private static void convert( ByteIterator p_inIter, 
                                 ByteIterator p_outIter, 
                                 int          p_copyLength,
                                 byte[]       p_ConversionTable )
    {
        assert ( p_inIter != p_outIter ) : "must clone iterators to do inplace conversion.  Using same iterator will cause errors (double increment)";

        for ( int i = 0; i < p_copyLength; ++i )
        {
            // Calculate the index into the conversion table.
            // Since the bit pattern for the incoming byte can represent
            // a value between -128 to 127 we need to normalize it
            // as a zero based offset for the lookup in the table.
            // There are no unsigned values in java so we can't simply
            // represent it as 0 - 255.
            
            p_outIter.set( p_ConversionTable[ 0xFF & p_inIter.get() ] );
            p_inIter.next();
            p_outIter.next();
        }
    }
    
    
    // indexing the following array by the short value of the
    // ASCII byte you will get the corresponding EBCDIC representation
    // of the byte.
    private static byte[] asciiToEbcdicConversionTable =
    {
        //          0           1            2           3           4            5           6            7
        //          8           9            a           b           c            d           e            f
        /* 00 */   (byte)0x00, (byte)0x01, (byte)0x02, (byte)0x03, (byte)0x37, (byte)0x2d, (byte)0x2e, (byte)0x2f,
        /* 08 */   (byte)0x16, (byte)0x05, (byte)0x25, (byte)0x0b, (byte)0x0c, (byte)0x0d, (byte)0x0e, (byte)0x0f,
        /* 10 */   (byte)0x10, (byte)0x11, (byte)0x12, (byte)0x13, (byte)0x3c, (byte)0x3d, (byte)0x32, (byte)0x26,
        /* 18 */   (byte)0x18, (byte)0x19, (byte)0x3f, (byte)0x27, (byte)0x1c, (byte)0x1d, (byte)0x1e, (byte)0x1f,
        /* 20 */   (byte)0x40, (byte)0x4f, (byte)0x7f, (byte)0x7b, (byte)0x5b, (byte)0x6c, (byte)0x50, (byte)0x7d,
        /* 28 */   (byte)0x4d, (byte)0x5d, (byte)0x5c, (byte)0x4e, (byte)0x6b, (byte)0x60, (byte)0x4b, (byte)0x61,
        /* 30 */   (byte)0xf0, (byte)0xf1, (byte)0xf2, (byte)0xf3, (byte)0xf4, (byte)0xf5, (byte)0xf6, (byte)0xf7,
        /* 38 */   (byte)0xf8, (byte)0xf9, (byte)0x7a, (byte)0x5e, (byte)0x4c, (byte)0x7e, (byte)0x6e, (byte)0x6f,
        /* 40 */   (byte)0x7c, (byte)0xc1, (byte)0xc2, (byte)0xc3, (byte)0xc4, (byte)0xc5, (byte)0xc6, (byte)0xc7,
        /* 48 */   (byte)0xc8, (byte)0xc9, (byte)0xd1, (byte)0xd2, (byte)0xd3, (byte)0xd4, (byte)0xd5, (byte)0xd6,
        /* 50 */   (byte)0xd7, (byte)0xd8, (byte)0xd9, (byte)0xe2, (byte)0xe3, (byte)0xe4, (byte)0xe5, (byte)0xe6,
        /* 58 */   (byte)0xe7, (byte)0xe8, (byte)0xe9, (byte)0x4a, (byte)0xe0, (byte)0x5a, (byte)0x5f, (byte)0x6d,
        /* 60 */   (byte)0x79, (byte)0x81, (byte)0x82, (byte)0x83, (byte)0x84, (byte)0x85, (byte)0x86, (byte)0x87,
        /* 68 */   (byte)0x88, (byte)0x89, (byte)0x91, (byte)0x92, (byte)0x93, (byte)0x94, (byte)0x95, (byte)0x96,
        /* 70 */   (byte)0x97, (byte)0x98, (byte)0x99, (byte)0xa2, (byte)0xa3, (byte)0xa4, (byte)0xa5, (byte)0xa6,
        /* 78 */   (byte)0xa7, (byte)0xa8, (byte)0xa9, (byte)0xc0, (byte)0x6a, (byte)0xd0, (byte)0xa1, (byte)0x07,
        /* 80 */   (byte)0x20, (byte)0x21, (byte)0x22, (byte)0x23, (byte)0x24, (byte)0x15, (byte)0x06, (byte)0x17,
        /* 88 */   (byte)0x28, (byte)0x29, (byte)0x2a, (byte)0x2b, (byte)0x2c, (byte)0x09, (byte)0x0a, (byte)0x1b,
        /* 90 */   (byte)0x30, (byte)0x31, (byte)0x1a, (byte)0x33, (byte)0x34, (byte)0x35, (byte)0x36, (byte)0x08,
        /* 98 */   (byte)0x38, (byte)0x39, (byte)0x3a, (byte)0x3b, (byte)0x04, (byte)0x14, (byte)0x3e, (byte)0xe1,
        /* a0 */   (byte)0x41, (byte)0x42, (byte)0x43, (byte)0x44, (byte)0x45, (byte)0x46, (byte)0x47, (byte)0x48,
        /* a8 */   (byte)0x49, (byte)0x51, (byte)0x52, (byte)0x53, (byte)0x54, (byte)0x55, (byte)0x56, (byte)0x57,
        /* b0 */   (byte)0x58, (byte)0x59, (byte)0x62, (byte)0x63, (byte)0x64, (byte)0x65, (byte)0x66, (byte)0x67,
        /* b8 */   (byte)0x68, (byte)0x69, (byte)0x70, (byte)0x71, (byte)0x72, (byte)0x73, (byte)0x74, (byte)0x75,
        /* c0 */   (byte)0x76, (byte)0x77, (byte)0x78, (byte)0x80, (byte)0x8a, (byte)0x8b, (byte)0x8c, (byte)0x8d,
        /* c8 */   (byte)0x8e, (byte)0x8f, (byte)0x90, (byte)0x9a, (byte)0x9b, (byte)0x9c, (byte)0x9d, (byte)0x9e,
        /* d0 */   (byte)0x9f, (byte)0xa0, (byte)0xaa, (byte)0xab, (byte)0xac, (byte)0xad, (byte)0xae, (byte)0xaf,
        /* d8 */   (byte)0xb0, (byte)0xb1, (byte)0xb2, (byte)0xb3, (byte)0xb4, (byte)0xb5, (byte)0xb6, (byte)0xb7,
        /* e0 */   (byte)0xb8, (byte)0xb9, (byte)0xba, (byte)0xbb, (byte)0xbc, (byte)0xbd, (byte)0xbe, (byte)0xbf,
        /* e8 */   (byte)0xca, (byte)0xcb, (byte)0xcc, (byte)0xcd, (byte)0xce, (byte)0xcf, (byte)0xda, (byte)0xdb,
        /* f0 */   (byte)0xdc, (byte)0xdd, (byte)0xde, (byte)0xdf, (byte)0xea, (byte)0xeb, (byte)0xec, (byte)0xed,
        /* f8 */   (byte)0xee, (byte)0xef, (byte)0xfa, (byte)0xfb, (byte)0xfc, (byte)0xfd, (byte)0xfe, (byte)0xff
    };
    
    // indexing the following array by the short value of the
    // EBCDIC byte you will get the corresponding ASCII representation
    // of the byte.
    private static byte[] ebcdicToAsciiConversionTable =
    {
        //          0           1            2           3           4            5           6            7
        //          8           9            a           b           c            d           e            f
        /* 00 */   (byte)0x00, (byte)0x01, (byte)0x02, (byte)0x03, (byte)0x9c, (byte)0x09, (byte)0x86, (byte)0x7f,
        /* 08 */   (byte)0x97, (byte)0x8d, (byte)0x8e, (byte)0x0b, (byte)0x0c, (byte)0x0d, (byte)0x0e, (byte)0x0f,
        /* 10 */   (byte)0x10, (byte)0x11, (byte)0x12, (byte)0x13, (byte)0x9d, (byte)0x85, (byte)0x08, (byte)0x87,
        /* 18 */   (byte)0x18, (byte)0x19, (byte)0x92, (byte)0x8f, (byte)0x1c, (byte)0x1d, (byte)0x1e, (byte)0x1f,
        /* 20 */   (byte)0x80, (byte)0x81, (byte)0x82, (byte)0x83, (byte)0x84, (byte)0x0a, (byte)0x17, (byte)0x1b,
        /* 28 */   (byte)0x88, (byte)0x89, (byte)0x8a, (byte)0x8b, (byte)0x8c, (byte)0x05, (byte)0x06, (byte)0x07,
        /* 30 */   (byte)0x90, (byte)0x91, (byte)0x16, (byte)0x93, (byte)0x94, (byte)0x95, (byte)0x96, (byte)0x04,
        /* 38 */   (byte)0x98, (byte)0x99, (byte)0x9a, (byte)0x9b, (byte)0x14, (byte)0x15, (byte)0x9e, (byte)0x1a,
        /* 40 */   (byte)0x20, (byte)0xa0, (byte)0xa1, (byte)0xa2, (byte)0xa3, (byte)0xa4, (byte)0xa5, (byte)0xa6,
        /* 48 */   (byte)0xa7, (byte)0xa8, (byte)0x5b, (byte)0x2e, (byte)0x3c, (byte)0x28, (byte)0x2b, (byte)0x21,
        /* 50 */   (byte)0x26, (byte)0xa9, (byte)0xaa, (byte)0xab, (byte)0xac, (byte)0xad, (byte)0xae, (byte)0xaf,
        /* 58 */   (byte)0xb0, (byte)0xb1, (byte)0x5d, (byte)0x24, (byte)0x2a, (byte)0x29, (byte)0x3b, (byte)0x5e,
        /* 60 */   (byte)0x2d, (byte)0x2f, (byte)0xb2, (byte)0xb3, (byte)0xb4, (byte)0xb5, (byte)0xb6, (byte)0xb7,
        /* 68 */   (byte)0xb8, (byte)0xb9, (byte)0x7c, (byte)0x2c, (byte)0x25, (byte)0x5f, (byte)0x3e, (byte)0x3f,
        /* 70 */   (byte)0xba, (byte)0xbb, (byte)0xbc, (byte)0xbd, (byte)0xbe, (byte)0xbf, (byte)0xc0, (byte)0xc1,
        /* 78 */   (byte)0xc2, (byte)0x60, (byte)0x3a, (byte)0x23, (byte)0x40, (byte)0x27, (byte)0x3d, (byte)0x22,
        /* 80 */   (byte)0xc3, (byte)0x61, (byte)0x62, (byte)0x63, (byte)0x64, (byte)0x65, (byte)0x66, (byte)0x67,
        /* 88 */   (byte)0x68, (byte)0x69, (byte)0xc4, (byte)0xc5, (byte)0xc6, (byte)0xc7, (byte)0xc8, (byte)0xc9,
        /* 90 */   (byte)0xca, (byte)0x6a, (byte)0x6b, (byte)0x6c, (byte)0x6d, (byte)0x6e, (byte)0x6f, (byte)0x70,
        /* 98 */   (byte)0x71, (byte)0x72, (byte)0xcb, (byte)0xcc, (byte)0xcd, (byte)0xce, (byte)0xcf, (byte)0xd0,
        /* a0 */   (byte)0xd1, (byte)0x7e, (byte)0x73, (byte)0x74, (byte)0x75, (byte)0x76, (byte)0x77, (byte)0x78,
        /* a8 */   (byte)0x79, (byte)0x7a, (byte)0xd2, (byte)0xd3, (byte)0xd4, (byte)0xd5, (byte)0xd6, (byte)0xd7,
        /* b0 */   (byte)0xd8, (byte)0xd9, (byte)0xda, (byte)0xdb, (byte)0xdc, (byte)0xdd, (byte)0xde, (byte)0xdf,
        /* b8 */   (byte)0xe0, (byte)0xe1, (byte)0xe2, (byte)0xe3, (byte)0xe4, (byte)0xe5, (byte)0xe6, (byte)0xe7,
        /* c0 */   (byte)0x7b, (byte)0x41, (byte)0x42, (byte)0x43, (byte)0x44, (byte)0x45, (byte)0x46, (byte)0x47,
        /* c8 */   (byte)0x48, (byte)0x49, (byte)0xe8, (byte)0xe9, (byte)0xea, (byte)0xeb, (byte)0xec, (byte)0xed,
        /* d0 */   (byte)0x7d, (byte)0x4a, (byte)0x4b, (byte)0x4c, (byte)0x4d, (byte)0x4e, (byte)0x4f, (byte)0x50,
        /* d8 */   (byte)0x51, (byte)0x52, (byte)0xee, (byte)0xef, (byte)0xf0, (byte)0xf1, (byte)0xf2, (byte)0xf3,
        /* e0 */   (byte)0x5c, (byte)0x9f, (byte)0x53, (byte)0x54, (byte)0x55, (byte)0x56, (byte)0x57, (byte)0x58,
        /* e8 */   (byte)0x59, (byte)0x5a, (byte)0xf4, (byte)0xf5, (byte)0xf6, (byte)0xf7, (byte)0xf8, (byte)0xf9,
        /* f0 */   (byte)0x30, (byte)0x31, (byte)0x32, (byte)0x33, (byte)0x34, (byte)0x35, (byte)0x36, (byte)0x37,
        /* f8 */   (byte)0x38, (byte)0x39, (byte)0xfa, (byte)0xfb, (byte)0xfc, (byte)0xfd, (byte)0xfe, (byte)0xff
    };
    
}
