package com.cboe.externalIntegrationServices.msgCodec.useCase.tests;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.cboe.externalIntegrationServices.msgCodec.useCase.CodecConstants;
import com.cboe.externalIntegrationServices.msgCodec.useCase.MyBuffer;
import com.cboe.externalIntegrationServices.msgCodec.useCase.MyCodec;
import com.cboe.externalIntegrationServices.msgCodec.useCase.MyCodec2;

/**
 * These use cases do the simple cases of interacting with codecs, message
 * codecs, and buffers.
 * 
 * @author degreefc
 * 
 */
public class ReadingBuffers extends TestCase implements CodecConstants {

    /**
     * Create two types of codecs, write copies of both, read only one type,
     * rewind.
     */
    public void test_ReadOnlyType1CodecsFromBuffer() throws Exception {
        MyBuffer mb = createBuffer();
        /*
         * ...pzzzzzzzzap..... mb magically transmits and reappears ...
         */
        /*
         * Loop over all type1 codecs ignoring all others.
         */
        MyCodec type1Result = new MyCodec("myCodecInstance_result");
        int expectedValue = 1;
        /*
         * BESTPRACTICE Read returns a true if a codec was found, use
         * while(read...
         */
        while (mb.read(type1Result)) {
            assertEquals("read1 1", expectedValue++, type1Result.getMyInt1());
            assertEquals("read1 2", expectedValue++, type1Result.getMyInt2());
        }
        assertEquals("expected number of codecs (values)", 7, expectedValue);
    }

    public void test_ReadOnlyType2CodecsFromBuffer() throws Exception {
        MyBuffer mb = createBuffer();
        /*
         * BESTPRACTICE Rewind a buffer before subsequent scans.
         */
        mb.rewind();
        /*
         * Look over all type2 codecs ignoring all others.
         */
        MyCodec2 type2Result = new MyCodec2("myCodecInstance_result");
        int expectedValue = 1001;
        while (mb.read(type2Result)) {
            assertEquals("read2 1", expectedValue++, type2Result.getMyLong());
            assertEquals("read2 2", expectedValue++, type2Result.getMyInt());
        }
        assertEquals("expected number of codecs (values)", 1005, expectedValue);

    }

    public void test_ScanForCodecsInBufferByIDAndRead() throws Exception {
        MyBuffer mb = createBuffer();
        /*
         * Loop over all codecs by inspecting the id and reading appropriately.
         */
        MyCodec type1Result = new MyCodec("myCodecInstance_result");
        MyCodec2 type2Result = new MyCodec2("myCodecInstance_result");
        int expectedValue = 1;
        int expected2Value = 1001;
        int codecID = -1;
        /*
         * BESTPRACTICE Check the returned value of nextCodecId for -1, EOF 
         */
        while ((codecID = mb.nextCodecId()) != -1) {
            if (codecID == MyCodecID) {
                mb.read(type1Result);
                assertEquals("read1 1", expectedValue++, type1Result.getMyInt1());
                assertEquals("read1 2", expectedValue++, type1Result.getMyInt2());
            } else if (codecID == MyCodec2ID) {
                mb.read(type2Result);
                assertEquals("read2 1", expected2Value++, type2Result.getMyLong());
                assertEquals("read2 2", expected2Value++, type2Result.getMyInt());
            } else {
                Assert.fail("Unexpected codecID: " + codecID);
            }
        }
        assertEquals("expected number of codecs (values)", 7, expectedValue);
        assertEquals("expected number of codecs (values2)", 1005, expected2Value);

    }

    public void test_ScanForCodecsInBufferByIDAndSkipType1() throws Exception {
        MyBuffer mb = createBuffer();
        /*
         * Loop over all codecs by inspecting the id and skipping one type.
         */
        MyCodec2 type2Result = new MyCodec2("myCodecInstance_result");
        int expectedValue = 0;
        int expected2Value = 1001;
        int codecID;
        while ((codecID = mb.nextCodecId()) >= 0) {
            if (codecID == MyCodecID) {
                /*
                 * BESTPRACTICE nextCodecId does not advance the read pointer
                 * BESTPRACTICE use skip() or read() to advance the read pointer
                 */
                mb.skip();
                expectedValue++;
            } else if (codecID == MyCodec2ID) {
                mb.read(type2Result);
                assertEquals("read2 1", expected2Value++, type2Result.getMyLong());
                assertEquals("read2 2", expected2Value++, type2Result.getMyInt());
            } else {
                Assert.fail("Unexpected codecID: " + codecID);
            }
        }
        assertEquals("expected number of codecs (values)", 3, expectedValue);
        assertEquals("expected number of codecs (values2)", 1005, expected2Value);

    }

    public void test_ScanForCodecsInBufferByIDAndSkipType2() throws Exception {
        MyBuffer mb = createBuffer();
        /*
         * Loop over all codecs by inspecting the id and skipping the other
         * type.
         */
        MyCodec type1Result = new MyCodec("myCodecInstance_result");
        int expectedValue = 1;
        int expected2Value = 0;
        int codecID;
        while ((codecID = mb.nextCodecId()) >= 0) {
            if (codecID == MyCodecID) {
                mb.read(type1Result);
                assertEquals("read1 1", expectedValue++, type1Result.getMyInt1());
                assertEquals("read1 2", expectedValue++, type1Result.getMyInt2());
            } else if (codecID == MyCodec2ID) {
                mb.skip();
                expected2Value++;
            } else {
                Assert.fail("Unexpected codecID: " + codecID);
            }
        }
        assertEquals("expected number of codecs (values)", 7, expectedValue);
        assertEquals("expected number of codecs (values2)", 2, expected2Value);

    }

    public void test_ScanForCodecsInBufferByIDAndSkipEverything() throws Exception {
        MyBuffer mb = createBuffer();
        /*
         * Loop over all codecs by inspecting the id and skipping everything.
         */
        int expectedValue = 0;
        int expected2Value = 0;
        int codecID;
        while ((codecID = mb.nextCodecId()) >= 0) {
            if (codecID == MyCodecID) {
                mb.skip();
                expectedValue++;
            } else if (codecID == MyCodec2ID) {
                mb.skip();
                expected2Value++;
            } else {
                Assert.fail("Unexpected codecID: " + codecID);
            }
        }
        assertEquals("expected number of codecs (values)", 3, expectedValue);
        assertEquals("expected number of codecs (values2)", 2, expected2Value);

    }

    private MyBuffer createBuffer() throws Exception {
        MyBuffer mb = new MyBuffer();
        mb.setHeaderInt(3);

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
        /*
         * BESTPRACTICE Use rewind on a buffer if you intend to read after write
         */
        mb.rewind();

        return mb;
    }
}
