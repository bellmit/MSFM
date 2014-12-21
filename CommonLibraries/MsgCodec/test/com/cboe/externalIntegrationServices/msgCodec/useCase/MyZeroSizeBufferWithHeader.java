package com.cboe.externalIntegrationServices.msgCodec.useCase;

import java.io.IOException;

import com.cboe.externalIntegrationServices.msgCodec.AbstractDataBuffer;

public class MyZeroSizeBufferWithHeader extends AbstractDataBuffer implements CodecConstants {

    static private ThreadLocal<MyBufferHeader> headerCodec;
    static {
        headerCodec = new ThreadLocal<MyBufferHeader>() {
            protected MyBufferHeader initialValue() {
                return new MyBufferHeader("myBufferHeader");
            }
        };
    }

    public MyZeroSizeBufferWithHeader() {
        super(MyZeroSizeBufferWithHeaderID, 0);
    }

    public void setHeaderInt(int hint1) {
        headerCodec.get().setMyHeaderInt(hint1);
    }

    public int getHeaderInt() {
        return headerCodec.get().getMyHeaderInt();
    }

    protected void decodeHeader() throws IOException {
        read(headerCodec.get());
    }

    protected void encodeHeader() {
        write(headerCodec.get());
    }

    public AbstractDataBuffer getDuplicateInstance() {
        return null;
    }
}