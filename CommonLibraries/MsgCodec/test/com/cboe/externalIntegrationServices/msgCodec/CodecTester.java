/*
 * Created on Oct 16, 2008
 */
package com.cboe.externalIntegrationServices.msgCodec;

import java.text.DecimalFormat;
import java.util.Arrays;

import junit.framework.TestCase;

/**
 * This class is a common superclass that has utilities that are useful for
 * testing a Field level codec.  For each kind of FieldCodec, extend this class
 * and write the appropriate test cases, and delegate to this class' methods
 * to check the physical byte storage, etc.
 */

public abstract class CodecTester extends TestCase
{
    private static final DecimalFormat      DEC_FMT         = new DecimalFormat("###,###,###,##0.000000");

    private static final byte   SPECIAL_BYTE = 0x7F;
    private byte[]              storage = new byte[256];

    protected void setUp() throws Exception {
        super.setUp();
        System.out.println(getClass().getName() + ": " + getName());
        
    }

    /**
     * Allows subclass to increase size of storage array for testing.
     * @param p_size
     */
    protected void setStorageSize(int p_size) {
        if (p_size > storage.length) 
            storage = new byte[p_size];
    }
    
    
    /**
     * <pre>
     * To use this method in a test class, write a doEncodeDecodeTest() method 
     * to test your own codec with a value and expected byte storage values
     * like this:
     * 
     * doEncodeDecodeTest(YourCodecDataType value,int startOffset, int...expectedBytes);
     *      yourCodec.setValue(value);
     *      encodeDecodeTest(yourCodec,startOffset,expectedBytes);
     *      assertEquals(value,yourCodec.getValue());
     *      
     * </pre>
     * @param codec
     * @param startOffset
     * @param expectedBytes
     */
    protected final void encodeDecodeTest(ICodec codec,
                                          int startOffset,
                                          int... expectedBytes) {
        int expectedEndOffset, actualEndOffset, i;

        /*
         * Encode test:
         * Expected bytes should be the exact number of bytes we expect to
         * be written when encoding, so startOffset + expectedBytes.length 
         * will be the expected endOffset returned from encode. 
         * Before encoding, we will put a special byte values into the 
         * storage, including a special byte just after the last byte to
         * be encoded.
         * We then invoke encode and check the encoded bytes to make sure they
         * match, and we also check the last byte to make sure it remains
         * the special value, which means it was untouched. 
         */

        expectedEndOffset = startOffset + expectedBytes.length;
        wipeStorage(startOffset, expectedEndOffset);
        actualEndOffset = codec.encode(storage, startOffset);

        assertEquals("endOffset", expectedEndOffset, actualEndOffset);

        for (i = 0; i < expectedBytes.length; i++) {
            byte b = (byte)expectedBytes[i];
            assertEquals("storage[startOffset+" + i + "]", b,
                    storage[startOffset + i]);
        }

        assertEquals("storage[startOffset+" + i + "]", SPECIAL_BYTE,
                storage[expectedEndOffset]);

        /*
         * Decode test: 
         * Put special bytes in place where we'll decode, then
         * over-write the bytes with the passed-in bytes, reset the codec value 
         * using reset, then decode.
         * We can check only the endOffset here, the caller must check the 
         * value to make sure it's OK. 
         */
        wipeStorage(startOffset, expectedEndOffset);
        for (i = 0; i < expectedBytes.length; i++) {
            storage[startOffset + i] = (byte)expectedBytes[i];
        }

        codec.reset();

        actualEndOffset = codec.decode(storage, startOffset);

        assertEquals("endOffset", expectedEndOffset, actualEndOffset);
    }

    private void wipeStorage(int startOffset,int endOffset) {
        int end = endOffset + 1;
        if (end > storage.length)
            end = storage.length;
        for(int i = startOffset; i < endOffset + 1; i++) {
            storage[i] = SPECIAL_BYTE;
        }
    }

    
    /**
     * Subclass may call this method to execute a performance test of their
     * codec.  This method iterates itersPerSample for numSamples times.
     * Subclass must implement getCodec() and perfTestSetValue() to set the
     * value of their codec to something.
     * @param itersPerSample
     * @param numSamples
     */
    void perfTest(int itersPerSample, int numSamples) {
        try {
            setUp();
        } catch (Exception e) {
            System.out.println("Error from setUp() method");
        }
        
        ICodec codec = getCodec();
        int count = 0, 
            encodeDecodeLoopLimit = itersPerSample / 10, 
            encodeLen, 
            decodeLen;

        /*
         * To measure the encode/decode method calls (called virtually here
         * via interface methods), we will only call the abstract method
         * to set the value 10 times, then we run the inner-most loop for
         * itersPerSample / 10.  By doing this, we will exclude the expense
         * of perfTestSetValue, since we don't want to measure that. However,
         * we also don't want to encode/decode the same thing always over and 
         * over, so we'll change it 10 times during the itersPerSample loop.
         */
        
        for(int sample = 0; sample < numSamples; sample++) {
            long before = System.currentTimeMillis();
            for(int setValLoop = 0; setValLoop < 10; setValLoop++) {
                perfTestSetValue(before,count++,storage);
                for(int i = 0; i < encodeDecodeLoopLimit; i++) {
                    encodeLen = codec.encode(storage, 0);
                }
            }
            long after          = System.currentTimeMillis();
            long elapsed        = after - before;
            long thru           = (long)itersPerSample * 1000 / elapsed;
            double  costPerOp   = (double)elapsed * 1000 / (double)itersPerSample;

            System.out.println(codec.getClass().getSimpleName() + " Encode: Elapsed: " + elapsed + 
                               " Iters: " + itersPerSample + 
                               " Throughput: " + thru + 
                               " Micros/Op: " + DEC_FMT.format(costPerOp));
//            System.out.println("Codec:\n" + codec.toString());
        }
        
        count = 0;
        for(int sample = 0; sample < numSamples; sample++) {
            long before = System.currentTimeMillis();
            for(int setValLoop = 0; setValLoop < 10; setValLoop++) {
                perfTestSetValue(before,count++,storage);
                for(int i = 0; i < encodeDecodeLoopLimit; i++) {
                    decodeLen = codec.decode(storage, 0);
                }
            }
            long after          = System.currentTimeMillis();
            long elapsed        = after - before;
            long thru           = (long)itersPerSample * 1000 / elapsed;
            double  costPerOp   = (double)elapsed * 1000 / (double)itersPerSample;

            System.out.println(codec.getClass().getSimpleName() + " Decode: Elapsed: " + elapsed + 
                               " Iters: " + itersPerSample + 
                               " Throughput: " + thru + 
                               " Micros/Op: " + DEC_FMT.format(costPerOp));
//            System.out.println("Codec:\n " + codec.toString());
        }

    
    }
    
    /**
     * Subclass should implement this method to return their codec
     * @return
     */
    protected abstract ICodec getCodec();
    
    /**
     * Subclass should implement this to set a value into their ICodec,
     * call encode, then call decode passing storage and offset 0,
     * then get the value and check to see if it's == the value they originally
     * set.
     * This method will be called in a hard-loop many times and the wall-clock
     * time will be measured and reported.
     * @param iterationCount
     * @param storage
     */
    protected abstract void perfTestSetValue(long timestamp,int iterationCount,byte[] storage);
    

    

    /**
     * This class can be used to make it easier to test Codecs by building
     * up an expected result that can be used to when invoking the 
     * encodeDecodeTest method.  This class is conceptually similar to a 
     * java.util.StringBuilder in that you can append bytes, chars, ints, or
     * Strings to it.  You can also append fixed-length strings to it by
     * supplying a "template" String which is repeated until the specified length
     * number of chars is added.
     * 
     * You can extract the rightmost values appended as a String, and all
     * values as a String, and you can get the values as an int.
     * 
     * See ByteArrayFieldCodec for a specific example of usage of this class.
     */
    
    public static class ExpectedResultBuilder {
        private int[]   value;
        private int     count;

        public ExpectedResultBuilder(int p_capacity) {
            value = new int[p_capacity];
        }
        
        public int length() {
            return count;
        }

        void expandCapacity(int minimumCapacity) {
            int newCapacity = (value.length + 1) * 2;
            if (minimumCapacity > newCapacity) {
                newCapacity = minimumCapacity;
            }
            int[] newValue = new int[newCapacity];
            System.arraycopy(value,0,newValue,0,value.length);
            value = newValue;
        }

        public ExpectedResultBuilder append(String str) {
            if (str == null) 
                return this;
            int len = str.length();
            if (len == 0) return this;
            int newCount = count + len;
            if (newCount > value.length)
                expandCapacity(newCount);
            for(int i = 0; i < len; i++) {
                value[count++] = str.charAt(i);
            }
            count = newCount;
            return this;
            }

        public ExpectedResultBuilder appendRepeat(String str,int totalLen) {
            int repeatCnt   = totalLen / str.length();
            int excess      = totalLen % str.length();
            
            for(int i = 0; i < repeatCnt; i++) {
                append(str);
            }
            if (excess > 0)
                append(str.substring(0,excess));
            return this;
        }

        public ExpectedResultBuilder append(char c) {
            int newCount = count + 1;
            if (newCount > value.length)
                expandCapacity(newCount);
            value[count++] = c;
            return this;
        }

        public ExpectedResultBuilder append(int... intValues) {
            int newCount = count + intValues.length;
            if (newCount > value.length)
                expandCapacity(newCount);
            for(int i = 0; i < intValues.length; i++) {
                value[count++] = intValues[i];
            }
            return this;
        }

        public ExpectedResultBuilder clear() {
            count = 0;
            return this;
        }

        public int[] toIntArray() {
            int[] copy = new int[count];
            System.arraycopy(value,0,copy,0,length());
            return copy;
        }
        
        public String toString() {
            char[] chars = new char[count];
            for(int i = 0; i < count; i++) {
                chars[i] = (char)value[i];
            }
            return new String(chars);
        }
        
        public String rightStr(int p_startOffset) {
            char[] chars = new char[count];
            for(int i = 0; i < count; i++) {
                chars[i] = (char)value[i];
            }
            return new String(chars,p_startOffset,count-p_startOffset);
        }
    }
}
