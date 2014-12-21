/*
 * Created on Oct 20, 2008
 */
package com.cboe.externalIntegrationServices.msgCodec;

import java.io.IOException;

/**
 * <pre>
 * This class manages a Block of AbstractDataBuffers.  Use this class to 
 * concatenate multiple buffers together so that they can be transmitted in
 * larger blocks more efficiently, and use this class to read through a set 
 * of concatenated buffers.
 * 
 * This class is not designed so that you can write buffers and 
 * then turn around and read them.  When using this class, use an instance
 * either for writing to buffers or for reading buffers from a byte array,
 * but not both.  You may get erroneous results if reads and writes are done.  
 * </pre>
 */
public class DataBufferBlock
{
    private static final int        SIZEOF_ID               = 2;
    private static final int        SIZEOF_LONG_BUFF_LEN    = 4;
    private static final int        SHORT_BUFF_LEN_LIMIT    = 32767;
    private static final int        SIZEOF_SHORT_BUFF_LEN   = 2;

    private static final int        BLOCK_LENGTH_OFFSET     = 0;
    private static final int        SIZEOF_BLOCK_LENGTH     = 4;
    private static final int        TIMESTAMP_OFFSET        = BLOCK_LENGTH_OFFSET + SIZEOF_BLOCK_LENGTH;
    private static final int        SIZEOF_TIMESTAMP        = 8;
    private static final int        SEQ_NUM_OFFSET          = TIMESTAMP_OFFSET + SIZEOF_TIMESTAMP; 
    private static final int        SIZEOF_SEQ_NUM          = 4;
    private static final int        FIRST_BUFFER_OFFSET     = SEQ_NUM_OFFSET + SIZEOF_SEQ_NUM;
    private static final int        SIZEOF_BLOCK_HEADER     = SIZEOF_BLOCK_LENGTH +
                                                              SIZEOF_TIMESTAMP +
                                                              SIZEOF_SEQ_NUM;

    // Min useful size must fit block header, and per-buffer header. 
    private static final int        MIN_SIZE   = SIZEOF_BLOCK_HEADER + SIZEOF_LONG_BUFF_LEN + 
                                                 SIZEOF_ID +    
                                                 AbstractDataBuffer.bufferOverheadSize() +
                                                 AbstractDataBuffer.codecOverheadSize();
    private byte[]  storage;                // the underlying array
    private int     writePosition;          // the index of next offset to write
    private int     readPosition;           // the index of next offset to read
    private int     endPosition;
    private long    timestamp;
    private int     seqNumber;
    
    public DataBufferBlock(int p_maxBlockSize) {
        if (p_maxBlockSize < MIN_SIZE)
            storage     = new byte[MIN_SIZE];
        else
            storage     = new byte[p_maxBlockSize];
        reset();
        rewind();
        endPosition     = p_maxBlockSize;
    }
    

    /**
     * Sets the underlying storage for the block and it's length. 
     * p_length should be passed to be the number of valid bytes in the storage 
     * array.
     * If p_length is > the size of the encoded blockLength, the block length
     * is decoded and writePosition is set to the blockLength.
     * The encoded blockLength is then checked against the passed-in p_length.
     * If the encoded blockLength is less than or equal to the given p_length,
     * the seq number and timestamp are decoded and the block is considered
     * to be "complete", meaning it is entirely contained within the storage
     * area, and it is safe to perform a read() operation to begin reading
     * buffers from it.
     * 
     * If you are using a Stream-oriented protocol to read blocks of data,
     * you MUST repeatedly call this method after every stream read() with the 
     * byte array you read data into and the byte count accumulated so far
     * from the stream for the current block.  You must then use isComplete() 
     * and compact() methods to process the data from the block. See the example 
     * code from the isComplete() method for more details on reading blocks with a 
     * Stream-oriented protocol.
     * 
     * If you are using a packet-oriented protocol and you know the byte array
     * you pass to this method will always contain a completed block, you can 
     * avoid the isComplete() check.  However, this assumes that your program's 
     * reading of data from the packet-oriented protocol is otherwise correct 
     * and that blocks of data always fit in a packet.  As a safety/sanity check,
     * you may wish to check isComplete() after every packet-oriented read 
     * and consider it an error if it returns false.
     * 
     * @param p_storage - Underlying byte array storage.
     * @param p_length - Number of valid bytes in the storage array.
     */
    public final void setStorage( byte[] p_storage,
                                  int    p_length) {
        storage         = p_storage;
        endPosition     = p_length;
        rewind();
        if (p_length >= SIZEOF_BLOCK_HEADER) {
            writePosition   = decodeInt(BLOCK_LENGTH_OFFSET);
            if (writePosition <= p_length) { // if (isComplete())
                seqNumber       = decodeInt(SEQ_NUM_OFFSET);
                timestamp       = decodeLong(TIMESTAMP_OFFSET);
            }
        }
        else
            /*
             * Technically, we should use a special "invalid" value here because 
             * writePosition is truly unknown since we cannot decode the 
             * block size. In this case, we need isComplete() to return false
             * if called, so the easiest way to make that happen is to 
             * make is endPosition + 1. This avoids a special invalid value 
             * check in isComplete().  
             */
            writePosition   = endPosition + 1;
    }

    /**
     * This method and compact() can be used to read blocks from a 
     * Stream-oriented protocol, (a TCP socket for example) or any situation 
     * where partial blocks may be read or when one read may contain multiple 
     * blocks. See the example usage below.
     * 
     * Returns true if the buffer is "complete".  A complete buffer is one in
     * which the encoded length is <= the length that was last set on a 
     * setStorage() method call.  If the buffer is not complete, you may not
     * read from it nor write to it.
     * 
     * Usage:
     *  In a stream-oriented reader, use this technique to handle partial and
     *  multiple blocks of data that may be read from an InputStream:
     *  
     *  DataBufferBlock block   = new DataBufferBlock(4096); 
     *  byte[] blockStorage     = new byte[1024];
     *  int remaining           = blockStorage.length;
     *  int lenSoFar            = 0;
     *  int bytesRead           = 0;
     *
     *  while((bytesRead = in.read(blockStorage,lenSoFar,remaining)) > 0) {
     *      lenSoFar   += bytesRead;
     *      remaining   = blockStorage.length - lenSoFar; 
     *      block.setStorage(blockStorage, lenSoFar);
     *      while(block.isComplete()) {
     *          while(block.read(buffer)) {
     *             doApplicationProcessing(buffer);
     *          }
     *          lenSoFar    = block.compact();
     *          remaining   = blockStorage.length - lenSoFar;
     *      }
     *  }
     * 
     * @return
     */
    public final boolean isComplete() {
        return writePosition <= endPosition; 
    }
    
    /**
     * This method and isComplete() can be used to read blocks from a 
     * Stream-oriented protocol, (a TCP socket for example) or any situation 
     * where partial blocks may be read or when one read may contain multiple 
     * blocks. See the example usage documented with the isComplete() method.
     * 
     * This method should be called after processing the Buffers from a complete 
     * block when using a Stream-oriented protocol. The bytes that follow 
     * the completed block currently at the front of the block's storage 
     * array are copied to the front of the block, over-writing the completed 
     * block. The copied bytes are then examined to determine if there is 
     * another completed block at the front of the storage array, and lengths
     * and positions are adjusted.
     * 
     * See the example code from the isComplete() method for how to use
     * this method.
     * 
     * @return
     */
    public final int compact() {
        byte[] sto = storage;
        int remaining = endPosition - writePosition;
        if (remaining > 0) {
            System.arraycopy(sto, writePosition, sto, 0, remaining);
        }
        setStorage(sto,remaining);
        return remaining;
    }
    
    /**
     * Encodes the block length, encodes the next available seq number, and
     * encodes the current time, and returns the internal storage array held
     * by this block.
     * 
     * Call this method every time just before you write the data to whatever
     * medium to which you are transmitting.
     * 
     * Use the getLength() method to determine how many bytes of the internal
     * storage are valid to write.
     * @return
     */
    public final byte[] getStorage() {
        return getStorage(seqNumber++,System.currentTimeMillis());
    }

    /**
     * Encodes the block length, encodes the given seq number, and encodes the
     * given timestamp and returns the internal storage array held by this block.
     * 
     * Call this method every time just before you write the data to whatever
     * medium to which you are transmitting, passing whatever seq number and
     * timestamp you want.
     * 
     * Use the getLength() method to determine how many bytes of the internal
     * storage are valid to write.
     * 
     * @param p_seqNum
     * @param p_timestamp
     * @return
     */
    public final byte[] getStorage(int p_seqNum, long p_timestamp) {
        encodeInt(BLOCK_LENGTH_OFFSET,writePosition);
        encodeInt(SEQ_NUM_OFFSET,p_seqNum);
        encodeLong(TIMESTAMP_OFFSET,p_timestamp);
        seqNumber   = p_seqNum;
        timestamp   = p_timestamp;
        return storage;
    }

    public final int getSeqNumber() {
        return seqNumber;
    }
    
    public final long getTimestamp() {
        return timestamp;
    }
    
    /**
     * Gets the number of bytes written to the BufferBlock.  Use this method
     * to determine how many bytes of the storage to transmit.
     * @return
     */
    public final int getLength() {
        return writePosition;
    }
    
    /**
     * IF there is space to fit the Buffer's contents inside the BufferBlock,
     * writes / appends the given Buffer to the BufferBlock. 
     * If there is unsufficient space in the block for the buffer contents,
     * returns FALSE.
     * 
     * Usage:
     *  To use this in your application, use the following technique in the 
     *  transmit / publisher Thread of your application.
     *  
     *  buffer = dequeueBufferFromOtherThread(IMMEDIATE_TIMEOUT);
     *  while (buffer == null ) {
     *      transmitBytes(block.getStorage(),0,block.getLength());
     *      block.reset();
     *      buffer = dequeueBufferFromOtherThread(INFINITE_TIMEOUT);
     *  }
     *  if (!block.write(buffer)) {
     *      transmitBytes(block.getStorage(),0,block.getLength());   
     *      block.reset();
     *      block.write(buffer);
     *  }
     *  
     * @param p_buff - A Buffer to write/append to the block storage.
     * @return true if successful, false if buffer does not fit in the remaining
     * space of the block.
     */
    public final boolean write(AbstractDataBuffer p_buff) {
        if (!isComplete()) {
            String msg = incompleteBufferMessage("write");
            throw new UnsupportedOperationException(msg);
        }
        /*
         * writePosition is where we start writing the length + buffId + data.
         * based on dataLength, the number of length bytes we encode is 2 or 4.
         * sizeOfEncodedLength() tells us how many length bytes there will be.
         * We do this to save space and to be able to pack more data into a
         * block when buffers are short in length.
         * 
         * Note the encoded length does NOT include itself nor the buffID. 
         * This is to avoid a self-reference (length cannot include itself 
         * because we don't know how many bytes is required to encode length 
         * unless we calculate the total length, which includes the length itself).
         */
        final int startPos      = writePosition;
        final int dataLength    = p_buff.length();
        final int sizeOfLen     = sizeOfEncodedLength(dataLength);
        final int sizeOfHeader  = sizeOfLen + SIZEOF_ID; 
        final int endBuffPos    = startPos + sizeOfHeader + dataLength;
        if (endBuffPos >= storage.length)
            return false;

        encodeBufferLength(startPos,dataLength,sizeOfLen);
        writeBuffId(p_buff.getBufferId(),startPos + sizeOfLen);
        p_buff.appendTo(storage,startPos + sizeOfHeader);
        writePosition   = endBuffPos;
        return true;
    }

    /**
     * <pre>
     * Reads the next occurrence of the given Buffer starting at the current
     * readPosition.
     * 
     * If the Buffer's bufferId is found in the Block, the following occurs:
     *  1)  The Buffer's storage is set to the same physical storage as a 
     *      region of this BufferBlock's physical storage.
     *      SEE NOTE BELOW; 
     *          This has implications for how your application must be written. 
     *  2)  The Buffer's header is decoded, which should cause instance variable
     *      attributes of the Buffer to be populated and accessible. 
     *  3)  The Buffer is rewound so it is prepared to read MsgCodecs.
     *  4)  true is returned by this method.
     * 
     * If the Buffer's bufferId is NOT found in the block, false is returned.
     * 
     * NOTE:
     *      If you need to pass the data from the Buffer that is populated by 
     *      this method in another Thread, you MUST make a duplicate instance 
     *      of the Buffer by invoking getDuplicateInstance() on it.  
     *      You then pass the duplicate to the other Thread.  
     *      
     *      DO NOT pass the Buffer you passed to this read() method to another 
     *      Thread, because that Buffer's physical storage area is shared with 
     *      the BufferBlock's physical storage, and that storage area is very 
     *      likely to be over-written when the next Block of data is processed.
     * 
     * Application Usage:
     *  To use this in your application, use a technique similar to one of 
     *  the following:
     *  
     * 1) If you fully process the entire contents of each Buffer in the same 
     *    Thread that receives the Block of data, you may do this:
     * 
     *      MyBuffer buff           = new MyBuffer();
     *      DataBufferBlock block   = new DataBufferBlock(MAX_SIZE);
     *      byte[]  storage         = block.getStorage();
     *      MyMsgCodec data         = MyMsgCodec.getInstance();
     *      
     *      while(true) {
     *          int bytesRead = readData(storage);
     *          block.setStorage(storage,bytesRead);
     *          while(block.read(buff)) {
     *              while(buff.read(data)) {
     *                  processingInThisThread(data);
     *              }
     *          }
     *      }
     *      
     *    Note that block and buffer are both re-used over and over again.
     *    No new objects are created using this technique, however, this is 
     *    single-Threaded which may limit performance scalability.
     * 
     * 2) If you need to process the incoming Buffers in another set of Threads,
     *    use this technique instead.
     *    
     *      MyBuffer buff           = new MyBuffer();
     *      DataBufferBlock block   = new DataBufferBlock(MAX_SIZE);
     *      byte[]  storage         = block.getStorage();
     *      MyMsgCodec data         = MyMsgCodec.getInstance();
     *      
     *      while(true) {
     *          int bytesRead = readData(storage);
     *          block.setStorage(storage,bytesRead);
     *          while(block.read(buff)) {
     *              MyBuffer dup = buff.getDuplicateInstance();
     *              Queue q = selectProcessingThreadQueue(dup.getRoutingKey());
     *              q.enqueue(dup);
     *          }
     *      }
     *      
     *      Note that block can be re-used over and over again, and one Buffer
     *      is used over and over again to walk through the Block.  However, 
     *      before queueing a Buffer to another Thread for processing, the 
     *      re-used Buffer must be duplicated so that a different instance is
     *      used.  If you do not do this, you may over-write the one Buffer
     *      while the Thread is processing it, with quite unpredictable
     *      results.
     * </pre>
     * @param p_buff
     * @return
     */
    public final boolean read(AbstractDataBuffer p_buff) throws IOException {
        if (!isComplete()) {
            String msg = incompleteBufferMessage("read");
            throw new UnsupportedOperationException(msg);
        }
        
        int         readPos     = readPosition;
        final int   lastBuffPos = writePosition;

        final int bufferId = p_buff.getBufferId();
        final byte b1 = (byte)(bufferId >> 8 & 0x00FF);
        final byte b2 = (byte)(bufferId & 0x00FF);
        int dataLength, dataPos;
        final byte[] stor = storage;
        while(readPos < lastBuffPos) {
            /*
             * Decode dataLength, figure out size of encoded length,
             * advance readPos to beyond length, and check storage for 
             * bufferId bytes.
             */
            dataLength  = decodeBufferLength(readPos);
            readPos     += sizeOfEncodedLength(dataLength);
            dataPos     = readPos + SIZEOF_ID;
            if (stor[readPos] == b1 && stor[readPos+1] == b2) {
                p_buff.setStorage(stor,dataPos,dataLength);
                readPosition = dataPos + dataLength;
                return true;
            }
            readPos += dataPos + dataLength;
        }
        return false;
    }

    /**
     * Gets the id of the next buffer within the block.  If no more buffers
     * are contained in the block, returns -1 to indicate end of block.  
     * @return
     */
    public final short nextBufferId() {
        if (!isComplete()) {
            String msg = incompleteBufferMessage("getNextBufferId");
            throw new UnsupportedOperationException(msg);
        }

        int readPos = readPosition;
        if (readPos < writePosition)
            // Position of buffId is based on how many bytes of encoded length we have.
            return (short)readBuffId(offsetAfterLength(readPos));
        else
            return -1;
    }

    /**
     * Skips over the Buffer at the current readPosition and advances the 
     * read position to the next Buffer in the Block.
     * Returns true if there is more Buffer data after skipping the current
     * one. Returns false if there is no more data in the Block. 
     * @return
     */
    public final boolean skip() {
        if (!isComplete()) {
            String msg = incompleteBufferMessage("skip");
            throw new UnsupportedOperationException(msg);
        }
        
        final int readPos     = readPosition;
        final int writePos    = writePosition;

        if (readPos < writePos) {
            final int dataLength = decodeBufferLength(readPos);
            int nextMsgPosition = readPos + sizeOfEncodedLength(dataLength) + 
                                  SIZEOF_ID +
                                  dataLength; 
                                   
            readPosition = nextMsgPosition;
            return true;
        }
        else 
            return false;
    }

    /**
     * Rewinds the BufferBlock to the beginning.
     * Use this method to rescan the Block repeatedly using different Buffers. 
     */
    public final void rewind() {
        readPosition = FIRST_BUFFER_OFFSET;
    }
    
    /**
     * Resets the write position to the beginning of the block, preparing it 
     * for re-use.  No bytes in the storage are modified, but the next write 
     * operation after calling this method will over-write any bytes in the 
     * BufferBlock's storage.
     * Use this method after transmitting the storage bytes in order to prepare
     * the Block for re-use.
     */
    public final void reset() {
        writePosition = FIRST_BUFFER_OFFSET;
        endPosition   = storage.length;
    }


    /**
     * Returns true if buffer is empty, I.E. has not been written to or has
     * just been reset.  This method is useful in publishers when you want to
     * know if a buffer contains data to be written or not.
     * @return
     */
    public final boolean isEmpty() {
        return writePosition == FIRST_BUFFER_OFFSET;
    }
    
    
    /**
     * Writes buffer id into storage at given offset.  buffId is encoded
     * as 2 byte unsigned short.
     * @param value
     * @param offset
     */
    private final void writeBuffId(int value,int offset) {
        storage[offset]     = (byte)(value >> 8 & 0x00FF);  // High byte
        storage[offset+1]   = (byte)(value & 0x00FF);       // Low byte
    }
    
    /**
     * Reads buffer id as 2 bytes of storage starting at offset treating them 
     * as an 2 byte unsigned short value, but returns the value in an int. 
     */
    private final int readBuffId(int offset) {
        return ((storage[offset] & 0xFF) << 8) |            // High byte 
                (storage[offset+1] & 0xFF);                 // Low byte
    }
    
    /**
     * Given a length, returns how many bytes are required to encode it.
     * @param len
     * @return - number of bytes required to encode length (SHORT_BUFF_LEN or LONG_BUFF_LEN)
     */
    private static final int sizeOfEncodedLength(int len) {
        if (len < SHORT_BUFF_LEN_LIMIT)
            return SIZEOF_SHORT_BUFF_LEN;
        else
            return SIZEOF_LONG_BUFF_LEN;
    }
    
    /**
     * Returns the offset of the bufferId that immediately follows the encoded
     * length bytes.  Input offset is assumed to be positioned on an encoded
     * length.  We examine the storage at the given offset, and if it's 
     * negative, it means the length is encoded as LONG_BUFF_LEN bytes, otherwise it's
     * SHORT_BUFF_LEN bytes.
     * @param offset
     * @return
     */
    private final int offsetAfterLength(final int offset) {
        if (storage[offset] >= 0) {
            return offset + SIZEOF_SHORT_BUFF_LEN;
        }
        else {
            return offset + SIZEOF_LONG_BUFF_LEN;
        }
    }
    /**
     * Encodes a length as SHORT_BUFF_LEN or LONG_BUFF_LEN bytes. Length is 
     * passed in, and sizeOfLen is passed in via a prior call to sizeOfEncodedLength().
     * If sizeOfLen is SHORT_BUFF_LEN, we store length as SHORT_BUFF_LEN with
     * sign bit turned off in first encoded length byte. 
     * If sizeOfLen is LONG_BUFF_LEN, we store length as 31 bits with sign bit 
     * on in first encoded byte to indicate we have a long length.
     * @param offset - Offset at which to begin writing
     * @param len - length value to write
     * @param sizeOfLen - number of bytes to write.
     */
    private final void encodeBufferLength(final int offset,final int len,final int sizeOfLen) {
        final byte[] sto = storage;
        if (sizeOfLen == SIZEOF_SHORT_BUFF_LEN) {
            sto[offset]     = (byte)(len >> 8 & 0x7F);  // High byte with sign bit OFF.
            sto[offset+1]   = (byte)(len      & 0xFF);  // Low byte
        }
        else {
            sto[offset+0] = (byte)(((len >> 24) & 0x7F) | 0x80);    // High byte with sign bit ON
            sto[offset+1] = (byte)((len >> 16)  & 0xFF);
            sto[offset+2] = (byte)((len >>  8)  & 0xFF);
            sto[offset+3] = (byte)((len)        & 0xFF);
        }
    }

    /**
     * Decodes length from storage at given offset.  If first byte is negative, 
     * it means length was encoded as 4 bytes with 31 bits and sign bit in 
     * first byte on (which is masked off). Otherwise it was encoded as 
     * SIZEOF_SHORT_BUFF_LEN with sign bit off in first encoded byte.
     * @param offset - Offset at which to begin decoding.
     * @return
     */
    private final int decodeBufferLength(int offset) {
        final byte[] sto = storage;
        byte b1 = sto[offset];
        int  len;
        if (b1 >= 0) {
            len = ((b1 & 0x7F) << 8) | (sto[offset+1] & 0xFF);
            return len;
        }
        else {
            len = (b1            & 0x0000007F) << 24 |
                  (sto[offset+1] & 0x000000FF) << 16 |
                  (sto[offset+2] & 0x000000FF) << 8  |
                  (sto[offset+3] & 0x000000FF);
            return len;
        }
    }

    /**
     * Decodes 4 bytes from storage[offset] as int value.  Used to encode
     * seq num and block length. 
     * @param offset
     * @return
     */
    private final int decodeInt(int offset) {
        final byte[] sto = storage;
        return  (sto[offset]   & 0x0000007F) << 24 |
                (sto[offset+1] & 0x000000FF) << 16 |
                (sto[offset+2] & 0x000000FF) << 8  |
                (sto[offset+3] & 0x000000FF);
    }
    
    /**
     * Encodes 4 byte int value into storage[offset]
     * Used to encode seq num and block length.
     * @param offset
     * @param value
     */
    private final void encodeInt(int offset, int value) {
        final byte[] sto = storage;
        sto[offset+0] = (byte)((value >> 24)  & 0x7F);   // High byte with sign bit OFF
        sto[offset+1] = (byte)((value >> 16)  & 0xFF);
        sto[offset+2] = (byte)((value >>  8)  & 0xFF);
        sto[offset+3] = (byte)((value)        & 0xFF);
    }

    /**
     * Decodes 8 bytes from storage[offset] into long value. Used to decode
     * block header timestamp.
     * @param startOffset
     * @return
     */
    private final long decodeLong(int startOffset) {
        final byte[] sto = this.storage;
        return  (((long)sto[startOffset+0] & 0xff) << 56) |
                (((long)sto[startOffset+1] & 0xff) << 48) |
                (((long)sto[startOffset+2] & 0xff) << 40) |
                (((long)sto[startOffset+3] & 0xff) << 32) |
                (((long)sto[startOffset+4] & 0xff) << 24) |
                (((long)sto[startOffset+5] & 0xff) << 16) |
                (((long)sto[startOffset+6] & 0xff) <<  8) |
                (((long)sto[startOffset+7] & 0xff) <<  0);
    }

    /**
     * Encodes 8 byte long value into storage[offset]
     * Used to encode block timestamp.
     * @param offset
     * @param value
     */
    private final void encodeLong(int startOffset,long value) {
        final byte[] sto = this.storage;
        int val;
        val = (int)((value >> 32) & 0xFFFFFFFF);
        sto[startOffset+0] = (byte)((val >> 24) & 0xFF);
        sto[startOffset+1] = (byte)((val >> 16) & 0xFF);
        sto[startOffset+2] = (byte)((val >>  8) & 0xFF);
        sto[startOffset+3] = (byte)((val) & 0xFF);

        val = (int)(value & 0xFFFFFFFF);
        sto[startOffset+4] = (byte)((val >> 24) & 0xFF);
        sto[startOffset+5] = (byte)((val >> 16) & 0xFF);
        sto[startOffset+6] = (byte)((val >>  8) & 0xFF);
        sto[startOffset+7] = (byte)((val) & 0xFF);
    }

    private String incompleteBufferMessage(String operation) {
        return "Cannot " + operation +  
                " DataBufferBlock. Buffer is not complete. endPosition=" + endPosition + 
                " writePosition=" + writePosition; 
    }
    
    public String toString() {
        /*
         * This is perhaps a little subtle...
         * When setStorage is called, 
         *  endPosition   == the specified length of the storage area or number of bytes read.
         *  writePosition == one of the following...
         *      IF endPosition >= sizeofBlockLength, writePosition == encodedLength
         *      IF endPosition <  sizeofBlockLength, writePosition = endPosition + 1
         *          (which causes isComplete() to return false).
         *      
         * So, when dumping the blocks, we want to dump only the portion of
         * the storage area that is the completed block.
         * If the block is incomplete, then we don't know how many bytes are 
         * valid, so we'll dump the entire block up to the length that was
         * specified on setStorage() (the endPosition).
         */
        int end = (isComplete()) ? writePosition : endPosition;
        return toString(0, end);
    }
    
    public final String toString(int p_start, int p_end) {
        if (storage == null) {
            return "Underlying storage is NULL";
        }
        if (storage.length == 0) {
           return "Underlying storage has 0 length";
        }
        if (p_start < 0) {
           return "Illegal start index: " + p_start;
        }
        if (p_end < p_start) {
           return "Start index (" + p_start + ") must be <= to end index (" + p_end + ")";
        }
        StringBuilder result = new StringBuilder();
        result.append("DataBufferBlock:");
        if (p_end > storage.length) {
           result.append(" End position is greater than storage length, truncating to storage length");
           p_end = storage.length;
        }
        result.append(" ReadPos: ").append(readPosition);
        result.append(" WritePos: ").append(writePosition);
        result.append(" EndPos: ").append(endPosition);
        result.append("\n");
        ByteUtils.hexDump(result, getClass().getSimpleName(), 0, storage,
                          p_start, p_end - p_start);
        return result.toString();
    }
    
    public final String logview() {
        int end = (isComplete()) ? writePosition : endPosition;
        StringBuilder result = new StringBuilder();
        ByteUtils.hexDump(result, getClass().getSimpleName(), 0, storage,
                          0, end);
        return result.toString();
    }

}
