package com.cboe.client.util.queue;

/**
 * DoublePriorityEventChannelIF.java
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

public interface DoublePriorityEventChannelIF extends SinglePriorityEventChannelIF
{
    public void   enqueueHighPriority(Object object);
    public void   enqueueHighPriority(Object object, ObjectObjectComparisonPolicyIF policy);

    public void   enqueueHighPriorityAll(Object[] objects);
    public void   enqueueHighPriorityAll(Object[] objects, ObjectObjectComparisonPolicyIF policy);

    public void   enqueueHighPriorityFront(Object object);
    public void   enqueueHighPriorityFront(Object object, ObjectObjectComparisonPolicyIF policy);

    public void   enqueueHighPriorityFrontAll(Object[] objects);
    public void   enqueueHighPriorityFrontAll(Object[] objects, ObjectObjectComparisonPolicyIF policy);

    public Object dequeueHighPriority() throws Exception;

    public Object dequeueHighPriority(long millis, Object timeoutObject) throws Exception;

    public void   dequeueHighPriorityAll(ObjectArrayHolderIF arrayHolder) throws Exception;

    public void   flushNormalPriority(ObjectArrayHolderIF arrayHolder) throws Exception;
    public void   flushHighPriority(ObjectArrayHolderIF arrayHolder) throws Exception;

    public int    normalPrioritySize();
    public int    highPrioritySize();
}
