/*
 * Created on Oct 14, 2008
 */
package com.cboe.externalIntegrationServices.msgCodec;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ByteFieldCodecTest extends CodecTester
{
    private ByteFieldCodec       codec;
    
    public static void main(String[] args) {

        boolean doPerfTest = true;  // Make true to run perf test
        if (doPerfTest) {
            ByteFieldCodecTest self = new ByteFieldCodecTest();
            self.perfTest(50000000,5);
            return;
        }
        
        junit.textui.TestRunner.run(ByteFieldCodecTest.class);
    }

    public static Test suite() {
        return new TestSuite(ByteFieldCodecTest.class);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        codec   = new ByteFieldCodec("TestByteCodec");
    }

    public void testEncodeDecode() {

        doEncodeDecodeTest(0,0,         0x00);
        doEncodeDecodeTest(1,4,         0x01);
        doEncodeDecodeTest(10,0,        0x0A);

        doEncodeDecodeTest(127,0,       0x7F);
        doEncodeDecodeTest(-128,0,      0x80);
        doEncodeDecodeTest(-1,0,        0xFF);
    }

    private void doEncodeDecodeTest(int value,int startOffset,int... expectedBytes) {
        codec.setValue((byte)value);
        encodeDecodeTest(codec, startOffset, expectedBytes);
        assertEquals("decoded value",value,codec.getValue());
    }

    public void testNewCopy() {
        ByteFieldCodec   codec2;
        codec2 = (ByteFieldCodec)codec.newCopy();
        assertNotSame(codec, codec2);
        assertEquals(0,codec.getValue());
    }

    public void testGetValueSetValue() {
        for(byte i = 0; i < 100; i++) {
            codec.setValue(i);
            assertEquals(i,codec.getValue());
        }
    }
    
    public void testReset() {
        codec.setValue((byte)10);
        assertEquals(10,codec.getValue());
        codec.reset();
        assertEquals(0,codec.getValue());
    }

    protected void perfTestSetValue(long timestamp,int iterationCount, byte[] storage) {
        byte v = (byte)(iterationCount & 0x000000FF);
        codec.setValue(v);
    }

    protected ICodec getCodec() {
        return codec;
    }

    
}
