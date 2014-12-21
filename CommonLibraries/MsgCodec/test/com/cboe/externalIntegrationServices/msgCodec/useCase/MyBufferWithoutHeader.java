package com.cboe.externalIntegrationServices.msgCodec.useCase;

import com.cboe.externalIntegrationServices.msgCodec.AbstractDataBuffer;

public class MyBufferWithoutHeader extends AbstractDataBuffer implements CodecConstants {

    public MyBufferWithoutHeader() {
        super(MyBufferID, 1024);
    }

    protected void decodeHeader() {
    }

    protected void encodeHeader() {
    }
    
    public AbstractDataBuffer getDuplicateInstance() {
        return null;
    }
}