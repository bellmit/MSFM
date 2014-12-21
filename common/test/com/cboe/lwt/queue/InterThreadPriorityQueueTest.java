/*
 * LwtInterThreadPriorityQueueTest.java
 * JUnit based test
 *
 * Created on February 26, 2002, 12:00 PM
 */

package com.cboe.lwt.queue;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.cboe.lwt.eventLog.ConsoleLogger;
import com.cboe.lwt.testUtils.SystemUtils;


/**
 *
 * @author dotyl
 */
public class InterThreadPriorityQueueTest extends TestCase
{
    private static final int TEST_ITERATIONS = 100000;  // 100K
    private static final int PERF_ITERATIONS = 2000000; // 2M
    private static final int MSG_POOL_SIZE   = 8192;// must be a multiple of 256
    static String[] msgs = null;
    
    
    static
    {        msgs = new String[ MSG_POOL_SIZE ];
        
        for ( int i = 0; i < MSG_POOL_SIZE; i++ )
        {
            msgs[i] = "test String " + i;
        }
    }
    
    public InterThreadPriorityQueueTest( java.lang.String testName )
    {
        super( testName );
    }
    
    
    public static void main( java.lang.String[] args )
    {
//        GlobalMetricsMonitor.initThroughputMonitor( System.out, 2000 ).go();
        
        new ConsoleLogger().setGlobal();
        junit.textui.TestRunner.run( InterThreadPriorityQueueTest.class );
        
        junit.textui.TestRunner.run( perfSuite() );
    }
    
    
    public void testOrow()
    {
        System.out.println( "InterThreadPriorityQueue Test OROW, iterations : " + TEST_ITERATIONS );
        doOrowTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 64, 1, 4 ), TEST_ITERATIONS, false );
        doOrowTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 256, 1, 4 ), TEST_ITERATIONS, false );
        doOrowTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 512, 1, 4 ), TEST_ITERATIONS, false );
        doOrowTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 64, 32, 4 ), TEST_ITERATIONS, false );
        doOrowTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 256, 32, 4 ), TEST_ITERATIONS, false );
        doOrowTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 512, 32, 4 ), TEST_ITERATIONS, false );
        doOrowTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 64, 512, 4 ), TEST_ITERATIONS, false );
        doOrowTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 256, 512, 4 ), TEST_ITERATIONS, false );
        doOrowTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 512, 512, 4 ), TEST_ITERATIONS, false );
    }
    
    public void testOrowMultDequeue()
    {
        System.out.println( "InterThreadPriorityQueue Test OROW MultiDequeue, iterations : " + TEST_ITERATIONS );
        doOrowMultDequeueTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 64, 1, 4 ), TEST_ITERATIONS, false );
        doOrowMultDequeueTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 256, 1, 4 ), TEST_ITERATIONS, false );
        doOrowMultDequeueTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 512, 1, 4 ), TEST_ITERATIONS, false );
        doOrowMultDequeueTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 64, 32, 4 ), TEST_ITERATIONS, false );
        doOrowMultDequeueTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 256, 32, 4 ), TEST_ITERATIONS, false );
        doOrowMultDequeueTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 512, 32, 4 ), TEST_ITERATIONS, false );
        doOrowMultDequeueTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 64, 512, 4 ), TEST_ITERATIONS, false );
        doOrowMultDequeueTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 256, 512, 4 ), TEST_ITERATIONS, false );
        doOrowMultDequeueTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 512, 512, 4 ), TEST_ITERATIONS, false );
    }
    
    public void testMrow()
    {
        System.out.println( "InterThreadPriorityQueue Test MROW, iterations : " + TEST_ITERATIONS );
        doMrowTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 64, 1, 4 ), TEST_ITERATIONS, false );
        doMrowTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 256, 1, 4 ), TEST_ITERATIONS, false );
        doMrowTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 512, 1, 4 ), TEST_ITERATIONS, false );
        doMrowTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 64, 32, 4 ), TEST_ITERATIONS, false );
        doMrowTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 256, 32, 4 ), TEST_ITERATIONS, false );
        doMrowTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 512, 32, 4 ), TEST_ITERATIONS, false );
        doMrowTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 64, 512, 4 ), TEST_ITERATIONS, false );
        doMrowTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 256, 512, 4 ), TEST_ITERATIONS, false );
        doMrowTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 512, 512, 4 ), TEST_ITERATIONS, false );
    }
    
    public void perfOrow()
    {
        System.out.println( "InterThreadPriorityQueue Performance test OROW witn LWT queue : iterations = 2000000" );
        doOrowTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 64, 1, 4 ), PERF_ITERATIONS, true );
        doOrowTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 256, 1, 4 ), PERF_ITERATIONS, true );
        doOrowTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 512, 1, 4 ), PERF_ITERATIONS, true );
        doOrowTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 64, 32, 4 ), PERF_ITERATIONS, true );
        doOrowTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 256, 32, 4 ), PERF_ITERATIONS, true );
        doOrowTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 512, 32, 4 ), PERF_ITERATIONS, true );
        doOrowTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 64, 512, 4 ), PERF_ITERATIONS, true );
        doOrowTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 256, 512, 4 ), PERF_ITERATIONS, true );
        doOrowTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 512, 512, 4 ), PERF_ITERATIONS, true );
    }


    public void perfOrowMultiDequeue()
    {
        System.out.println( "InterThreadPriorityQueue Performance test OROW Multi dequeue witn LWT queue : iterations = 2000000" );
        doOrowMultDequeueTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 64, 1, 4 ), PERF_ITERATIONS, true );
        doOrowMultDequeueTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 256, 1, 4 ), PERF_ITERATIONS, true );
        doOrowMultDequeueTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 512, 1, 4 ), PERF_ITERATIONS, true );
        doOrowMultDequeueTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 64, 32, 4 ), PERF_ITERATIONS, true );
        doOrowMultDequeueTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 256, 32, 4 ), PERF_ITERATIONS, true );
        doOrowMultDequeueTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 512, 32, 4 ), PERF_ITERATIONS, true );
        doOrowMultDequeueTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 64, 512, 4 ), PERF_ITERATIONS, true );
        doOrowMultDequeueTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 256, 512, 4 ), PERF_ITERATIONS, true );
        doOrowMultDequeueTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 512, 512, 4 ), PERF_ITERATIONS, true );
    }
    
    public void perfMrow()
    {
        System.out.println( "InterThreadPriorityQueue Performance test MROW witn LWT queue : iterations = 2000000" );
        doMrowTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 64, 3, 4 ), PERF_ITERATIONS, true );
        doMrowTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 256, 3, 4 ), PERF_ITERATIONS, true );
        doMrowTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 512, 3, 4 ), PERF_ITERATIONS, true );
        doMrowTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 64, 32, 4 ), PERF_ITERATIONS, true );
        doMrowTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 256, 32, 4 ), PERF_ITERATIONS, true );
        doMrowTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 512, 32, 4 ), PERF_ITERATIONS, true );
        doMrowTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 64, 512, 4 ), PERF_ITERATIONS, true );
        doMrowTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 256, 512, 4 ), PERF_ITERATIONS, true );
        doMrowTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 512, 512, 4 ), PERF_ITERATIONS, true );
    }
    
    public void perfMrmw()
    {
        System.out.println( "InterThreadPriorityQueue Performance test MRMW : iterations = 2000000" );
        doMrmwTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 64, 3, 4 ), PERF_ITERATIONS, true );
        doMrmwTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 256, 3, 4 ), PERF_ITERATIONS, true );
        doMrmwTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 512, 3, 4 ), PERF_ITERATIONS, true );
        doMrmwTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 64, 32, 4 ), PERF_ITERATIONS, true );
        doMrmwTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 256, 32, 4 ), PERF_ITERATIONS, true );
        doMrmwTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 512, 32, 4 ), PERF_ITERATIONS, true );
        doMrmwTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 64, 512, 4 ), PERF_ITERATIONS, true );
        doMrmwTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 256, 512, 4 ), PERF_ITERATIONS, true );
        doMrmwTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 512, 512, 4 ), PERF_ITERATIONS, true );
    }
    
    public void doOrowTest( final InterThreadPriorityQueue   p_queue, 
                            final int     p_iterations,
                            final boolean p_printResults )
    {
        System.out.println( "InterThreadPriorityQueue test One Reader, One Writer: Depth = " + p_queue.getCapacity() );
        
        
        Thread writer = new Thread()
                {
                    public void run()
                    {
                        long startTime = System.currentTimeMillis();
                        int i = 0;
                        try
                        {
                            for( i = 0; i < p_iterations; i++ )
                            {
                                p_queue.enqueue( InterThreadPriorityQueueTest.msgs[i % 8192], 1 );
                            }
                            p_queue.flush();

                        }
                        catch( QueueException ex )
                        {
                            ex.printStackTrace();
                        }
                        long throughput = SystemUtils.getThroughput( i, startTime, System.currentTimeMillis() );
                        if( p_printResults )
                        {
                            System.out.println( "    - Write Throughput : " + throughput );
                        }
                    }
                };

        
        Thread reader = new Thread()
                {

                    public void run()
                    {
                        try
                        {
                            int i = 0;
                            do
                            {
                                String msg = ( String )p_queue.dequeue();
                                if( msg != InterThreadPriorityQueueTest.msgs[i % 8192] )
                                {
                                    System.out.println( "Message number : " + i + " Not equal" );
                                    System.out.println( "expected : " + InterThreadPriorityQueueTest.msgs[i % 8192] );
                                    System.out.println( "received : " + msg );
                                    Assert.fail();
                                }
                                i++;
                            } while( true );
                        }
                        catch( QueueException size )
                        {
                            return;
                        }
                    }

                };

        
        reader.start();
        writer.start();
        try
        {
            writer.join();
            reader.interrupt();
            reader.join();
        }
        catch( InterruptedException ex )
        {
            ex.printStackTrace();
            Assert.fail();
        }
    }
    
    public void doOrowMultDequeueTest( final InterThreadPriorityQueue p_queue, 
                                       final int p_iterations, 
                                       final boolean p_printResults )
    {
        System.out.println( "InterThreadPriorityQueue test One Reader, One Writer ( MultDequeue ) : Depth = " + p_queue.getCapacity() );
        Thread writer = new Thread( "Writer" )
                {

                    public void run()
                    {
                        long startTime = System.currentTimeMillis();
                        int i = 0;
                        try
                        {
                            for( i = 0; i < p_iterations; i++ )
                            {
                                p_queue.enqueue( InterThreadPriorityQueueTest.msgs[i % 8192], 0 );
                            }
                            p_queue.flush();
                        }
                        catch( QueueException ex )
                        {
                            ex.printStackTrace();
                        }
                        finally
                        {
                            long throughput = SystemUtils.getThroughput( i, startTime, System.currentTimeMillis() );
                            if( p_printResults )
                                System.out.println( "    - Write Throughput : " + throughput );
                        }
                    }

                };
        
        
        Thread reader = new Thread( "Reader" )
                {

                    public void run()
                    {
                        int recvCount = 0;
                        try
                        {
                            int reads = 0;
                            do
                            {
                                FixedQueue dequeuedMsgs = p_queue.dequeueMultiple();
                                
                                if ( dequeuedMsgs != null )
                                {
                                    while ( ! dequeuedMsgs.isEmpty() )
                                    {
                                        Object msg = dequeuedMsgs.dequeue();
                                        int totalMsgs = dequeuedMsgs.available();
                                        
                                        if( msg != InterThreadPriorityQueueTest.msgs[recvCount % 8192] )
                                        {
                                            System.out.println( "ERROR: got " + totalMsgs + ", msgs in bundle " + reads );
                                            System.out.println( "Total message number : " + recvCount + " Not equal" );
                                            System.out.println( "expected : " + InterThreadPriorityQueueTest.msgs[recvCount % 8192] );
                                            System.out.println( "received : " + ( String )msg );
                                            System.out.println( "\n rest of bundle\n-------------------" );
                                            
                                            while ( ! dequeuedMsgs.isEmpty() )
                                            {    
                                                System.out.println( "BAD  : bundle " + reads + ", msg is = ( " + ( String )msg + " )" );
                                            }
                                            System.exit( -1 );
                                        }
                                        recvCount++;
                                    }

                                    reads++;
                                }
                                else
                                {
                                    p_queue.flush();
                                }
                            } while( recvCount < p_iterations );
                            Assert.assertEquals( p_iterations, recvCount );
                        }
                        catch( QueueException ex )
                        {
                            ex.printStackTrace();
                            assertTrue( false );
                            return;
                        }
                        catch( Throwable ex )
                        {
                            ex.printStackTrace();
                            assertTrue( false );
                            return;
                        }
                        finally
                        {
                            Assert.assertEquals( p_iterations, recvCount );
                        }
                    }

                };
        
        
        reader.start();
        writer.start();
        try
        {
            writer.join();
            reader.join();
        }
        catch( InterruptedException ex )
        {
            ex.printStackTrace();
            Assert.fail();
        }
    }
    

    public void doMrowTest( final InterThreadPriorityQueue p_queue, 
                            final int p_iterations, 
                            final boolean p_printResults )
    {
        System.out.println( "InterThreadPriorityQueue test Multiple Readers,One Writer: Depth = " + p_queue.getCapacity() );
        
        Thread writer = new Thread( "Writer" )
                {
                    public void run()
                    {
                        long startTime = System.currentTimeMillis();
                        int i = 0;
                        try
                        {
                            for( i = 0; i < p_iterations; i++ )
                            {
                                p_queue.enqueue( InterThreadPriorityQueueTest.msgs[i % 8192], i % 4 );
                            }
                            p_queue.flush();
                        }
                        catch( QueueException ex )
                        {
                            ex.printStackTrace();
                        }
                        long throughput = SystemUtils.getThroughput( i, startTime, System.currentTimeMillis() );
                        if( p_printResults )
                            System.out.println( "    - Write Throughput    : " + throughput );
                    }

                };

        
        Thread readers[] = new Thread[8];
        
        for( int i = 0; i < 8; i++ )
        {
            readers[i] = new Thread( "Reader " + i )
                    {
                        public void run()
                        {
                            long startTime = System.currentTimeMillis();
                            long endTime = 0L;
                            int j = 0;
                            do
                            {
                                try
                                {
                                    p_queue.dequeue();
                                }
                                catch( QueueException size )
                                {
                                    endTime = System.currentTimeMillis();
                                    long throughput = SystemUtils.getThroughput( j, startTime, endTime );
                                    if( p_printResults )
                                    {
                                        System.out.println( "    - p_queue Closed after " + j + " Read, Throughput : " + throughput + " <IOE>" );
                                    }
                                    return;
                                }
                                j++;
                            } while( true );
                        }

                    };
        }
            
        for( int i = 0; i < 8; i++ )
        {
            readers[i].start();
        }
        
        writer.start();
        try
        {
            writer.join();
            for( int i = 0; i < 8; i++ )
            {
                readers[i].interrupt();
            }
            for( int i = 0; i < 8; i++ )
            {
                readers[i].join();
            }
        }
        catch( InterruptedException ex )
        {
            ex.printStackTrace();
            Assert.fail();
        }
    }
    
    public void doMrmwTest( final InterThreadPriorityQueue p_queue, 
                            final int p_iterations, 
                            final boolean p_printResults )
    {
        System.out.println( "InterThreadPriorityQueue test Multiple Readers,Multiple Writers: Depth = " + p_queue.getCapacity() );
        
		Thread writers[] = new Thread[4];
		for( int i = 0; i < 4; i++ ) {
			writers[i] = new Thread( "Writer" + i )
                {
                    public void run()
                    {
                        long startTime = System.currentTimeMillis();
                        int j = 0;
                        try
                        {
                            for( j = 0; j < p_iterations; j++ )
                            {
                                p_queue.enqueue( InterThreadPriorityQueueTest.msgs[j % 8192], j % 4 );
                            }
                            p_queue.flush();
                        }
                        catch( QueueException ex )
                        {
                            ex.printStackTrace();
                        }
                        long throughput = SystemUtils.getThroughput( j, startTime, System.currentTimeMillis() );
                        if( p_printResults )
                            System.out.println( "    - Write Throughput    : " + throughput );
                    }

                };
		}

        
        Thread readers[] = new Thread[8];
        
        for( int i = 0; i < 8; i++ )
        {
            readers[i] = new Thread( "Reader " + i )
                    {
                        public void run()
                        {
                            long startTime = System.currentTimeMillis();
                            long endTime = 0L;
                            int j = 0;
                            do
                            {
                                try
                                {
                                    p_queue.dequeue();
                                }
                                catch( QueueException size )
                                {
                                    endTime = System.currentTimeMillis();
                                    long throughput = SystemUtils.getThroughput( j, startTime, endTime );
                                    if( p_printResults )
                                        System.out.println( "    - p_queue Closed after " + j + " Read, Throughput : " + throughput + " <IOE>" );
                                    return;
                                }
                                j++;
                            } while( true );
                        }

                    };
        }
            
        for( int i = 0; i < 8; i++ )
        {
            readers[i].start();
        }
        
		for( int i = 0; i < 4; i++ ) {
			writers[i].start();
		}
        try
        {
			for ( int i = 0; i < 4; i++ ) {
				writers[i].join();
			}
            for( int i = 0; i < 8; i++ )
            {
                readers[i].interrupt();
            }
            for( int i = 0; i < 8; i++ )
            {
                readers[i].join();
            }
        }
        catch( InterruptedException ex )
        {
            ex.printStackTrace();
            Assert.fail();
        }
    }
    public void testRead()
    {
        doReadAndOrderTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 64, 1, 4 ) );
        doReadAndOrderTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 128, 1, 4 ) );
        doReadAndOrderTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 64, 32, 4 ) );
        doReadAndOrderTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 128, 32, 4 ) );
    }
    
    public void doReadAndOrderTest( InterThreadPriorityQueue p_queue )
    {
        try
        {
            System.out.println( "doReadAndOrderTest: Depth = " + p_queue.getCapacity() );

            String msg1 = "test String1";
            String msg2 = "test String2";
            String msg3 = "test String3";
            Assert.assertEquals( p_queue.available(), 0 );
            
            p_queue.enqueue( msg1, 1 );
            p_queue.enqueue( msg2, 1 );
            p_queue.flush();
            Assert.assertTrue( p_queue.dequeue() == msg1 );
            
            p_queue.enqueue( msg3, 1 );
            p_queue.flush();
            Assert.assertTrue( p_queue.dequeue() == msg2 );
            Assert.assertTrue( p_queue.dequeue() == msg3 );

        
            Assert.assertEquals( p_queue.available(), 0 );
            
            p_queue.enqueue( msg1, 3 );
            p_queue.enqueue( msg2, 2 );
            p_queue.flush();
            Assert.assertTrue( p_queue.dequeue() == msg2 );
            
            p_queue.enqueue( msg3, 1 );
            p_queue.flush();
            Assert.assertTrue( p_queue.dequeue() == msg1 );
            Assert.assertTrue( p_queue.dequeue() == msg3 );
        }
        catch( QueueException ex )
        {
            ex.printStackTrace();
            Assert.fail();
        }
    }
    
    
    public void Deadlock()
    {
        doDeadlockTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 2, 1, 4 ) );
        doDeadlockTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 2, 8, 4 ) );
        doDeadlockTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 256, 1, 4 ) );
        doDeadlockTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 128, 256, 4 ) );
        doDeadlockTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 512, 1, 4 ) );
        doDeadlockTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 512, 16, 4 ) );
        
        System.out.println( "NOTE: Stressed Deadlock Tests may take as long as 20 seconds each" );
        
        doStressedDeadlockTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 2, 1, 4 ) );
        doStressedDeadlockTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 2, 8, 4 ) );
        doStressedDeadlockTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 256, 1, 4 ) );
        doStressedDeadlockTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 128, 256, 4 ) );
        doStressedDeadlockTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 512, 1, 4 ) );
        doStressedDeadlockTest( InterThreadPriorityQueue.getInstance( "MsgQueue", 512, 16, 4 ) );
    }
   
    
    public void doDeadlockTest( final InterThreadPriorityQueue p_queue )
    {
        System.out.println( "DeadLock test: Depth = " + p_queue.getCapacity() );
        
        Thread writer = new Thread( "Writer" )
        {
            
            public void run()
            {
                int i = 0;
                try
                {
                    for( i = 0; i < TEST_ITERATIONS; i++ )
                    {
                        p_queue.enqueue( InterThreadPriorityQueueTest.msgs[i % 8192], i % 4 );
                    }
                    p_queue.flush();
                }
                catch( QueueException ex )
                {
                    ex.printStackTrace();
                    Assert.fail();
                }
            }

        };
        
        
        Thread reader = new Thread( "Reader" )
        {
            
            public void run()
            {
                try
                {
                    int i = 0;
                    while( true )
                    {
                        p_queue.dequeue();
                        i++;
                    }
                }
                catch( QueueException size )
                {
                    return;
                }
            }

        };
        
        
        Thread control = new Thread( "Control" )
        {
            public void run()
            {
                while( true )
                {
                    try
                    {
                        p_queue.clear();
                        Thread.sleep( 1000 );
                    }
                    catch( Exception ex )
                    {
                        return;
                    }
                }
            }

        };
        
        
        reader.start();
        writer.start();
        control.start();
        try
        {
            writer.join();
            reader.interrupt();
            control.interrupt();
            reader.join();
            control.join();
        }
        catch( InterruptedException ex )
        {
            ex.printStackTrace();
            Assert.fail();
        }
    }
   
    
    public void doStressedDeadlockTest( final InterThreadPriorityQueue p_queue )
    {
        System.out.println( "Stressed DeadLock test: Depth = " + p_queue.getCapacity() );
        
        Thread writer = new Thread( "FQ Writer" )
        {
            
            public void run()
            {
                int i = 0;
                try
                {
                    while( true )
                    {
                        p_queue.enqueue( InterThreadPriorityQueueTest.msgs[i % 8192], 0 );
                    }
                }
                catch( QueueInterruptedException ex )
                {
                    // do nothing
                }
                catch( QueueException ex )
                {
                    ex.printStackTrace();
                    Assert.fail();
                }
            }

        };
        
        
        Thread reader = new Thread( "FQ Reader" )
        {
            
            public void run()
            {
                try
                {
                    int i = 0;
                    while( true )
                    {
                        p_queue.dequeue();
                        i++;
                        sleep( 10 ); // allow the queue to fill
                    }
                }
                catch( Exception ex )
                {
                    return;
                }
            }

        };
        
        
        writer.start();
        
        try
        {
            // test clear on a full queue
            while ( ! p_queue.isFull( 0 ) )
            {
                Thread.sleep( 50 );
            }
            
            p_queue.clear();
            
            // test on contended, yet often full queue
            reader.start();
            
            for ( int i = 0; i < 10; ++i )
            {
                Thread.sleep( 500 );
                
                p_queue.clear();
            }
        
            writer.interrupt();
            writer.join();
            
            // test clear on an empty queue
            while ( ! p_queue.isEmpty() )
            {
                p_queue.clear();
                Thread.sleep( 100 );
            }
            
            p_queue.clear();
            
            
            reader.interrupt();
            reader.join();
        }
        catch( Exception ex )
        {
            ex.printStackTrace();
            Assert.fail();
        }
    }
    
    
    
    public static Test suite()
    {
        TestSuite suite = new TestSuite( InterThreadPriorityQueueTest.class );
        
        return suite;
    }

   
    public static Test perfSuite()
    {
        TestSuite suite = new TestSuite();
        
        suite.addTest( new InterThreadPriorityQueueTest( "perfOrow" ) );
		suite.addTest( new InterThreadPriorityQueueTest( "perfMrow" ) );
		suite.addTest( new InterThreadPriorityQueueTest( "perfMrmw" ) );
        suite.addTest( new InterThreadPriorityQueueTest( "perfOrowMultiDequeue" ) );
        
        return suite;
    }
    
}