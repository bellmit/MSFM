/*
 * Created on Oct 16, 2008
 */
package com.cboe.externalIntegrationServices.msgCodec;

import java.text.DecimalFormat;

import junit.framework.TestCase;

/**
 * A performance unit test harness for a MsgCodec.
 */

public abstract class MsgCodecPerfTester extends TestCase
{
    private static final DecimalFormat      DEC_FMT         = new DecimalFormat("###,###,###,##0.0000");

    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Subclass may call this method to execute a performance test of their
     * codec.  This method iterates itersPerSample for numSamples times.
     * Subclass must implement getCodec() and perfTestSetValue() to set the
     * value of their codec to something.
     * @param itersPerSample
     * @param numSamples
     */
    void perfTest(int itersPerSample, int numSamples) throws Exception {
        try {
            setUp();
        } catch (Exception e) {
            System.out.println("Error from setUp() method");
        }
        
        int count = 0; 

        for(int sample = 0; sample < numSamples; sample++) {
            long before = System.currentTimeMillis();
            count = 0;
            for(int i = 0; i < itersPerSample; i++) {
                count += perfTestSetValuesAndWrite(before,count);
            }
            long after          = System.currentTimeMillis();
            long elapsed        = after - before;
            long thru           = (long)count * 1000 / elapsed;
            double costPerOp    = (double)elapsed * 1000 / (double)count;

            System.out.println(getClass().getSimpleName() + " SetValues/Write: Elapsed: " + elapsed + 
                               " Iters: " + itersPerSample + 
                               " Throughput: " + thru + 
                               " Micros/Op: " + DEC_FMT.format(costPerOp));
        }
        
        for(int sample = 0; sample < numSamples; sample++) {
            long before = System.currentTimeMillis();
            count = 0;
            for(int i = 0; i < itersPerSample; i++) {
                count += perfTestReadAndGetValues(before,count);
            }
            long after          = System.currentTimeMillis();
            long elapsed        = after - before;
            long thru           = (long)count * 1000 / elapsed;
            double costPerOp    = (double)elapsed * 1000 / (double)count;

            System.out.println(getClass().getSimpleName() + " GetValues/Read: Elapsed: " + elapsed + 
                               " Iters: " + itersPerSample + 
                               " Throughput: " + thru + 
                               " Micros/Op: " + DEC_FMT.format(costPerOp));
        }
        
        
    }
    
    /**
     * Subclass should implement this to set values into their MsgCodec
     * in preparation for a write.
     * @param iterationCount
     * @param storage
     */
    protected abstract int perfTestSetValuesAndWrite(long timestamp,int iterationCount);

    /**
     * Subclass should implement this to get values from their MsgCodec.
     * @param timestamp
     * @param iterationCount
     */
    protected abstract int perfTestReadAndGetValues(long timestamp,int iterationCount) throws Exception;

}
