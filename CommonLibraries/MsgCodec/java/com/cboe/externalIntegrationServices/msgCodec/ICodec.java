/*
 * Created on Oct 6, 2008
 */
package com.cboe.externalIntegrationServices.msgCodec;

/**
 * This is the interface for a MsgCodec and a FieldCodec. This allows
 * a MsgCodec and FieldCodec to be treated polymorphically so that a Msg may
 * contain sub-structures or groupings of fields which is itself also a Msg.
 * 
 * The methods on this class, though public, are not intended to be called 
 * directly by user applications.  This class is public only because it is 
 * necessary so that an application may write their own application-specific
 * fields and encode/decode them as they see fit (I.E. as with an application
 * specific Price field for example).
 * 
 * @author hammj
 */
public interface ICodec
{
    /**
     * Implementor should do the following:
     * <pre>
     * 1) Decode into self from byte[] storage starting at startOffset.
     *      While decoding, increment offset internally and "read" bytes 
     *      from the storage array.
     * 2) Return the offset immediately following the last byte read. 
     *      This avoids having to track a length seperately.
     *      Example: 
     *          if startOffset == 5 and 4 bytes are decoded, return value == 9  
     * NOTES: 
     *      Do NOT return a length, return the offset as described.
     *      Do NOT check array bounds when consuming bytes (for speed).  
     *      Bounds checking is handled via Exceptions and checks in the calling
     *      software.
     * </pre>      
     * @param storage - byte array where data to be decoded is stored.
     * @param startOffset - index into byte[] storage to begin decoding
     * @return - index into byte[] storage immediately following last decoded byte.
     */
    public int decode(byte[] storage, int startOffset);

    /**
     * Implementor should do the following:
     * <pre>
     * 1) Encode from self into byte[] storage starting at startOffset.
     *      While encoding, increment offset internally and write bytes into 
     *      storage array.
     * 2) Return the offset immediately following the last byte written.
     *      This avoids having to track a length seperately.
     * NOTES:
     *      Do NOT return a length, return the offset as described.
     *      Do NOT check array bounds when consuming bytes (for speed).  
     *      Bounds checking is handled via Exceptions and checks in the calling
     *      software.
     * </pre>      
     * @param storage - byte array where data to be encoded is stored.
     * @param startOffset - index into byte[] storage to begin encoding
     * @return - index into byte[] storage immediately following last encoded byte.
     */
    public int encode(byte[] storage, int startOffset);

    /**
     * Implementor should return a new copy of itself with internal value
     * reset to a default (null, zero, whatever "default" means to the ICodec).
     * @return - New ICodec instance of self.
     */
    public ICodec newCopy();
    
    /**
     * Implementor should reset it's value to some default.  Default value
     * may be null, zero, etc. depending on implementor.
     */
    public void reset();

    /**
     * @return Implementor should return the "typical" maximum size required
     * to store the field value as a byte count.  For ICodecs that implement
     * fixed-length encoding, return the fixed-length encoding. For ICodecs
     * that have variable length encodings, this method should return a maximum 
     * size that is "typical" for the application, I.E. the most frequently 
     * occurring. Generally, it is best if this covers 99% or more of all 
     * cases, because this method will be used to calculate a size for a buffer,
     * and if the size is under-estimated, buffers may be resized too 
     * frequently, which can hurt performance. 
     */
    public int typicalMaximumSize();

    /**
     * Pretty print the contents of the codec.
     * 
     * @param out destination of the printed output
     * @param printIndent the number of indented space characters on each new
     * line
     */
    public void toString(StringBuilder out, int printIndent);

}
