/*
 * Created on Oct 20, 2008
 */
package com.cboe.externalIntegrationServices.msgCodec;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ByteArrayFieldCodecTest extends CodecTester
{
    private ByteArrayFieldCodec         codec;
    private final String                VALUE_STR = "W_MAIN";
    private final byte[]                valueAsBytes = VALUE_STR.getBytes();
    private boolean                     setValueAsBytes;
    private int                         setValueMethod;
    
    public static void main(String[] args) {

        boolean doPerfTest = true;  // Make true to run perf test
        if (doPerfTest) {
            ByteArrayFieldCodecTest self = new ByteArrayFieldCodecTest();
            System.out.println("SetValue using String");
            self.perfTest(50000000,5);
            self.setValueAsBytes = true;
            System.out.println("SetValue using Bytes");
            self.perfTest(50000000,5);
            return;
        }
        
        junit.textui.TestRunner.run(ByteArrayFieldCodecTest.class);
    }

    public static Test suite() {
        return new TestSuite(ByteArrayFieldCodecTest.class);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        codec   = new ByteArrayFieldCodec("TestByteArrayFieldCodec",32);
    }

    public void testEncodeDecode() {

        String digits = "1234567890";
        setStorageSize(32771);          // Enough for 32767 + 2 length bytes + extra byte at end.
        CodecTester.ExpectedResultBuilder expect = new CodecTester.ExpectedResultBuilder(32768);
        
        for(int i = 0; i < 3; i++) {
            setValueMethod = i;

            // Shorter than 127, length bytes are 1 byte.
            expect.clear().append(0x03).append("ABC");
            encodeDecodeTest(expect.rightStr(1),expect.toIntArray());

            expect.clear().append(0x06).append("W_MAIN");
            encodeDecodeTest(expect.rightStr(1),expect.toIntArray());

            // length <= 127 is 1 byte encoded length - boundary condition
            expect.clear().append(0x7F).appendRepeat(digits, 127);
            encodeDecodeTest(expect.rightStr(1),expect.toIntArray());

            // length > 127 is 2 byte encoded length with sign bit of high byte on
            expect.clear().append(0x80,0x80).appendRepeat(digits,128);
            encodeDecodeTest(expect.rightStr(2),expect.toIntArray());
            
            // Remaining tests are lengths > 128 requiring 2 bytes of length 
            expect.clear().append(0x80,0xFF).appendRepeat(digits,255);
            encodeDecodeTest(expect.rightStr(2),expect.toIntArray());

            expect.clear().append(0x81,0x00).appendRepeat(digits,256);
            encodeDecodeTest(expect.rightStr(2),expect.toIntArray());

            expect.clear().append(0x83,0xFF).appendRepeat(digits,1023);
            encodeDecodeTest(expect.rightStr(2),expect.toIntArray());
            
            expect.clear().append(0x84,0x00).appendRepeat(digits,1024);
            encodeDecodeTest(expect.rightStr(2),expect.toIntArray());

            expect.clear().append(0xFF,0xFF).appendRepeat(digits,32767);
            encodeDecodeTest(expect.rightStr(2),expect.toIntArray());

            try {
                expect.clear().append(0xFF,0xFF).appendRepeat(digits,32768);
                encodeDecodeTest(expect.rightStr(2),expect.toIntArray());
                fail("Expected exception not thrown attempting to assign value > length 32767");
            } catch (IllegalArgumentException t) {
                // Success
            } catch (Throwable t) {
                fail("Unexpected exception thrown attempting to assign value > length 32767");
            }

            expect.clear().append(0x80,0x00);
            encodeDecodeTest(null,expect.toIntArray());

        }
    }

    private void encodeDecodeTest(String str,int... expectedBytes) {
        int strLen, expectedLen;
        byte[] strBytes;
        
        if (str != null) {
            strLen      = str.length();
            expectedLen = strLen;
            strBytes    = str.getBytes();
        }
        else {
            strLen      = 0;
            expectedLen = -1;
            strBytes    = null;
        }
        // Test all 3 setValue type methods, by array, by reference to array, and by String
        switch(setValueMethod) {
            case 0: codec.setValue(strBytes,strLen);
                    break;
            case 1: codec.setValueStorage(strBytes,strLen);
                    break;
            case 2: codec.setValue(str);
                    break;
        }
        
        super.encodeDecodeTest(codec, 0, expectedBytes); 

        byte[] actualValue  = codec.getValue();
        int    actualLen    = codec.getLength();

        assertEquals(expectedLen,actualLen);

        for(int i = 0; i < actualLen; i++) {
            assertEquals("value[" + i + "]",str.charAt(i),(char)actualValue[i]);
        }
        
        assertEquals(str,codec.getStringValue());
        
    }

    public void testNewCopy() {
        ByteArrayFieldCodec   codec2;
        codec2 = (ByteArrayFieldCodec)codec.newCopy();
        assertNotSame(codec, codec2);
        assertEquals(0,codec.getLength());
        assertNotNull(codec.getValue());
    }
    
    protected void perfTestSetValue(long timestamp, int iterationCount, byte[] storage) {
        if (setValueAsBytes)
            codec.setValue(valueAsBytes,valueAsBytes.length);
        else
            codec.setValue(VALUE_STR);
    }

    protected ICodec getCodec() {
        return codec;
    }

    
}
