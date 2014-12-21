package com.cboe.externalIntegrationServices.msgCodec.useCase.tests.rollout;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.cboe.externalIntegrationServices.msgCodec.DataBufferBlock;
import com.cboe.externalIntegrationServices.msgCodec.useCase.CodecConstants;
import com.cboe.externalIntegrationServices.msgCodec.useCase.MyBuffer;
import com.cboe.externalIntegrationServices.msgCodec.useCase.MyCodec;
import com.cboe.externalIntegrationServices.msgCodec.useCase.MyCodec2;

/**
 * Write a version 2 buffer (the header is different) and read with an original
 * buffer (v1).
 * 
 * @author degreefc
 * 
 */
public class RolloutTests extends TestCase implements CodecConstants {

    public void test_WriteV2AndV1ReadV2() throws Exception {
        /*
         * This simulates a server that supports the old and new versions and a
         * client that only expects the new version.
         * 
         * BESTPRACTICE A newer versioned buffer must have the same ID as
         * previous versions of the same buffer.
         */
        MyBufferVersioned mbV2 = createBuffer();
        /*
         * Write this v2 buffer to the block
         */
        DataBufferBlock blockOut = new DataBufferBlock(4096);
        blockOut.write(mbV2);
        /*
         * Simulate the transmission to a client
         */
        DataBufferBlock blockIn = new DataBufferBlock(4096);
        blockIn.setStorage(blockOut.getStorage(), blockOut.getLength());
        /*
         * Notice that the old version of the buffer is what is being used for
         * the read. The difference in a buffer from one release to the next
         * will only be the codec used for the header information.
         */
        MyBufferVersioned mb = new MyBufferVersioned();
        blockIn.read(mb);
        /*
         * Read the new version of the header codec. The v2 buffer will have
         * written both versions to the header area. Old releases that are still
         * running will continue to look for the header they expect. In this
         * example it is not the message codec that has been versioned, it is
         * the header codec. Mostly this is behind the scenes and controlled in
         * the buffer's code. Access the header fields will take care of the
         * backward compatibility issues.
         */
        MyCodec type1Result = new MyCodec("myCodecInstance_result");
        int expectedValue = 1;
        /*
         * BESTPRACTICE Read returns a true if a codec was found, use
         * while(read...
         */
        while (mb.read(type1Result)) {

            assertEquals("header value", 1000, mb.getHeaderInt());
            assertEquals("header value", 10000, mb.getHeaderInt2());

            assertEquals("read1 1", expectedValue++, type1Result.getMyInt1());
            assertEquals("read1 2", expectedValue++, type1Result.getMyInt2());
        }
        assertEquals("expected number of codecs (values)", 7, expectedValue);
    }

    public void test_WriteV2ReadV2orV1() throws Exception {
        /*
         * This simulates a server that supports only the newest version and
         * a client that can handle either the new or the old.
         * 
         */
        MyBufferComplete mbV2 = createBufferComplete();
        /*
         * Write this v2 buffer to the block
         */
        DataBufferBlock blockOut = new DataBufferBlock(4096);
        blockOut.write(mbV2);
        /*
         * Simulate the transmission to a client
         */
        DataBufferBlock blockIn = new DataBufferBlock(4096);
        blockIn.setStorage(blockOut.getStorage(), blockOut.getLength());
        /*
         * Notice that the old version of the buffer is what is being used for
         * the read. The difference in a buffer from one release to the next
         * will only be the codec used for the header information.
         */
        MyBufferVersioned mb = new MyBufferVersioned();
        blockIn.read(mb);
        /*
         * Read the new version of the header codec. The v2 buffer will have
         * written both versions to the header area. Old releases that are still
         * running will continue to look for the header they expect. In this
         * example it is not the message codec that has been versioned, it is
         * the header codec. Mostly this is behind the scenes and controlled in
         * the buffer's code. Access the header fields will take care of the
         * backward compatibility issues.
         */
        MyCodec type1Result = new MyCodec("myCodecInstance_result");
        int expectedValue = 1;
        /*
         * BESTPRACTICE Read returns a true if a codec was found, use
         * while(read...
         */
        while (mb.read(type1Result)) {

            assertEquals("header value", 1000, mb.getHeaderInt());
            assertEquals("header value", 10000, mb.getHeaderInt2());

            assertEquals("read1 1", expectedValue++, type1Result.getMyInt1());
            assertEquals("read1 2", expectedValue++, type1Result.getMyInt2());
        }
        assertEquals("expected number of codecs (values)", 7, expectedValue);
    }

    public void test_WriteV2AndV1ReadV1() throws Exception {
        /*
         * This simulates a server that has rolled out a new version that is
         * backwards compatible and clients are still on the old release.
         * 
         * BESTPRACTICE A newer versioned buffer must have the same ID as
         * previous versions of the same buffer.
         */
        MyBufferVersioned mbV2 = createBuffer();
        /*
         * Write this v2 buffer to the block
         */
        DataBufferBlock blockOut = new DataBufferBlock(4096);
        blockOut.write(mbV2);
        /*
         * Simulate the transmission to a client
         */
        DataBufferBlock blockIn = new DataBufferBlock(4096);
        blockIn.setStorage(blockOut.getStorage(), blockOut.getLength());
        /*
         * Notice that the old version of the buffer is what is being used for
         * the read. The difference in a buffer from one release to the next
         * will only be the codec used for the header information.
         */
        MyBuffer mb = new MyBuffer();
        blockIn.read(mb);
        /*
         * Read the old version of the header codec. The v2 buffer will have
         * written both versions to the header area. Old releases that are still
         * running will continue to look for the header they expect. In this
         * example it is not the message codec that has been versioned, it is
         * the header codec. Mostly this is behind the scenes and controlled in
         * the buffer's code. Access the header fields will take care of the
         * backward compatibility issues.
         */
        MyCodec type1Result = new MyCodec("myCodecInstance_result");
        int expectedValue = 1;
        /*
         * BESTPRACTICE Read returns a true if a codec was found, use
         * while(read...
         */
        while (mb.read(type1Result)) {

            assertEquals("header value", 1000, mb.getHeaderInt());

            assertEquals("read1 1", expectedValue++, type1Result.getMyInt1());
            assertEquals("read1 2", expectedValue++, type1Result.getMyInt2());
        }
        assertEquals("expected number of codecs (values)", 7, expectedValue);
    }

    public void test_WriteV2ReadV1() throws Exception {
        /*
         * This simulates a client that hasn't rolled yet and the server no
         * longer supports the version it expects.
         */
        MyBufferComplete mbV2 = createBufferComplete();
        /*
         * Write this v2 buffer to the block
         */
        DataBufferBlock blockOut = new DataBufferBlock(4096);
        blockOut.write(mbV2);
        /*
         * Simulate the transmission to a client
         */
        DataBufferBlock blockIn = new DataBufferBlock(4096);
        blockIn.setStorage(blockOut.getStorage(), blockOut.getLength());
        /*
         * Notice that the old version of the buffer is what is being used for
         * the read. The difference in a buffer from one release to the next
         * will only be the codec used for the header information.
         */
        MyBuffer mb = new MyBuffer();

        try {
            blockIn.read(mb);
        } catch (IllegalArgumentException e) {
            assertEquals("error msg", "Could not find header with codec Id = 3", e.getMessage());
            return;
        }
        Assert.fail("error expected");
    }

    public void test_WriteV1ReadV2() throws Exception {
        /*
         * This simulates a client that only expects the new version while
         * the server is still only support the old version.
         */
        MyBuffer mbV1 = createBufferDeprecated();
        /*
         * Write this v1 buffer to the block
         */
        DataBufferBlock blockOut = new DataBufferBlock(4096);
        blockOut.write(mbV1);
        /*
         * Simulate the transmission to a client
         */
        DataBufferBlock blockIn = new DataBufferBlock(4096);
        blockIn.setStorage(blockOut.getStorage(), blockOut.getLength());
        /*
         * Notice that the old version of the buffer is what is being used for
         * the read. The difference in a buffer from one release to the next
         * will only be the codec used for the header information.
         */
        MyBufferComplete mb = new MyBufferComplete();

        try {
            blockIn.read(mb);
        } catch (IllegalArgumentException e) {
            assertEquals("error msg", "Could not find header with codec Id = 8", e.getMessage());
            return;
        }
        Assert.fail("error expected");
    }

    public void test_WriteV1ReadV2orV1() throws Exception {
        /*
         * This simulates a client that can handle both versions while
         * the server only supports the old version.
         */
        MyBuffer mbV1 = createBufferDeprecated();
        /*
         * Write this v1 buffer to the block
         */
        DataBufferBlock blockOut = new DataBufferBlock(4096);
        blockOut.write(mbV1);
        /*
         * Simulate the transmission to a client
         */
        DataBufferBlock blockIn = new DataBufferBlock(4096);
        blockIn.setStorage(blockOut.getStorage(), blockOut.getLength());

        MyBufferVersioned mb = new MyBufferVersioned();
        blockIn.read(mb);
        
        assertEquals("header value 1", 1000, mb.getHeaderInt());
        assertEquals("header value 2", 0, mb.getHeaderInt2());
    }

    private MyBuffer createBufferDeprecated() throws Exception {
        MyBuffer mb = new MyBuffer();
        mb.setHeaderInt(1000);

        MyCodec type1 = new MyCodec("type1");
        MyCodec2 type2 = new MyCodec2("type2");

        type2.setMyLong(1001);
        type2.setMyInt(1002);
        mb.write(type2);

        type1.setMyInt1(1);
        type1.setMyInt2(2);
        mb.write(type1);

        type1.setMyInt1(3);
        type1.setMyInt2(4);
        mb.write(type1);

        type2.setMyLong(1003);
        type2.setMyInt(1004);
        mb.write(type2);

        type1.setMyInt1(5);
        type1.setMyInt2(6);
        mb.write(type1);

        mb.rewind();

        return mb;
    }


    private MyBufferVersioned createBuffer() throws Exception {
        MyBufferVersioned mb = new MyBufferVersioned();
        mb.setHeaderInt(1000);
        mb.setHeaderInt2(10000);

        MyCodec type1 = new MyCodec("type1");
        MyCodec2 type2 = new MyCodec2("type2");

        type2.setMyLong(1001);
        type2.setMyInt(1002);
        mb.write(type2);

        type1.setMyInt1(1);
        type1.setMyInt2(2);
        mb.write(type1);

        type1.setMyInt1(3);
        type1.setMyInt2(4);
        mb.write(type1);

        type2.setMyLong(1003);
        type2.setMyInt(1004);
        mb.write(type2);

        type1.setMyInt1(5);
        type1.setMyInt2(6);
        mb.write(type1);

        mb.rewind();

        return mb;
    }

    private MyBufferComplete createBufferComplete() throws Exception {
        MyBufferComplete mb = new MyBufferComplete();
        mb.setHeaderInt(1000);
        mb.setHeaderInt2(10000);

        MyCodec type1 = new MyCodec("type1");
        MyCodec2 type2 = new MyCodec2("type2");

        type2.setMyLong(1001);
        type2.setMyInt(1002);
        mb.write(type2);

        type1.setMyInt1(1);
        type1.setMyInt2(2);
        mb.write(type1);

        type1.setMyInt1(3);
        type1.setMyInt2(4);
        mb.write(type1);

        type2.setMyLong(1003);
        type2.setMyInt(1004);
        mb.write(type2);

        type1.setMyInt1(5);
        type1.setMyInt2(6);
        mb.write(type1);

        mb.rewind();

        return mb;
    }
}
