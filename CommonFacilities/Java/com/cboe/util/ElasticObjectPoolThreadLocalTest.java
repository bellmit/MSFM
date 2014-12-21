package com.cboe.util;

import java.util.Random;

import junit.framework.TestCase;

public class ElasticObjectPoolThreadLocalTest extends TestCase
{

    public ElasticObjectPoolThreadLocalTest(String p_name)
    {
        super(p_name);
        // TODO Auto-generated constructor stub
    }

    public static class PoolMe implements Copyable
    {
        private long aqTID;
        public int val;

        public Object copy()
        {
            return new PoolMe();
        }

        public long getAcquiringThreadId()
        {
            return aqTID;
        }

        public void setAcquiringThreadId(long p_acquiringThreadId)
        {
            aqTID = p_acquiringThreadId;
        }
        
        public void clear()
        {
            val=0;
        }
    }
    
    public void testBasicEOPTL() throws Exception
    {
        ElasticObjectPoolThreadLocal.traceMode = false;
        final int segSize = 5;
        final int numThreads = 4;
        final ObjectPool<PoolMe> pool = new ElasticObjectPoolThreadLocal<PoolMe>(
                new PoolMe(), segSize + (segSize*2)*numThreads, segSize,
                ElasticObjectPoolThreadLocal.ExtraObjectPolicy.NEW_SEGMENT, 0);
        final long startNS = System.nanoTime();
        final long startMS = System.currentTimeMillis();
        
        final boolean poolOn = true;
        
        System.out.println("poolOn="+poolOn+", traceMode="+ElasticObjectPoolThreadLocal.traceMode);

        Thread[] threads = new Thread[numThreads];
        for (int k = 0; k < threads.length; k++)
        {
            final int tnum=k;
            threads[k] = new Thread("TL-test-"+k) { public void run() {
                PoolMe[] objs = new PoolMe[segSize*2]; // (force an overflow into the global pool)
                for (int j=0; j < 100000; j++)
                {
                    Random rnd = new Random();
                    for (int i = 0; i < objs.length; i++)
                    {
                        objs[i] = poolOn ? pool.checkOut() : new PoolMe();
                        assertEquals("0 on checkout ["+i+"]", 0, objs[i].val);
                        objs[i].val=tnum*1000 + (i+1);
                        if (rnd.nextInt(10)==0)
                        {
                            try
                            {
                                Thread.currentThread().sleep(1);
                            }
                            catch (InterruptedException e) {}
                        }
                    }
                    for (int i = 0; i < objs.length; i++)
                    {
                        if (poolOn)
                            pool.checkIn(objs[i]);
                    }
                }
                final long elapsedNS = System.nanoTime() - startNS;
                final long elapsedMS = System.currentTimeMillis() - startMS;
                System.out.println("Done! ["+Thread.currentThread().getName()+"] [elasped="+elapsedMS+" ms]");
                System.out.println("Done! ["+Thread.currentThread().getName()+"][elasped="+elapsedNS+" ns] "+pool);
            }};
        }
        for (int i = 0; i < threads.length; i++)
        {
            threads[i].start();
        }
        for (int i = 0; i < threads.length; i++)
        {
            threads[i].join();
        }
    }
}
