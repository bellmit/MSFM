/*
 * Created on Oct 2, 2008
 */
package com.cboe.externalIntegrationServices.msgCodec;

/**
 * A FieldCodec that implements a short field. The field is encoded into the
 * storage buffer un-compressed, using 2 bytes.
 * @author hammj
 */
public class ShortFieldCodec extends AbstractFieldCodec implements ICodec
{
    private static final int        MAX_SIZE = 2;
    private short                   value;
    
    public ShortFieldCodec(String p_name) {
        super(p_name,"short");
    }

    public int decode(byte[] storage, int startOffset) {
        value   = (short)((storage[startOffset+0] << 8) | 
                          (storage[startOffset+1] & 0xff));
        return startOffset + MAX_SIZE;
    }

    public int encode(byte[] storage, int startOffset) {
        final short val = value;
        storage[startOffset+0] = (byte)(val >> 8);
        storage[startOffset+1] = (byte)(val & 0xFF);
        return startOffset + MAX_SIZE;
    }
    
    public short getValue() {
        return value;
    }
    
    public void setValue(short p_value) {
        value = p_value;
    }

    public ICodec newCopy() {
        return new ShortFieldCodec(getFieldName());
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
