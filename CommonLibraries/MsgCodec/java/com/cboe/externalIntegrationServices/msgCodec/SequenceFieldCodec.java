/*
 * Created on Oct 2, 2008
 */
package com.cboe.externalIntegrationServices.msgCodec;

/**
 * A FieldCodec that implements a repeating Sequence (an array) of other ICodecs.
 * A Sequence may consist of "primitive" FieldCodecs or "structs" such as 
 * MsgCodecs.
 * 
 * To use a Sequence, create an instance of the Sequence supplying a  
 * 
 * @author hammj
 */
public class SequenceFieldCodec extends AbstractFieldCodec implements ICodec
{
    private static final int        DEFAULT_LENGTH  = 4;
    private int                     currentNumElements;
    private int                     maxNumElementsSoFar;
    private ICodec[]                elements;
    
    public SequenceFieldCodec(String p_name,int p_estimatedMaxNumElements,
                              ICodec p_elementTypeTemplateObject) {

        super(p_name,"SequenceOf(" + p_elementTypeTemplateObject.getClass().getSimpleName() + ")");
        int len = p_estimatedMaxNumElements;
        if (len > Short.MAX_VALUE) {
            throw new ArrayIndexOutOfBoundsException("Cannot create Sequence: " +
                    getFieldName() + " with estimated max num elements " + p_estimatedMaxNumElements + 
                    ". Estimated max num elements must be less or equal to " + 
                    Short.MAX_VALUE);
            
        }
        if (len <= 0)
            len = DEFAULT_LENGTH;
        elements       = new ICodec[len];
        elements[0]    = p_elementTypeTemplateObject;
        if (p_estimatedMaxNumElements > 0)
            copyElementsThroughIndex(p_estimatedMaxNumElements-1);
        else
            maxNumElementsSoFar  = 1;
    }

    public int decode(byte[] storage, int startOffset) {
        int nextOffset = decodeNumElements(storage, startOffset);
        final int numElems = currentNumElements;
        final ICodec[] elems = elements;

        for(int i = 0; i < numElems; i++) {
            nextOffset = elems[i].decode(storage, nextOffset); 
        }
        return nextOffset;
    }

    public int encode(byte[] storage, int startOffset) {
        int nextOffset = encodeNumElements(storage, startOffset);
        final int numElems = currentNumElements;
        final ICodec[] elems = elements;

        for(int i = 0; i < numElems; i++) {
            nextOffset = elems[i].encode(storage, nextOffset); 
        }

        return nextOffset;
    }
    
    public int getNumElements() {
        return currentNumElements;
    }
    /**
     * Gets the ICodec at the given index.
     * Expands size of internal array if necessary, adjusts current length,
     * and creates duplicates of the ICodecs for elements 0 through p_index
     * if necessary.
     * @param p_index
     * @return
     */
    public ICodec getElement(int p_index) {
        final int len = p_index + 1;

        rangeCheckAndInsureCapacity(p_index,len);
        
        if (len > currentNumElements)
            currentNumElements = len;

        ICodec currentElement = elements[p_index];
        if (currentElement == null) {
            copyElementsThroughIndex(p_index);
            currentElement = elements[p_index];
        }
        return currentElement;
    }

    private final void rangeCheckAndInsureCapacity(int p_index,int minCapacity) {
        if (p_index < 0) {
            throw new ArrayIndexOutOfBoundsException("Cannot index Sequence: " + 
                            getFieldName() + "[" + p_index + "]. Index cannot be negative");
        }
        if (minCapacity > elements.length) {
            if (minCapacity > Short.MAX_VALUE) {
                throw new ArrayIndexOutOfBoundsException("Cannot index Sequence: " +
                            getFieldName() + "[" + p_index + "]. Index must be less than " + 
                            Short.MAX_VALUE);
            }
            int newSize = (elements.length * 3)/2 + 1; // 1.5 times + 1
            if (newSize > Short.MAX_VALUE)
                newSize = Short.MAX_VALUE;

            if (newSize < minCapacity)
                newSize = minCapacity;
                
            final ICodec[] newArray = new ICodec[newSize];
            System.arraycopy(elements, 0, newArray, 0, elements.length);
            elements = newArray;
        }
    }

    private final void copyElementsThroughIndex(int p_index) {
        ICodec templateFactoryCodec = elements[0];
        for(int i = 1; i <= p_index; i++) {
            if (elements[i] == null)
                elements[i] = templateFactoryCodec.newCopy();
        }
        maxNumElementsSoFar = p_index + 1;
    }

    public ICodec newCopy() {
        return new SequenceFieldCodec(getFieldName(),maxNumElementsSoFar,elements[0].newCopy());
    }

    public void reset() {
        final ICodec[]  elems = elements;
        for(int i = 0; i < currentNumElements; i++) {
            elems[i].reset();
        }
        currentNumElements = 0;
    }

    public int typicalMaximumSize() {
        return elements[0].typicalMaximumSize() * maxNumElementsSoFar;
    }

    public void toString(StringBuilder out, int printIndent) {
        
        StringBuilder title = new StringBuilder();
        super.toString(title, printIndent);
        title.append("NumElems: ");
        title.append(currentNumElements);
        
        byte[] data = new byte[4];
        int len = encodeNumElements(data, 0);
        
        ByteUtils.hexDump(out, title.toString(), 0, data, 0, len);
        
        for(int i = 0; i < currentNumElements; i++) {
            elements[i].toString(out, printIndent + 2);
        }
    }
    
    /**
     * Encodes the numElements in compressed format using 1 or 2 bytes starting
     * at offset. Returns the offset after the encoded numElements.
     * 
     * numElements is limited to values 0 through 32767. Negative values are 
     * not allowed.
     * 
     * The sign bit of the first byte is used to indicate whether the numElements 
     * is encoded using 1 or 2 bytes.  
     * 
     * If the numElements is 0 to 127 inclusive, 1 byte is used to encode it.
     * The sign bit of the value will be off, indicating 1 byte encoding.
     * If the length is > 127, 2 bytes are used to encode the value, and the
     * sign bit of the first byte is turned on to indicate 2 byte encoding.  
     * @param offset - start offset to encode numElements.
     * @return offset after encoded numElements.
     */
    private final int encodeNumElements(byte[] storage,int offset) {
        final int len = currentNumElements;
        if (len <= 127) {
            storage[offset]       = (byte)(len & 0x7F);
            return offset + 1;
        }
        else {
            storage[offset]     = (byte)((len >> 8 & 0x7F) | 0x80); // High byte with sign bit.
            storage[offset+1]   = (byte)(len & 0x00FF);             // Low byte
            return offset + 2;
        }
    }

    /**
     * Decodes the numElements using the compressed-format mentioned above in 
     * encodeNumElements(). Returns the offset after the decoded value.
     * 
     * If byte at offset is >= 0, numElements was encoded as 1 byte, so just 
     * consume the value.
     * 
     * If byte at offset is negative, numElements was encoded with 2 bytes, so
     * strip sign bit of first byte, shift, and bitwise or-in the 2nd byte.
     * 
     * @param offset - start offset to decode numElements.
     * @return offset after decoded numElements.
     */
    private final int decodeNumElements(byte[] storage,int offset) {
        byte b1 = storage[offset];
        if (b1 >= 0) {
            currentNumElements = b1;
            return offset + 1;
        }
        else {
            byte b2 = storage[offset+1];
            int len = ((b1 & 0x7F) << 8) | (b2 & 0xFF);
            currentNumElements = len;
            return offset + 2;
        }
    }
}
