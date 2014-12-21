/*
 * Created on Oct 2, 2008
 */
package com.cboe.externalIntegrationServices.msgCodec;

import java.util.ArrayList;

/**
 * This is the common superclass for all MsgCodecs. Applications should
 * extend this class when the application has a data "struct" that is to be 
 * stored in a "MarketBuffer" (a subclass of AbstractDataBuffer).
 * 
 * The implementor must choose a unique codecId to represent the "type" of codec
 * that is implemented.  This codecId must be globally unique across all
 * MsgCodecs and this must be managed by the application. 
 * 
 * The implementor's constructor should invoke the add() method to create new
 * FieldCodecs of various types to build up the fields in the "struct" represented
 * by the MsgCodec implementation.  The implementor should have setter/getter 
 * methods to access these fields, and these setter/getter methods should 
 * delegate to the appropriate setValue/getValue methods on the various 
 * FieldCodecs.   The application implementor may choose to have a setter/getter
 * for each field individually, or perhaps a multi-argument method to set
 * several fields at once. In addition, the implementor may choose to implement
 * a getter for each field individually, or a "copyInto" type getter that
 * copies the fields into a well-known java object or struct.
 * @author hammj
 */
public abstract class MsgCodec implements ICodec
{
    private final ArrayList<ICodec>                 fields;
    private final String                            codecName;
    private final short                             id;
    private int                                     maxSize = 0;
    
    /**
     * BESTPRACTICE Do not override the MsgCodec name, id constructor; use the
     * name constructor instead
     */
    protected MsgCodec(String p_name,short p_codecId) {
        codecName       = p_name;
        id              = p_codecId;
        fields          = new ArrayList<ICodec>();
    }

    protected final ICodec add(ICodec p_field) {
        fields.add(p_field);
        return p_field;
    }

    public final short getCodecId() {
        return id;
    }
    
    public final String getName() {
        return codecName;
    }

    /**
     * Encodes each field added to this MsgCodec by looping through each
     * ICodec field and telling it to encode (a virtual method call).
     * In some cases, it may be necessary to override this method for best
     * performance, and invoke encode directly on each field in-line, which 
     * is why this method is not final.
     * @param storage - The storage into which the field data will be encoded.
     * @param startOffset - The beginning offset of the array to start placing bytes. 
     * @return - The offset immediately following the last byte encoded.
     */
    public int encode(byte[] storage,int startOffset) {
        int nextStartOffset = startOffset;
        for(int i = 0; i < fields.size(); i++) {
            nextStartOffset = fields.get(i).encode(storage, nextStartOffset);
        }
        if (nextStartOffset == startOffset)
            throw new IllegalArgumentException("no fields defined for " + getClass().getSimpleName()
                    + " (" + getName() + ")");
        return nextStartOffset;
    }

    /**
     * Decodes each field added to this MsgCodec by looping through each
     * ICodec field and telling it to decode (a virtual method call).
     * In some cases, it may be necessary to override this method for best
     * performance, and invoke decode directly on each field in-line, which 
     * is why this method is not final.
     * @param storage - The storage from which the field data will be decoded.
     * @param startOffset - The beginning offset of the array to start decoding bytes. 
     * @return - The offset immediately following the last byte decoded.
     */
    public int decode(byte[] storage,int startOffset) {
        int nextStartOffset = startOffset;
        for(int i = 0; i < fields.size(); i++) {
            nextStartOffset = fields.get(i).decode(storage, nextStartOffset);
        }
        return nextStartOffset;
    }

    public void reset() {
        for(int i = 0; i < fields.size(); i++) {
            fields.get(i).reset();
        }
    }
    
    public int typicalMaximumSize() {
        if (maxSize <= 0) {
            int sum       = AbstractDataBuffer.codecOverheadSize();
            for(int i = 0; i < fields.size(); i++) {
                sum += fields.get(i).typicalMaximumSize(); 
            }
            maxSize = sum;
        }
        return maxSize;
    }
    
    public String toString() {
        StringBuilder out = new StringBuilder();
        toString(out, 0);
        return out.toString();        
    }
    
    public void toString(StringBuilder out, int printIndent) {
        StringBuilder title = new StringBuilder();
        for (int s = 0; s < printIndent; s++)
            title.append(" ");
        title.append("\"");
        title.append(getName());
        title.append("\" ");
        title.append(getClass().getSimpleName());
        title.append(" (");
        title.append(id);
        title.append(")");

        /*
         * Try twice to allocate enough storage to encode this Codec.
         * If we fail twice, forget the dump and append an error msg and
         * the individual field dump.
         */
        int storageSize = typicalMaximumSize() * 2;
        byte[] data     = new byte[storageSize];
        int dLen        = -1;
        try {
            dLen = encode(data, 0);
        } catch(ArrayIndexOutOfBoundsException e) {
            data = new byte[32767];
            try {
                dLen = encode(data, 0);
            } catch (ArrayIndexOutOfBoundsException e2) {
                dLen = -1;
                out.append(title.toString());
                out.append(": Error in toString() for this codec.");
                out.append(" Encode results in ArrayIndexOutOfBounds exception: (");
                out.append(e2.getMessage()).append(")\n"); 
            }
        }
        if (dLen >= 0) {
            ByteUtils.hexDump(out, title.toString(), 0, data, 0, dLen);
        }

        for (int i = 0; i < fields.size(); i++) {
            fields.get(i).toString(out, printIndent + 2);
        }
    }
    
    
}
