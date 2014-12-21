/*
 * Created on Oct 21, 2008
 */
package com.cboe.externalIntegrationServices.msgCodec;

/**
 * A FieldCodec that implements a String field that contains US-ASCII 7 bit
 * characters in the range 0x01 through 0x6F. The ASCII values 0x00 (null char)
 * and 0x7F (delete char) are reserved.
 * 
 * The ASCII Strings managed by this Codec must contain 7-bit US-ASCII 
 * characters. A String that contains unicode characters will have it's high 
 * byte stripped off and ignored.  A String containing character values greater
 * than 0x6F or == 0x00 will have it's top bit stripped off. 
 * 0x7F will be treated as a null value, and 0x00 is treated as an empty String.  
 * 
 * The above restrictions are not checked so as to speed performance.
 * @author hammj
 */

public class AsciiStringFieldCodec extends AbstractFieldCodec
{
    private static final String     EMPTY_STRING        = "";
    private static final int        EMPTY_STRING_LENGTH = 0;
    private static final int        NULL_STRING_LENGTH  = -1;
    private static final char       NULL_STRING_CHAR    = (char)0x7F;
    private static final char       EMPTY_STRING_CHAR   = (char)0x00;
    private final int               TYPICAL_MAX_SIZE;

    /*
     * To avoid requiring a length to be stored (an extra 1-2 bytes) and to
     * simplify encode/decode operations, we encode the String using 
     * FIX-FAST-like encoding where the sign bit of each byte is a "stop bit".
     * For example, to encode "ABC", we encode 'A', 'B', and ('C' | 0x80).
     * 
     * When decoding, we scan for negative byte values, and when encoding, we
     * bitwise OR 0x80 onto the last byte to make it negative.  All other 
     * encoded bytes are masked with 0x7F to insure their top bit is off.
     * 
     * This means all encoded Strings must be at least 1 byte.
     * To encode null, we use the special byte 0x7F (delete char), which will 
     * always be the last and only byte and is thus encoded as 0xFF.
     * To encode empty Strings (""), we use the special byte value 0x00, which
     * will be the last and only byte and is always encoded as 0x80.
     * 
     * To keep track of the state of the String value, the length will be
     * used to indicate whether the String is null, zero length or positive 
     * length.
     */
    private int                     length;
    private char[]                  valueChars;
    private String                  stringValue;

    public AsciiStringFieldCodec(String p_name,int p_typicalMaxSize) {
        super(p_name,"String");
        TYPICAL_MAX_SIZE = p_typicalMaxSize;
        valueChars = new char[p_typicalMaxSize * 2];
    }

    /**
     * Decodes storage into valueChars array.
     * Re-sizes valueChars if needed.
     */
    public int decode(byte[] storage, int startOffset) {
        final int max   = valueChars.length;
        int len         = 0;
        int src         = startOffset;
        byte b          = storage[src++];
        while(b >= 0) {
            if (len >= max) {
                char[] newValChars = new char[len * 2];
                System.arraycopy(valueChars,0,newValChars,0,len);
                valueChars         = newValChars;
            }
            valueChars[len++] = (char)b;
            b = storage[src++];
        }
        valueChars[len++]   = (char)(b & 0x7F);
        /*
         * If we decoded 1 char, check for special empty and null values.
         */
        if (len == 1) {
            final char firstChar = valueChars[0];
            if (firstChar == EMPTY_STRING_CHAR) {
                len = EMPTY_STRING_LENGTH;
                stringValue = EMPTY_STRING;
            }
            else if (firstChar == NULL_STRING_CHAR) {
                len = NULL_STRING_LENGTH;
                stringValue = null;
            }
            else {
                stringValue = new String(valueChars,0,len);
            }
        } 
        else {
            stringValue = new String(valueChars,0,len);
        }
        length              = len;
        return src;
    }

    /**
     * Encodes bytes from stringValue into storage.
     */
    public int encode(byte[] storage, int startOffset) {
        int dest        = startOffset;
        int len         = length;
        if (len > EMPTY_STRING_LENGTH) {
            final String str = stringValue;
            len--;                              // Omit last byte
            if (str == null) {
                
            }
            for(int i = 0; i < len; i++) {
                storage[dest++] = (byte)str.charAt(i);
            }
            // Copy last byte with high bit turned on to indicate end of string.
            storage[dest++] = (byte)(str.charAt(len) | 0x80);
        }
        else {
            if (len == EMPTY_STRING_LENGTH) {
                storage[dest++] = (byte)(EMPTY_STRING_CHAR | 0x80);
            }
            else {
                storage[dest++] = (byte)(NULL_STRING_CHAR | 0x80);
            }
        }
        return dest;
    }
    
    /**
     * Returns value last set by setValue() or value last decoded.
     * May return null if last setValue() was null or last decoded value was null.
     * @return
     */
    public String getValue() {
        return stringValue;
    }

    public void setValue(String p_value) {
        if (p_value == null) {
            length = NULL_STRING_LENGTH;
        }
        else {
            length = p_value.length();  // This takes care of empty and non-empty strings
        }
        stringValue = p_value;
    }
    public ICodec newCopy() {
        return new AsciiStringFieldCodec(getFieldName(),TYPICAL_MAX_SIZE);
    }

    public void reset() {
        setValue(null);
    }

    public int typicalMaximumSize() {
        return TYPICAL_MAX_SIZE;
    }

    public void toString(StringBuilder out, int printIndent) {
        
        StringBuilder title = new StringBuilder();
        super.toString(title, printIndent);
        title.append(getValue());
        
        byte[] data = new byte[TYPICAL_MAX_SIZE * 2];
        int len = encode(data, 0);
        
        ByteUtils.hexDump(out, title.toString(), 0, data, 0, len);
    }

}
