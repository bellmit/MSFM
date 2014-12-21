package com.cboe.externalIntegrationServices.msgCodec.useCase.tests.rollout;

import java.io.IOException;

import com.cboe.externalIntegrationServices.msgCodec.AbstractDataBuffer;
import com.cboe.externalIntegrationServices.msgCodec.useCase.CodecConstants;

/**
 * This class only supports version 2 of the header. Version 1 clients will be
 * unsupported if they don't upgrade.
 * 
 * @author degreefc
 * 
 */
public class MyBufferComplete extends AbstractDataBuffer implements CodecConstants {

    static private class VersionedHeader {
        private MyBufferHeaderV2 v2;
    }

    static private ThreadLocal<VersionedHeader> header;
    static {
        header = new ThreadLocal<VersionedHeader>() {
            protected VersionedHeader initialValue() {
                VersionedHeader vh = new VersionedHeader();
                vh.v2 = new MyBufferHeaderV2("header v2");
                return vh;
            }
        };
    }

    int                                         headerInt;
    int                                         headerInt2;

    public MyBufferComplete() {
        super(MyBufferID, 1024);
    }

    /**
     * Move the values from the header codec(s) into the parallel instance
     * variables. The codec may be valid only within the scope of this method.
     */
    protected void decodeHeader() throws IOException {
        VersionedHeader vh = header.get();
        if (!read(vh.v2))
            throw new IllegalArgumentException("Could not find header with codec Id = "
                    + vh.v2.getCodecId());
        setHeaderInt(vh.v2.getMyHeaderInt());
        setHeaderInt2(vh.v2.getMyHeaderInt2());
        return;
    }

    protected void encodeHeader() {
        VersionedHeader vh = header.get();
        vh.v2.setMyHeaderInt(getHeaderInt());
        vh.v2.setMyHeaderInt2(getHeaderInt2());
        write(vh.v2);
    }

    public AbstractDataBuffer getDuplicateInstance() {
        return null;
    }

    public int getHeaderInt2() {
        return headerInt2;
    }

    public void setHeaderInt2(int headerInt2) {
        this.headerInt2 = headerInt2;
    }

    public int getHeaderInt() {
        return headerInt;
    }

    public void setHeaderInt(int headerInt) {
        this.headerInt = headerInt;
    }
}