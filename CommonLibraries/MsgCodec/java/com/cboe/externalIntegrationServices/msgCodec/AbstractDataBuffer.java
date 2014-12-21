/*
 * Created on Jun 27, 2008
 */
package com.cboe.externalIntegrationServices.msgCodec;

import java.io.IOException;

/**
 * <pre>
 * This class manages a Buffer into which structured application data can be 
 * stored as a byte array. The purpose of storing data this way is to facilitate 
 * transport of data across some medium such as a network connection, or 
 * written to disk, etc.  The contents of multiple Buffers may be concatenated 
 * together into "Buffer Blocks" (another Class) so that larger chunks of data can
 * be transmitted for efficiency.
 * 
 * The idea is to use this Buffer to store "correlated structured artifacts" 
 * that result from an application transaction where the application needs to
 * transmit that data to another process or several processes for further 
 * processing.
 * 
 * In particular, a problem we are trying to solve is to store different 
 * artifacts from a single transaction into a contiguous block of memory 
 * so that object creation is reduced, and so that only one "chunk" of data 
 * need be transmitted from the originating process.
 * 
 * A buffer has the following physical structure encoded in a byte array:
 * 
 *  headerLength
 *  headerCodecId
 *  headerData
 *  codec1Length
 *  codec1Id
 *  codec1Data
 *  codec2Length    [optional, if multiple different codecs stored in same buffer]
 *  codec2Id
 *  codec2Data
 *  codec1Length    [optional, if multiple occurrences of codec1 data is associated with this buffer]
 *  codec1Id
 *  codec1Data
 *
 * A buffer contains a header which is intended to store "key" or "routing data"
 * fields that are common to all of the data in the buffer.  I.E. product key
 * and productClassKey might be an example of the header fields.
 * 
 * After the header is stored the contents of one or more data elements 
 * implemented as a subclass of "MsgCodec" (encoder/decoder), which is another
 * Class in this utility package.  A MsgCodec is collection of fields 
 * (like a "message") that knows how to temporarily store values and knows
 * how to encode and decode itself into/from a Buffer.
 * A MsgCodec has an "id" which is like a msg type or structure type that 
 * uniquely identifies that MsgCodec. It is up to the application to manage 
 * Codec ids and Buffer ids, and to make a constants class or similar that can 
 * be used to identify them uniquely across the entire application.
 * 
 * When the application is ready to store data into the buffer, it first
 * sets the values of the MsgCodec's fields.  After the field values of the 
 * MsgCodec have been set, you then ask the buffer to write() the MsgCodec into 
 * the Buffer. This operation causes the MsgCodec to encode itself into the 
 * Buffer's underlying byte array storage. 
 * 
 * As an example of how this class and the MsgCodec classes are used, assume an 
 * application has a quote transaction that yields the following artifacts:
 *      Quote History data
 *      Book Depth data  
 *      Current Market data (if the quote hits the top of book) 
 * Assume also that all of the 3 kinds of data above have something in common
 * such productClassKey and productKey, and that this is used to route Buffers
 * internally inside the process to other Threads via a queue for eventual
 * transmission to a downstream application.
 *  
 * The way this could be done is as follows:
 * 1)   Write a "MarketDataBuffer" that extends this Buffer class and has the 
 *      instance variables:
 *              int productKey 
 *              int productClassKey 
 *      These values will also be encoded into the header of the Buffer, but 
 *      will also remain available as instance variables for fast retrieval to 
 *      avoid having to re-decode them from the Buffer. 
 * 2)   Write a MarketDataBufferHeaderCodec that extends MsgCodec that contains
 *      these fields:
 *              IntFieldCodec   productKey
 *              IntFieldCodec   productClassKey
 *      Instances of this class should be stored as a ThreadLocal to avoid 
 *      object creation and to facilitate unsynchronized usage across Threads.
 *      You re-use this class over and over to encode/decode the Buffer header.
 * 3)   In MarketDataBuffer, implement the abstract methods encodeHeader() / decodeHeader().  
 *      encodeHeader() Populates the MarketDataBufferHeaderCodec's fields, then 
 *                     invokes write(headerCodec);
 *      decodeHeader() Invokes read(headerCodec), then retrieves the 
 *                     MarketDataBufferHeaderCodec's fields 
 *      This writes / reads the productKey and productClassKey to/from the 
 *      header area of the buffer. 
 * 4)   Write the following MsgCodec Classes which extend MsgCodec:
 *      BookDepthCodec containing fields:       
 *              ByteFieldCodec      updateType 
 *              ByteFieldCodec      side
 *              PriceFieldCodec     price
 *              IntFieldCodec       volume
 *      QuoteHistoryCodec containing fields:    
 *              PriceFieldCodec     bidPrice
 *              PriceFieldCodec     askPrice
 *              IntFieldCodec       bidSize
 *              IntFieldCodec       askSize
 *              StringFieldCodec    userId
 *      CurrentMarketCodec containing fields:
 *              PriceFieldCodec     bestBidPrice
 *              PriceFieldCodec     bestAskPrice
 *              SequenceCodec(CurrMktVolCodec)  bidVolumes
 *              SequenceCodec(CurrMktVolCodec)  askVolumes
 *      CurrMktVolCodec containing fields:
 *              ByteFieldCodec      volType
 *              IntFieldCodec       volume
 *              BooleanFieldCodec   multipleParties
 *          
 * For each MsgCodec implementation above, write setXXX() and getXXX() methods
 * for each of the fields contained in the MsgCodec. Each setXXX(), getXXX()
 * stores or retrieves the value of the corresponding field.
 * 
 * To write the contents of a MsgCodec into the Buffer, access the MsgCodec
 * implementation above and invoke setXXX() methods to populate the structure.
 * When the fields have been populated with values, you say
 * buffer.write(MsgCodec) to store the values from the MsgCodec into 
 * byte-serialized format into the Buffer.
 * 
 * To read the contents of a MsgCodec from the buffer, you first call
 * buffer.read(MsgCodec) which loads the serialized byte values into the 
 * fields of the MsgCodec.  You then ask for the values using getXXX() methods
 * on the MsgCodec.
 * 
 * NOTE that MsgCodecs should be re-used over and over. DO NOT create a 
 * MsgCodec for each transaction; that would be terribly expensive.  Either 
 * access a ThreadLocal MsgCodec that you re-use over and over, or store the 
 * MsgCodec in instance variables of long-lived objects.  It is STRONGLY 
 * recommended you do NOT share the same MsgCodec across Threads, as this 
 * would require synchronization.  Nothing can prevent you from doing this, but
 * doing so would be very expensive, and doing so is regarded as a 
 * "worst possible practice".  
 * 
 * The usage pattern to write a MsgCodec should be:
 *  Retrieve a ThreadLocal copy once into a local or instance variable,
 *  Populate it's values using setXXX() methods,
 *  Then write it to the buffer.
 * The usage pattern to read a MsgCodec should be:
 *  Retrieve a ThreadLocal copy once into a local or instance variable,
 *  read it from the buffer.
 *  Retrieve the values from the MsgCodec via getXXX() methods.
 * 
 * By using MsgCodecs in this manner, you minimize object creation in your 
 * application, and you can "piggy-back" multiple different but related kinds 
 * of data onto the same object, rather than creating different objects for 
 * each kind of data.
 * </pre>      
 * @author hammj
 */
public abstract class AbstractDataBuffer
{
    private static final int        SIZEOF_HEADER_LENGTH = 2;
    private static final int        SIZEOF_LENGTH = 2;
    private static final int        SIZEOF_ID = 2;
    private static final int        SIZEOF_CODEC_HDR = SIZEOF_LENGTH + SIZEOF_ID;
    // Min useful size must fit headerLen + 1 codec + 1 field
    // I will assume the field to be at least 4 bytes.  If buffer holds < 4 bytes, 
    // this wastes a small amount of space
    private static final int        MIN_SIZE   = SIZEOF_HEADER_LENGTH + SIZEOF_CODEC_HDR + 4;
    
    private final short bufferId;
    private byte[]  storage;                // the underlying array
    private int     startBufferPosition;    // the index of the start of the buffer. 
    private int     writePosition;          // the index of next offset to write
    private int     readPosition;           // the index of next offset to read

    protected AbstractDataBuffer(short p_bufferId,int p_buffSize) {
        bufferId    = p_bufferId;
        if (p_buffSize < MIN_SIZE) 
            throw new IllegalArgumentException("Cannot create " + 
                                               this.getClass().getSimpleName() + 
                                               " with buffSize " + p_buffSize +
                                               ". Minimum size is " + MIN_SIZE);
        else {
            storage             = new byte[p_buffSize];
            // Other vars set to zero. No need to repeat that here.
        }
    }

    /**
     * Use this constructor when subclass wishes to create a Buffer with no
     * storage, as in the case when reading only.
     * @param p_bufferId
     * @param p_storage
     */
    protected AbstractDataBuffer(short p_bufferId,byte[] p_storage) {
        bufferId    = p_bufferId;
        storage     = p_storage;
    }
    
    /**
     * This method should encode data from instance variables in the subclass 
     * implementation that is stored in the header of the buffer.  To encode 
     * the header data, a HeaderMsgCodec that contains FieldCodecs matching the 
     * data types of the subclass' instance variables should be initialized and 
     * then written to the buffer using write(headerCodec).  To avoid object 
     * allocation of the HeaderMsgCodec for each Buffer instance, the implementor
     * should re-use a Thread local instance of the HeaderMsgCodec.
     */
    protected abstract void encodeHeader();
    /**
     * This method should decode header data from the buffer and store them into 
     * instance variables in the subclass implementation.  The decode the header
     * data, a HeaderMsgCodec that contains FieldCodecs matching the
     * data types of the subclass' instance variables should be used.  The
     * implementor should call read(headerCodec), then ask the headerCodec
     * for it's values and copy them into the instance variables of the subclass
     * implementation.  To avoid object allocation of the HeaderMsgCodec for
     * each Buffer instance, the implementor should re-use a a Thread local
     * instance of the HeaderMsgCodec.
     */
    protected abstract void decodeHeader() throws IOException;
    
    /**
     * This method should return a duplicate Instance of the current Buffer.
     * The duplicate Instance should be a different Buffer object that 
     * contains the same values as "this" Buffer, but it should NOT share
     * the same memory storage unless doing so is Thread safe.
     *  
     * If the duplicate object returned by this method is modified in any way, 
     * those modifications MUST NOT affect the current object, and vice-versa.
     * 
     * The subclass may choose to implement this method so that it's Buffers
     * are new instances, or it may choose to pool them.  That is entirely up
     * to the application's subclass implementation. 
     * 
     * The subclass implementation must copy it's instance variables from this
     * object to the newly created object. To copy the contents of the storage,
     * the subclass must invoke duplicate.copyContentsFrom(this) to copy the
     * contents of the storage array to the duplicate.
     * @return
     */
    public abstract AbstractDataBuffer getDuplicateInstance();
    
    /**
     * Writes the contents of the MsgCodec into the storage bytes backing this Buffer.
     * If writePosition == startBufferPosition, this means this buffer's header
     * has not been encoded, so invoke encodeHeader() first before encoding the
     * given codec.
     * NOTE Subtlety here:
     * If encodeHeader() is invoked, the only way available for the subclass
     * to encode it's header is to use a MsgCodec and invoke write(headerCodec).
     * This results in a recursive call to write (which we are already in).
     * However, before we call encodeHeader(), we advance the writePosition
     * to no longer be the startBufferPosition, so when encodeHeader() calls
     * write, we fall through the header encoding logic and write the
     * headerCodec normally.  Upon return from the recursive write() call,
     * we calculate the header length and store that in the buffer, and 
     * advance the writePosition to the position beyond the header.
     * Then we fall through to the normal MsgCodec write logic and we write
     * the outer-most codec we were originally called with.   
     * @param p_codec
     */
    public final void write(MsgCodec p_codec) {
        boolean encodeFailed;
        int encodeStart, encodeEnd = 0;
        encodeStart = writePosition;
        if (encodeStart == startBufferPosition) {
            writePosition += SIZEOF_LENGTH;
            encodeHeader();
            encodeEnd = writePosition;
            int headerLen = encodeEnd - encodeStart;
            writeShort(headerLen,encodeStart);
            encodeStart = writePosition;
        }
        /*
         * A Ping message will not have data - only a header.  This method
         * will be called with a null in that case.  The header is only
         * written as a by-product of calling this method.
         */
        if (p_codec != null)
            do {
                try {
                    /*
                     * Skip over header, tell codec to encode, store
                     * length and codec id.
                     */
                    encodeEnd       = p_codec.encode(storage,encodeStart + SIZEOF_CODEC_HDR);
                    writePosition   = encodeEnd;
                    encodeFailed    = false;
                    int codecLen    = encodeEnd - encodeStart;
                    writeShort(codecLen,encodeStart);
                    writeShort(p_codec.getCodecId(),encodeStart + SIZEOF_LENGTH);
                } catch (ArrayIndexOutOfBoundsException e) {
                    encodeFailed = true;
                    resizeStorage();
                }
            } while(encodeFailed);
    }

    /**
     * Reads the next occurrence of the given MsgCodec from storage starting
     * at the current readPosition.
     * If data for the given MsgCodec is found, the data is decoded into the
     * MsgCodec and true is returned.
     * If data for the given MsgCodec is not found, false is returned and 
     * the contents of the MsgCodec are not unmodified.
     * 
     * Before a read can occur, the buffer must be rewound or it's storage must
     * be set (storage can be set only via a BufferBlock).  If neither has
     * occurred, an UnsupportedOperationException is thrown.
     * 
     * @param p_codec - A MsgCodec to read from the Buffer.
     * @return true if data was found and decoded into the MsgCodec, false if
     * data for the given MsgCodec is not found.
     */
    public final boolean read(MsgCodec p_codec) throws IOException {
        int         readPos     = readPosition;
        final int   endPosition = writePosition;

        if (readPos == startBufferPosition) {
            throw new UnsupportedOperationException(this.getClass().getSimpleName() +
                        ": Cannot read MsgCodec " + p_codec.getClass().getSimpleName() + 
                        "(" + p_codec.getName() + 
                        ") from buffer. rewind() must be called before read can occur."); 
        }
        
        /*
         * Starting at readPosition, walk through codec data using lengths until 
         * we find data for a codecId matching this codec's Id.
         * If we find one, tell codec to decode it's data, then set readPosition
         * to the next codec data in the buffer and return true.
         * If we do not find a codec within the bounds of writePosition, return
         * false to indicate no more occurrences of that codec exists.
         * To get better performance and avoid repeated getField opcodes in the 
         * java compiled byteCode, copy instance var values we repeatedly access
         * into local final vars.  Also, to avoid decoding codecId over and 
         * over when scanning for it, break up passed-in codec's codecId into
         * 2 bytes and scan for those individually.  
         */
        final int codecId = p_codec.getCodecId();
        final byte b1 = (byte)(codecId >> 8 & 0x00FF);
        final byte b2 = (byte)(codecId & 0x00FF);
        int len, i;
        final byte[] stor = storage;
        while(readPos < endPosition) {
            len = readShort(readPos);
            
            if (len <= 0) {
                /*
                 * This most likely is a corrupted area of the file.  0 length is never valid.
                 */
                throw new IOException(this.getClass().getSimpleName() + ": reading an invalid length of "
                        + len + " at read position " + readPos + " in buffer");
            }
            
            i   = readPos + SIZEOF_LENGTH;
            if (stor[i] == b1 && stor[i+1] == b2) {
                p_codec.decode(stor,readPos + SIZEOF_CODEC_HDR);
                readPosition = readPos + len;
                return true;
            }
            readPos += len;
        }
        return false;
    }
    
    /**
     * Gets the id of the next MsgCodec within the Buffer.  If no more MsgCodecs
     * are stored in the Buffer, returns -1 to indicate none remain.
     * Use this method when you want to dynamically select the MsgCodec to read
     * based on the codecId.  
     * @return - CodecId of next codec or -1 to indicate end of Buffer.
     */
    public final short nextCodecId() {
        int readPos = readPosition;
        if (readPos == startBufferPosition) {
            throw new UnsupportedOperationException(this.getClass().getSimpleName() +
                        ": Cannot get nextCodecId() from buffer. " + 
                        "rewind() must be called before nextCodecId() can be called."); 
        }
        if (readPos < writePosition) 
            return (short)readShort(readPos + SIZEOF_LENGTH);
        else
            return -1;
    }
    
    /**
     * Skips over the MsgCodec at the current readPosition. Returns true if
     * there is more MsgCodec data after skipping the current one. Returns false
     * if there is no more data in the Buffer.
     * 
     * @return true when there is more data after this skip
     * @throws Exception when the skip did not position the pointer further into the buffer.
     */
    public final boolean skip() throws IOException {
        final int readPos     = readPosition;
        final int writePos    = writePosition;

        if (readPos == startBufferPosition) {
            throw new UnsupportedOperationException(this.getClass().getSimpleName() +
                        ": Cannot skip() in buffer. " + 
                        "rewind() must be called before skip() can be called."); 
        }

        if (readPos < writePos) {
            int nextMsgPosition = readPos + readShort(readPos);
            
            if (nextMsgPosition <= readPosition)
                /*
                 * This most likely is a corrupted area of the file.  Skipping nowhere, or backwards, is never
                 * a valid operation.
                 */
                throw new IOException(this.getClass().getSimpleName() + ": skipping an invalid length of "
                        + (nextMsgPosition - readPosition) + " at read position " + readPos + " in buffer");
          
            readPosition = nextMsgPosition;
            return true;
        } else
            return false;
       
    }
    
    /**
     * Rewinds the buffer to the beginning of the codec area (after the header),
     * and if the header has not been decoded, it is decoded.
     * Use this method to rescan the buffer for a different type of codec. 
     * This method is also called when setStorage is invoked via the BufferBlock.
     */
    public final void rewind() throws IOException {
        internalRewind(startBufferPosition, readPosition);
    }

    /**
     * Resets the writePosition to the beginning of the Buffer, preparing it 
     * for re-use. A subsequent call to write() will cause the header to be
     * encoded again, and the Buffer is over-written.
     * This method should be used if Buffers are pooled or re-used over and over.
     * Before each re-use, invoke this method.
     * This method does NOT need to be invoked if Buffer is not re-used.
     */
    public final void reset() {
        writePosition = startBufferPosition;
    }


    /**
     * <pre>
     * This method must be called from the subclass' implementation of it's
     * getDuplicateInstance() method. It should be called like this:
     * 
     *      duplicate.copyContentsFrom(this);
     * 
     * Upon entry to this method, our current "this" object is the duplicate, 
     * and the "this" argument passed by the caller is the other object from 
     * which we copy it's contents.
     * 
     * The subclass musts have created the duplicate Instance at some point, 
     * which means we have a storage area that is non-null. We check to see if 
     * the length of the other Buffer is > our storage, and if so, re-allocate 
     * our storage large enough to hold the other's length().  Then we copy 
     * length() bytes from the other into our buffer.
     * </pre>
     * @param p_other 
     */
    protected final void copyContentsFrom(AbstractDataBuffer p_other) {
        final int otherLen      = p_other.length();
        final int otherStart    = p_other.startBufferPosition;

        if (otherLen > storage.length) {
            storage = new byte[otherLen];
        }
        /*
         * Copy bytes from other Buffer's storage at startBufferPosition into 
         * this storage at offset 0.
         * Adjust endBufferPosition, read, and write Position to be similar
         * but relative to 0 offset vs startBufferPosition.
         */
        System.arraycopy(p_other.storage,
                         otherStart,
                         this.storage,
                         0,
                         otherLen);
        this.readPosition      = p_other.readPosition  - otherStart;
        this.writePosition     = p_other.writePosition - otherStart;
    }
    
    
    /**
     * Assigns the specified p_physicalStorage to this Buffer.
     * This method is used by the Buffer Block utility to prepare a Buffer for
     * reading data from a region of an underlying storage array.
     * The writePosition is set to the endBufferPosition, and the read 
     * position is reset to the startBufferPosition so that any call to read(codec) 
     * after invoking this method will cause the header to be decoded. 
     * NOTE: Package private on purpose. Not for public use.
     * @param p_physicalStorage The underlying array
     * @param p_startBufferPosition the offset of beginning of the buffer in the storage.
     * @param p_length the the number of valid bytes past startBufferPosition.
     */
    final void setStorage( byte[] p_physicalStorage,
                           int    p_startBufferPosition,
                           int    p_length) throws IOException {
        /*
         * Safeties are off on this for speed.  We will assume 
         * BufferBlock will take care of any checking and make sure it doesn't
         * set invalid storage, positions, or lengths.
         */
        storage             = p_physicalStorage;
        startBufferPosition = p_startBufferPosition;
        final int end       = p_startBufferPosition + p_length;
        writePosition       = end;
        readPosition        = p_startBufferPosition;
        internalRewind(p_startBufferPosition, p_startBufferPosition);        
    }

    final void appendTo(byte[] p_intoStorage,int startOffset) {
        System.arraycopy(storage,startBufferPosition,p_intoStorage,startOffset,length());
    }
    
    final int length() {
        return writePosition - startBufferPosition;
    }

    public final short getBufferId() {
        return bufferId;
    }
    
    /**
     * Used by MsgCodec.typicalMaximumSize() method to account for the overhead
     * of the codec header.
     * @return
     */
    protected static final int codecOverheadSize() {
        return SIZEOF_CODEC_HDR;
    }
    
    /**
     * Used below in estimatedMaxBufferSize(), but also may be used by Subclasses
     * if they deem necessary.
     * @return
     */
    protected static final int bufferOverheadSize() {
        return SIZEOF_HEADER_LENGTH;
    }
    

    /**
     * <pre>
     * This method should be used ** STATICALLY ** inside your Buffer 
     * implementation or Buffer Factory implementation to calculate a STATIC 
     * default estimated buffer size given the MsgCodecs you intend to place 
     * into the buffer.  
     * 
     * Pass the HeaderCodec for the Buffer and a list of MsgCodecs you intend 
     * to place into the Buffer. For those MsgCodecs that have Sequence 
     * elements, make sure you pre-create the Sequence to the most frequently 
     * occurring (99%+) size, otherwise this estimated calculation will be low 
     * and buffers may be resized frequently as they are used.
     *  
     * As a best practice, it is STRONGLY recommended you invoke this method 
     * ONCE in a static block of either the Buffer implementation or a Buffer 
     * Factory implementation and you store the calculated value in a static int 
     * variable.  
     * 
     * This method is EXPENSIVE, so DO NOT use it at runtime when creating 
     * Buffers on the fly. Invoking this method causes an array of MsgCodecs 
     * to be implicitly allocated, and each MsgCodec is iterated over and asked
     * for it's typicalMaxLength(), which in turn causes each MsgCodec to 
     * iterate over it's fields asking for the field's typicalMaxLength(). 
     * </pre>
     * @param msgCodecsToBePlacedInBuffer
     * @return
     */
    public static final int estimatedMaxBufferSize(MsgCodec... msgCodecsToBePlacedInBuffer) {
        int size = bufferOverheadSize();
        if (msgCodecsToBePlacedInBuffer != null) {
            for(int i = 0; i < msgCodecsToBePlacedInBuffer.length; i++) {
                size += msgCodecsToBePlacedInBuffer[i].typicalMaximumSize();
            }
        }
        size += 16; // A little extra for "fudge factor".
        return size;
    }
    
    /**
     * Parameterized version of rewind used internally.  This is done too
     * avoid repeated getField opcodes when rewind is called from setStorage().
     * @param startPos - startPosition instance var value.
     * @param readPos - readPosition instance var value.
     */
    private final void internalRewind(final int startPos,final int readPos) throws IOException {
        final int endHeaderPos  = startPos + readShort(startPos);
        /*
         * If buffer is positioned at startPosition, we decodeHeader() which 
         * will in turn call read() with a headerCodec to decode the header 
         * fields. When decoding a header, we need to limit the scan area
         * read() uses to just the header region.  To do this, we save off
         * writePosition and set it temporarily to the end of the header area,
         * then after decodeHeader(), we restore it.
         */
        if (readPos == startPos) {
            readPosition += SIZEOF_HEADER_LENGTH;
            final int saveWritePos = writePosition;
            writePosition = endHeaderPos;
            decodeHeader();
            writePosition = saveWritePos;
        }
        readPosition = endHeaderPos;
    }

    /**
     * Writes 2 lower bytes of int value into storage starting at offset. 
     * Data is written as an unsigned short. 
     */
    private final void writeShort(int value,int offset) {
        storage[offset]     = (byte)(value >> 8 & 0x00FF);  // High byte
        storage[offset+1]   = (byte)(value & 0x00FF);       // Low byte
    }
    
    /**
     * Reads 2 bytes of storage starting at offset treating them as an unsigned
     * short value, but returns the value in an int. 
     */
    private final int readShort(int offset) {
        return ((storage[offset] & 0xFF) << 8) |            // High byte 
                (storage[offset+1] & 0xFF);                 // Low byte
    }

    /**
     * Resizes the storage.
     * This method is called on a write operation if a codec indexes out 
     * of bounds while it's trying to encode it's data into bytes. 
     * During a write, we have a "safeties off" approach where we just 
     * assume the storage is large enough so we don't have to constantly 
     * check.  Java will check for us anyway, so we just catch the 
     * ArrayIndexOutOfBounds exception and if it happens, we resize the storage
     * and redo the encoding operation.
     * The application needs to be careful to make it's Buffer storage large
     * enough to handle it's 99.99% case so that this resizing does not happen,
     * because it is rather expensive if it does happen.
     */
    private final void resizeStorage() {
        int newSize = storage.length * 2;
        byte[] newStorage = new byte[newSize];
        System.arraycopy(storage, startBufferPosition, newStorage, startBufferPosition, storage.length);
        storage = newStorage;
    }
    
    public String toString() {
        return toString(startBufferPosition, writePosition);
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
        if (p_end > storage.length) {
           result.append("AbstractDataBuffer: End index is greater than storage length, truncating to storage length");
           p_end = storage.length;
        }
        ByteUtils.hexDump(result, getClass().getSimpleName() + " (" + bufferId + ")", 0, storage,
                          p_start, p_end - p_start);
        return result.toString();
    }
}
