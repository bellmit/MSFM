package com.cboe.externalIntegrationServices.msgCodec.useCase.tests;

import junit.framework.TestCase;

import com.cboe.externalIntegrationServices.msgCodec.AbstractDataBuffer;
import com.cboe.externalIntegrationServices.msgCodec.DataBufferBlock;
import com.cboe.externalIntegrationServices.msgCodec.ICodec;
import com.cboe.externalIntegrationServices.msgCodec.LongFieldCodec;
import com.cboe.externalIntegrationServices.msgCodec.MsgCodec;
import com.cboe.externalIntegrationServices.msgCodec.useCase.CodecConstants;

/**
 * These use cases do the simple cases of interacting with codecs, message
 * codecs, and buffers.
 * 
 * @author degreefc
 * 
 */
public class PingTest extends TestCase implements CodecConstants {

    static class PingBuffer extends AbstractDataBuffer implements
            CodecConstants {

        protected PingBuffer(short id, int size) {
            super(id, size);
        }

        protected void decodeHeader() {
        }

        protected void encodeHeader() {
        }

        public AbstractDataBuffer getDuplicateInstance() {
            return null;
        }

    }

    static class PingMessage extends MsgCodec implements CodecConstants {
        final private LongFieldCodec timestamp;

        public PingMessage(String p_name) {
            super(p_name, (short) 1);
            timestamp = new LongFieldCodec("pingdata");
            this.add(timestamp);
        }

        public ICodec newCopy() {
            return new PingMessage(getName());
        }

        public void setTimestamp(long ts) {
            this.timestamp.setValue(ts);
        }

        public long getTimestamp() {
            return this.timestamp.getValue();
        }

    }

    public void test_Ping() throws Exception {

        PingMessage msgOut = new PingMessage("OUT");
        msgOut.setTimestamp(123L);
        PingMessage msgIn = publishThenConsume(msgOut);
        assertEquals("timestamp", msgOut.getTimestamp(), msgIn.getTimestamp());
    }

    private PingMessage publishThenConsume(PingMessage msgOut) throws Exception {

        PingBuffer buf = new PingBuffer((short) 2, 100);
        buf.write(msgOut);
        DataBufferBlock blockOut = new DataBufferBlock(256);
        blockOut.write(buf);

        DataBufferBlock blockIn = new DataBufferBlock(0);
        blockIn.setStorage(blockOut.getStorage(), blockOut.getLength());

        System.out.println(blockIn);

        PingBuffer bufIn = new PingBuffer((short) 2, 100);
        assertTrue("read of buffer", blockIn.read(bufIn));

        System.out.println(bufIn);

        PingMessage msgIn = new PingMessage("IN");

        assertTrue("read of codec", bufIn.read(msgIn));

        System.out.println(msgIn);
        return msgIn;
    }
}
