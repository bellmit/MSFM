/*
 * Created on Oct 2, 2008
 */
package com.cboe.externalIntegrationServices.msgCodec;

/**
 * A FieldCodec that implements a byte array field.
 * 
 * Byte array fields are limited to lengths of up to 32767 inclusive. Any 
 * attempt to assign a value longer than this throws a RuntimeException. 
 * Zero-length arrays are allowed, and null values are supported.
 * 
 * Byte values in the array may be 8 bit binary values. They are not limited
 * in the range of possible values for each byte.
 * 
 * Consider using this type of field when you have a sequence of single-byte 
 * data. If you avoid using the method getStringValue() on this class, you may 
 * be able to avoid object creation.  IF you use getStringValue() however, a 
 * new String is created each time the method is invoked.
 * 
 * If you need a String field that contains 7 bit ASCII bytes, consider using 
 * AsciiStringFieldCodec instead. That class, however, also creates new Strings 
 * when the value is asked for.  
 *  
 * If your String field contains a limited set of values (say a few hundred,
 * perhaps up to 1000), you may consider using LimitedValuesStringField, which
 * attempts to avoid object creation. 
 * 
 * @author hammj
 */
public class ByteArrayFieldCodec extends AbstractFieldCodec implements ICodec
{
    private final int               MAX_LENGTH          = 32767;
    private final int               NULL_VALUE_LENGTH   = -1;
    private final int               TYPICAL_MAX_SIZE;
    private byte[]                  valueStorage;
    private int                     length;
    private char[]                  valueAsChars;
    
    public ByteArrayFieldCodec(String p_name,int p_typicalMaxSize) {
        super(p_name,"byte[]");
        TYPICAL_MAX_SIZE = p_typicalMaxSize;
        valueStorage = new byte[p_typicalMaxSize * 2];
    }

    /**
     * Decodes length, re-allocates value storage if needed, copies data 
     * from storage into value.
     */
    public int decode(byte[] storage, int startOffset) {
        final int startData = decodeLength(storage,startOffset);
        final int len = length;
        if (len > 0) {
            if (len > valueStorage.length) {
                valueStorage = new byte[len * 2];
            }
            System.arraycopy(storage,startData,valueStorage,0,len);
            return startData + len;
        }
        else {
            return startData;
        }
    }

    /**
     * Encodes length and bytes into storage.
     */
    public int encode(byte[] storage, int startOffset) {
        final int startData = encodeLength(storage,startOffset);
        final int len = length;
        if (len > 0) {
            System.arraycopy(valueStorage,0,storage,startData,length);
            return startData + length;
        }
        else {
            return startData;
        }
    }
    

    /**
     * Returns the byte array value as a String.
     * A new String object and it's backing char array is created each time 
     * this method is called.  If you do not require a String, you may use
     * byte[] getValue() instead to return the storage array held by this class.
     * @return
     */
    public String getStringValue() {
        final int len       = length;
        if (len == NULL_VALUE_LENGTH) {
            return null;
        }
        char[] valChars = valueAsChars;
        if (valChars == null || valChars.length < valueStorage.length) {
            valChars = new char[valueStorage.length];
            valueAsChars = valChars;
        }
        final byte[] val    = valueStorage;
        for(int i = 0; i < len; i++) {
            valChars[i] = (char)val[i];
        }
        return new String(valChars,0,len);
    }
    
    /**
     * Returns the internal storage array owned by this field. If a null value
     * was decoded or was assigned to this field, null is returned.
     * 
     * BE CAREFUL HOW YOU USE THIS METHOD !!!
     * 
     * The array returned by this method is the storage array used to store
     * the value for this field.  In effect, you are given access to a private
     * variable held by this class. The returned byte array is NOT a copy of
     * the values.  If you need a copy, you MUST make it yourself.
     * 
     * To determine the number of valid bytes in the array, use the getLength()
     * method.  The array.length value is NOT the number of valid bytes; it
     * is the max size of the storage area.
     * 
     * If you modify the bytes in the array returned by this method, you will
     * corrupt the actual value of this field.
     * 
     * If you need the bytes returned by this method in another Thread, you 
     * MUST create a new object and copy the bytes returned here into that
     * new object. 
     * 
     * @return
     */
    public byte[] getValue() {
        if (length == NULL_VALUE_LENGTH) {
            return null;
        }
        return valueStorage;
    }
    
    /**
     * <pre>
     * Returns the internal storage array owned by this field, regardless of
     * the value that was last set or decoded. If a null value was last decoded 
     * or was last assigned to this field, THE VALUE STORAGE FOR THIS
     * FIELD IS RETURNED ANYWAY !!!
     * 
     * BE CAREFUL HOW YOU USE THIS METHOD !!!
     * 
     * The array returned by this method is the storage array used to store
     * the value for this field.  In effect, you are given access to a private
     * variable held by this class. The returned byte array is NOT a copy of
     * the values.  If you need a copy, you MUST make it yourself.
     * 
     * To determine the number of valid bytes in the array, use the getLength()
     * method.  The array.length value is NOT the number of valid bytes; it
     * is the max size of the storage area.
     * 
     * If you modify the bytes in the array returned by this method, you are 
     * in effect modifying the actual byte values of this field !!!
     * 
     * If you need the bytes returned by this method in another Thread, you 
     * MUST create a new object and copy the bytes returned here into that
     * new object.
     * 
     * The purpose and intended usage of this method is to avoid object
     * creation such as in this example:
     * 
     * Assume you need to encode "ClassSymbol" into a byte array which you will
     * transmit by setting the bytes of this field. Assume that ClassSymbol
     * has this method:   
     *      int getClassSymbol(byte[] copyIntoBytes);
     * that copies the internal bytes from the ClassSymbol into the specified 
     * byte array.
     * 
     * Assume you have a "smart" MsgCodec that holds a ByteArrayFieldCodec and
     * it has a method:
     *      setClassSymbol(ClassSymbol p_symbol);
     *      
     * This method could be coded as follows:
     * 
     *  void setClassSymbol(ClassSymbol p_sym) {
     *      byte[]  codecStorage = classSymbolByteArrayCodec.getValueStorage();
     *      int len = p_sym.getClassSymbol(codecStorage);
     *      classSymbolByteArrayCodec.setStorage(codecStorage,len);
     *  }
     *      
     * In the example above, we in-effect "steal" the internal storage from
     * classSymbolByteArrayCodec (which is a ByteArrayFieldCodec), we then give
     * that storage area to the ClassSymbol so IT copies the bytes from it's
     * internal storage into the given storage, then we "give back" the same
     * internal storage back to the classSymbolByteArrayCodec, and set it's
     * length.
     * 
     * By doing this, we avoid extra object creation and we avoid an extra array
     * copy of the symbol's bytes.
     * </pre>
     * @return
     */
    public byte[] getValueStorage() {
        return valueStorage;
    }
    
    /**
     * Copies the value of this field into the given storage, and returns the 
     * number of bytes copied (returns same value as getLength() method below).
     * If the storage passed is not large enough to store the entire value
     * of this field, no bytes are copied to the given storage array and the 
     * value -1 is returned to indicate no bytes were copied.
     * If the length of the value is zero, zero bytes are copied, and zero is
     * returned.
     * If the value is null, zero bytes are copied, and zero is returned.
     * Note that this method does not allow you to distinguish between a
     * null value and a zero-length value.  If you need to know the difference
     * between null and zero-length values, do not use this method. Use 
     * getValue() instead, or use getLength() and check for less than 0 to 
     * indicate a null value.
     * @param storage - Storage array into which bytes are copied.
     * @return - Number of bytes copied if storage is large enough to hold the
     * value of this field, otherwise, -1 to indicate storage is not large
     * enough to hold the bytes from this field, or zero to indicate empty
     * value or null value.
     */
    public int getValue(byte[] storage) {
        final int len = length;
        if (storage.length < len || len < 0)
            return -1;
        System.arraycopy(valueStorage,0,storage,0,length);
        return length;
    }
    
    /**
     * Returns the number of bytes decoded or whose value was last set.
     * If the value of the field is null, -1 is returned.
     * @return
     */
    public int getLength() {
        return length;
    }

    /**
     * Sets the storage array where the value of this field is stored to be 
     * the given array. This allows you to set the storage area for this field 
     * so that you may avoid a copy to assign the byte value held by this 
     * field.
     * 
     * The intended use is for when you re-use a byte array in your application
     * that you wish to repeatedly encode into a buffer.   Instead of copying
     * your byte array into this field's byte array and then encoding a Buffer
     * (which copies the bytes again into a Buffer), you may instead set the 
     * storage array held by this field to avoid the copy operation.
     * 
     * Be careful using this method !!!
     * There are side-effects which are subtle depending on usage.
     * For example, if you setStorage(), then decode a Buffer, then access
     * the storage you set without first calling byte[] getValue(), it is possible
     * that the value passed to setStorage() and the value returned by 
     * byte[] getValue() may not be the same object.  It depends on whether the
     * data decoded from the Buffer fit inside the value passed to setStorage.
     * 
     * @param p_value
     * @param p_len
     */
    public void setValueStorage(byte[] p_value,int p_len) {
        checkLength(p_len);
        if (p_value == null)
            length = NULL_VALUE_LENGTH;
        else {
            valueStorage   = p_value;
            length  = p_len;
        }
    }
    
    /**
     * Copies the data from p_value for p_len bytes into the internal storage 
     * array held by this field.  If necessary, the internal storage array
     * held by this field is resized. 
     * @param p_value
     * @param p_len
     */
    public void setValue(byte[] p_value,int p_len) {
        checkLength(p_len);
        if (p_value == null) {
            length = NULL_VALUE_LENGTH;
        }
        else {
            length = p_len;
            if (p_len > valueStorage.length) {
                valueStorage = new byte[p_len * 2];
            }
            System.arraycopy(p_value,0,valueStorage,0,p_len);
        }
    }

    /**
     * Copies the low-order byte from each character in the given String into 
     * the internal byte storage array held by this field. p_str.length()
     * bytes are copied, and if necessary, the internal storage array may be
     * resized to fit the bytes from the String.
     * 
     * This method does NOT use String.getBytes(), so no new objects are
     * created, however, Strings containing unicode characters whose value
     * exceeds 256 will be converted improperly.  This is safe, however, for
     * String values containing only US-ASCII characters.
     * @param p_str
     */
    public void setValue(String p_str) {
        if (p_str == null) {
            length = NULL_VALUE_LENGTH;
        }
        else {
            final int len       = p_str.length();
            checkLength(len);
            byte[] val          = valueStorage;
            length = len;
            if (len > valueStorage.length) {
                val = new byte[len * 2];
                valueStorage = val;
            }
            for(int i = 0; i < len; i++) {
                val[i] = (byte)p_str.charAt(i);
            }
        }
    }

    
    public ICodec newCopy() {
        return new ByteArrayFieldCodec(getFieldName(),TYPICAL_MAX_SIZE);
    }

    public void reset() {
        length = 0;
    }

    public int typicalMaximumSize() {
        return TYPICAL_MAX_SIZE;
    }

    public void toString(StringBuilder out, int printIndent) {
        
        StringBuilder title = new StringBuilder();
        super.toString(title, printIndent);
        title.append(getValue());
        
        byte[] data = new byte[valueStorage.length];
        int len = encode(data, 0);
        
        ByteUtils.hexDump(out, title.toString(), 0, data, 0, len);
    }
    
    /**
     * Encodes the length in compressed format using 1 or 2 bytes starting
     * at offset. Returns the offset after the encoded length.
     * 
     * Lengths are purposefully limited to values 0 through MAX_LENGTH,
     * but a length of -1 is used to represent a null value. 
     * 
     * The sign bit of the first byte is used to indicate whether the length 
     * is encoded using 1 or 2 bytes.  
     * 
     * If the length is < 0, this this indicates a null value, which is encoded 
     * as a special 2-byte "negative zero", 0x80 0x00.
     * 
     * If the length is >= 0 and <= 127, we use 1 byte to encode the length.  
     * We simply store the value, which will be a single byte whose sign is
     * positive.
     * 
     * If the length is > 127, we use 2 bytes to encode the length.
     * The high byte has it's sign bit turned on.
     * This allows lengths up to 32767.
     * @param offset - start offset to encode length.
     * @return offset after encoded length.
     */
    private final int encodeLength(byte[] storage,int offset) {
        final int len = length;
        if (len < 0) {
            storage[offset]     = (byte)0x80;
            storage[offset+1]   = 0x00;
            return offset + 2;
        }
        else if (len <= 127) {
            storage[offset]       = (byte)len;
            return offset + 1;
        }
        else {
            storage[offset]     = (byte)((len >> 8 & 0x7F) | 0x80); // High byte with sign bit.
            storage[offset+1]   = (byte)(len & 0x00FF);             // Low byte
            return offset + 2;
        }
    }

    /**
     * Decodes the length using the compressed-format mentioned above in 
     * encodeLength(). Returns the offset after the decoded length.
     * 
     * If length >= 0 and <= 127, 1 byte was used to encode value, and the 
     * single byte value is equal to the length.
     * 
     * If length is > 127 or < 0, 2 bytes were used to encode, and the sign 
     * bit of the first byte is on, resulting in a negative first byte value.
     * 
     * A null is represented by the bytes 0x80 and 0x00, which is like a 
     * "negative zero".  The first byte is negative to indicate 2 bytes were
     * used to encode the value, and the 2nd byte is the special value 0x00
     * which if the array was length == 0, would have been encoded as a 
     * single byte, so this is distinguishable from a zero-length byte array. 
     * @param offset - start offset to decode length.
     * @return offset after decoded length.
     */
    private final int decodeLength(byte[] storage,int offset) {
        byte b1 = storage[offset];
        if (b1 >= 0) {
            length = b1;
            return offset + 1;
        }
        else {
            byte b2 = storage[offset+1];
            int len = ((b1 & 0x7F) << 8) | (b2 & 0xFF);
            if (len == 0)
                len = -1;
            length = len;
            return offset + 2;
        }
    }
    
    private final void checkLength(int p_len) {
        if (p_len > MAX_LENGTH) {
            throw new IllegalArgumentException(getClass().getSimpleName() +
                        ": Cannot set byte value longer than " + MAX_LENGTH + " bytes.");
        }
    }
}
