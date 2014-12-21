package com.cboe.externalIntegrationServices.msgCodec.useCase.tests.rollout;

import java.io.IOException;

import com.cboe.externalIntegrationServices.msgCodec.AbstractDataBuffer;
import com.cboe.externalIntegrationServices.msgCodec.useCase.CodecConstants;
import com.cboe.externalIntegrationServices.msgCodec.useCase.MyBufferHeader;

public class MyBufferVersioned extends AbstractDataBuffer implements CodecConstants {

    /**
     * This inner class is a wrapper for the various versions that can be
     * managed by this buffer. It provides an efficiency of a single threadlocal
     * rather than a threadlocal per versioned header.
     */
    static private class VersionedHeader {
        private MyBufferHeader   v1;
        private MyBufferHeaderV2 v2;
    }

    static private ThreadLocal<VersionedHeader> header;
    static {
        header = new ThreadLocal<VersionedHeader>() {
            protected VersionedHeader initialValue() {
                VersionedHeader vh = new VersionedHeader();
                vh.v1 = new MyBufferHeader("header v1");
                vh.v2 = new MyBufferHeaderV2("header v2");
                return vh;
            }
        };
    }

    /*
     * BESTPRACTICE Store the header fields in instance variables during
     * decodeHeader()
     */
    int                                         headerInt;
    int                                         headerInt2;

    public MyBufferVersioned() {
        /*
         * BESTPRACTICE Even on subsequent versions the original buffer id must
         * not be changed. This allows backwards compatible clients to find the
         * buffer - otherwise it would not be decodable.
         */
        super(MyBufferID, 1024);
    }

    public void setHeaderInt(int hint1) {
        headerInt = hint1;
    }

    public int getHeaderInt() {
        return headerInt;
    }

    /**
     * Move the values from the header codec(s) into the parallel instance
     * variables. The codec may be valid only within the scope of this method.
     */
    protected void decodeHeader() throws IOException {
        VersionedHeader vh = header.get();
        /*
         * BESTPRACTICE Allow for any known version to be loaded. And only load
         * the best one, then stop.
         * 
         * BESTPRACTICE Decoding is easiest if encoding was ordered.
         */
        short codecId = 0;
        while ((codecId = nextCodecId()) != -1) {
            if (codecId == vh.v2.getCodecId()) {
                read(vh.v2);
                setHeaderInt(vh.v2.getMyHeaderInt());
                setHeaderInt2(vh.v2.getMyHeaderInt2());
                return;
            }
            if (codecId == vh.v1.getCodecId()) {
                read(vh.v1);
                setHeaderInt(vh.v1.getMyHeaderInt());
                return;
            }
            /*
             * You can only pass over the header codecs one time. No rewinding.
             */
            try {
                skip();
            } catch (Exception e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
        throw new IllegalArgumentException("missing v1 and v2 header in buffer");
    }

    protected void encodeHeader() {
        VersionedHeader vh = header.get();
        /*
         * BESTPRACTICE It is easier to decode if the latest released headers
         * are writen first to the buffer.
         */
        vh.v2.setMyHeaderInt(getHeaderInt());
        vh.v2.setMyHeaderInt2(getHeaderInt2());
        write(vh.v2);

        vh.v1.setMyHeaderInt(getHeaderInt());
        write(vh.v1);
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
}