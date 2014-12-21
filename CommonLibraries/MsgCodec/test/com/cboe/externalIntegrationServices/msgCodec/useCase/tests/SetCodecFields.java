package com.cboe.externalIntegrationServices.msgCodec.useCase.tests;

import junit.framework.TestCase;

import com.cboe.externalIntegrationServices.msgCodec.useCase.CodecConstants;
import com.cboe.externalIntegrationServices.msgCodec.useCase.MyBuffer;
import com.cboe.externalIntegrationServices.msgCodec.useCase.MyCodec;
import com.cboe.externalIntegrationServices.msgCodec.useCase.MyTopLevelCodec;

/**
 * These use cases do the simple cases of interacting with codecs, message
 * codecs, and buffers.
 * 
 * @author degreefc
 * 
 */
public class SetCodecFields extends TestCase implements CodecConstants {

    public void test_SettingInts() {

        MyCodec mc = new MyCodec("myCodecInstance1");
        mc.setMyInt1(1);
        mc.setMyInt2(2);
        /*
         * It is not likely that you would interact with a codec in this
         * fashion. Encoding and decoded are usually hidden within the read /
         * write method calls on a buffer. But this is possible.
         */
        assertEquals("before write 1", 1, mc.getMyInt1());
        assertEquals("before write 2", 2, mc.getMyInt2());

        byte[] encoded = new byte[20];
        mc.encode(encoded, 0);

        MyCodec mc2 = new MyCodec("myCodecInstance2");
        mc2.decode(encoded, 0);

        assertEquals("after read 1", 1, mc2.getMyInt1());
        assertEquals("after read 2", 2, mc2.getMyInt2());
    }

    public void test_SimpleCodecInBuffer() throws Exception {
        /*
         * A buffer to be used for the duration of the thread.
         */
        MyBuffer mb = new MyBuffer();
        /*
         * Fill in the header information before any write messages.
         */
        mb.setHeaderInt(3);
        /*
         * A message codec that is a reusable instance but not thread safe.
         */
        MyCodec mc = new MyCodec("myCodecInstance1");
        mc.setMyInt1(1);
        mc.setMyInt2(2);
        /*
         * Encode the message coded into the buffer.
         */
        mb.write(mc);
        mb.rewind();

        // System.out.println(mb.toString());
        // System.out.println(mc.toString());
        /*
         * ...pzzzzzzzzap..... mb magically transmits and reappears ...
         */
        MyCodec mc2 = new MyCodec("myCodecInstance2");
        assertTrue(mb.read(mc2));

        assertEquals("after read 1", 1, mc2.getMyInt1());
        assertEquals("after read 2", 2, mc2.getMyInt2());
    }

    public void test_bufferWithMixedCodecs() throws Exception {
        /*
         * A buffer to be used for the duration of the thread.
         */
        MyBuffer mb = new MyBuffer();
        /*
         * Fill in the header information before any write messages.
         */
        mb.setHeaderInt(3);
        /*
         * A message codec that is a reusable instance but not thread safe.
         */
        MyCodec mca = new MyCodec("myCodecInstance_a");
        mca.setMyInt1(1);
        mca.setMyInt2(2);
        /*
         * A message codec that is a reusable instance but not thread safe.
         */
        MyCodec mcb = new MyCodec("myCodecInstance_b");
        mcb.setMyInt1(3);
        mcb.setMyInt2(4);
        /*
         * Encode the message coded into the buffer.
         */
        mb.write(mca);
        mb.write(mcb);
        
        mb.rewind();
        
        // System.out.println(mb.toString());
        // System.out.println(mca.toString());
        // System.out.println(mcb.toString());
        /*
         * ...pzzzzzzzzap..... mb magically transmits and reappears ...
         */
        MyCodec mcR = new MyCodec("myCodecInstance_result");
        assertTrue(mb.read(mcR));
        assertEquals("after read 1", 1, mcR.getMyInt1());
        assertEquals("after read 2", 2, mcR.getMyInt2());
        assertTrue(mb.read(mcR));
        assertEquals("after read 3", 3, mcR.getMyInt1());
        assertEquals("after read 4", 4, mcR.getMyInt2());
        /*
         * BESTPRACTICE Check for "false" on buffer read for missing codecs.
         */
        assertFalse("End of buffer expected", mb.read(mcR));
    }

    public void test_EmbeddedCodecInBuffer() throws Exception {
        /*
         * A buffer to be used for the duration of the thread.
         */
        MyBuffer mb = new MyBuffer();
        /*
         * Fill in the header information before any write messages.
         */
        mb.setHeaderInt(3);
        /*
         * A message codec that is a reusable instance but not thread safe.
         */
        MyTopLevelCodec topLevel = new MyTopLevelCodec("myTopLevelCodec");
        topLevel.setMyLong(1023);
        topLevel.getEmbeddedCodec().setMyInt1(5);
        topLevel.getEmbeddedCodec().setMyInt2(6);
        /*
         * Encode the message codec into the buffer.
         */
        mb.write(topLevel);

        mb.rewind();
        
        // System.out.println(mb.toString());
        // System.out.println(topLevel.toString());
        /*
         * ...pzzzzzzzzap..... mb magically transmits and reappears ...
         */
        MyTopLevelCodec mcR = new MyTopLevelCodec("myCodecInstance_result");
        assertTrue(mb.read(mcR));
        assertEquals("after read 1", 1023, mcR.getMyLong());
        assertEquals("after read 2", 5, mcR.getEmbeddedCodec().getMyInt1());
        assertEquals("after read 2", 6, mcR.getEmbeddedCodec().getMyInt2());
    }

}
