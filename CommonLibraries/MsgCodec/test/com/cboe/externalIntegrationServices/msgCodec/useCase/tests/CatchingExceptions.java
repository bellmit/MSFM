package com.cboe.externalIntegrationServices.msgCodec.useCase.tests;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.cboe.externalIntegrationServices.msgCodec.useCase.CodecConstants;
import com.cboe.externalIntegrationServices.msgCodec.useCase.MyBuffer;
import com.cboe.externalIntegrationServices.msgCodec.useCase.MyBufferWithoutHeader;
import com.cboe.externalIntegrationServices.msgCodec.useCase.MyCodec2;
import com.cboe.externalIntegrationServices.msgCodec.useCase.MyZeroSizeBuffer;
import com.cboe.externalIntegrationServices.msgCodec.useCase.MyZeroSizeBufferWithHeader;

/**
 * 
 * @author degreefc
 * 
 */
public class CatchingExceptions extends TestCase implements CodecConstants {

    /**
     * A zero sized buffer will cause an exception on create.
     */
    public void test_WriteToAZeroSizedBufferWithoutHeader() {
        try {
            new MyZeroSizeBuffer();
            Assert.fail("ZeroSizeBuffer should have failed with an IllegalArgumentException");
        } catch (Exception e) {
            assertEquals("Expected exception", "IllegalArgumentException", e.getClass()
                    .getSimpleName());
        }
    }

    /**
     * A zero sized buffer will cause an exception on create.
     */
    public void test_WriteToAZeroSizedBufferWithHeader() {
        try {
            new MyZeroSizeBufferWithHeader();
            Assert.fail("ZeroSizeBuffer should have failed with an IllegalArgumentException");
        } catch (Exception e) {
            assertEquals("Expected exception", "IllegalArgumentException", e.getClass()
                    .getSimpleName());
        }
    }

    /**
     * Reading before a rewind causes an error.
     */
    public void test_ReadBeforeRewind() {
        MyBuffer mb = new MyBuffer();
        MyCodec2 type2 = new MyCodec2("type2");
        try {
            mb.read(type2);
            Assert
                    .fail("Read before rewind should have failed with an UnsupportedOperationException");
        } catch (Exception e) {
            assertEquals("Expected exception", "UnsupportedOperationException", e.getClass()
                    .getSimpleName());
        }
    }

    /**
     * Reading an empty buffer with unfilled header.
     */
    public void test_ReadAnEmptyBuffer() {
        MyBuffer mb = new MyBuffer();
        try {
            mb.rewind();
        } catch (Exception e) {
            assertEquals("Expected exception", "IllegalArgumentException", e.getClass()
                    .getSimpleName());
        }
    }

    /**
     * Reading an empty buffer with filled header.
     */
    public void test_ReadAnEmptyBufferWithUsedHeader() {
        MyBuffer mb = new MyBuffer();
        mb.setHeaderInt(1);
        try {
            mb.rewind();
        } catch (Exception e) {
            assertEquals("Expected exception", "IllegalArgumentException", e.getClass()
                    .getSimpleName());
        }
    }

    /**
     * Reading an empty buffer.
     */
    public void test_ReadAnEmptyBufferWithoutHeader() throws Exception {
        MyBufferWithoutHeader mb = new MyBufferWithoutHeader();
        MyCodec2 type2 = new MyCodec2("type2");
        mb.rewind();
        try {
            mb.read(type2);
        } catch (Exception e) {
            assertEquals("Expected exception", "UnsupportedOperationException", e.getClass()
                    .getSimpleName());
        }
    }

    public void test_ExceptionInEncodeHeader() {
        MyBuffer mb = new MyBuffer();
        /*
         * -1 is coded to cause an exception
         */
        mb.setHeaderInt(-1);
        MyCodec2 type2 = new MyCodec2("type2");
        type2.setMyInt(0);
        try {
            mb.write(type2);
            Assert.fail("expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            assertEquals("forced exception", "-1", e.getMessage());
        } catch (Exception e) {
            Assert.fail("Expected IndexOutOfBoundsException but got " + e.getMessage());
        }
    }

    public void test_ExceptionInEncodeHeaderField() {
        MyBuffer mb = new MyBuffer();
        /*
         * -3 is coded to cause an exception
         */
        mb.setHeaderInt(-3);
        MyCodec2 type2 = new MyCodec2("type2");
        type2.setMyInt(0);
        try {
            mb.write(type2);
            Assert.fail("expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            assertEquals("forced exception", "-3", e.getMessage());
        } catch (Exception e) {
            Assert.fail("Expected IndexOutOfBoundsException but got " + e.getMessage());
        }
    }

    public void test_ExceptionInDecodeHeader() {
        MyBuffer mb = new MyBuffer();
        /*
         * -2 is coded to cause an exception
         */
        mb.setHeaderInt(-2);
        MyCodec2 type2 = new MyCodec2("type2");
        type2.setMyInt(0);
        mb.write(type2);
        try {
            mb.rewind();
            Assert.fail("expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            assertEquals("forced exception", "-2", e.getMessage());
        } catch (Exception e) {
            Assert.fail("Expected IndexOutOfBoundsException but got " + e.getMessage());
        }
    }

    public void test_ExceptionInDecodeHeaderField() {
        MyBuffer mb = new MyBuffer();
        /*
         * -4 is coded to cause an exception
         */
        mb.setHeaderInt(-4);
        MyCodec2 type2 = new MyCodec2("type2");
        type2.setMyInt(0);
        mb.write(type2);
        try {
            mb.rewind();
            Assert.fail("expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            assertEquals("forced exception", "-4", e.getMessage());
        } catch (Exception e) {
            Assert.fail("Expected IndexOutOfBoundsException but got " + e.getMessage());
        }
    }

    public void test_ExceptionInEncodeCodecField() {
        MyBuffer mb = new MyBuffer();
        mb.setHeaderInt(0);
        MyCodec2 type2 = new MyCodec2("type2");
        try {
            /*
             * -3 is coded to cause an exception
             */
            type2.setMyInt(-3);
            Assert.fail("expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            assertEquals("forced exception", "-3", e.getMessage());
        } catch (Exception e) {
            Assert.fail("Expected IndexOutOfBoundsException but got " + e.getMessage());
        }
    }

    public void test_ExceptionInDecodeCodecField() throws Exception {
        MyBuffer mb = new MyBuffer();
        mb.setHeaderInt(0);
        MyCodec2 type2 = new MyCodec2("type2");
        /*
         * -4 is coded to cause an exception
         */
        type2.setMyInt(-4);
        mb.write(type2);
        mb.rewind();
        mb.read(type2);
        try {
            type2.getMyInt();
            Assert.fail("expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            assertEquals("forced exception", "-4", e.getMessage());
        } catch (Exception e) {
            Assert.fail("Expected IndexOutOfBoundsException but got " + e.getMessage());
        }
    }

    public void test_WriteReadNoRewind() {
        MyBuffer mb = new MyBuffer();
        mb.setHeaderInt(0);
        MyCodec2 type2 = new MyCodec2("type2");
        type2.setMyInt(0);
        mb.write(type2);
        try {
            mb.read(type2);
            Assert.fail("expected UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            return;
        } catch (Exception e) {
            Assert.fail("Expected UnsupportedOperationException but got " + e.getMessage());
        }
    }

}
