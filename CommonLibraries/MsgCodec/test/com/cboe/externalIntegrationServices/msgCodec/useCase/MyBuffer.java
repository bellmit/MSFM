package com.cboe.externalIntegrationServices.msgCodec.useCase;

import java.io.IOException;

import com.cboe.externalIntegrationServices.msgCodec.AbstractDataBuffer;

public class MyBuffer extends AbstractDataBuffer implements CodecConstants {
    /*
     * BESTPRACTICE Use thread locals to store the header codecs
     * 
     * There is only one header per buffer per thread. It could be created new
     * for each buffer but it is not. For efficiency a thread local holds on to
     * a reusable codec per thread.
     */
    static private ThreadLocal<MyBufferHeader> headerCodec;
    static {
        /*
         * BESTPRACTICE Initialize thread local header codecs in a static
         * initializer
         */
        headerCodec = new ThreadLocal<MyBufferHeader>() {
            protected MyBufferHeader initialValue() {
                return new MyBufferHeader("myBufferHeader");
            }
        };
    }

    int                                        headerInt;

    public MyBuffer() {
        /*
         * BESTPRACTICE Constuct a buffer to hold a reasonable sized storage
         */
        super(MyBufferID, 1024);
    }

    public void setHeaderInt(int hint1) {
        headerInt = hint1;
    }

    public int getHeaderInt() {
        return headerInt;
    }

    protected void decodeHeader() throws IOException {
        /*
         * BESTPRACTICE Take advantage of codec decoding for buffer headers
         */
        MyBufferHeader v1 = headerCodec.get();
        if (!read(v1))
            throw new IllegalArgumentException("Could not find header with codec Id = "
                    + v1.getCodecId());
        setHeaderInt(v1.getMyHeaderInt());
        /*
         * -2 must throw an exception for use case testing
         */
        if (-2 == getHeaderInt())
            throw new IndexOutOfBoundsException("-2");
    }

    protected void encodeHeader() {
        /*
         * BESTPRACTICE Take advantage of codec encoding for buffer headers
         * 
         * Use the "work" header codec to encode the instance variables. Then
         * write the codec to the buffer. The header codec is only valid within
         * the scope of this method.
         */
        /*
         * -1 must throw an exception for use case testing
         */
        if (-1 == getHeaderInt())
            throw new IndexOutOfBoundsException("-1");

        MyBufferHeader header = headerCodec.get();
        header.setMyHeaderInt(getHeaderInt());

        write(headerCodec.get());
    }

    public AbstractDataBuffer getDuplicateInstance() {
        return null;
    }
}