package com.cboe.externalIntegrationServices.msgCodec.useCase;

import java.io.IOException;

import com.cboe.externalIntegrationServices.msgCodec.AbstractDataBuffer;
import com.cboe.externalIntegrationServices.msgCodec.AsciiStringFieldCodec;
import com.cboe.externalIntegrationServices.msgCodec.ICodec;
import com.cboe.externalIntegrationServices.msgCodec.MsgCodec;

public class PingMessageBuffer extends AbstractDataBuffer implements CodecConstants {

    static class PingHeader extends MsgCodec implements CodecConstants {

        final private AsciiStringFieldCodec pingData;

        public PingHeader(String p_name) {
            super(p_name, PingHeaderID);
            pingData = new AsciiStringFieldCodec("pingData", 11);
            add(pingData);
        }

        public ICodec newCopy() {
            return new MyBufferHeader(getName());
        }
        public AsciiStringFieldCodec getPingData() {
            return pingData;
        }
    }
    
    
    static private ThreadLocal<PingHeader> header;
    static {
        header = new ThreadLocal<PingHeader>() {
            protected PingHeader initialValue() {
                return new PingHeader("pingHeader");
            }
        };
    }

    String pingData;
    
    /**
     * 10 is the minimum buffer size; otherwise an exception will occur.
     */
    public PingMessageBuffer() {
        super(PingBufferID, 10);
    }

    public void setPingData(String data) {
        pingData = data;
    }
    
    public String getPingData() {
        return pingData;
    }

    protected void decodeHeader() throws IOException {
        PingHeader ph = header.get();
        read(ph);
        pingData = ph.getPingData().getValue();
    }

    protected void encodeHeader() {
        PingHeader ph = header.get();
        ph.getPingData().setValue(pingData);
        write(ph);
    }

    public AbstractDataBuffer getDuplicateInstance() {
        return null;
    }
}