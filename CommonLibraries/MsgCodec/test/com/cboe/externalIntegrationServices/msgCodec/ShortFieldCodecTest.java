/*
 * Created on Oct 14, 2008
 */
package com.cboe.externalIntegrationServices.msgCodec;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ShortFieldCodecTest extends CodecTester
{
    private ShortFieldCodec       codec;
    
    public static void main(String[] args) {

        boolean doPerfTest = true;  // Make true to run perf test
        if (doPerfTest) {
            ShortFieldCodecTest self = new ShortFieldCodecTest();
            self.perfTest(50000000,5);
            return;
        }
        
        junit.textui.TestRunner.run(ShortFieldCodecTest.class);
    }

    public static Test suite() {
        return new TestSuite(ShortFieldCodecTest.class);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        codec   = new ShortFieldCodec("TestShortCodec");
    }

    public void testEncodeDecode() {

        doEncodeDecodeTest(0,0,         0x00,0x00);
        doEncodeDecodeTest(1,4,         0x00,0x01);
        doEncodeDecodeTest(10,0,        0x00,0x0A);

        doEncodeDecodeTest(255,0,       0x00,0xFF);
        doEncodeDecodeTest(256,0,       0x01,0x00);
        doEncodeDecodeTest(-256,0,      0xFF,0x00);
        
        doEncodeDecodeTest(32767,0,     0x7F,0xFF);
        doEncodeDecodeTest(-32768,0,    0x80,0x00);
        doEncodeDecodeTest(-1,0,        0xFF,0xFF);
    }

    private void doEncodeDecodeTest(int value,int startOffset,int... expectedBytes) {
        codec.setValue((short)value);
        encodeDecodeTest(codec, startOffset, expectedBytes);
        assertEquals("decoded value",value,codec.getValue());
    }

    public void testNewCopy() {
        ShortFieldCodec   codec2;
        codec2 = (ShortFieldCodec)codec.newCopy();
        assertNotSame(codec, codec2);
        assertEquals(0,codec.getValue());
    }

    public void testGetValueSetValue() {
        for(short i = 0; i < 100; i++) {
            codec.setValue(i);
            assertEquals(i,codec.getValue());
        }
    }
    
    public void testReset() {
        codec.setValue((short)10);
        assertEquals(10,codec.getValue());
        codec.reset();
        assertEquals(0,codec.getValue());
    }

    protected void perfTestSetValue(long timestamp,int iterationCount, byte[] storage) {
        short v = (short)(iterationCount & 0x0000FFFF);
        codec.setValue(v);
    }

    protected ICodec getCodec() {
        return codec;
    }

    
}
