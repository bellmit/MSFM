/*
 * Created on Oct 20, 2008
 */
package com.cboe.externalIntegrationServices.msgCodec;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AsciiStringFieldCodecTest extends CodecTester
{
    private AsciiStringFieldCodec       codec;
    private final String                VALUE_STR = "W_MAIN";
    
    public static void main(String[] args) {

        boolean doPerfTest = true;  // Make true to run perf test
        if (doPerfTest) {
            AsciiStringFieldCodecTest self = new AsciiStringFieldCodecTest();
            self.perfTest(50000000,5);
            return;
        }
        
        junit.textui.TestRunner.run(AsciiStringFieldCodecTest.class);
    }

    public static Test suite() {
        return new TestSuite(AsciiStringFieldCodecTest.class);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        codec   = new AsciiStringFieldCodec("TestAsciiStringFieldCodec",32);
    }

    public void testEncodeDecode() {
        CodecTester.ExpectedResultBuilder expect = new CodecTester.ExpectedResultBuilder(65000);
        String str;
        String digits = "1234567890";
        setStorageSize(65536);          // Enough for Strings > 32K

        str = "ABC";
        expect.clear().append(str);
        doEncodeDecodeTest(expect.toString(),expect.toIntArray());
        
        str = "W_MAIN";
        expect.clear().append(str);
        doEncodeDecodeTest(expect.toString(),expect.toIntArray());
        

        expect.clear().appendRepeat(digits, 127);
        doEncodeDecodeTest(expect.toString(),expect.toIntArray());
        
        expect.clear().appendRepeat(digits, 32769);
        doEncodeDecodeTest(expect.toString(),expect.toIntArray());

        expect.clear().appendRepeat(digits, 60000);
        doEncodeDecodeTest(expect.toString(),expect.toIntArray());

        str = "";
        expect.clear().append(0x00);
        doEncodeDecodeTest(str,expect.toIntArray());

        str = null;
        expect.clear().append(0x7F);
        doEncodeDecodeTest(str,expect.toIntArray());
        
    }

    private void doEncodeDecodeTest(String str,int... expectedBytes) {

        // Always expect negative value for last byte.
        expectedBytes[expectedBytes.length-1] |= 0x80;
        codec.setValue(str);
        super.encodeDecodeTest(codec, 0, expectedBytes); 
        assertEquals(str,codec.getValue());
    }

    public void testNewCopy() {
        AsciiStringFieldCodec   codec2;
        codec2 = (AsciiStringFieldCodec)codec.newCopy();
        assertNotSame(codec, codec2);
        assertNull(codec.getValue());
    }
    
    protected void perfTestSetValue(long timestamp, int iterationCount, byte[] storage) {
        codec.setValue(VALUE_STR);
    }

    protected ICodec getCodec() {
        return codec;
    }
}
