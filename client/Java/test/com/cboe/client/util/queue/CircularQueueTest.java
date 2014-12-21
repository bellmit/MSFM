package com.cboe.client.util.queue;

import com.cboe.client.util.collections.ObjectArrayHolder;
import com.cboe.client.util.collections.ObjectObjectComparisonPolicy;
import com.cboe.client.util.collections.ObjectInspectionPolicyIF;
import com.cboe.client.util.collections.ObjectVisitorIF;
import com.cboe.instrumentationService.instrumentors.QueueInstrumentor;
import junit.framework.JUnit4TestAdapter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import org.junit.Test;      // annotation
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class CircularQueueTest
{
    // Allow this JUnit 4 test to run under JUnit3
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(CircularQueueTest.class);
    }

    private final static Integer ONE = 1;
    private final static Integer TWO = 2;
    private final static Integer THREE = 3;
    private final static Integer FOUR = 4;
    private final static Integer FIVE = 5;
    private final static Integer SIX = 6;
    private final static Integer four[] = { 1, 2, 3, 4 };

    // Test constructors, capacity(), size(), setQueueInstrumentor(),
    // getQueueInstrumentor()
    @Test public void testConstructor()
    {
        CircularQueue cq = new CircularQueue();
        assertEquals(64, cq.capacity());
        assertEquals(0, cq.size());
        assertNull(cq.getQueueInstrumentor());

        QueueInstrumentor qi = mock(QueueInstrumentor.class);
        cq = new CircularQueue(qi);
        assertEquals(64, cq.capacity());
        assertEquals(0, cq.size());
        assertSame(qi, cq.getQueueInstrumentor());

        cq = new CircularQueue(30);
        assertEquals(32, cq.capacity());
        assertEquals(0, cq.size());
        assertNull(cq.getQueueInstrumentor());

        cq = new CircularQueue(35, qi);
        assertEquals(64, cq.capacity());
        assertEquals(0, cq.size());
        assertSame(qi, cq.getQueueInstrumentor());
    }

    @Test public void testAdd()
    {
        QueueInstrumentor qi = mock(QueueInstrumentor.class);
        CircularQueue cq = new CircularQueue(2, qi);
        when(qi.getHighWaterMark()).thenReturn(0L);
        assertEquals(2, cq.capacity());
        assertEquals(0, cq.size());
        assertTrue(cq.add(ONE));
        assertEquals(2, cq.capacity());
        assertEquals(1, cq.size());
        assertTrue(cq.add(TWO));
        assertEquals(2, cq.capacity());
        assertEquals(2, cq.size());

        assertTrue(1 == (Integer) cq.remove());
        assertEquals(2, cq.capacity());
        assertEquals(1, cq.size());
        assertTrue(2 == (Integer) cq.remove());
        assertEquals(2, cq.capacity());
        assertEquals(0, cq.size());

        verify(qi, times(2)).incEnqueued(1L);
        verify(qi).setHighWaterMark(1L);
        verify(qi).setHighWaterMark(2L);
        verify(qi, times(2)).incDequeued(1L);
        assertNull(cq.remove());

        cq = new CircularQueue(2);
        assertTrue(cq.add(ONE));
        assertTrue(cq.add(TWO));
        assertTrue(cq.add(THREE));
        assertEquals(4, cq.capacity());
        assertEquals(3, cq.size());
        assertTrue(1 == (Integer) cq.remove());
        assertTrue(cq.add(FOUR));
        assertTrue(cq.add(FIVE));
        assertEquals(4, cq.capacity());
        assertEquals(4, cq.size());
        assertTrue(2 == (Integer) cq.remove());
        assertTrue(3 == (Integer) cq.remove());
        assertTrue(4 == (Integer) cq.remove());
        assertTrue(5 == (Integer) cq.remove());
        assertEquals(0, cq.size());
        assertEquals(4, cq.capacity());

        cq = new CircularQueue(4);
        assertTrue(cq.add(ONE));
        assertTrue(cq.add(TWO));
        assertFalse(cq.add(ONE,
                ObjectObjectComparisonPolicy.RejectSameObjectComparisonPolicy));
        assertTrue(cq.add(THREE,
                ObjectObjectComparisonPolicy.RejectSameObjectComparisonPolicy));
        assertEquals(3, cq.size());
    }

    @Test public void testAddAll()
    {
        QueueInstrumentor qi = mock(QueueInstrumentor.class);
        CircularQueue cq = new CircularQueue(2, qi);
        Integer three[] = { 1, 2, 3 };
        when(qi.getHighWaterMark()).thenReturn(0L);
        assertTrue(cq.addAll(three));
        verify(qi).setHighWaterMark(3L);
        assertTrue(1 == (Integer) cq.remove());
        assertTrue(2 == (Integer) cq.remove());
        assertTrue(3 == (Integer) cq.remove());

        cq = new CircularQueue(2);
        assertTrue(cq.add(TWO));
        assertFalse(cq.addAll(three,
                ObjectObjectComparisonPolicy.RejectSameObjectComparisonPolicy));
        assertEquals(1, cq.size());
        assertEquals(2, cq.capacity());
        assertTrue(2 == (Integer) cq.remove());

        qi = mock(QueueInstrumentor.class);
        when(qi.getHighWaterMark()).thenReturn(0L);
        cq = new CircularQueue(2, qi);
        assertTrue(cq.addAll(four, 1, 3));
        verify(qi).incEnqueued(3L);
        verify(qi).setHighWaterMark(3L);
        assertEquals(3, cq.size());
        assertEquals(4, cq.capacity());
        assertTrue(2 == (Integer) cq.remove());
        assertTrue(3 == (Integer) cq.remove());
        assertTrue(4 == (Integer) cq.remove());

        cq = new CircularQueue(2);
        assertTrue(cq.add(FOUR));
        assertFalse(cq.addAll(four, 1, 3,
                ObjectObjectComparisonPolicy.RejectSameObjectComparisonPolicy));
        assertEquals(1, cq.size());
        assertEquals(2, cq.capacity());
        assertTrue(4 == (Integer) cq.remove());
    }

    @Test public void testInsert()
    {
        QueueInstrumentor qi = mock(QueueInstrumentor.class);
        CircularQueue cq = new CircularQueue(2, qi);

        when(qi.getHighWaterMark()).thenReturn(0L);
        cq.insert(ONE);
        cq.insert(TWO);
        assertEquals(2, cq.size());
        assertEquals(2, cq.capacity());
        assertTrue(2 == (Integer) cq.remove());
        assertTrue(1 == (Integer) cq.remove());
        verify(qi, times(2)).incEnqueued(1L);
        verify(qi).setHighWaterMark(1L);
        verify(qi).setHighWaterMark(2L);
        verify(qi, times(2)).incDequeued(1L);
        assertNull(cq.remove());

        assertTrue(cq.insert(TWO,
                ObjectObjectComparisonPolicy.RejectSameObjectComparisonPolicy));
        assertFalse(cq.insert(TWO,
                ObjectObjectComparisonPolicy.RejectSameObjectComparisonPolicy));
        assertTrue(cq.insert(THREE,
                ObjectObjectComparisonPolicy.RejectSameObjectComparisonPolicy));
        assertEquals(2, cq.size());
        assertTrue(3 == (Integer) cq.remove());
        assertTrue(2 == (Integer) cq.remove());
    }

    @Test public void testRemove()
    {
        QueueInstrumentor qi = mock(QueueInstrumentor.class);
        CircularQueue cq = new CircularQueue(2, qi);
        assertNull(cq.remove());

        when(qi.getHighWaterMark()).thenReturn(0L);
        cq.add(ONE);
        cq.add(TWO);
        assertEquals(2, cq.size());
        assertTrue(1 == (Integer) cq.remove());
        assertTrue(2 == (Integer) cq.remove());
        verify(qi, times(2)).incDequeued(1L);

        qi = mock(QueueInstrumentor.class);
        cq = new CircularQueue(4, qi);
        assertTrue(cq.addAll(four));
        assertTrue(1 == (Integer) cq.remove());
        assertTrue(cq.add(FIVE));
        // contents now 2 3 4 5
        assertEquals(2, cq.remove(new ObjectInspectionPolicyIF() {
            public boolean inspect(Object o) { return (Integer) o % 2 == 1;}
            } ));
        assertEquals(2, cq.size());
        assertTrue(2 == (Integer) cq.remove());
        assertTrue(4 == (Integer) cq.remove());
        verify(qi).incDequeued(2);
    }

    @Test public void testAllowedByPolicy()
    {
        CircularQueue cq = new CircularQueue(4);
        cq.addAll(four);
        assertFalse(cq.allowedByPolicy(THREE,
                ObjectObjectComparisonPolicy.RejectSameObjectComparisonPolicy));
        assertTrue(cq.allowedByPolicy(THREE,
                ObjectObjectComparisonPolicy.AcceptSameObjectComparisonPolicy));
        assertTrue(cq.allowedByPolicy(FIVE,
                ObjectObjectComparisonPolicy.AcceptSameObjectComparisonPolicy));
        cq.remove();
        cq.add(FIVE);
        assertFalse(cq.allowedByPolicy(THREE,
                ObjectObjectComparisonPolicy.RejectSameObjectComparisonPolicy));
        assertTrue(cq.allowedByPolicy(THREE,
                ObjectObjectComparisonPolicy.AcceptSameObjectComparisonPolicy));
        assertTrue(cq.allowedByPolicy(ONE,
                ObjectObjectComparisonPolicy.AcceptSameObjectComparisonPolicy));
    }

    @Test public void testGetData()
    {
        CircularQueue cq = new CircularQueue(2);
        cq.addAll(four);
        ObjectArrayHolder oah = new ObjectArrayHolder();
        cq.getData(oah);
        assertEquals(4, oah.size());
        assertTrue(1 == (Integer) oah.getKey(0));
        assertTrue(2 == (Integer) oah.getKey(1));
        assertTrue(3 == (Integer) oah.getKey(2));
        assertTrue(4 == (Integer) oah.getKey(3));

        assertTrue(1 == (Integer) cq.remove());
        assertTrue(2 == (Integer) cq.remove());
        cq.add(FIVE);
        cq.add(SIX);
        oah = new ObjectArrayHolder();
        cq.getData(oah);
        assertEquals(4, oah.size());
        assertTrue(3 == (Integer) oah.getKey(0));
        assertTrue(4 == (Integer) oah.getKey(1));
        assertTrue(5 == (Integer) oah.getKey(2));
        assertTrue(6 == (Integer) oah.getKey(3));
    }

    @Test public void testClear()
    {
        CircularQueue cq = new CircularQueue(4);
        cq.addAll(four);
        ObjectArrayHolder oah = new ObjectArrayHolder();
        cq.clear(oah);
        assertEquals(0, cq.size());
        assertEquals(4, oah.size());
        assertTrue(1 == (Integer) oah.getKey(0));
        assertTrue(2 == (Integer) oah.getKey(1));
        assertTrue(3 == (Integer) oah.getKey(2));
        assertTrue(4 == (Integer) oah.getKey(3));

        cq = new CircularQueue(2);
        cq.addAll(four);
        assertTrue(1 == (Integer) cq.remove());
        assertTrue(2 == (Integer) cq.remove());
        cq.add(FIVE);
        cq.add(SIX);
        oah = new ObjectArrayHolder();
        cq.clear(oah);
        assertEquals(0, cq.size());
        assertEquals(4, oah.size());
        assertTrue(3 == (Integer) oah.getKey(0));
        assertTrue(4 == (Integer) oah.getKey(1));
        assertTrue(5 == (Integer) oah.getKey(2));
        assertTrue(6 == (Integer) oah.getKey(3));
    }

    @Test public void testIsEmpty()
    {
        CircularQueue cq = new CircularQueue(2);
        assertTrue(cq.isEmpty());
        cq.add(ONE);
        assertFalse(cq.isEmpty());
    }

    class Visitor implements ObjectVisitorIF
    {
        StringBuilder result;
        Visitor(StringBuilder sb) { result = sb; }
        public int visit(Object o) { result.append(' ').append(o); return 0; }
    }

    @Test public void testAcceptVisitor()
    {
        StringBuilder result = new StringBuilder();
        Visitor visitor = new Visitor(result);
        CircularQueue cq = new CircularQueue(2);
        cq.addAll(four);
        cq.acceptVisitor(visitor);
        assertEquals(" 1 2 3 4", result.toString());

        cq.remove();
        cq.add(FIVE);
        result = new StringBuilder();
        visitor = new Visitor(result);
        cq.acceptVisitor(visitor);
        assertEquals(" 2 3 4 5", result.toString());
    }
}
