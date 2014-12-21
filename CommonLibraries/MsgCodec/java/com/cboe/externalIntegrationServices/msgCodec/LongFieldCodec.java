/*
 * Created on Oct 2, 2008
 */
package com.cboe.externalIntegrationServices.msgCodec;

/**
 * A FieldCodec that implements a long field. The field is encoded into the
 * storage buffer un-compressed, using 8 bytes.
 * @author hammj
 */
public class LongFieldCodec extends AbstractFieldCodec implements ICodec
{
    private static final int        MAX_SIZE = 8;
    private long                    value;
    
    public LongFieldCodec(String p_name) {
        super(p_name,"long");
    }

    public int decode(byte[] storage, int startOffset) {
        value = (((long)storage[startOffset+0] & 0xff) << 56) |
                (((long)storage[startOffset+1] & 0xff) << 48) |
                (((long)storage[startOffset+2] & 0xff) << 40) |
                (((long)storage[startOffset+3] & 0xff) << 32) |
                (((long)storage[startOffset+4] & 0xff) << 24) |
                (((long)storage[startOffset+5] & 0xff) << 16) |
                (((long)storage[startOffset+6] & 0xff) <<  8) |
                (((long)storage[startOffset+7] & 0xff) <<  0);
        return startOffset + MAX_SIZE;
    }

    public int encode(byte[] storage, int startOffset) {
        int val;
        val = (int)((value >> 32) & 0xFFFFFFFF);
        storage[startOffset+0] = (byte)((val >> 24) & 0xFF);
        storage[startOffset+1] = (byte)((val >> 16) & 0xFF);
        storage[startOffset+2] = (byte)((val >>  8) & 0xFF);
        storage[startOffset+3] = (byte)((val) & 0xFF);

        val = (int)(value & 0xFFFFFFFF);
        storage[startOffset+4] = (byte)((val >> 24) & 0xFF);
        storage[startOffset+5] = (byte)((val >> 16) & 0xFF);
        storage[startOffset+6] = (byte)((val >>  8) & 0xFF);
        storage[startOffset+7] = (byte)((val) & 0xFF);
        
        return startOffset + MAX_SIZE;
    }
    
    public long getValue() {
        return value;
    }
    
    public void setValue(long p_value) {
        value = p_value;
    }

    public ICodec newCopy() {
        return new LongFieldCodec(getFieldName());
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

        byte[] encodedBytes  = new byte[MAX_SIZE];
        encode(encodedBytes,0);
        
        ByteUtils.hexDump(out, title.toString(), printIndent, 
                          encodedBytes, 0, encodedBytes.length);
    }
}
