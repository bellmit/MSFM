package com.cboe.infrastructureServices.queue;

import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.TestCase;

/**
 * A suite of unit tests for the base queue impl.
 * Note that this does NOT test any possible transaction boundary issues.
 * Note that several tests are time-dependent, and assume that queue operations
 * will be execute in 100ms or less.  On extremely slow hardware, some tests may fail
 * because of this, yielding a false negative result.
 *
 * @author Steven Sinclair
 */
public class QueueBaseImplTest extends TestCase
{
    public static final int INF = Queue.INFINITE_TIMEOUT;
    public static final boolean EXPECT_DONE = true;
    public static final boolean EXPECT_NULL = true;
    public static final boolean BREAK_ON_NULL = true;
    public static final String[] TEST12 = { "1", "2" };
    public static final String[] TEST123 = { "1", "2", "3" };

    public Queue queue;
    public long markTime;

    ///////////////////////////////////////////////////////////////////////////
    //
    // framework methods:
    //

    static volatile int uniqueNameThingy = 0;

    public QueueBaseImplTest(String methodName)
    {
        super(methodName);
        ListDefinition underlyingListImpl = new TransientListDefinition();
        underlyingListImpl.setListName("testQ_" + (++uniqueNameThingy));
        queue = new QueueBaseImpl(underlyingListImpl, underlyingListImpl.getListName() + "_myInstrumentation");
	}
    
    /**
     * Runs the unit tests.
     */
    public static void main(String args[])
    {
        junit.textui.TestRunner.run(QueueBaseImplTest.class);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    // peek(...) tests:
    //
    public void testPeek_Empty() throws Exception
    {
        Object peeked = queue.peek(0);
        assertNull("Expected no result from peek(0) on empty queue", peeked);
    }
        
    public void testPeek_EmptyWithTimeout() throws Exception
    {
        long fr, elapsed;
        fr = System.currentTimeMillis();
        Object peeked = queue.peek(100);
        elapsed = System.currentTimeMillis() - fr;
        assertNull("Expected no result from peek(100) on empty queue", peeked);
        assertTrue("Expected to wait at least 100ms on peek(100): " + elapsed + "ms", elapsed >= 100);
    }
        
    public void testPeekMultiple_Empty() throws Exception
    {
        Object peeked = queue.peekMultiple(10);
        assertNull("Expected no result from peek(1000) on empty queue", peeked);
    }
        
    public void testPeekMultiple_EmptyWithTimeout() throws Exception
    {
        long fr, elapsed;
        fr = System.currentTimeMillis();
        Object peeked = queue.peekMultiple(10, 100);
        elapsed = System.currentTimeMillis() - fr;
        assertNull("Expected no result from peekMultiple(100) on empty queue", peeked);
        assertTrue("Expected to wait not-very-long on peekMultiple(100): " + elapsed + "ms", elapsed < 50);
    }
    
    public void testPeek_Normal() throws Exception
    {
        final String data1 = "data1";
        final String data2 = "data2";
        queue.enqueue(data1);
        queue.enqueue(data2);
        Object peeked = queue.peek(0);
        assertEquals("Expected " + data1 + " from peek(0)", data1, peeked);
    }
        
    public void testPeekMultiple_Normal() throws Exception
    {
        long fr, elapsed;
        final String data1 = "data1";
        final String data2 = "data2";
        queue.enqueue(data1);
        queue.enqueue(data2);
        
        fr = System.currentTimeMillis();
        Object[] peeked = queue.peekMultiple(10);
        elapsed = System.currentTimeMillis() - fr;
        assertNotNull("Expected result from peek(100) on empty queue", peeked);
        assertTrue("Expected to wait not-very-long for peekMultiple(int) on non-empty queue: " + elapsed + "ms", elapsed < 50);
        assertEquals("Expected 2 items in result", 2, peeked.length);
        assertEquals("Unexpected 1st item in result", data1, peeked[0]);
        assertEquals("Unexpected 2nd item in result", data2, peeked[1]);
    }
        
        
    public void testPeekMultiple_NormalWithTimeout() throws Exception
    {
        long fr, elapsed;
        final String data1 = "data1";
        final String data2 = "data2";
        queue.enqueue(data1);
        queue.enqueue(data2);
        fr = System.currentTimeMillis();
        Object[] peeked = queue.peekMultiple(10, 100);
        elapsed = System.currentTimeMillis() - fr;
        assertNotNull("Expected result from peekMultiple(100) on empty queue", peeked);
        assertTrue("Expected to wait not-very-long on peekMultiple(100): " + elapsed + "ms", elapsed < 50);
        assertEquals("Expected 2 items in result", 2, peeked.length);
        assertEquals("Unexpected 1st item in result", data1, peeked[0]);
        assertEquals("Unexpected 2nd item in result", data2, peeked[1]);
    }



    ///////////////////////////////////////////////////////////////////////////
    //
    // enqueue(...) tests:
    //

    public void testEnqueue_Normal() throws Exception
    {
        DequeueTester deq = new DequeueTester(INF, 3, BREAK_ON_NULL);
        deq.start();
        queue.enqueue("1");
        queue.enqueue("2");
        queue.enqueue("3");
        nap(200);
        assertDequeued(TEST123, deq);
    }

    // This test also includes basic validation of basic queue methods (isFull(), etc)
    //
    public void testEnqueue_Timeout() throws Exception
    {
        queue.setMaxQueueDepth(2);

        assertEquals("Unexpected queue size on init", 0, queue.size());
        assertTrue("Expected empty queue on init", queue.isEmpty());
        assertTrue("Expected non-full queue on init", !queue.isFull());
        assertEquals("Unexpected max depth", 2, queue.getMaxQueueDepth());
        assertEquals("Unexpected name", "testQ", queue.getQueueName());

        mark();
        queue.enqueue("1", 200);
        queue.enqueue("2", 200);
        assertMaxElapsed(100);      // these should enqueue quickly

        assertEquals("Unexpected queue size", 2, queue.size());
        assertTrue("Expected non-empty queue after enq's", !queue.isEmpty());
        assertTrue("Expected full queue after enq's", queue.isFull());

        mark();
        try
        {
            queue.enqueue("3", 200);
            assertTrue("Expected QueueFullException!", false);
        }
        catch (QueueFullException ex)
        {
            // (expected)
        }
        assertMinElapsed(200);  // at least 200ms before dequeuer dequeued anything
        assertMaxElapsed(300);  // at most 300ms for timeout code to be valid.

        DequeueTester deq2 = new DequeueTester(INF, 2, BREAK_ON_NULL);  // dequeue leftovers.
        deq2.start();
        nap(200);
        assertDequeued(TEST12, deq2);
        assertEquals("Unexpected queue size after test", 0, queue.size());
        assertTrue("Expected empty queue after test", queue.isEmpty());
        assertTrue("Expected non-full queue after test", !queue.isFull());
    }

    public void testEnqueue_Wait() throws Exception
    {
        queue.setMaxQueueDepth(2);
        mark();
        queue.enqueue("1", 200);
        queue.enqueue("2", 200);
        assertMaxElapsed(100);      // these should enqueue quickly

        // start dequeueing in 200 millis
        DequeueTester deq1 = new DequeueTester(INF, 1, BREAK_ON_NULL)
            { public void execute() throws Exception { nap(100); super.execute(); } }; // nap before executing
        deq1.start();

        mark();
        queue.enqueue("3", 300);
        assertMinElapsed(100);  // at least 200ms before dequeuer dequeued anything
        assertMaxElapsed(300);  // at most 300ms for timeout code to be valid.

        nap(200);
        assertDequeued(new Object[]{"1"}, deq1);

        DequeueTester deq2 = new DequeueTester(INF, 2, BREAK_ON_NULL);  // dequeue leftovers.
        deq2.start();
        nap(200);
        assertDequeued(new Object[]{"2","3"}, deq2);
        assertEquals("Queue not empty after test!", 0, queue.size());
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    // enqueue with "clear when full" option set
    //

    public void testEnqueue_ClearWhenFull() throws Exception
    {
        queue.setMaxQueueDepth(2);
        queue.setClearOnEnqueueFailure(true);
        mark();
        queue.enqueue("1", 200);
        queue.enqueue("2", 200);
        assertEquals("Unexepcted queue size!", 2, queue.size());
        queue.enqueue("3", 200);
        assertEquals("Unexepcted queue size!", 1, queue.size());
        Object obj = queue.dequeue();
        assertEquals("Unexepcted queue contents!", "3", obj);
    }



    ///////////////////////////////////////////////////////////////////////////
    //
    // dequeue interrupted tests:
    //

    public void testDequeue_interrupted() throws Exception
    {
        DequeueTester deq = new DequeueTester(10000, 3, BREAK_ON_NULL);
        deq.expectedThrowable = QueueInterruptedException.class;
        deq.start();
        nap(100);
        deq.interrupt();
        nap(100);
        assertTrue("Expected exception in dequeuer", deq.throwable != null);
        assertTrue("Unexpected exception type in dequeuer: " + deq.throwable, deq.expectedThrowable.isInstance(deq.throwable));
    }

    public void testDequeueMultiple_interrupted() throws Exception
    {
        DequeueMultipleTester deq = new DequeueMultipleTester(3, 10000);
        deq.expectedThrowable = QueueInterruptedException.class;
        deq.start();
        nap(100);
        deq.interrupt();
        nap(100);
        assertTrue("Expected exception in dequeuer", deq.throwable != null);
        assertTrue("Unexpected exception type in dequeuer: " + deq.throwable, deq.expectedThrowable.isInstance(deq.throwable));
    }

    public void testDequeueFully_interrupted() throws Exception
    {
        DequeueFullyTester deq = new DequeueFullyTester(3, 10000, 10000);
        deq.expectedThrowable = QueueInterruptedException.class;
        deq.start();
        nap(100);
        deq.interrupt();
        nap(100);
        assertTrue("Expected exception in dequeuer", deq.throwable != null);
        assertTrue("Unexpected exception type in dequeuer: " + deq.throwable, deq.expectedThrowable.isInstance(deq.throwable));
    }

    public void testDequeueFully2_interrupted() throws Exception
    {
        DequeueFully2Tester deq = new DequeueFully2Tester(3, 10000, 10000);
        deq.expectedThrowable = QueueInterruptedException.class;
        deq.start();
        nap(100);
        deq.interrupt();
        nap(100);
        assertTrue("Expected exception in dequeuer", deq.throwable != null);
        assertTrue("Unexpected exception type in dequeuer: " + deq.throwable, deq.expectedThrowable.isInstance(deq.throwable));
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    // dequeue(...) tests:
    //

    public void testDequeue_Normal() throws Exception
    {
        DequeueTester deq = new DequeueTester(INF, 3, BREAK_ON_NULL);
        deq.start();
        nap(100);
        queue.enqueue("1");
        queue.enqueue("2");
        queue.enqueue("3");
        nap(200);
        assertDequeued(TEST123, deq);
    }

    public void testDequeue_Prefilled() throws Exception
    {
        DequeueTester deq = new DequeueTester(INF, 3, BREAK_ON_NULL);
        queue.enqueue("1");
        queue.enqueue("2");
        queue.enqueue("3");
        deq.start();
        nap(200);
        assertDequeued(TEST123, deq);
    }

    public void testDequeue_Timeout() throws Exception
    {
        DequeueTester deq = new DequeueTester(200, 1, BREAK_ON_NULL);
        deq.start();
        nap(100);
        assertDequeued(0, deq, !EXPECT_DONE, EXPECT_NULL);
        nap(300);
        assertDequeued(0, deq, EXPECT_DONE, EXPECT_NULL);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    // dequeueMultiple(...) tests:
    //

    public void testDequeueMultiple_Normal() throws Exception
    {
        DequeueMultipleTester deq = new DequeueMultipleTester(3, INF);
        deq.start();
        queue.enqueue("1");
        queue.enqueue("2");
        queue.enqueue("3");
        nap(200);
        assertDequeued(TEST123, deq);
    }

    public void testDequeueMultiple_Prefilled() throws Exception
    {
        DequeueMultipleTester deq = new DequeueMultipleTester(3);
        queue.enqueue("1");
        queue.enqueue("2");
        queue.enqueue("3");
        deq.start();
        nap(200);
        assertDequeued(TEST123, deq);
    }

    public void testDequeueMultiple_PartiallyPrefilled() throws Exception
    {
        DequeueMultipleTester deq = new DequeueMultipleTester(3, INF);
        deq.atLeast = 2;
        queue.enqueue("1");
        deq.start();
        nap(100);
        queue.enqueue("2");
        queue.enqueue("3");
        nap(500);
        assertDequeued(TEST123, deq);
        assertEquals("Dequeuer dequeued more times than expected", 2, deq.numDequeues);
    }

    public void testDequeueMultiple_Timeout_Normal() throws Exception
    {
        DequeueMultipleTester deq = new DequeueMultipleTester(3, 200);
        deq.start();
        nap(100);
        queue.enqueue("1");
        queue.enqueue("2");
        nap(200);
        assertDequeued(TEST12, deq);
    }

    public void testDequeueMultiple_Timeout_Prefilled() throws Exception
    {
        DequeueMultipleTester deq = new DequeueMultipleTester(3, 200);
        deq.atLeast = 2;
        queue.enqueue("1");
        deq.start();
        nap(100);
        assertDequeued(new Object[]{"1"}, deq, !EXPECT_DONE);
        queue.enqueue("2");
        nap(300);
        assertDequeued(TEST12, deq);
        assertEquals("Dequeuer dequeued more times than expected", 2, deq.numDequeues);
    }

    public void testDequeueMultiple_WaitMaxTime() throws Exception
    {
        DequeueMultipleTester deq = new DequeueMultipleTester(3, 700);
        deq.start();
        nap(300);
        assertDequeued(0, deq, !EXPECT_DONE, EXPECT_NULL);
        queue.enqueue("1");
        queue.enqueue("2");
        nap(300);
        assertDequeued(TEST12, deq);
    }

    class DequeueMultipleTester extends DequeueTester
    {
        // config:
        int num;
        int atLeast = -1;

        // result:
        int numDequeues = 0;

        DequeueMultipleTester(int num)
        {
            this(num, -2);
        }
        DequeueMultipleTester(int num, int maxTime)
        {
            super(maxTime, 1, BREAK_ON_NULL);
            this.num = num;
        }
        public Object[] dequeue() throws Exception
        {
            return (timeout >= INF)
                ? queue.dequeueMultiple(timeout, num)
                : queue.dequeueMultiple(num);
        }
        public void execute() throws Exception
        {
            do
            {
                if ((lastResult = dequeue()) != null)
                {
                    resultList.addAll(Arrays.asList(lastResult));
                }
                else if (breakOnNull)
                {
                    break;
                }
                numDequeues++;
            } while (resultList.size() < atLeast);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    // dequeueFully(...) tests:
    //

    public void testDequeueFully_Normal() throws Exception
    {
        DequeueFullyTester deq = new DequeueFullyTester(3, INF, INF);
        deq.start();
        nap(100);
        queue.enqueue("1");
        queue.enqueue("2");
        queue.enqueue("3");
        nap(200);
        assertDequeued(TEST123, deq);
    }

    public void testDequeueFully_Prefilled() throws Exception
    {
        DequeueFullyTester deq = new DequeueFullyTester(3, INF, INF);
        queue.enqueue("1");
        queue.enqueue("2");
        queue.enqueue("3");
        deq.start();
        nap(200);
        assertDequeued(TEST123, deq);
    }

    public void testDequeueFully_PartiallyPrefilled() throws Exception
    {
        DequeueFullyTester deq = new DequeueFullyTester(3, INF, INF);
        queue.enqueue("1");
        deq.start();
        queue.enqueue("2");
        queue.enqueue("3");
        nap(200);
        assertDequeued(TEST123, deq);
    }

    public void testDequeueFully_Timeout_Normal() throws Exception
    {
        DequeueFullyTester deq = new DequeueFullyTester(3, 200, 200);
        deq.start();
        nap(100);
        queue.enqueue("1");
        queue.enqueue("2");
        nap(200);
        assertDequeued(TEST12, deq);
    }

    public void testDequeueFully_Timeout_Prefilled() throws Exception
    {
        DequeueFullyTester deq = new DequeueFullyTester(3, 200, 200);
        queue.enqueue("1");
        deq.start();
        nap(100);
        assertDequeued(0, deq, !EXPECT_DONE, EXPECT_NULL);
        queue.enqueue("2");
        nap(300);
        assertDequeued(TEST12, deq);
    }

    public void testDequeueFully_WaitMaxTime() throws Exception
    {
        DequeueFullyTester deq = new DequeueFullyTester(3, 200, 700);
        deq.start();
        nap(300);
        assertDequeued(0, deq, !EXPECT_DONE, EXPECT_NULL);
        queue.enqueue("1");
        queue.enqueue("2");
        nap(300);
        assertDequeued(TEST12, deq);
    }

    public void testDequeueFully_WaitBlockTime() throws Exception
    {
        DequeueFullyTester deq = new DequeueFullyTester(3, 200, 600);
        deq.start();
        nap(100);
        queue.enqueue("1");
        queue.enqueue("2");
        nap(300);
        assertDequeued(TEST12, deq);
    }

    class DequeueFullyTester extends DequeueTester
    {
        int blockTime;
        int num;
        DequeueFullyTester(int num, int blockTime, int maxTime)
        {
            super(maxTime, 1, BREAK_ON_NULL);
            this.blockTime = blockTime;
            this.num = num;
        }
        public Object[] dequeue() throws Exception
        {
            return queue.dequeueFully(num, blockTime, timeout);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    // dequeueFully(...) (version 2) tests:
    //

    public void testDequeueFully2_Normal() throws Exception
    {
        DequeueFully2Tester deq = new DequeueFully2Tester(3, INF, INF);
        deq.start();
        nap(100);
        queue.enqueue("1");
        queue.enqueue("2");
        queue.enqueue("3");
        nap(200);
        assertDequeued(TEST123, deq);
    }

    public void testDequeueFully2_Prefilled() throws Exception
    {
        DequeueFully2Tester deq = new DequeueFully2Tester(3, INF, INF);
        queue.enqueue("1");
        queue.enqueue("2");
        queue.enqueue("3");
        deq.start();
        nap(200);
        assertDequeued(TEST123, deq);
    }

    public void testDequeueFully2_PartiallyPrefilled() throws Exception
    {
        DequeueFully2Tester deq = new DequeueFully2Tester(3, INF, INF);
        queue.enqueue("1");
        deq.start();
        queue.enqueue("2");
        queue.enqueue("3");
        nap(200);
        assertDequeued(TEST123, deq);
    }

    public void testDequeueFully2_Timeout_Normal() throws Exception
    {
        DequeueFully2Tester deq = new DequeueFully2Tester(3, 200, 200);
        deq.start();
        nap(100);
        queue.enqueue("1");
        queue.enqueue("2");
        nap(200);
        assertDequeued(TEST12, deq);
    }

    public void testDequeueFully2_Timeout_Prefilled() throws Exception
    {
        DequeueFully2Tester deq = new DequeueFully2Tester(3, 200, 200);
        queue.enqueue("1");
        deq.start();
        nap(100);
        assertDequeued(0, deq, !EXPECT_DONE, EXPECT_NULL);
        queue.enqueue("2");
        nap(300);
        assertDequeued(TEST12, deq);
    }

    public void testDequeueFully2_WaitMaxTime() throws Exception
    {
        DequeueFully2Tester deq = new DequeueFully2Tester(3, 200, 700);
        deq.start();
        nap(300);
        assertDequeued(0, deq, !EXPECT_DONE, EXPECT_NULL);
        queue.enqueue("1");
        queue.enqueue("2");
        nap(300);
        assertDequeued(TEST12, deq);
    }

    public void testDequeueFully2_WaitBlockTime() throws Exception
    {
        DequeueFully2Tester deq = new DequeueFully2Tester(3, 200, 600);
        deq.start();
        nap(100);
        queue.enqueue("1");
        queue.enqueue("2");
        nap(300);
        assertDequeued(TEST12, deq);
    }

    /**
     * The "new" dequeue fully method sig
     */
    class DequeueFully2Tester extends DequeueFullyTester
    {
        DequeueFully2Tester(int num, int blockTime, int maxTime)
        {
            super(num, blockTime, maxTime);
        }

        public Object[] dequeue() throws Exception
        {
            Object[] deq = new Object[num];
            int numDeq = queue.dequeueFully(deq, num, blockTime, timeout);
            if (numDeq == 0)
            {
                return null;
            }
            else if (numDeq == num)
            {
                return deq;
            }
            else
            {
                Object[] result = new Object[numDeq];
                System.arraycopy(deq, 0, result, 0, numDeq);
                return result;
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    // Supporting methods / inner base classes:

    protected void mark()
    {
        markTime = System.currentTimeMillis();
    }

    protected void assertMinElapsed(int millis)
    {
        assertTrue("Mark not set!", markTime!=0);
        long now = System.currentTimeMillis();
        assertTrue("Not enough time elapsed: " + (now-markTime) + "<" + markTime, now - markTime >= millis);
    }

    protected void assertMaxElapsed(int millis)
    {
        assertTrue("Mark not set!", markTime!=0);
        long now = System.currentTimeMillis();
        assertTrue("Too much time elapsed: " + (now-markTime) + ">" + millis, now - markTime <= millis);
    }

    protected void assertDequeued(Object[] expected, DequeueTester deq)
    {
        assertDequeued(expected, deq, EXPECT_DONE);
    }

    protected void assertDequeued(Object[] expected, DequeueTester deq, boolean expectDone)
    {
        assertDequeued(expected.length, deq, expectDone, !EXPECT_NULL);
        for (int i=0; i < expected.length; i++)
        {
            assertEquals("Unexpected result [" + i + "]", expected[i], deq.resultList.get(i));
        }
    }

    protected void assertDequeued(int numExpected, DequeueTester deq, boolean expectDone, boolean expectNull)
    {
        assertTrue("Dequeue thread should " + (expectDone?"":"not ") + "have terminated by now.", (expectDone && !deq.isAlive() || !expectDone && deq.isAlive()) ); // XOR
        assertTrue("Dequeue thread threw throwable " + deq.throwable, deq.throwable==null);
        assertTrue("Dequeued " + (expectNull?"not ":"") + "null", (expectNull && deq.lastResult==null || !expectNull && deq.lastResult != null)); // XOR
        assertEquals("Dequeued incorrect # elements; resultlist=" + deq.resultList, numExpected, deq.resultList.size());
    }

    protected void nap(int millis)
    {
        try { Thread.currentThread().sleep(millis); } catch (Exception ex) {}
    }

    class DequeueTester extends Thread
    {
        // test params:
        int numDequeues;
        boolean breakOnNull;
        int timeout;
        Class expectedThrowable = null;

        // test results:
        Throwable throwable = null;
        Object[] lastResult;
        ArrayList resultList = new ArrayList();

        DequeueTester(int timeout, int numDeq, boolean breakOnNull)
        {
            this.timeout = timeout;
            this.numDequeues = numDeq;
            this.breakOnNull = breakOnNull;
        }
        public Object[] dequeue() throws Exception
        {
            Object obj = queue.dequeue(timeout);
            return (obj == null) ? null : new Object[] { obj };
        }
        public void execute() throws Exception
        {
            for (int i=0; i < numDequeues; i++)
            {
                if ((lastResult = dequeue()) != null)
                {
                    resultList.addAll(Arrays.asList(lastResult));
                }
                else if (breakOnNull)
                {
                    break;
                }
            }
        }
        public void run()
        {
            try
            {
                execute();
            }
            catch (ThreadDeath td)
            {
                throw td;
            }
            catch (Throwable ex)
            {
                if (expectedThrowable == null || !expectedThrowable.isInstance(ex))
                {
                    ex.printStackTrace();
                }
                throwable = ex;
            }
        }

    }

}
