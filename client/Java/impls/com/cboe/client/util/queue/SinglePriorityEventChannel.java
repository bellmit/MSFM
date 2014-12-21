package com.cboe.client.util.queue;

/**
 * SinglePriorityEventChannel.java
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
import com.cboe.instrumentationService.instrumentors.*;

public class SinglePriorityEventChannel implements SinglePriorityEventChannelIF
{
    protected final CircularQueue normalPriorityChannel;
    protected final CalculateSizeVisitor calculateSizeVisitor = new CalculateSizeVisitor();

    public class EventChannelInstrumentation implements SinglePriorityEventChannelIF.EventChannelInstrumentationIF
    {
        public int totalDequeued;
        public int totalEnqueued;
        public int totalFlushed;
        public int highWaterMark;
        public int size;
        public int depth;

        public EventChannelInstrumentation()
        {

        }

        private EventChannelInstrumentation(QueueInstrumentor queueInstrumentor, int size, int depth)
        {
            if (queueInstrumentor != null)
            {
                this.totalDequeued = (int) queueInstrumentor.getDequeued();
                this.totalEnqueued = (int) queueInstrumentor.getEnqueued();
                this.totalFlushed  = (int) queueInstrumentor.getFlushed();
                this.highWaterMark = (int) queueInstrumentor.getHighWaterMark();
            }

            this.size  = size;
            this.depth = depth;
        }

        public int totalDequeued()
        {
            return totalDequeued;
        }

        public int totalEnqueued()
        {
            return totalEnqueued;
        }

        public int totalFlushed()
        {
            return totalFlushed;
        }

        public int highWaterMark()
        {
            return highWaterMark;
        }

        public int currentSize()
        {
            return size;
        }

        public int currentDepth()
        {
            return depth;
        }
    }

    public synchronized SinglePriorityEventChannelIF.EventChannelInstrumentationIF getEventChannelInstrumentation()
    {
        calculateSizeVisitor.clear();

        normalPriorityChannel.acceptVisitor(calculateSizeVisitor);

        QueueInstrumentor queueInstrumentor = normalPriorityChannel.getQueueInstrumentor();

        return new EventChannelInstrumentation(queueInstrumentor,
                normalPriorityChannel.size(),
                calculateSizeVisitor.size());
    }

    public SinglePriorityEventChannel()
    {
        normalPriorityChannel = new CircularQueue(128);
    }

    public SinglePriorityEventChannel(int capacity)
    {
        normalPriorityChannel = new CircularQueue(capacity);
    }

    public QueueInstrumentor getQueueInstrumentor()
    {
        return normalPriorityChannel.getQueueInstrumentor();
    }

    public void enqueue(Object object)
    {
        synchronized (this)
        {
            if (normalPriorityChannel.add(object))
            {
                notify();
            }
        }
    }

    public boolean enqueue(Object object, ObjectObjectComparisonPolicyIF policy)
    {
        synchronized (this)
        {
            if (normalPriorityChannel.add(object, policy))
            {
                notify();
                return true;
            }
            return false;
        }
    }

    public void enqueueAll(Object[] objects)
    {
        synchronized (this)
        {
            if (normalPriorityChannel.addAll(objects))
            {
                notify();
            }
        }
    }

    public void enqueueAll(Object[] objects, ObjectObjectComparisonPolicyIF policy)
    {
        synchronized (this)
        {
            if (normalPriorityChannel.addAll(objects, policy))
            {
                notify();
            }
        }
    }

    public Object dequeue() throws Exception
    {
        synchronized (this)
        {
            while (true)
            {
                if (!normalPriorityChannel.isEmpty())
                {
                    return normalPriorityChannel.remove();
                }

                wait();
            }
        }
    }

    public Object dequeue(long millis, Object timeoutObject) throws Exception
    {
        if (millis < 1)
        {
            return dequeue();
        }

        long targetTime = System.currentTimeMillis() + millis;
        long curTime;

        synchronized (this)
        {
            while (true)
            {
                if (!normalPriorityChannel.isEmpty())
                {
                    return normalPriorityChannel.remove();
                }

                curTime = System.currentTimeMillis();
                if (targetTime <= curTime)
                {
                    return timeoutObject;
                }

                wait(targetTime - curTime);
            }
        }
    }

    public void dequeueAll(ObjectArrayHolderIF arrayHolder) throws Exception
    {
        synchronized (this)
        {
            normalPriorityChannel.clear(arrayHolder);
        }
    }

    public void flush(ObjectArrayHolderIF arrayHolder) throws Exception
    {
        synchronized (this)
        {
            normalPriorityChannel.clear(arrayHolder);
        }
    }

    public int size()
    {
        synchronized (this)
        {
            return normalPriorityChannel.size();
        }
    }

    public ObjectVisitorIF acceptVisitor(ObjectVisitorIF visitor)
    {
        synchronized (this)
        {
            return normalPriorityChannel.acceptVisitor(visitor);
        }
    }

    public void incOverlaid(int overlaid)
    {
        if (normalPriorityChannel.getQueueInstrumentor() != null)
        {
            normalPriorityChannel.getQueueInstrumentor().incOverlaid(overlaid);
        }
    }

    public void setQueueInstrumentor(QueueInstrumentor queueInstrumentor)
    {
        normalPriorityChannel.setQueueInstrumentor(queueInstrumentor);
    }
}
