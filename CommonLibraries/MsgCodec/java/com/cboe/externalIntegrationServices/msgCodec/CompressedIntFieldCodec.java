/*
 * Created on Oct 20, 2008
 */
package com.cboe.externalIntegrationServices.msgCodec;

import javax.naming.OperationNotSupportedException;

/**
 * <pre>
 * A FieldCodec that implements an int field with FAST-compressed encoding.
 * The amount of compression depends on the value of the field.  
 * 
 * Consider carefully the range of values that will be required for this field 
 * before using this class; DO NOT USE IF the most likely values are outside
 * the range of about -1,000,000 to 1,000,000 because the cost of encoding 
 * values outside this range will be more than if an un-compressed Int field 
 * were used. Use only if "occasional" values are outside this range, but
 * most are inside the above range.
 * 
 * Some good/bad examples of fields you might use this Codec for include:
 *  
 * Good Usage                                       Bad Usage (DO NOT USE)
 * individual quote bid/ask volumes                 total bid/ask volumes
 * individual order/trade volumes                   total trade/order volumes
 * whole prices (US Equities/Options)               fractional prices in billionths 
 * fractional prices in thousandths                 fractional prices in millionths
 * whole + fractional prices in implied pennies     transaction counters
 * yymmdd dates as int values                       timestamps in millis, nanos, etc.
 * hhmmdd times as int values                       timestamps as millis since midnight
 * timestamps as second since midnight 
 * 
 * The field value is encoded into the storage buffer using a compression method 
 * taken from the FAST (Fix Adapted For Streaming) specification. The encoding
 * is that of a FIX-FAST "I32 Copy" (Signed 32 bit Integer) data type, with
 * copy operator. Unlike FAST, however, we do not use presence-maps here,
 * we are simply using the FAST binary value compression scheme.
 * 
 * All encoded bytes use 7 bits, with the sign bit of each byte reserved to 
 * indicate the last byte of the encoded value.
 * The sign bit for the int value is bit 6 of the leftmost encoded byte.
 * 
 * The value of the field determines it's encoded space requirements as follows:
 * 
 * Values >= -64       and < 64 are encoded in 1 byte.          (6 bits  == 8 - 1 stop bit -1 for sign)
 * Values >= -8192     and < 8192 are encoded in 2 bytes.       (13 bits == 6 + 7)
 * Values >= -1048576  and < 1048576 are encoded in 3 bytes.    (20 bits == 6 + 7 + 7)
 * Values >= -134217728 and < 134217728 are encoded in 4 bytes. (27 bits == 6 + 7 + 7 + 7)
 * Values <  -134217728 and >= 134217728 are encoded in 5 bytes (32 bits == 6 + 7 + 7 + 7 + 5)
 *
 * Note that for large values, 5 bytes are required to encode the value, which 
 * is wasteful.
 * </pre>
 * @author hammj
 */
public class CompressedIntFieldCodec extends AbstractFieldCodec
{
    private final static int        SIGN_MASK_I32 = 0x40; // for sign bit mask.

    private static final int        MAX_SIZE = 5;
    private int                     value;

    public CompressedIntFieldCodec(String p_name) {
        super(p_name,"compressedInt");
    }

    /**
     * Decodes signed int value using FAST 7-bit encoding/decoding.
     */
    public final int decode(final byte[] storage, final int startOffset) {
        int  i          = startOffset;
        byte readByte   = storage[i++];
        int v;

        /*
         * bit 6 of leftmost byte (2 ** 6 == 0x40) is sign of encoded int value.
         * If on, make initial value -1 (negative), else initial value is 0 
         * (positive)
         * 
         * There is a max of 5 bytes, so instead of a loop, we'll inline
         * to make fewer branches.
         */
        
        // Byte startOffset + 0
        v = (readByte & SIGN_MASK_I32) != 0 ? -1 : 0;   // determine the sign
        v = (v << 7 );                          // shift sign and put value in least significant
        if( readByte < 0 ) {                    // final byte?
            value = v  | (readByte & 0x7f);
            return i;
        }
        v = v | readByte;

        // Byte startOffset + 1
        readByte = storage[i++];
        v = (v << 7 );                          // shift 7 and OR in value.
        if( readByte < 0 ) {                    // final byte?
            value = v  | (readByte & 0x7f);
            return i;
        }
        v = v | readByte;

        // Byte startOffset + 2
        readByte = storage[i++];
        v = (v << 7 );                          // shift byte 1 data and put value in least significant
        if( readByte < 0 ) {                    // final byte?
            value = v  | (readByte & 0x7f);
            return i;
        }
        v = v | readByte;

        // Byte startOffset + 3
        readByte = storage[i++];
        v = (v << 7 );                          // shift byte 2 data and put value in least significant
        if( readByte < 0 ) {                    // final byte?
            value = v  | (readByte & 0x7f);
            return i;
        }
        v = v | readByte;

        // Byte startOffset + 4
        readByte = storage[i++];
        v = (v << 7 );                          // shift byte 3 data and put value in least significant
        if( readByte < 0 ) {                    // final byte?
            value = v  | (readByte & 0x7f);
            return i;
        }

        /*
         * If we make it here, we have a serious problem; we have not seen
         * the negative valued byte that indicates end of field.
         * Encoded values cannot take more than 5 bytes.
         */
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName());
        sb.append(" : Error decoding compressed int value.  Compressed data ");
        sb.append("does not terminate in stop-byte after 5 bytes consumed. ");
        sb.append("Stopped decoding at offset: ").append(i);
        sb.append("Buffer data: ");
        int len = storage.length - startOffset;
        ByteUtils.hexDump(sb, "", 0, storage, 0, len);
        throw new IllegalArgumentException(sb.toString());
    }

    public int encode(byte[] storage, int startOffset) {
        final int v = value;

        final int fieldLen = signed32Size(v);
        int i = startOffset;

        if( fieldLen > 4)
            storage[i++] = (byte)((v >> 28) & 0x7f); // unique code for signed
        if( fieldLen > 3)
            storage[i++] = (byte)((v >> 21) & 0x7f);
        if( fieldLen > 2)
            storage[i++] = (byte)((v >> 14) & 0x7f);
        if( fieldLen > 1)
            storage[i++] = (byte)((v >> 7) & 0x7f);
        storage[i++] = (byte)((v & 0x7f) | 0x80);

        return i;
    }

    private final int signed32Size (int val) {
        if (val >= 0) {
            if (val <  0x00000040) return 1; // 64
            if (val <  0x00002000) return 2; // 8192
            if (val <  0x00100000) return 3; // 1048576
            if (val <  0x08000000) return 4; // 134217728

        }
        else {
            if (val >= 0xffffffc0) return 1; // -64
            if (val >= 0xffffe000) return 2; // -8192
            if (val >= 0xfff00000) return 3; // -1048576
            if (val >= 0xf8000000) return 4; // -124317728
        }
        return 5;
    }
    
    public int getValue() {
        return value;
    }
    
    public void setValue(int p_value) {
        value = p_value;
    }

    public ICodec newCopy() {
        return new CompressedIntFieldCodec(getFieldName());
    }

    public void reset() {
        value = 0;
    }

    public int typicalMaximumSize() {
        return MAX_SIZE;
    }

    public void toString(StringBuilder out, int printIndent) {
        
        StringBuilder title = new StringBuilder();
        super.toString(title, printIndent);
        title.append(getValue());
        
        byte[] data = new byte[MAX_SIZE];
        int len = encode(data, 0);
        
        ByteUtils.hexDump(out, title.toString(), 0, data, 0, len);
    }


}
