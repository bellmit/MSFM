/*
 * Created on Mar 23, 2004
 * 
 * To change the template for this generated file go to Window - Preferences -
 * Java - Code Generation - Code and Comments
 */

package com.cboe.lwt.transaction;

import java.util.Random;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.cboe.lwt.testUtils.SystemUtils;


/**
 * @author dotyl
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class LockMgrTest extends TestCase
{
    public LockMgrTest( String p_testName ){
        super( p_testName );
    }
        
    
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run( suite() );
        junit.textui.TestRunner.run( perfSuite() );
    }

    
    public static Test suite()
    {
        TestSuite suite = new TestSuite( LockMgrTest.class );
        
        return suite;
    }
    
    
    public void test()
    {
        final int BLOCKS = 10;
        final int BLOCK_SIZE = 50;
        final int ITERATIONS = 2;
        Integer[] toLockObjs = new Integer[BLOCKS];
        for ( int i = 0; i < BLOCKS; i++ )
        {
            toLockObjs[ i ] = new Integer( i );
        }
        
        PerfThread notRunAsThread = new PerfThread( new LockMgr(), 
                                                    BLOCKS, 
                                                    BLOCK_SIZE, 
                                                    ITERATIONS, 
                                                    toLockObjs, 
                                                    true );
        notRunAsThread.run();  // run in this thread
    }
    
    
    public static Test perfSuite()
    {
        TestSuite suite = new TestSuite();
        
        suite.addTest( new LockMgrTest( "perfLocks" ) );
        
        return suite;
    }

    
    /**
     * Benchmark lock manager performance
     *  
     */
    public void perfLocks()
    {
        LockMgr mgr = new LockMgr();
        int threads = Integer.getInteger( "threads", 4 ).intValue();
        int blocks = Integer.getInteger( "blocks", 500 ).intValue();
        int blockSize = Integer.getInteger( "blockSize", 20 ).intValue();
        int iterations = Integer.getInteger( "iterations", 1000000 ).intValue();
        
        System.out.println( "Threads    : " + threads );
        System.out.println( "Blocks     : " + blocks );
        System.out.println( "Block Size : " + blockSize );
        System.out.println( "Iterations : " + iterations );
        Integer[] toLockObjs = new Integer[blocks];
        long startTime = System.currentTimeMillis();
        for ( int i = 0; i < blocks; i++ )
        {
            toLockObjs[ i ] = new Integer( i );
        }
        long throughput = SystemUtils.getThroughput( blocks * blockSize, startTime, System.currentTimeMillis() );
        System.out.println( "Add Elements -- Blocks : " + blocks + ", BlockSize : " + blockSize + " -- Throughput: " + throughput );
        PerfThread[] perfThreads = new PerfThread[threads];
        for ( int i = 0; i < threads; ++i )
        {
            perfThreads [ i ] = new PerfThread( mgr, 
                                                blocks, 
                                                blockSize, 
                                                iterations, 
                                                toLockObjs, 
                                                false );
            perfThreads [ i ].start();
        }
        try
        {
            for ( int i = 0; i < threads; ++i )
            {
                perfThreads [ i ].join();
            }
        }
        catch ( InterruptedException ex )
        {
            System.out.println( "Exception waiting for threads\n" );
            ex.printStackTrace();
        }
    }


    ////////////////////////////////////////////////////////////////////////////
    // inner class
    private static class PerfThread extends Thread
    {
        LockMgr         mgr;
        int             blocks;
        int             blockSize;
        int             iterations;
        Integer[]       objs;
        boolean         quietMode;


        PerfThread( LockMgr   p_mgr, 
                    int       p_blocks, 
                    int       p_blockSize, 
                    int       p_iterations, 
                    Integer[] p_objs, 
                    boolean   p_quietMode )
        {
            mgr        = p_mgr;
            blocks     = p_blocks;
            blockSize  = p_blockSize;
            iterations = p_iterations;
            objs       = p_objs;
            quietMode  = p_quietMode;
        }


        public void run()
        {
            long throughput;
            Random rand = new Random();
            if ( ! quietMode )
            {
                System.out.println( "Starting Thread : " + toString() );
            }
            
            long startTime = System.currentTimeMillis();
            try
            {
                for ( int i = 0; i < iterations; i++ )
                {
                    int block = rand.nextInt( blocks );
                    
                    if ( objs[ block ] == null )
                    {
                        System.out.println( "Null block at index : " + block );
                    }
                    
                    mgr.lock( objs [ block ] );
                    mgr.unlock( objs[ block ] );
                }
            }
            catch ( InterruptedException ex )
            {
                System.out.println( "Thread : " + toString() + " | Interrupted " );
                ex.printStackTrace();
            }
            finally
            {
                if ( ! quietMode )
                {
                    throughput = SystemUtils.getThroughput( iterations, startTime, System.currentTimeMillis() );
                    System.out.println( toString() + "-- Add Elements -- Iterations : " + iterations + ", Blocks : " + blocks + ", BlockSize : " + blockSize + " -- Throughput: " + throughput );
                }
            }
        }
    }
    // inner class
    ////////////////////////////////////////////////////////////////////////////
}
