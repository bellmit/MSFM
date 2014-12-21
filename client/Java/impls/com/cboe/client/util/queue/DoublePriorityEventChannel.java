package com.cboe.client.util.queue;

/**
 * DoublePriorityEventChannel.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 *
 * Double-priority, timeout enabled, waitable queue
 *
 */

import com.cboe.client.util.collections.*;
import com.cboe.instrumentationService.instrumentors.*;

public class DoublePriorityEventChannel implements DoublePriorityEventChannelIF
{
    protected final CircularQueue highPriorityChannel;
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

        highPriorityChannel.acceptVisitor(calculateSizeVisitor);
        normalPriorityChannel.acceptVisitor(calculateSizeVisitor);

        QueueInstrumentor queueInstrumentor = normalPriorityChannel.getQueueInstrumentor();

        return new EventChannelInstrumentation(queueInstrumentor,
                highPriorityChannel.size() + normalPriorityChannel.size(),
                calculateSizeVisitor.size());
    }

    public DoublePriorityEventChannel()
    {
        this(128, 16);
    }

    public DoublePriorityEventChannel(int normalCapacity)
    {
        this(normalCapacity, 16);
    }

    public DoublePriorityEventChannel(int normalCapacity, int highCapacity)
    {
        highPriorityChannel   = new CircularQueue(highCapacity);
        normalPriorityChannel = new CircularQueue(normalCapacity);
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

    public void enqueueHighPriority(Object object)
    {
        synchronized (this)
        {
            if (highPriorityChannel.add(object))
            {
                notify();
            }
        }
    }

    public void enqueueHighPriority(Object object, ObjectObjectComparisonPolicyIF policy)
    {
        synchronized (this)
        {
            if (highPriorityChannel.add(object, policy))
            {
                notify();
            }
        }
    }

    public void enqueueHighPriorityFront(Object object)
    {
        synchronized (this)
        {
            if (highPriorityChannel.insert(object))
            {
                notify();
            }
        }
    }

    public void enqueueHighPriorityFront(Object object, ObjectObjectComparisonPolicyIF policy)
    {
        synchronized (this)
        {
            if (highPriorityChannel.insert(object, policy))
            {
                notify();
            }
        }
    }

    public void enqueueHighPriorityAll(Object[] objects)
    {
        synchronized (this)
        {
            if (highPriorityChannel.addAll(objects))
            {
                notify();
            }
        }
    }

    public void enqueueHighPriorityAll(Object[] objects, ObjectObjectComparisonPolicyIF policy)
    {
        synchronized (this)
        {
            if (highPriorityChannel.addAll(objects, policy))
            {
                notify();
            }
        }
    }

    public void enqueueHighPriorityFrontAll(Object[] objects)
    {
        synchronized (this)
        {
            boolean shouldNotify = false;
            for (int i = objects.length-1; i >= 0; --i)
            {
                boolean changed = highPriorityChannel.insert(objects[i]);
                shouldNotify = shouldNotify || changed;
            }
            if (shouldNotify)
            {
                notify();
            }
        }
    }

    public void enqueueHighPriorityFrontAll(Object[] objects, ObjectObjectComparisonPolicyIF policy)
    {
        synchronized (this)
        {
            for (Object o : objects)
            {
                if (!highPriorityChannel.allowedByPolicy(o, policy))
                {
                    return;
                }
            }

            boolean shouldNotify = false;
            for (int i = objects.length-1; i >= 0; --i)
            {
                boolean changed = highPriorityChannel.insert(objects[i]);
                shouldNotify = shouldNotify || changed;
            }
            if (shouldNotify)
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
                if (!highPriorityChannel.isEmpty())
                {
                    return highPriorityChannel.remove();
                }

                if (!normalPriorityChannel.isEmpty())
                {
                    return normalPriorityChannel.remove();
                }

                wait();
            }
        }
    }

    public Object dequeueHighPriority() throws Exception
    {
        synchronized (this)
        {
            while (true)
            {
                if (!highPriorityChannel.isEmpty())
                {
                    return highPriorityChannel.remove();
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
                if (!highPriorityChannel.isEmpty())
                {
                    return highPriorityChannel.remove();
                }

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

    public Object dequeueHighPriority(long millis, Object timeoutObject) throws Exception
    {
        if (millis < 1)
        {
            return dequeueHighPriority();
        }

        long targetTime = System.currentTimeMillis() + millis;
        long curTime;

        synchronized (this)
        {
            while (true)
            {
                if (!highPriorityChannel.isEmpty())
                {
                    return highPriorityChannel.remove();
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

    public void dequeueHighPriority(ObjectArrayHolderIF arrayHolder) throws Exception
    {
        synchronized (this)
        {
            while (true)
            {
                if (!highPriorityChannel.isEmpty())
                {
                    highPriorityChannel.clear(arrayHolder);
                    return;
                }

                wait();
            }
        }
    }

    public void dequeueAll(ObjectArrayHolderIF arrayHolder) throws Exception
    {
        synchronized(this)
        {
            highPriorityChannel.clear(arrayHolder);
            normalPriorityChannel.clear(arrayHolder);
        }
    }

    public void dequeueHighPriorityAll(ObjectArrayHolderIF arrayHolder) throws Exception
    {
        synchronized(this)
        {
            highPriorityChannel.clear(arrayHolder);
        }
    }

    public void flush(ObjectArrayHolderIF arrayHolder) throws Exception
    {
        flushNormalPriority(arrayHolder);
    }

    public void flushNormalPriority(ObjectArrayHolderIF arrayHolder) throws Exception
    {
        synchronized(this)
        {
            normalPriorityChannel.clear(arrayHolder);
        }
    }

    public void flushHighPriority(ObjectArrayHolderIF arrayHolder) throws Exception
    {
        synchronized(this)
        {
            highPriorityChannel.clear(arrayHolder);
        }
    }

    public int size()
    {
        synchronized (this)
        {
            return highPriorityChannel.size() + normalPriorityChannel.size();
        }
    }

    public int normalPrioritySize()
    {
        synchronized (this)
        {
            return normalPriorityChannel.size();
        }
    }

    public int highPrioritySize()
    {
        synchronized (this)
        {
            return highPriorityChannel.size();
        }
    }

    public ObjectVisitorIF acceptVisitor(ObjectVisitorIF visitor)
    {
        synchronized(this)
        {
            highPriorityChannel.acceptVisitor(visitor);
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
        highPriorityChannel.setQueueInstrumentor(queueInstrumentor);
    }
}
