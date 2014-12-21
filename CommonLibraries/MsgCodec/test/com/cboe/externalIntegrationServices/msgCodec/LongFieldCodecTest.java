/*
 * Created on Oct 14, 2008
 */
package com.cboe.externalIntegrationServices.msgCodec;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class LongFieldCodecTest extends CodecTester
{
    private LongFieldCodec      codec;
    
    public static void main(String[] args) {
        
        boolean doPerfTest = true;  // Make true to run perf test
        if (doPerfTest) {
            LongFieldCodecTest self = new LongFieldCodecTest();
            self.perfTest(50000000,5);
            return;
        }
        junit.textui.TestRunner.run(LongFieldCodecTest.class);
    }

    public static Test suite() {
        return new TestSuite(LongFieldCodecTest.class);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        codec   = new LongFieldCodec("TestLongCodec");
    }

    public void testEncodeDecode() {

        doEncodeDecodeTest(0,0,         0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00);
        doEncodeDecodeTest(1,4,         0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x01);
        doEncodeDecodeTest(10,0,        0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x0A);

        doEncodeDecodeTest(255,0,       0x00,0x00,0x00,0x00,0x00,0x00,0x00,0xFF);
        doEncodeDecodeTest(256,0,       0x00,0x00,0x00,0x00,0x00,0x00,0x01,0x00);
        doEncodeDecodeTest(-256,0,      0xFF,0xFF,0xFF,0xFF,0xFF,0xFF,0xFF,0x00);
        
        doEncodeDecodeTest(65535,0,     0x00,0x00,0x00,0x00,0x00,0x00,0xFF,0xFF);
        doEncodeDecodeTest(65536,0,     0x00,0x00,0x00,0x00,0x00,0x01,0x00,0x00);
        doEncodeDecodeTest(-65536,0,    0xFF,0xFF,0xFF,0xFF,0xFF,0xFF,0x00,0x00);

        // 2 ** 24
        long two24 = 1 << 24;
        doEncodeDecodeTest(two24-1,0,   0x00,0x00,0x00,0x00,0x00,0xFF,0xFF,0xFF);
        doEncodeDecodeTest(two24,0,     0x00,0x00,0x00,0x00,0x01,0x00,0x00,0x00);
        doEncodeDecodeTest(-two24,0,    0xFF,0xFF,0xFF,0xFF,0xFF,0x00,0x00,0x00);
        
        // 2 ** 32 
        long two32 = 1L << 32;
        doEncodeDecodeTest(two32-1L,0,  0x00,0x00,0x00,0x00,0xFF,0xFF,0xFF,0xFF);
        doEncodeDecodeTest(two32,0,     0x00,0x00,0x00,0x01,0x00,0x00,0x00,0x00);   
        
        // 2 ** 40
        long two40 = 1L << 40;
        doEncodeDecodeTest(two40-1L,0,  0x00,0x00,0x00,0xFF,0xFF,0xFF,0xFF,0xFF);
        doEncodeDecodeTest(two40,0,     0x00,0x00,0x01,0x00,0x00,0x00,0x00,0x00);   
        
        // 2 ** 48
        long two48 = 1L << 48;
        doEncodeDecodeTest(two48-1L,0,  0x00,0x00,0xFF,0xFF,0xFF,0xFF,0xFF,0xFF);
        doEncodeDecodeTest(two48,0,     0x00,0x01,0x00,0x00,0x00,0x00,0x00,0x00);   

        // 2 ** 48
        long two56 = 1L << 56;
        doEncodeDecodeTest(two56-1L,0,  0x00,0xFF,0xFF,0xFF,0xFF,0xFF,0xFF,0xFF);
        doEncodeDecodeTest(two56,0,     0x01,0x00,0x00,0x00,0x00,0x00,0x00,0x00);   

        // 2 ** 63 -1 and minus 2 ** 63
        long two63 = 1L << 63;          // really minus 2 ** 63
        doEncodeDecodeTest(two63-1,0,   0x7F,0xFF,0xFF,0xFF,0xFF,0xFF,0xFF,0xFF);
        doEncodeDecodeTest(two63,0,     0x80,0x00,0x00,0x00,0x00,0x00,0x00,0x00);   
        
        doEncodeDecodeTest(-1,0,        0xFF,0xFF,0xFF,0xFF,0xFF,0xFF,0xFF,0xFF);
    
    }

    private void doEncodeDecodeTest(long value,int startOffset,
                                    int... expectedBytes) {
        codec.setValue(value);
        encodeDecodeTest(codec, startOffset, expectedBytes);
        assertEquals("decoded value",value,codec.getValue());
    }

    public void testNewCopy() {
        LongFieldCodec   codec2;
        codec2 = (LongFieldCodec)codec.newCopy();
        assertNotSame(codec, codec2);
        assertEquals(0,codec.getValue());
    }

    public void testGetValueSetValue() {
        for(int i = 0; i < 100; i++) {
            codec.setValue(i);
            assertEquals(i,codec.getValue());
        }
    }
    
    protected ICodec getCodec() {
        return codec;
    }

    protected void perfTestSetValue(long timestamp, int iterationCount, byte[] storage) {
        codec.setValue(timestamp + iterationCount);
    }

}
