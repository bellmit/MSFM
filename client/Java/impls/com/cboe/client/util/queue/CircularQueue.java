package com.cboe.client.util.queue;

/**
 * CircularQueue.java
 *
 * @author Dmitry Volpyansky
 *
 */

import com.cboe.client.util.collections.*;
import com.cboe.client.util.*;
import com.cboe.instrumentationService.instrumentors.*;

public class CircularQueue implements HasSizeIF
{
    protected Object[] elements;
    protected int      head;
    protected int      tail;
    protected int      size;
    protected int      highestTableIndex;

    protected QueueInstrumentor queueInstrumentor;

    public CircularQueue()
    {
        this(64, null);
    }

    public CircularQueue(QueueInstrumentor queueInstrumentor)
    {
        this(64, queueInstrumentor);
    }

    public CircularQueue(int capacity)
    {
        this(capacity, null);
    }

    public CircularQueue(int capacity, QueueInstrumentor queueInstrumentor)
    {
        capacity = IntegerHelper.higherPowerOf2(capacity);

        elements = new Object[capacity];

        highestTableIndex = capacity - 1;

        setQueueInstrumentor(queueInstrumentor);
    }

    public void setQueueInstrumentor(QueueInstrumentor queueInstrumentor)
    {
        this.queueInstrumentor = queueInstrumentor;
    }

    public QueueInstrumentor getQueueInstrumentor()
    {
        return queueInstrumentor;
    }

    public boolean add(Object key)
    {
        if (size > highestTableIndex)
        {
            expandQueue(1);
        }

        elements[tail] = key;

        tail = (tail + 1) & highestTableIndex;

        size++;

        if (queueInstrumentor != null)
        {
            queueInstrumentor.incEnqueued(1);

            if (size > queueInstrumentor.getHighWaterMark())
            {
                queueInstrumentor.setHighWaterMark(size);
            }
        }

        return true;
    }

    public boolean add(Object object, ObjectObjectComparisonPolicyIF policy)
    {
        if (!allowedByPolicy(object, policy))
        {
            return false;
        }

        return add(object);
    }

    public boolean addAll(Object[] objects)
    {
        final int objectsLength = objects.length;

        if (size + objectsLength > highestTableIndex)
        {
            expandQueue(objectsLength);
        }

        for (int i = 0; i < objectsLength; i++)
        {
            elements[tail] = objects[i];

            tail = (tail + 1) & highestTableIndex;
        }

        size += objectsLength;

        if (queueInstrumentor != null)
        {
            queueInstrumentor.incEnqueued(objectsLength);

            if (size > queueInstrumentor.getHighWaterMark())
            {
                queueInstrumentor.setHighWaterMark(size);
            }
        }

        return true;
    }

    public boolean addAll(Object[] objects, ObjectObjectComparisonPolicyIF policy)
    {
        for (int i = 0; i < objects.length; i++)
        {
            if (!allowedByPolicy(objects[i], policy))
            {
                return false;
            }
        }

        return addAll(objects);
    }

    public boolean addAll(Object[] objects, int offset, int length, ObjectObjectComparisonPolicyIF policy)
    {
        for (int i = offset; i < offset+length; i++)
        {
            if (!allowedByPolicy(objects[i], policy))
            {
                return false;
            }
        }

        return addAll(objects, offset, length);
    }

    public boolean addAll(Object[] objects, int offset, int length)
    {
        if (size + length > highestTableIndex)
        {
            expandQueue(length);
        }

        length += offset;

        for (int i = offset; i < length; i++)
        {
            elements[tail] = objects[i];

            tail = (tail + 1) & highestTableIndex;
        }

        size += length - offset;

        if (queueInstrumentor != null)
        {
            queueInstrumentor.incEnqueued(length - offset);

            if (size > queueInstrumentor.getHighWaterMark())
            {
                queueInstrumentor.setHighWaterMark(size);
            }
        }

        return true;
    }

    public boolean insert(Object object, ObjectObjectComparisonPolicyIF policy)
    {
        if (!allowedByPolicy(object, policy))
        {
            return false;
        }

        return insert(object);
    }

    public boolean insert(Object key)
    {
        if (size > highestTableIndex)
        {
            expandQueue(1);
        }

        head = (head - 1) & highestTableIndex;

        elements[head] = key;

        size++;

        if (queueInstrumentor != null)
        {
            queueInstrumentor.incEnqueued(1);

            if (size > queueInstrumentor.getHighWaterMark())
            {
                queueInstrumentor.setHighWaterMark(size);
            }
        }

        return true;
    }

    public Object remove()
    {
        if (size == 0)
        {
            return null;
        }

        Object key = elements[head];

        elements[head] = null;

        head = (head + 1) & highestTableIndex;

        size--;

        if (queueInstrumentor != null)
        {
            queueInstrumentor.incDequeued(1);
        }

        return key;
    }

    public int remove(ObjectInspectionPolicyIF policy)
    {
        if (size == 0)
        {
            return 0;
        }

        int removed = 0;

        if (head < tail)
        {
            for (int i = head; i < tail; i++)
            {
                if (policy.inspect(elements[i]))
                {
                    System.arraycopy(elements, i + 1, elements, i, tail - i);

                    elements[tail] = null;

                    size--;

                    tail--;

                    removed++;
                }
            }
        }
        else
        {
            int tempSize = size;
            int last = highestTableIndex + 1;

            for (int i = head; i < last && tempSize > 0; i++, tempSize--)
            {
                if (policy.inspect(elements[i]))
                {
                    System.arraycopy(elements, i + 1, elements, i, highestTableIndex - i);

                    elements[highestTableIndex] = null;

                    size--;

                    last--;

                    removed++;
                }
            }

            if (removed > 0)
            {
                System.arraycopy(elements, head, elements, head + removed, highestTableIndex + 1 - removed - head);

                for (int i = 0; i < removed; i++)
                {
                    elements[head + i] = null;
                }

                head += removed;
            }

            last = tail - 1;

            for (int i = 0; i < last && tempSize > 0; i++, tempSize--)
            {
                if (policy.inspect(elements[i]))
                {
                    System.arraycopy(elements, i + 1, elements, i, tail - i);

                    elements[tail] = null;

                    size--;

                    removed++;
                }
            }

            if (policy.inspect(elements[last]))
            {
                elements[last] = null;

                size--;

                removed++;

                tail = (last) & highestTableIndex;
            }
        }

        if (queueInstrumentor != null && removed > 0)
        {
            queueInstrumentor.incDequeued(removed);
        }

        return removed;
    }

    public boolean allowedByPolicy(Object object, ObjectObjectComparisonPolicyIF policy)
    {
        if (size == 0)
        {
            return true;
        }

        int comparison;

        if (head < tail)
        {
            for (int i = head; i < tail; i++)
            {
                comparison = policy.compare(object, elements[i]);

                if (comparison == ObjectObjectComparisonPolicyIF.CONTINUE)
                {
                    continue;
                }
                if (comparison == ObjectObjectComparisonPolicyIF.ACCEPT)
                {
                    return true;
                }
                if (comparison == ObjectObjectComparisonPolicyIF.REJECT)
                {
                    return false;
                }
            }

            return true;
        }

        int tempSize = size;

        for (int i = head; i <= highestTableIndex && tempSize > 0; i++, tempSize--)
        {
            comparison = policy.compare(object, elements[i]);

            if (comparison == ObjectObjectComparisonPolicyIF.CONTINUE)
            {
                continue;
            }
            if (comparison == ObjectObjectComparisonPolicyIF.ACCEPT)
            {
                return true;
            }
            if (comparison == ObjectObjectComparisonPolicyIF.REJECT)
            {
                return false;
            }
        }

        for (int i = 0; i < tail && tempSize > 0; i++, tempSize--)
        {
            comparison = policy.compare(object, elements[i]);

            if (comparison == ObjectObjectComparisonPolicyIF.CONTINUE)
            {
                continue;
            }
            if (comparison == ObjectObjectComparisonPolicyIF.ACCEPT)
            {
                return true;
            }
            if (comparison == ObjectObjectComparisonPolicyIF.REJECT)
            {
                return false;
            }
        }

        return true;
    }

    public void getData(ObjectArrayHolderIF arrayHolder)
    {
        if (size == 0)
        {
            return;
        }

        if (head < tail)
        {
            for (int i = head; i < tail; i++)
            {
                arrayHolder.add(elements[i]);
            }
        }
        else
        {
            int tempSize = size;
        
            for (int i = head; i <= highestTableIndex && tempSize > 0; i++, tempSize--)
            {
                arrayHolder.add(elements[i]);
            }

            for (int i = 0; i < tail && tempSize > 0; i++, tempSize--)
            {
                arrayHolder.add(elements[i]);
            }
        }
    }

    public int size()
    {
        return size;
    }

    public int capacity()
    {
        return highestTableIndex + 1;
    }

    public void clear(ObjectArrayHolderIF arrayHolder)
    {
        if (size != 0)
        {
            if (head < tail)
            {
                for (int i = head; i < tail; i++)
                {
                    arrayHolder.add(elements[i]);
                }
            }
            else
            {
                int tempSize = size;
            
                for (int i = head; i <= highestTableIndex && tempSize > 0; i++, tempSize--)
                {
                    arrayHolder.add(elements[i]);
                }

                for (int i = 0; i < tail && tempSize > 0; i++, tempSize--)
                {
                    arrayHolder.add(elements[i]);
                }
            }
        }

        clear();
    }

    public void clear()
    {
        for (int i = 0; i < elements.length; i++)
        {
            elements[i] = null;
        }

        if (queueInstrumentor != null)
        {
            queueInstrumentor.incFlushed(size);
        }

        head = 0;
        tail = 0;
        size = 0;
    }

    public boolean isEmpty()
    {
        return size == 0;
    }

    public ObjectVisitorIF acceptVisitor(ObjectVisitorIF visitor)
    {
        if (size == 0)
        {
            return visitor;
        }

        if (head < tail)
        {
            for (int i = head; i < tail; i++)
            {
                visitor.visit(elements[i]);
            }
        }
        else
        {
            int tempSize = size;
        
            for (int i = head; i <= highestTableIndex && tempSize > 0; i++, tempSize--)
            {
                visitor.visit(elements[i]);
            }

            for (int i = 0; i < tail && tempSize > 0; i++, tempSize--)
            {
                visitor.visit(elements[i]);
            }
        }

        return visitor;
    }

    private void expandQueue(int additional)
    {
        int capacity = IntegerHelper.higherPowerOf2(size + additional);

        if (head < tail)
        {
            elements = CollectionHelper.arrayclone(elements, head, size, capacity);
        }
        else
        {
            elements = CollectionHelper.arraycloneCombine(elements, head, tail, capacity);
        }

        tail = size;

        head = 0;

        highestTableIndex = capacity - 1;
    }
}
