package com.cboe.client.util.queue;

/**
 * SinglePriorityEventChannelIF.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 *
 * Single-priority, timeout enabled, waitable queue
 *
 */

import com.cboe.client.util.collections.*;
import com.cboe.client.util.*;
import com.cboe.instrumentationService.instrumentors.*;

public interface SinglePriorityEventChannelIF extends HasSizeIF
{
    public void   enqueue(Object object);
    public boolean enqueue(Object object, ObjectObjectComparisonPolicyIF policy);

    public void   enqueueAll(Object[] object);
    public void   enqueueAll(Object[] object, ObjectObjectComparisonPolicyIF policy);

    public Object dequeue() throws Exception;

    public Object dequeue(long millis, Object timeoutObject) throws Exception;

    public void   dequeueAll(ObjectArrayHolderIF arrayHolder) throws Exception;

    public void   flush(ObjectArrayHolderIF arrayHolder) throws Exception;

    public int    size();

    public void   incOverlaid(int overlaid);

    public ObjectVisitorIF acceptVisitor(ObjectVisitorIF visitor);

    public EventChannelInstrumentationIF getEventChannelInstrumentation();

    public QueueInstrumentor getQueueInstrumentor();
    public void setQueueInstrumentor(QueueInstrumentor queueInstrumentor);

    public interface EventChannelInstrumentationIF
    {
        public int totalDequeued();
        public int totalEnqueued();
        public int totalFlushed();
        public int highWaterMark();
        public int currentSize();
        public int currentDepth();
    }
}
