/*
 * ObjectPoolTest.java
 * JUnit based test
 *
 * Created on July 12, 2002, 3:19 PM
 */

package com.cboe.lwt.queue;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.cboe.lwt.pool.*;
import com.cboe.lwt.testUtils.SystemUtils;

/**
 *
 * @author dotyl
 */
public class ObjectPoolTest extends TestCase
{
    
    public ObjectPoolTest(java.lang.String testName)
    {
        super(testName);
    }
    
    public static void main(java.lang.String[] args)
    {
        junit.textui.TestRunner.run(suite());
        junit.textui.TestRunner.run( perfSuite() );
    }
    
    public static Test suite()
    {
        TestSuite suite = new TestSuite(ObjectPoolTest.class);
        
        return suite;
    }
    
    
    public static Test perfSuite()
    {
        TestSuite suite = new TestSuite();
        
        suite.addTest( new ObjectPoolTest( "perfThreads" ) );

        return suite;
    }
    
    public void testGetInstance()
    {
        System.out.println("testGetInstance");
        
        ObjectPool pool = ObjectPool.getInstance( "Test", 50 );
        
        for ( int i = 0; i < 60; ++i )
        {
            pool.checkIn( new Integer( i ) );
        }
        
        // should be only 50 available, though 60 were checked in... 
        // extra ten were discarded because pool is full
        assertEquals( 50, pool.available() );
    }
    
    public void testAvailable()
    {
        // tested with testGetInstance
    }
    
    public void testCheckIn()
    {
        // tested with testCheckOut
    }
    
    public void testCheckOut()
    {
        System.out.println("testCheckOut");
        
        ObjectPool pool = ObjectPool.getInstance( "Test2", 50 );
        
        for ( int i = 0; i < 20; ++i )
        {
            pool.checkIn( new Integer( i ) );
        }
        
        assertEquals( 20, pool.available() );

        for ( int i = 0; i < 40; ++i )
        {
            pool.checkIn( new Integer( i ) );
        }

        // stopped at max size
        assertEquals( 50, pool.available() );

        // checkOut returns some values
        for ( int i = 0; i < 30; ++i )
        {
            assertTrue( pool.checkOut() != null );
        }
        assertEquals( 20, pool.available() );

        // empty pool
        for ( int i = 0; i < 20; ++i )
        {
            assertTrue( pool.checkOut() != null );
        }
        assertEquals( 0, pool.available() );
        
        // insure that checkout with none available returns null
        assertEquals( null, pool.checkOut() );
        
        // check in some, checkout some, check in some more
        for ( int i = 0; i < 20; ++i )
        {
            pool.checkIn( new Integer( i ) );
        }
        assertEquals( 20, pool.available() );

        for ( int i = 0; i < 10; ++i )
        {
            assertTrue( pool.checkOut() != null );
        }
        assertEquals( 10, pool.available() );
        
        for ( int i = 0; i < 20; ++i )
        {
            pool.checkIn( new Integer( i ) );
        }
        assertEquals( 30, pool.available() );
    
        // empty pool
        for ( int i = 0; i < 30; ++i )
        {
            assertTrue( pool.checkOut() != null );
        }
        assertEquals( 0, pool.available() );
        
        // insure that checkout with none available returns null
        assertEquals( null, pool.checkOut() );
    }
    private static class TestThread extends Thread
    {
        ObjectPool pool;
        int checkInOuts;
        int loops;
        
        public TestThread( ObjectPool p_pool, int p_checkInOuts, int p_loops )
        {
            pool = p_pool;
            checkInOuts = p_checkInOuts;
            loops = p_loops;
        }
        
        
        public void run()
        {
            CircularQueue q = new CircularQueue( checkInOuts + 2 );

            for ( int i = 0; i < loops; ++i )
            {
                for ( int j = 0; j < checkInOuts; ++j )
                {
                    Object o = pool.checkOut();
                    if ( o == null )
                    {
                        o = new Integer( j );
                    }
                    
                    assert ( o != null ) : "j = " + j + ", i = " + i;
                    
                    q.enqueue( o );
                }
                for ( int k = 0; k < checkInOuts; ++k )
                {
                    Object o = q.dequeue();
                    assert ( o != null ) : "k = " + k + ", i = " + i;
                    pool.checkIn( o );
                }
            }
        }
    }

    
    public void perfThreads()
    {
        doPerfTestBaseline();
        
        doPerfTest();
        
        doPerfThreadsTest( 2 );
        doPerfThreadsTest( 8 );
        doPerfThreadsTest( 16 );
    }

    
    public void doPerfTestBaseline()
    {
        System.gc();
        
        final int ALLOCATIONS = 2000000;

        System.out.println("Performance Test: Simple allocation - " + ALLOCATIONS );
        
        Object[] store = new Object[ ALLOCATIONS ];
        
        long startTime = System.currentTimeMillis();

        for ( int i = 0; i < ALLOCATIONS; ++i )
        {
            store[ i ] = new Integer( i );
        }

        long throughput = SystemUtils.getThroughput( ALLOCATIONS, startTime, System.currentTimeMillis() ); // *2 because of check in and out
 
        System.out.println( "allocations : " + ALLOCATIONS + " -- Operation Throughput: " + throughput );
    }

    
    public void doPerfTest()
    {
        System.gc();
        
        final int ALLOCATIONS  = 2000000;
        final int POOL_SIZE    = 500;
        final int WARMED_SIZE  = 100;
        
        final ObjectPool pool  = ObjectPool.getInstance( "PerfTestPool", POOL_SIZE );
        
        // use THREADS because of possible roundoff errors
        System.out.println("Performance Test: single thread checking in/out");
       
        for ( int i = 0; i < WARMED_SIZE; ++i )
        {
            pool.checkIn( new Integer( i ) );
        }
        
        long startTime = System.currentTimeMillis();

        for ( int i = 0; i < ALLOCATIONS; ++i )
        {
            Object obj = pool.checkOut();
            pool.checkIn( obj );
        }

        long throughput = SystemUtils.getThroughput( ALLOCATIONS, startTime, System.currentTimeMillis() ); // *2 because of check in and out
 
        System.out.println( "Allocations : " + ALLOCATIONS + " -- Operation Throughput: " + throughput );
        System.out.println( pool.getStats() );
    }

    
    public void doPerfThreadsTest( int p_numThreads )
    {
        System.gc();
        
        final int CHECK_IN_OUTS  = 10000;
        final int LOOPS          = 100;
        final int TOTAL_ENTRIES  = CHECK_IN_OUTS * LOOPS * p_numThreads;
        final ObjectPool pool    = ObjectPool.getInstance( "PerfTestPool", 100000 );
        
        // use THREADS because of possible roundoff errors
        System.out.println("Performance Test: " + p_numThreads + " threads simultaneously checking in/out " + TOTAL_ENTRIES + " elements");
       
        Thread[] workers = new TestThread[ p_numThreads ];

        for ( int i = 0; i < p_numThreads; ++i )
        {
            workers[i] = new TestThread( pool, CHECK_IN_OUTS, LOOPS );
        }
        long startTime = System.currentTimeMillis();

        try
        {
            for ( int i = 0; i < p_numThreads; ++i )
            {
                workers[i].start();
            }

            for ( int i = 0; i < p_numThreads; ++i )
            {
                workers[i].join();
            }
        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
        }

        long throughput = SystemUtils.getThroughput( TOTAL_ENTRIES * 2, startTime, System.currentTimeMillis() ); // *2 because of check in and out
 
        System.out.println( "checkIns : " + TOTAL_ENTRIES + ", checkouts : " + TOTAL_ENTRIES + " -- Operation Throughput: " + throughput );
        System.out.println( pool.getStats() );
    }

}
