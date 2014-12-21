/*
 * Created on Oct 14, 2008
 */
package com.cboe.externalIntegrationServices.msgCodec;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class IntFieldCodecTest extends CodecTester
{
    private IntFieldCodec       codec;
    
    public static void main(String[] args) {

        boolean doPerfTest = true;  // Make true to run perf test
        if (doPerfTest) {
            IntFieldCodecTest self = new IntFieldCodecTest();
            self.perfTest(50000000,5);
            return;
        }
        
        junit.textui.TestRunner.run(IntFieldCodecTest.class);
    }

    public static Test suite() {
        return new TestSuite(IntFieldCodecTest.class);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        codec   = new IntFieldCodec("TestIntCodec");
    }

    public void testEncodeDecode() {

        doEncodeDecodeTest(0,0,         0x00,0x00,0x00,0x00);
        doEncodeDecodeTest(1,4,         0x00,0x00,0x00,0x01);
        doEncodeDecodeTest(10,0,        0x00,0x00,0x00,0x0A);

        doEncodeDecodeTest(255,0,       0x00,0x00,0x00,0xFF);
        doEncodeDecodeTest(256,0,       0x00,0x00,0x01,0x00);
        doEncodeDecodeTest(-256,0,      0xFF,0xFF,0xFF,0x00);
        
        doEncodeDecodeTest(65535,0,     0x00,0x00,0xFF,0xFF);
        doEncodeDecodeTest(65536,0,     0x00,0x01,0x00,0x00);
        doEncodeDecodeTest(-65536,0,    0xFF,0xFF,0x00,0x00);

        // 2 ** 24
        int two24 = 1 << 24;
        doEncodeDecodeTest(two24-1,0,   0x00,0xFF,0xFF,0xFF);
        doEncodeDecodeTest(two24,0,     0x01,0x00,0x00,0x00);
        doEncodeDecodeTest(-two24,0,    0xFF,0x00,0x00,0x00);
        
        // 2 ** 31 -1 and minus 2 ** 31
        int two31 = 1 << 31;            // really minus 2 ** 31
        doEncodeDecodeTest(two31-1,0,   0x7F,0xFF,0xFF,0xFF);
        doEncodeDecodeTest(two31,0,     0x80,0x00,0x00,0x00);   
        
        doEncodeDecodeTest(-1,0,        0xFF,0xFF,0xFF,0xFF);
    
    }

    private void doEncodeDecodeTest(int value,int startOffset,int... expectedBytes) {
        codec.setValue(value);
        encodeDecodeTest(codec, startOffset, expectedBytes);
        assertEquals("decoded value",value,codec.getValue());
    }

    public void testNewCopy() {
        IntFieldCodec   codec2;
        codec2 = (IntFieldCodec)codec.newCopy();
        assertNotSame(codec, codec2);
        assertEquals(0,codec.getValue());
    }

    public void testGetValueSetValue() {
        for(int i = 0; i < 100; i++) {
            codec.setValue(i);
            assertEquals(i,codec.getValue());
        }
    }
    
    public void testReset() {
        codec.setValue(10);
        assertEquals(10,codec.getValue());
        codec.reset();
        assertEquals(0,codec.getValue());
    }

    protected void perfTestSetValue(long timestamp,int iterationCount, byte[] storage) {
        codec.setValue(iterationCount);
    }

    protected ICodec getCodec() {
        return codec;
    }

    
}
