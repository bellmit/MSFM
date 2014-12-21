package com.cboe.client.util.queue;

import com.cboe.client.util.collections.ObjectArrayHolder;
import com.cboe.client.util.collections.ObjectObjectComparisonPolicy;
import com.cboe.client.util.collections.ObjectVisitorIF;
import com.cboe.client.util.queue.SinglePriorityEventChannel.EventChannelInstrumentation;
import com.cboe.instrumentationService.instrumentors.QueueInstrumentor;
import junit.framework.JUnit4TestAdapter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import org.junit.Test;      // annotation


public class SinglePriorityEventChannelTest
{
    // Allow this JUnit 4 test to run under JUnit3
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(SinglePriorityEventChannelTest.class);
    }

    private static final String A = "A";
    private static final String B = "B";
    private static final String C = "C";
    private static final String ABC[] = { A, B, C };

    @Test public void testConstructor()
    {
        SinglePriorityEventChannel spec = new SinglePriorityEventChannel();
        assertEquals(128, spec.normalPriorityChannel.capacity());
        assertEquals(0, spec.normalPriorityChannel.size());

        spec = new SinglePriorityEventChannel(20);
        assertEquals(32, spec.normalPriorityChannel.capacity());
        assertEquals(0, spec.normalPriorityChannel.size());
    }

    @Test public void testGetEventChannelInstrumentation() throws Exception
    {
        SinglePriorityEventChannel spec = new SinglePriorityEventChannel();
        EventChannelInstrumentation eci = (EventChannelInstrumentation)
                spec.getEventChannelInstrumentation();
        assertEquals(0, eci.totalDequeued());
        assertEquals(0, eci.totalEnqueued());
        assertEquals(0, eci.totalFlushed());
        assertEquals(0, eci.highWaterMark());
        assertEquals(0, eci.currentSize());
        assertEquals(0, eci.currentDepth());

        QueueInstrumentor qi = new QueueInstrumentorForTest();
        spec.normalPriorityChannel.setQueueInstrumentor(qi);
        Object o = new Object();
        spec.enqueue(o);
        spec.enqueue(o);
        spec.dequeue();
        eci = (EventChannelInstrumentation)
                spec.getEventChannelInstrumentation();
        assertEquals(1, eci.totalDequeued());
        assertEquals(2, eci.totalEnqueued());
        assertEquals(0, eci.totalFlushed());
        assertEquals(2, eci.highWaterMark());
        assertEquals(1, eci.currentSize());
        assertEquals(1, eci.currentDepth());
    }

    @Test public void testGetQueueInstrumentor()
    {
        SinglePriorityEventChannel spec = new SinglePriorityEventChannel();
        assertNull(spec.getQueueInstrumentor());

        QueueInstrumentor qi = new QueueInstrumentorForTest();
        spec.normalPriorityChannel.setQueueInstrumentor(qi);
        assertSame(qi, spec.getQueueInstrumentor());
    }

    @Test public void testEnqueue() throws Exception
    {
        SinglePriorityEventChannel spec = new SinglePriorityEventChannel();
        spec.enqueue(A);
        spec.enqueue(B);
        spec.enqueue(C);
        spec.enqueue(A);
        assertSame(A, spec.dequeue());
        assertSame(B, spec.dequeue());
        assertSame(C, spec.dequeue());
        assertSame(A, spec.dequeue());
        assertEquals(0, spec.size());

        assertTrue(spec.enqueue(A,
                ObjectObjectComparisonPolicy.RejectSameObjectComparisonPolicy));
        assertTrue(spec.enqueue(B,
                ObjectObjectComparisonPolicy.RejectSameObjectComparisonPolicy));
        assertTrue(spec.enqueue(C,
                ObjectObjectComparisonPolicy.RejectSameObjectComparisonPolicy));
        assertFalse(spec.enqueue(A,
                ObjectObjectComparisonPolicy.RejectSameObjectComparisonPolicy));
        assertEquals(3, spec.size());
        assertSame(A, spec.dequeue());
        assertSame(B, spec.dequeue());
        assertSame(C, spec.dequeue());
    }

    @Test public void testEnqueueAll() throws Exception
    {
        SinglePriorityEventChannel spec = new SinglePriorityEventChannel();
        spec.enqueueAll(ABC);
        assertEquals(3, spec.size());
        assertSame(A, spec.dequeue());
        assertSame(B, spec.dequeue());
        assertSame(C, spec.dequeue());
        assertEquals(0, spec.size());

        spec.enqueue(A,
                ObjectObjectComparisonPolicy.RejectSameObjectComparisonPolicy);
        spec.enqueueAll(ABC,
                ObjectObjectComparisonPolicy.RejectSameObjectComparisonPolicy);
        assertEquals(1, spec.size());
        assertSame(A, spec.dequeue());
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
        SinglePriorityEventChannel ec;
        public Enqueuer(SinglePriorityEventChannel dpec)
        {
            ec = dpec;
        }
        public void run()
        {
            pause(1000);
            ec.enqueue(C);
        }
    }

    @Test public void testDequeue() throws Exception
    {
        SinglePriorityEventChannel spec = new SinglePriorityEventChannel();
        spec.enqueue(A);
        spec.enqueue(B);
        assertSame(A, spec.dequeue());
        assertSame(B, spec.dequeue());
        new Enqueuer(spec).start();
        assertSame(C, spec.dequeue());  // block until Enqueuer does its job

        String missing = "missing";
        spec.enqueue(A);
        spec.enqueue(B);
        spec.enqueue(C);
        assertSame(A, spec.dequeue(500, missing));
        assertSame(B, spec.dequeue(0, missing));
        assertSame(C, spec.dequeue(500, missing));
        assertSame(missing, spec.dequeue(500, missing));
    }

    @Test public void testDequeueAll() throws Exception
    {
        SinglePriorityEventChannel spec = new SinglePriorityEventChannel();
        ObjectArrayHolder oah = new ObjectArrayHolder();
        spec.dequeueAll(oah);
        assertEquals(0, oah.size());

        spec.enqueue(A);
        spec.enqueueAll(ABC);
        spec.dequeueAll(oah);
        assertEquals(4, oah.size());
        assertSame(A, oah.getKey(0));
        assertSame(A, oah.getKey(1));
        assertSame(B, oah.getKey(2));
        assertSame(C, oah.getKey(3));
    }

    @Test public void testFlush() throws Exception
    {
        SinglePriorityEventChannel dpec = new SinglePriorityEventChannel();
        dpec.enqueue(A);
        dpec.enqueue(B);

        ObjectArrayHolder oah = new ObjectArrayHolder();
        dpec.flush(oah);
        assertEquals(2, oah.size());
        assertSame(A, oah.getKey(0));
        assertSame(B, oah.getKey(1));
    }

    @Test public void testSize()
    {
        SinglePriorityEventChannel dpec = new SinglePriorityEventChannel();
        assertEquals(0, dpec.size());
        dpec.enqueue(A);
        assertEquals(1, dpec.size());
        dpec.enqueue(B);
        assertEquals(2, dpec.size());
    }

    class Visitor implements ObjectVisitorIF
    {
        StringBuilder result;
        Visitor(StringBuilder sb) { result = sb; }
        public int visit(Object o) { result.append(' ').append(o); return 0; }
    }

    @Test public void testAcceptVisitor()
    {
        SinglePriorityEventChannel dpec = new SinglePriorityEventChannel();
        StringBuilder sb = new StringBuilder();
        Visitor v = new Visitor(sb);
        dpec.acceptVisitor(v);
        assertEquals("", sb.toString());

        dpec.enqueue(A);
        dpec.enqueue(B);
        dpec.acceptVisitor(v);
        assertEquals(" A B", sb.toString());
    }

    @Test public void testIncOverlaid()
    {
        SinglePriorityEventChannel dpec = new SinglePriorityEventChannel();
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
        SinglePriorityEventChannel dpec = new SinglePriorityEventChannel();
        assertNull(dpec.normalPriorityChannel.getQueueInstrumentor());

        QueueInstrumentor qi = new QueueInstrumentorForTest();
        dpec.setQueueInstrumentor(qi);
        assertSame(qi, dpec.normalPriorityChannel.getQueueInstrumentor());
    }
}
