/*
 * Created on Oct 2, 2008
 */
package com.cboe.externalIntegrationServices.msgCodec;

/**
 * A FieldCodec that implements an int field. The field is encoded into the
 * storage buffer un-compressed, using 4 bytes.
 * @author hammj
 */
public class IntFieldCodec extends AbstractFieldCodec implements ICodec
{
    private static final int        MAX_SIZE = 4;
    private int                     value;
    
    public IntFieldCodec(String p_name) {
        super(p_name,"int");
    }

    public int decode(byte[] storage, int startOffset) {
        value   = (storage[startOffset+0] & 0x000000FF) << 24 |
                  (storage[startOffset+1] & 0x000000FF) << 16 |
                  (storage[startOffset+2] & 0x000000FF) << 8  |
                  (storage[startOffset+3] & 0x000000FF);
        return startOffset + MAX_SIZE;
    }

    public int encode(byte[] storage, int startOffset) {
        final int val = value;
        storage[startOffset+0] = (byte)((val >> 24) & 0xFF);
        storage[startOffset+1] = (byte)((val >> 16) & 0xFF);
        storage[startOffset+2] = (byte)((val >>  8) & 0xFF);
        storage[startOffset+3] = (byte)((val) & 0xFF);
        return startOffset + MAX_SIZE;

    }
    
    public int getValue() {
        return value;
    }
    
    public void setValue(int p_value) {
        value = p_value;
    }

    public ICodec newCopy() {
        return new IntFieldCodec(getFieldName());
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
