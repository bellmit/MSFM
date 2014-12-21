package com.cboe.client.util.queue;

import com.cboe.client.util.collections.ObjectArrayHolder;
import com.cboe.client.util.collections.ObjectObjectComparisonPolicy;
import com.cboe.client.util.collections.ObjectVisitorIF;
import com.cboe.client.util.queue.DoublePriorityEventChannel.EventChannelInstrumentation;
import com.cboe.instrumentationService.instrumentors.QueueInstrumentor;
import junit.framework.JUnit4TestAdapter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import org.junit.Test;      // annotation


public class DoublePriorityEventChannelTest
{
    // Allow this JUnit 4 test to run under JUnit3
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(DoublePriorityEventChannelTest.class);
    }

    private static final String A = "A";
    private static final String B = "B";
    private static final String C = "C";
    private static final String ABC[] = { A, B, C };

    @Test public void testConstructor()
    {
        DoublePriorityEventChannel dpec = new DoublePriorityEventChannel();
        assertEquals(128, dpec.normalPriorityChannel.capacity());
        assertEquals(16, dpec.highPriorityChannel.capacity());
        assertEquals(0, dpec.normalPriorityChannel.size());
        assertEquals(0, dpec.highPriorityChannel.size());

        dpec = new DoublePriorityEventChannel(42);
        assertEquals(64, dpec.normalPriorityChannel.capacity());
        assertEquals(16, dpec.highPriorityChannel.capacity());
        assertEquals(0, dpec.normalPriorityChannel.size());
        assertEquals(0, dpec.highPriorityChannel.size());

        dpec = new DoublePriorityEventChannel(15, 30);
        assertEquals(16, dpec.normalPriorityChannel.capacity());
        assertEquals(32, dpec.highPriorityChannel.capacity());
        assertEquals(0, dpec.normalPriorityChannel.size());
        assertEquals(0, dpec.highPriorityChannel.size());
    }

    @Test public void testGetEventChannelInstrumentation() throws Exception
    {
        DoublePriorityEventChannel dpec = new DoublePriorityEventChannel();
        EventChannelInstrumentation eci = (EventChannelInstrumentation)
                dpec.getEventChannelInstrumentation();
        assertEquals(0, eci.totalDequeued());
        assertEquals(0, eci.totalEnqueued());
        assertEquals(0, eci.totalFlushed());
        assertEquals(0, eci.highWaterMark());
        assertEquals(0, eci.currentSize());
        assertEquals(0, eci.currentDepth());

        QueueInstrumentor qi = new QueueInstrumentorForTest();
        dpec.normalPriorityChannel.setQueueInstrumentor(qi);
        Object o = new Object();
        dpec.enqueue(o);
        dpec.enqueueHighPriority(o); // not part of Enqueued count
        dpec.enqueue(o);
        dpec.dequeue(); // takes high-priority item, not part of Dequeued count
        eci = (EventChannelInstrumentation)
                dpec.getEventChannelInstrumentation();
        assertEquals(0, eci.totalDequeued());
        assertEquals(2, eci.totalEnqueued());
        assertEquals(0, eci.totalFlushed());
        assertEquals(2, eci.highWaterMark());
        assertEquals(2, eci.currentSize());
        assertEquals(2, eci.currentDepth());
    }

    @Test public void testGetQueueInstrumentor()
    {
        DoublePriorityEventChannel dpec = new DoublePriorityEventChannel();
        assertNull(dpec.getQueueInstrumentor());

        QueueInstrumentor qi = new QueueInstrumentorForTest();
        dpec.normalPriorityChannel.setQueueInstrumentor(qi);
        assertSame(qi, dpec.getQueueInstrumentor());
    }

    @Test public void testEnqueue() throws Exception
    {
        DoublePriorityEventChannel dpec = new DoublePriorityEventChannel();
        dpec.enqueue(A);
        dpec.enqueue(B);
        dpec.enqueue(C);
        dpec.enqueue(A);
        assertSame(A, dpec.dequeue());
        assertSame(B, dpec.dequeue());
        assertSame(C, dpec.dequeue());
        assertSame(A, dpec.dequeue());
        assertEquals(0, dpec.size());

        assertTrue(dpec.enqueue(A,
                ObjectObjectComparisonPolicy.RejectSameObjectComparisonPolicy));
        assertTrue(dpec.enqueue(B,
                ObjectObjectComparisonPolicy.RejectSameObjectComparisonPolicy));
        assertTrue(dpec.enqueue(C,
                ObjectObjectComparisonPolicy.RejectSameObjectComparisonPolicy));
        assertFalse(dpec.enqueue(A,
                ObjectObjectComparisonPolicy.RejectSameObjectComparisonPolicy));
        assertEquals(3, dpec.size());
        assertSame(A, dpec.dequeue());
        assertSame(B, dpec.dequeue());
        assertSame(C, dpec.dequeue());
    }

    @Test public void testEnqueueAll() throws Exception
    {
        DoublePriorityEventChannel dpec = new DoublePriorityEventChannel();
        dpec.enqueueAll(ABC);
        assertEquals(3, dpec.size());
        assertSame(A, dpec.dequeue());
        assertSame(B, dpec.dequeue());
        assertSame(C, dpec.dequeue());
        assertEquals(0, dpec.size());

        dpec.enqueue(A,
                ObjectObjectComparisonPolicy.RejectSameObjectComparisonPolicy);
        dpec.enqueueAll(ABC,
                ObjectObjectComparisonPolicy.RejectSameObjectComparisonPolicy);
        assertEquals(1, dpec.size());
        assertSame(A, dpec.dequeue());
    }

    @Test public void testEnqueueHighPriority() throws Exception
    {
        DoublePriorityEventChannel dpec = new DoublePriorityEventChannel();
        dpec.enqueueHighPriority(A);
        dpec.enqueueHighPriority(B,
                ObjectObjectComparisonPolicy.RejectSameObjectComparisonPolicy);
        dpec.enqueueHighPriority(A,
                ObjectObjectComparisonPolicy.RejectSameObjectComparisonPolicy);
        dpec.enqueueHighPriority(B);
        assertEquals(0, dpec.normalPriorityChannel.size());
        assertEquals(3, dpec.highPriorityChannel.size());
        assertSame(A, dpec.dequeue());
        assertSame(B, dpec.dequeue());
        assertSame(B, dpec.dequeue());        
    }

    @Test public void testEnqueueHighPriorityFront() throws Exception
    {
        DoublePriorityEventChannel dpec = new DoublePriorityEventChannel();
        dpec.enqueueHighPriorityFront(A);
        dpec.enqueueHighPriorityFront(B);
        dpec.enqueueHighPriorityFront(C,
                ObjectObjectComparisonPolicy.RejectSameObjectComparisonPolicy);
        dpec.enqueueHighPriorityFront(A,
                ObjectObjectComparisonPolicy.RejectSameObjectComparisonPolicy);
        assertEquals(0, dpec.normalPriorityChannel.size());
        assertEquals(3, dpec.highPriorityChannel.size());
        assertSame(C, dpec.dequeue());
        assertSame(B, dpec.dequeue());
        assertSame(A, dpec.dequeue());
    }

    @Test public void testEnqueueHighPriorityAll() throws Exception
    {
        DoublePriorityEventChannel dpec = new DoublePriorityEventChannel();
        dpec.enqueue(A);
        dpec.enqueueHighPriority(C);
        dpec.enqueueHighPriorityAll(ABC,
                ObjectObjectComparisonPolicy.RejectSameObjectComparisonPolicy);
        assertEquals(1, dpec.highPriorityChannel.size());
        dpec.enqueueHighPriorityAll(ABC);
        assertEquals(4, dpec.highPriorityChannel.size());
        assertSame(C, dpec.dequeue());
        assertSame(A, dpec.dequeue());
        assertSame(B, dpec.dequeue());
        assertSame(C, dpec.dequeue());
        assertSame(A, dpec.dequeue());
    }

    @Test public void testEnqueueHighPriorityFrontAll() throws Exception
    {
        DoublePriorityEventChannel dpec = new DoublePriorityEventChannel();
        dpec.enqueue(A);
        dpec.enqueueHighPriority(C);
        dpec.enqueueHighPriorityFrontAll(ABC,
                ObjectObjectComparisonPolicy.RejectSameObjectComparisonPolicy);
        assertEquals(1, dpec.highPriorityChannel.size());
        dpec.enqueueHighPriorityFrontAll(ABC);
        assertEquals(4, dpec.highPriorityChannel.size());
        assertSame(A, dpec.dequeue());
        assertSame(B, dpec.dequeue());
        assertSame(C, dpec.dequeue());
        assertSame(C, dpec.dequeue());
        assertSame(A, dpec.dequeue());
    }

    private void pause(int ms)
    {
        try
        {
            Thread.sleep(ms);
        }
        catch (InterruptedException e)
        {
            // We don't care that sleep ended early
        }
    }

    private class Enqueuer extends Thread
    {
        DoublePriorityEventChannel ec;
        public Enqueuer(DoublePriorityEventChannel dpec)
        {
            ec = dpec;
        }
        public void run()
        {
            pause(1000);
            ec.enqueueHighPriority(C);
        }
    }

    @Test public void testDequeue() throws Exception
    {
        DoublePriorityEventChannel dpec = new DoublePriorityEventChannel();
        dpec.enqueue(A);
        dpec.enqueueHighPriority(B);
        assertSame(B, dpec.dequeue());
        assertSame(A, dpec.dequeue());
        new Enqueuer(dpec).start();
        assertSame(C, dpec.dequeue());  // block until Enqueuer does its job

        String missing = "missing";
        dpec.enqueue(B);
        dpec.enqueue(C);
        dpec.enqueueHighPriority(A);
        assertSame(A, dpec.dequeue(500, missing));
        assertSame(B, dpec.dequeue(0, missing));
        assertSame(C, dpec.dequeue(500, missing));
        assertSame(missing, dpec.dequeue(500, missing));
    }

    @Test public void testDequeueHighPriority() throws Exception
    {
        DoublePriorityEventChannel dpec = new DoublePriorityEventChannel();
        dpec.enqueue(A);
        dpec.enqueueHighPriority(B);
        assertSame(B, dpec.dequeueHighPriority());
        new Enqueuer(dpec).start();
        assertSame(C, dpec.dequeueHighPriority());  // waits for Enqueuer

        String missing = "missing";
        dpec = new DoublePriorityEventChannel();
        dpec.enqueueHighPriorityAll(ABC);
        assertSame(A, dpec.dequeue(0, missing));
        assertSame(B, dpec.dequeue(500, missing));
        assertSame(C, dpec.dequeue(500, missing));
        assertSame(missing, dpec.dequeue(500, missing));

        ObjectArrayHolder oah = new ObjectArrayHolder();
        new Enqueuer(dpec).start();
        dpec.dequeueHighPriority(oah);  // waits for Enqueuer
        assertEquals(1, oah.size());
        assertSame(C, oah.getKey(0));

        dpec.enqueueHighPriority(C);
        dpec.enqueueHighPriority(B);
        oah = new ObjectArrayHolder();
        dpec.dequeueHighPriority(oah);
        assertEquals(2, oah.size());
        assertSame(C, oah.getKey(0));
        assertSame(B, oah.getKey(1));
    }

    @Test public void testDequeueAll() throws Exception
    {
        DoublePriorityEventChannel dpec = new DoublePriorityEventChannel();
        ObjectArrayHolder oah = new ObjectArrayHolder();
        dpec.dequeueAll(oah);
        assertEquals(0, oah.size());

        dpec.enqueueAll(ABC);
        dpec.enqueueHighPriority(A);
        dpec.dequeueAll(oah);
        assertEquals(4, oah.size());
        assertSame(A, oah.getKey(0));
        assertSame(A, oah.getKey(1));
        assertSame(B, oah.getKey(2));
        assertSame(C, oah.getKey(3));
    }

    @Test public void testDequeueHighPriorityAll() throws Exception
    {
        DoublePriorityEventChannel dpec = new DoublePriorityEventChannel();
        ObjectArrayHolder oah = new ObjectArrayHolder();
        dpec.dequeueHighPriorityAll(oah);
        assertEquals(0, oah.size());

        dpec.enqueue(A);
        dpec.enqueueHighPriority(B);
        dpec.dequeueHighPriorityAll(oah);
        assertEquals(1, oah.size());
        assertSame(B, oah.getKey(0));
    }

    @Test public void testFlush() throws Exception
    {
        DoublePriorityEventChannel dpec = new DoublePriorityEventChannel();
        dpec.enqueue(A);
        dpec.enqueue(B);
        dpec.enqueueHighPriority(C);

        ObjectArrayHolder oah = new ObjectArrayHolder();
        dpec.flush(oah);
        assertEquals(2, oah.size());
        assertSame(A, oah.getKey(0));
        assertSame(B, oah.getKey(1));
    }

    @Test public void testFlushNormalPriority() throws Exception
    {
        DoublePriorityEventChannel dpec = new DoublePriorityEventChannel();
        dpec.enqueue(A);
        dpec.enqueue(B);
        dpec.enqueueHighPriority(C);

        ObjectArrayHolder oah = new ObjectArrayHolder();
        dpec.flushNormalPriority(oah);
        assertEquals(2, oah.size());
        assertSame(A, oah.getKey(0));
        assertSame(B, oah.getKey(1));
    }

    @Test public void testFlushHighPriority() throws Exception
    {
        DoublePriorityEventChannel dpec = new DoublePriorityEventChannel();
        dpec.enqueue(A);
        dpec.enqueue(B);
        dpec.enqueueHighPriority(C);

        ObjectArrayHolder oah = new ObjectArrayHolder();
        dpec.flushHighPriority(oah);
        assertEquals(1, oah.size());
        assertSame(C, oah.getKey(0));
    }

    @Test public void testSize()
    {
        DoublePriorityEventChannel dpec = new DoublePriorityEventChannel();
        assertEquals(0, dpec.size());
        dpec.enqueue(A);
        assertEquals(1, dpec.size());
        dpec.enqueue(B);
        assertEquals(2, dpec.size());
        dpec.enqueueHighPriority(A);
        assertEquals(3, dpec.size());
        dpec.enqueueHighPriority(B);
        assertEquals(4, dpec.size());
    }

    @Test public void testNormalPrioritySize()
    {
        DoublePriorityEventChannel dpec = new DoublePriorityEventChannel();
        assertEquals(0, dpec.normalPrioritySize());
        dpec.enqueue(A);
        assertEquals(1, dpec.normalPrioritySize());
        dpec.enqueue(B);
        assertEquals(2, dpec.normalPrioritySize());
        dpec.enqueueHighPriority(A);
        assertEquals(2, dpec.normalPrioritySize());
        dpec.enqueueHighPriority(B);
        assertEquals(2, dpec.normalPrioritySize());
    }

    @Test public void testHighPrioritySize()
    {
        DoublePriorityEventChannel dpec = new DoublePriorityEventChannel();
        assertEquals(0, dpec.highPrioritySize());
        dpec.enqueue(A);
        assertEquals(0, dpec.highPrioritySize());
        dpec.enqueue(B);
        assertEquals(0, dpec.highPrioritySize());
        dpec.enqueueHighPriority(A);
        assertEquals(1, dpec.highPrioritySize());
        dpec.enqueueHighPriority(B);
        assertEquals(2, dpec.highPrioritySize());
    }

    class Visitor implements ObjectVisitorIF
    {
        StringBuilder result;
        Visitor(StringBuilder sb) { result = sb; }
        public int visit(Object o) { result.append(' ').append(o); return 0; }
    }

    @Test public void testAcceptVisitor()
    {
        DoublePriorityEventChannel dpec = new DoublePriorityEventChannel();
        StringBuilder sb = new StringBuilder();
        Visitor v = new Visitor(sb);
        dpec.acceptVisitor(v);
        assertEquals("", sb.toString());

        dpec.enqueue(A);
        dpec.enqueue(B);
        dpec.enqueueHighPriority(C);
        dpec.acceptVisitor(v);
        assertEquals(" C A B", sb.toString());
    }

    @Test public void testIncOverlaid()
    {
        DoublePriorityEventChannel dpec = new DoublePriorityEventChannel();
        QueueInstrumentor qi = new QueueInstrumentorForTest();
        dpec.normalPriorityChannel.setQueueInstrumentor(qi);
        assertEquals(0, qi.getOverlaid());

        dpec.incOverlaid(2);
        assertEquals(2, qi.getOverlaid());
        dpec.incOverlaid(1);
        assertEquals(3, qi.getOverlaid());
    }

    @Test public void testSetQueueInstrumentor()
    {
        DoublePriorityEventChannel dpec = new DoublePriorityEventChannel();
        assertNull(dpec.normalPriorityChannel.getQueueInstrumentor());
        assertNull(dpec.highPriorityChannel.getQueueInstrumentor());

        QueueInstrumentor qi = new QueueInstrumentorForTest();
        dpec.setQueueInstrumentor(qi);
        assertSame(qi, dpec.normalPriorityChannel.getQueueInstrumentor());
        assertSame(qi, dpec.highPriorityChannel.getQueueInstrumentor());
    }
}
