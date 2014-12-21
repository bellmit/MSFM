package com.cboe.externalIntegrationServices.msgCodec.useCase;

import com.cboe.externalIntegrationServices.msgCodec.AbstractDataBuffer;

public class MyZeroSizeBuffer extends AbstractDataBuffer implements CodecConstants {

    public MyZeroSizeBuffer() {
        /*
         * BESTPRACTICE A zero sized buffer is bad, don't do it.
         */
        super(MyZeroSizeBufferID, 0);
    }

    protected void decodeHeader() {
    }

    protected void encodeHeader() {
    }

    public AbstractDataBuffer getDuplicateInstance() {
        return null;
    }
}