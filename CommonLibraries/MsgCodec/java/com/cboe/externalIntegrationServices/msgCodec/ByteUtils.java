package com.cboe.externalIntegrationServices.msgCodec;


/**
 * This is a utility class which contains several utility functions that
 * operate by Byte Arrays 
 * @author hammj
 */
public class ByteUtils
{

    final static byte        ZERO_BYTE       = (byte)'0';
    final static byte        DECIMAL_BYTE    = (byte)'.';
   
    final static byte [] DigitTens = {
        '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
        '1', '1', '1', '1', '1', '1', '1', '1', '1', '1',
        '2', '2', '2', '2', '2', '2', '2', '2', '2', '2',
        '3', '3', '3', '3', '3', '3', '3', '3', '3', '3',
        '4', '4', '4', '4', '4', '4', '4', '4', '4', '4',
        '5', '5', '5', '5', '5', '5', '5', '5', '5', '5',
        '6', '6', '6', '6', '6', '6', '6', '6', '6', '6',
        '7', '7', '7', '7', '7', '7', '7', '7', '7', '7',
        '8', '8', '8', '8', '8', '8', '8', '8', '8', '8',
        '9', '9', '9', '9', '9', '9', '9', '9', '9', '9' };

    final static byte [] DigitOnes = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

    final static byte[] digits = { '0','1','2','3','4','5','6','7','8','9' };
    
    final static double[] base10Table = 
    {   1.0, 
        10.0, 
        100.0, 
        1000.0, 
        10000.0, 
        100000.0, 
        1000000.0, 
        10000000.0,
        100000000.0, 
        1000000000.0};
    
    /*
     * HEX_CHARS array. Static array of hex chars for decimal to hex conversions.
     */
    private static final char[] HEX_CHARS = new char[16];

    static {
        byte b;
        int i = 0;
        for (b = '0'; b <= '9'; b++)
            HEX_CHARS[i++] = (char) b;
        for (b = 'A'; b <= 'F'; b++)
            HEX_CHARS[i++] = (char) b;
    }

    /**
     * Appends the hexadecimal representation of a byte to a character array at
     * a given location. It is assumed the char array is large enough to fit
     * the 2 hex chars that are added.
     * @param b - the byte value to convert to a hex character representation
     * @param chars - A char array. The hex representation of the byte value
     *        will be added to this array beginning starting at the startOffset.
     * @param startOffset - The array index at which hex chars will be added.
     * @return offset after the hex chars that were added (startOffset + 2).
     */
    private static int appendHexChars(byte b, char[] chars, int startOffset) {
        int byteAsInt, offset;

        byteAsInt = (int) b;

        offset = startOffset;
        chars[offset++] = HEX_CHARS[byteAsInt >> 4 & 0x0F];
        chars[offset++] = HEX_CHARS[byteAsInt & 0x0F];
        return offset;

    }

    /**
     * Appends the hexadecimal representation of a 4 byte integer to a character 
     * array at a given location. It is assumed the char array is large enough 
     * to fit the 8 hex chars that are added.
     * @param i - the byte value to convert to a hex character representation
     * @param chars - A char array. The hex representation of the int value
     *        will be added to this array beginning starting at the startOffset.
     * @param startOffset - The array index at which hex chars will be added.
     * @return offset after the hex chars that were added (startOffset + 4).
     */
    private static int appendHexChars(int i, char[] chars, int startOffset) {
        int offset;
        int value;

        offset  = startOffset;
        value   = i; 
        chars[offset++] = HEX_CHARS[value >> 28 & 0x0F];
        chars[offset++] = HEX_CHARS[value >> 24 & 0x0F];
        chars[offset++] = HEX_CHARS[value >> 20 & 0x0F];
        chars[offset++] = HEX_CHARS[value >> 16 & 0x0F];
        chars[offset++] = HEX_CHARS[value >> 12 & 0x0F];
        chars[offset++] = HEX_CHARS[value >> 8 & 0x0F];
        chars[offset++] = HEX_CHARS[value >> 4 & 0x0F];
        chars[offset++] = HEX_CHARS[value & 0x0F];
        return offset;
    }

    /**
     * Displays the contents of a byte array in a commonly seen hex + ASCII dump 
     * format. The format of the dumped data looks like that commonly found on 
     * many computer platforms. The leftmost column is the offset from the 
     * start of the dump address in hexadecimal. There are then charsPerRow / 4 
     * sets of columns containing a hex value for every 4 bytes of memory. On 
     * the far right is an ASCII representation. Non-printing characters are
     * represented by a "." character in this area. 
     * 
     * Here is an example of how the output might look if the charsPerRow was 
     * passed as 16.
     *  
     * 00000000  66697273 74207370 69202020 20202062  |first spi      b|
     * 00000010  79202020 7365745F 70617468 7320696E  |y   set_paths in| 
     * 00000020  636C7564 653B6C69 73745F6C 69627261  |clude;list_libra| 
     * 00000030  72795F70 61746873 20696E63 6C756465  |ry_paths include|
     * 
     * 1234567890123456789012345678901234567890123456789012345678901234567890
     *          1         2         3         4         5         6 
     *
     * This is a private, dumb, fast and not-well-encapsulated version of the 
     * "core" dump logic. It is parameterized to make it easier to write more 
     * useful public wrapper/delegator methods in the future. No memory is 
     * allocated by this method, unless println() allocates memory when it is 
     * invoked. To prevent memory allocation, the caller must supply a char 
     * array which is used as a temporary variable by this method.
     * 
     * The caller passes the PrintStream to which we print, the number of chars 
     * we print per row and a charBuff which is used as temporary storage for 
     * placing the bytes to be printed. The caller must pass all of this data;  
     * this way, we can provide multiple "wrapper" dump formats in the future 
     * if need be, I.E. a 16 byte wide, 32 byte wide, etc., or perhaps a format 
     * that passes a ByteIterator or String, for example.  In this case the 
     * caller would have to be able to convert whatever it is passed to dump 
     * into a byte array. 
     * 
     * The caller MUST provide the correctly sized array for the charBuff. If 
     * it does not, either garbage could be printed, or we may index out of 
     * bounds.
     *  
     * The caller may need to synchronize on the charBuff if it chooses to use 
     * a static global variable to make it's method thread-safe. We assume 
     * synchronization issues are taken care of in the caller. This method 
     * should never be exposed to the public; it's purpose is to be fast and 
     * stupid. The calling / wrapper methods should have the idiot-proofing.  
     * @param p_array - The byte array to dump
     * @param p_startOffset - The starting element of the array to dump.
     * @param p_numBytes - The number of bytes to dump.
     * @param charsPerRow - The number of bytes per row of the dump output.
     * @param charBuff - A char buffer which will be used as temporary storage. The caller provides 
     * this so this method does not have to allocate memory. The caller must size this array to
     * be the correct size that correlates with the length of each line of output based on the 
     * charsPerRow.
     * @param out - A PrintStream to which we write the formatted dump. Typically, System.out
     * will be passed in this argument, but it can be any PrintStream.
     */

    private static void hexDump(    String p_nameTag,
                                    byte[] p_array,
                                    int p_startOffset,
                                    int p_numBytes,
                                    int p_charsPerRow,
                                    char[] p_charBuff,
                                    StringBuilder out) {
        byte asciiByteValue;
        int offset, i, relOffset, endOffset, outLen, printedOffset;

        // Check bounds of array. If startOffset puts us out of bounds, print a msg and return.
        if (p_startOffset < 0 || p_startOffset >= p_array.length) {
            out.append(
                "Cannot hexDump array. StartOffset("
                    + p_startOffset
                    + ") is outside the array bounds [0-"
                    + p_array.length
                    + "]")
               .append("\n");
            return;
        }

        /*
         * Compute endOffset and adjust if it would cause us to index out of bounds.
         * endOffset is 1 more than the last p_array cell we will reference. 
         */

        endOffset = p_startOffset + p_numBytes;
        if (endOffset > p_array.length)
            endOffset = p_array.length;

        printedOffset = 0;
        offset = p_startOffset;
        
        while (offset < endOffset) {

            // Append the offset as a hex number and 1 of the 2 spaces that
            // follow it.

            outLen = 0;
            outLen = appendHexChars(printedOffset, p_charBuff, outLen);
            p_charBuff[outLen++] = ':';

            // Now build the hex representation.
            // On the 0th, 4th, 8th, , etc. element we'll pre-pend a blank
            // If the relOffset is >= endOffset, we put in 2 blanks, not hex
            // digits.

            for (i = 0; i < p_charsPerRow; i++) {
                relOffset = offset + i;
                if ((i & 0x03) == 0)
                    p_charBuff[outLen++] = ' ';
                if (relOffset < endOffset)
                    outLen = appendHexChars(p_array[relOffset], p_charBuff,
                        outLen);
                else {
                    p_charBuff[outLen++] = ' ';
                    p_charBuff[outLen++] = ' ';
                }
            }

            /*
             * Now append 2 spaces and the "| delimiter; we'll format the ASCII
             * portion now.
             */
            p_charBuff[outLen++] = ' ';
            p_charBuff[outLen++] = ' ';
            p_charBuff[outLen++] = '|';

            for (i = 0; i < p_charsPerRow; i++) {
                relOffset = offset + i;
                if (relOffset < endOffset) {
                    asciiByteValue = p_array[relOffset];
                    if (asciiByteValue < ' ' || asciiByteValue > '~')
                        asciiByteValue = '.';
                } else {
                    asciiByteValue = ' ';
                }
                p_charBuff[outLen++] = (char) asciiByteValue;
            }
            /*
             * Add on final "|" at the end, print to print stream, and increment
             * offset by charsPerRow.
             */
            p_charBuff[outLen++] = '|';
            out.append(p_charBuff);
            if (offset == p_startOffset && p_nameTag.length() > 0)
                out.append("  ").append(p_nameTag);
            out.append("\n");
            offset += p_charsPerRow;
            printedOffset += p_charsPerRow;

        }
    }


    /**
     * Computes the size of the dump buff needed given a number of chars per
     * row.
     * @param p_charsPerRow
     * @return - Size of dumpBuff needed.
     */
    private static final int dumpBuffSize(int p_charsPerRow) {

        /*
         * Output looks like this for charsPerRow = 16
         * 123456                                    78                9
         * 0000  66697273 74207370 69202020 20202062  |first spi      b|
         * 0010  79202020 7365745F 70617468 7320696E  |y   set_paths in|
         * 
         * Offset is 8 digits + 2 spaces = 10
         * " |" before text area is = 2 bytes
         * Trailing "|" is 1 byte.
         * This adds up to 13 bytes of fixed chars.
         * The number of hex digitis is 2 * charsPerRow
         * The hex digits are clustered in groups of 8 hex digits (4 bytes) 
         * separated by spaces, so there are charsPerRow / 4 spaces to seperate 
         * the clusters of 8 hex digits.
         * The ascii portion is charsPerRow chars also.
         */
        return (13 + (p_charsPerRow * 2) + (p_charsPerRow / 4) + p_charsPerRow); 
    }

    private static final int CHARS_PER_ROW = 16;
    private static final char[] dumpBuff = new char[dumpBuffSize(CHARS_PER_ROW)];

    /**
     * Dumps a name tag and a byte array in a hex dump + ASCII format.  The format of the 
     * dump looks like the following:
     *  
     *      NameTag 
     *      00000000  66697273 74207370 69202020 20202062  |first spi      b|
     *      00000010  79202020 7365745F 70617468 7320696E  |y   set_paths in| 
     *      00000020  636C7564 653B6C69 73745F6C 69627261  |clude;list_libra| 
     *      00000030  72795F70 61746873 20696E63 6C756465  |ry_paths include|
     * 
     *      1234567890123456789012345678901234567890123456789012345678901234567890
     *               1         2         3         4         5         6
     * 
     * The leftmost column is the offset. This is followed by 4 sets of 4 bytes
     * represented as hex digits (which requires 8 hex digits to represent 4 binary bytes). 
     * On the far right is an ASCII representation, surrounded by "|" characters. Non-printing
     * characters are represented by a "." in the ASCII-formatted area.
     * @param p_array
     * @param startOffset
     * @param numBytes
     */

    public static void hexDump( StringBuilder out, 
                                String p_nameTag,
                                byte[] p_array,
                                int p_startOffset,
                                int p_numBytes) {

        synchronized (dumpBuff) {
            hexDump(p_nameTag,
                    p_array,
                    p_startOffset,
                    p_numBytes,
                    CHARS_PER_ROW,
                    dumpBuff,
                    out);
        }
    }

    /**
     * @deprecated Use the hexDump method without the p_printIndent parameter.
     * 
     * @param out
     * @param p_nameTag
     * @param p_printIndent
     * @param p_array
     * @param p_startOffset
     * @param p_numBytes
     */
    public static void hexDump( StringBuilder out, 
                                String p_nameTag,
                                int p_printIndent,
                                byte[] p_array,
                                int p_startOffset,
                                int p_numBytes) {

        synchronized (dumpBuff) {
            hexDump(p_nameTag,
                    p_array,
                    p_startOffset,
                    p_numBytes,
                    CHARS_PER_ROW,
                    dumpBuff,
                    out);
        }
    }


    /**
     * Dumps a name Tag and a String in a hex dump + ASCII format.  
     * The format of the dump looks like the following:
     *  
     *      NameTag 
     *      00000000  66697273 74207370 69202020 20202062  |first spi      b|
     *      00000010  79202020 7365745F 70617468 7320696E  |y   set_paths in| 
     *      00000020  636C7564 653B6C69 73745F6C 69627261  |clude;list_libra| 
     *      00000030  72795F70 61746873 20696E63 6C756465  |ry_paths include|
     * 
     *      1234567890123456789012345678901234567890123456789012345678901234567890
     *               1         2         3         4         5         6
     * 
     * The leftmost column is the offset into the String. This is followed by 4 sets of 4 bytes
     * represented as hex digits (which requires 8 hex digits to represent 4 binary bytes). 
     * On the far right is an ASCII representation, surrounded by "|" characters. Non-printing
     * characters are represented by a "." in the ASCII-formatted area.
     * @param p_array
     * @param startOffset
     * @param numBytes
     */

    public static void hexDump(StringBuilder out, String p_nameTag, String stringToDump) {

        out.append(p_nameTag).append("\n");
        byte[] bytesToDump;

        bytesToDump = stringToDump.getBytes();
        synchronized (dumpBuff) {
            hexDump("",
                    bytesToDump,
                    0,
                    bytesToDump.length,
                    CHARS_PER_ROW,
                    dumpBuff,
                    out);
        }

    }
}
