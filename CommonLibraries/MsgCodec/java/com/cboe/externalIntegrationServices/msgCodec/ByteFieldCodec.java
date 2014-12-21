/*
 * Created on Oct 2, 2008
 */
package com.cboe.externalIntegrationServices.msgCodec;

/**
 * A FieldCodec that implements a byte field. The field is encoded into the
 * storage buffer un-compressed, using 1 byte.
 * @author hammj
 */
public class ByteFieldCodec extends AbstractFieldCodec implements ICodec
{
    private static final int        MAX_SIZE = 1;
    private byte                    value;
    
    public ByteFieldCodec(String p_name) {
        super(p_name,"byte");
    }

    public int decode(byte[] storage, int startOffset) {
        value   = storage[startOffset]; 
        return startOffset + MAX_SIZE;
    }

    public int encode(byte[] storage, int startOffset) {
        storage[startOffset] = value;
        return startOffset + MAX_SIZE;
    }
    
    public byte getValue() {
        return value;
    }
    
    public void setValue(byte p_value) {
        value = p_value;
    }

    public ICodec newCopy() {
        return new ByteFieldCodec(getFieldName());
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
