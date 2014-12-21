/*
 * Created on Oct 14, 2008
 */
package com.cboe.externalIntegrationServices.msgCodec;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class CompressedIntFieldCodecTest extends CodecTester
{
    private CompressedIntFieldCodec       codec;
    
    public static void main(String[] args) {

        boolean doPerfTest = true;  // Make true to run perf test
        if (doPerfTest) {
            CompressedIntFieldCodecTest self = new CompressedIntFieldCodecTest();
            self.perfTest(50000000,5);
            return;
        }
        
        junit.textui.TestRunner.run(CompressedIntFieldCodecTest.class);
    }

    public static Test suite() {
        return new TestSuite(CompressedIntFieldCodecTest.class);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        codec   = new CompressedIntFieldCodec("TestCompressedIntCodec");
    }

    public void testEncodeDecode() {

        doEncodeDecodeTest(0,           0x80);  //  0000 0000
                                                //  x+00 0000
        doEncodeDecodeTest(1,           0x81);  //  0000 0001
                                                //  x+00 0001
        doEncodeDecodeTest(-1,          0xFF);  //  1111 1111
                                                //  x-11 1111
        doEncodeDecodeTest(10,          0x8A);  //  0000 1010
                                                //  x+00 1010

        // 8 = 1000         9 = 1001        A = 1010        B = 1011
        // C = 1100         D = 1101        E = 1110        F = 1111

        doEncodeDecodeTest(63,          0xBF);          //  0011 1111
                                                        //  x+11 1111
        doEncodeDecodeTest(64,          0x00,0xC0);     //  0000 0000 0100 0000
                                                        //  o+00 0000 x100 0000
        doEncodeDecodeTest(-63,         0xC1);          //  1100 0001                                                        
                                                        //  x-00 0001
        doEncodeDecodeTest(-64,         0xC0);          //  1100 0000
                                                        //  x-00 0000

        // 2 bytes required to encode                                            
        doEncodeDecodeTest(127,         0x00,0xFF);     // 0000 0000 0111 1111
                                                        // o+00 0000 x111 1111
        doEncodeDecodeTest(128,         0x01,0x80);     // 0000 0000 1000 0000
                                                        // o+00 0001 x000 0000
        doEncodeDecodeTest(-127,        0x7F,0x81);     // 1111 1111 1000 0001
                                                        // o-11 1111 x000 0001
        doEncodeDecodeTest(-128,        0x7F,0x80);     // 1111 1111 1000 0000
                                                        // o-11 1111 x000 0000

        doEncodeDecodeTest(255,         0x01,0xFF);     // 1111 1111
                                                        // o+00 0001 x111 1111
        doEncodeDecodeTest(256,         0x02,0x80);     // 0001 0000 0000
                                                        // o+00 0010 x000 0000
        doEncodeDecodeTest(-256,        0x7E,0x80);     // 1111 0000 0000
                                                        // o-11 1110 x000 0000

        // 2 ** 13 boundary of 2-3 byte encoding 
        doEncodeDecodeTest(8191,        0x3F,0xFF);     // 0001 1111 1111 1111
                                                        // o+11 1111 o111 1111
        doEncodeDecodeTest(8192,        0x00,0x40,0x80);// 0000 0000 0010 0000 0000 0000
                                                        // o+00 0000 o100 0000 x000 0000
        doEncodeDecodeTest(-8192,       0x40,0x80);     // 1111 1111 1110 0000 0000 0000
                                                        // o100 0000 x000 0000
        // 2 ** 20 boundary of 3-4 byte encoding
        doEncodeDecodeTest(1048575,     0x3F,0x7F,0xFF);        // 0000 1111 1111 1111 1111 1111      
                                                                // o+11 1111 o111 1111 x111 1111
        
        doEncodeDecodeTest(1048576,     0x00,0x40,0x00,0x80);   // 0000 0000 0001 0000 0000 0000 0000 0000      
                                                                // o+00 0000 o100 0000 o000 0000 x000 0000
        

        doEncodeDecodeTest(-1048576,    0x40,0x00,0x80);        // 1111 0000 0000 0000 0000 0000          
                                                                // o100 0000 o000 0000 x000 0000
        
        // 2 ** 27 boundary of 4-5 byte encoding

        // 0000 0111 1111 1111 1111 1111 1111 1111
        // o+11 1111 o111 1111 o111 1111 x111 1111
        doEncodeDecodeTest(134217727,   0x3f,0x7F,0x7F,0xFF);
        
        // 0000 1000 0000 0000 0000 0000 0000 0000
        // o000 0000 o100 0000 o000 0000 o000 0000 x000 0000
        doEncodeDecodeTest(134217728,   0x00,0x40,0x00,0x00,0x80);
        
        // 1111 1000 0000 0000 0000 0000 0000 0000
        // o100 0000 o000 0000 o000 0000 x000 0000
        doEncodeDecodeTest(-134217728,  0x40,0x00,0x00,0x80);
        
        // 2 ** 31 maxint boundary
        
        // 0111 1111 1111 1111 1111 1111 1111 1111
        // o+00 0111 o111 1111 o111 1111 o111 1111 x111 1111
        doEncodeDecodeTest(2147483647,  0x07,0x7F,0x7F,0x7F,0xFF);
    
        // 1000 0000 0000 0000 0000 0000 0000 0000
        // o-11 1000 o000 0000 o000 0000 o000 0000 x000 0000
        doEncodeDecodeTest(-2147483648, 0x78,0x00,0x00,0x00,0x80);
        
    }

    private void doEncodeDecodeTest(int value,int... expectedBytes) {
        codec.setValue(value);
        encodeDecodeTest(codec, 0, expectedBytes);
        assertEquals("decoded value",value,codec.getValue());
    }

    public void testNewCopy() {
        CompressedIntFieldCodec   codec2;
        codec2 = (CompressedIntFieldCodec)codec.newCopy();
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
