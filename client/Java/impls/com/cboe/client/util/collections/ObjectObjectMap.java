package com.cboe.client.util.collections;

/**
 * ObjectObjectMap.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED BY VELOCITY TEMPLATE ENGINE FROM /vobs/dte/client/generator/DV_XYMap.java (KEY_TYPE=Object, VALUE_TYPE=Object)
 *
 */

import com.cboe.client.util.*;

public class ObjectObjectMap implements HasSizeIF
{
    protected Object[][] keys;
    protected Object[][] values;
    protected int[]        numberKeys;
    protected int          tableHighestIndex;
    protected volatile int size;

    public Object ValueNotFound;

    public static final int VALUE_ADDED     = -12345;
    public static final int VALUE_REPLACED  = -23456;
    public static final int VALUE_UNCHANGED = -34567;

    public static final int DEFAULT_INITIAL_CAPACITY       = 256;
    public static final int DEFAULT_ENTRY_LIST_CAPACITY    = 16;
    public static final int MAXIMUM_CAPACITY               = 1024 * 16;

    public static final ObjectObjectMap unsynchronizedMap()             {return new ObjectObjectMap();}
    public static final ObjectObjectMap unsynchronizedMap(int capacity) {return new ObjectObjectMap(capacity);}
    public static final ObjectObjectMap synchronizedMap()               {return new ObjectObjectMapMT();}
    public static final ObjectObjectMap synchronizedMap(int capacity)   {return new ObjectObjectMapMT(capacity);}

    public static class ObjectObjectMapMT extends ObjectObjectMap
    {
        public ObjectObjectMapMT()
        {
            super();
        }
        public ObjectObjectMapMT(int capacity)
        {
            super(capacity);
        }
        public synchronized boolean containsKey(Object key)
        {
            return super.containsKey(key);
        }
        public synchronized boolean containsValue(Object value)
        {
            return super.containsValue(value);
        }
        public synchronized void getKeysForValue(Object value, ObjectArrayHolderIF arrayHolder)
        {
            super.getKeysForValue(value, arrayHolder);
        }
        public synchronized Object getValueForKey(Object key)
        {
            return super.getValueForKey(key);
        }
        public synchronized int putKeyValue(Object key, Object value)
        {
            return super.putKeyValue(key, value);
        }
        public synchronized int putKeyValue(Object key, Object value, ObjectObjectKeyValuePolicyIF policy)
        {
            return super.putKeyValue(key, value, policy);
        }
        public synchronized Object removeKey(Object key)
        {
            return super.removeKey(key);
        }
        public synchronized Object removeKey(Object key, ObjectObjectKeyValuePolicyIF policy)
        {
            return super.removeKey(key, policy);
        }
        public synchronized ObjectObjectVisitorIF acceptVisitor(ObjectObjectVisitorIF visitor)
        {
            return super.acceptVisitor(visitor);
        }
        public synchronized ObjectObjectMap clear()
        {
            return super.clear();
        }
        public synchronized ObjectObjectMap clear(ObjectObjectArrayHolderIF arrayHolder)
        {
            return super.clear(arrayHolder);
        }
    };

    public ObjectObjectMap()
    {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    public ObjectObjectMap(int capacity)
    {
        if (capacity < 2)
        {
            capacity = DEFAULT_INITIAL_CAPACITY;
        }
        else if (capacity > MAXIMUM_CAPACITY)
        {
            capacity = MAXIMUM_CAPACITY;
        }
        else
        {
            capacity = IntegerHelper.higherPowerOf2(capacity);
        }

        keys              = new Object[capacity][];
        values            = new Object[capacity][];
        numberKeys        = new int[capacity];
        tableHighestIndex = capacity - 1;
    }

    public Object getValueNotFound()
    {
        return ValueNotFound;
    }

    public ObjectObjectMap setValueNotFound(Object ValueNotFound)
    {
        this.ValueNotFound = ValueNotFound;

        return this;
    }

    public int size()
    {
        return size;
    }

    public boolean isEmpty()
    {
        return size == 0;
    }

    public boolean containsKey(Object key)
    {
        int bucketIndex = (int) (key.hashCode() & tableHighestIndex);

        if (numberKeys[bucketIndex] > 0)
        {
            Object[] local_keys = keys[bucketIndex];

            for (int keyIndex = numberKeys[bucketIndex]; --keyIndex >= 0; )
            {
                if (key == local_keys[keyIndex] || key.equals(local_keys[keyIndex]))
                {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean containsValue(Object value)
    {
        int      bucketSize;
        int      keyIndex;
        Object[] local_values;

        for (int bucketIndex = keys.length; --bucketIndex >= 0; )
        {
            bucketSize = numberKeys[bucketIndex];

            if (bucketSize > 0)
            {
                local_values = values[bucketIndex];

                for (keyIndex = 0; keyIndex < bucketSize; keyIndex++)
                {
                    if (value == local_values[keyIndex] || value.equals(local_values[keyIndex]))
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public void getKeysForValue(Object value, ObjectArrayHolderIF arrayHolder)
    {
        int      bucketSize;
        int      keyIndex;
        Object[] local_values;

        for (int bucketIndex = keys.length; --bucketIndex >= 0; )
        {
            bucketSize = numberKeys[bucketIndex];

            if (bucketSize > 0)
            {
                local_values = values[bucketIndex];

                for (keyIndex = 0; keyIndex < bucketSize; keyIndex++)
                {
                    if (value == local_values[keyIndex] || value.equals(local_values[keyIndex]))
                    {
                        arrayHolder.add(keys[bucketIndex][keyIndex]);
                    }
                }
            }
        }
    }

    public Object getValueForKey(Object key)
    {
        int bucketIndex = (int) (key.hashCode() & tableHighestIndex);

        if (numberKeys[bucketIndex] > 0)
        {
            Object[] local_keys = keys[bucketIndex];

            for (int keyIndex = numberKeys[bucketIndex]; --keyIndex >= 0; )
            {
                if (key == local_keys[keyIndex] || key.equals(local_keys[keyIndex]))
                {
                    return values[bucketIndex][keyIndex];
                }
            }
        }

        return ValueNotFound;
    }

    public void getData(ObjectObjectArrayHolderIF arrayHolder)
    {
        int             bucketSize;
        int             keyIndex;
        Object[]   local_keys;
        Object[] local_values;

        for (int bucketIndex = 0; bucketIndex < keys.length; bucketIndex++)
        {
            bucketSize = numberKeys[bucketIndex];

            if (bucketSize > 0)
            {
                local_values = values[bucketIndex];
                local_keys   = keys[bucketIndex];

                for (keyIndex = 0; keyIndex < bucketSize; keyIndex++)
                {
                    arrayHolder.add(local_keys[keyIndex], local_values[keyIndex]);
                }
            }
        }
    }

    public int putKeyValue(Object key, Object value)
    {
        int bucketIndex = (int) (key.hashCode() & tableHighestIndex);

        if (keys[bucketIndex] == null)
        {
            keys[bucketIndex]       = new Object[DEFAULT_ENTRY_LIST_CAPACITY];
            values[bucketIndex]     = new Object[DEFAULT_ENTRY_LIST_CAPACITY];
            keys[bucketIndex][0]    = key;
            values[bucketIndex][0]  = value;
            numberKeys[bucketIndex] = 1;
            size++;

            return VALUE_ADDED;
        }

        int keyIndex;
        Object[] local_keys = keys[bucketIndex];
        Object[] local_values = values[bucketIndex];
        int local_numberKeys = numberKeys[bucketIndex];

        for (keyIndex = 0; keyIndex < local_numberKeys; keyIndex++)
        {
            if (key == local_keys[keyIndex] || key.equals(local_keys[keyIndex]))
            {
                local_values[keyIndex] = value;
                return VALUE_REPLACED;
            }
        }

        // if we got here, this is completely new

        if (local_numberKeys >= local_keys.length) // need to expand
        {
            if (keyIndex == local_numberKeys) // if at end, then just arrayclone, and append later
            {
                keys[bucketIndex]   = CollectionHelper.arrayclone(local_keys,   0, local_keys.length, local_keys.length << 1);
                values[bucketIndex] = CollectionHelper.arrayclone(local_values, 0, local_keys.length, local_keys.length << 1);
            }
            else
            {
                keys[bucketIndex]   = CollectionHelper.arraycloneExpandGap(local_keys,   0, local_keys.length, local_keys.length << 1, keyIndex, 1);
                values[bucketIndex] = CollectionHelper.arraycloneExpandGap(local_values, 0, local_keys.length, local_keys.length << 1, keyIndex, 1);
            }
        }
        else
        {
            if (keyIndex < local_numberKeys)
            {
                System.arraycopy(local_values, keyIndex, local_values, keyIndex + 1, local_numberKeys - keyIndex);
                System.arraycopy(local_keys,   keyIndex, local_keys,   keyIndex + 1, local_numberKeys - keyIndex);
            }
        }

        keys[bucketIndex][keyIndex]   = key;
        values[bucketIndex][keyIndex] = value;

        numberKeys[bucketIndex]++;
        size++;

        return VALUE_ADDED;
    }

    public int putKeyValue(Object key, Object value, ObjectObjectKeyValuePolicyIF policy)
    {
        int bucketIndex = (int) (key.hashCode() & tableHighestIndex);

        if (keys[bucketIndex] == null)
        {
            if (!policy.canInsert(key, value))
            {
                return VALUE_UNCHANGED;
            }

            keys[bucketIndex]       = new Object[DEFAULT_ENTRY_LIST_CAPACITY];
            values[bucketIndex]     = new Object[DEFAULT_ENTRY_LIST_CAPACITY];
            keys[bucketIndex][0]    = key;
            values[bucketIndex][0]  = value;
            numberKeys[bucketIndex] = 1;
            size++;

            return VALUE_ADDED;
        }

        int keyIndex;
        Object[] local_keys = keys[bucketIndex];
        Object[] local_values = values[bucketIndex];
        int local_numberKeys = numberKeys[bucketIndex];

        for (keyIndex = 0; keyIndex < local_numberKeys; keyIndex++)
        {
            if (key == local_keys[keyIndex] || key.equals(local_keys[keyIndex]))
            {
                if (policy.canUpdate(keys[bucketIndex][keyIndex], values[bucketIndex][keyIndex], value))
                {
                    local_values[keyIndex] = value;
                    return VALUE_REPLACED;
                }

                return VALUE_UNCHANGED;
            }
        }

        // if we got here, this is completely new

        if (!policy.canInsert(key, value))
        {
            return VALUE_UNCHANGED;
        }

        if (local_numberKeys >= local_keys.length) // need to expand
        {
            if (keyIndex == local_numberKeys) // if at end, then just arrayclone, and append later
            {
                keys[bucketIndex]   = CollectionHelper.arrayclone(local_keys,   0, local_keys.length, local_keys.length << 1);
                values[bucketIndex] = CollectionHelper.arrayclone(local_values, 0, local_keys.length, local_keys.length << 1);
            }
            else
            {
                keys[bucketIndex]   = CollectionHelper.arraycloneExpandGap(local_keys,   0, local_keys.length, local_keys.length << 1, keyIndex, 1);
                values[bucketIndex] = CollectionHelper.arraycloneExpandGap(local_values, 0, local_keys.length, local_keys.length << 1, keyIndex, 1);
            }
        }
        else
        {
            if (keyIndex < local_numberKeys)
            {
                System.arraycopy(local_values, keyIndex, local_values, keyIndex + 1, local_numberKeys - keyIndex);
                System.arraycopy(local_keys,   keyIndex, local_keys,   keyIndex + 1, local_numberKeys - keyIndex);
            }
        }

        keys[bucketIndex][keyIndex]   = key;
        values[bucketIndex][keyIndex] = value;

        numberKeys[bucketIndex]++;
        size++;

        return VALUE_ADDED;
    }

    public Object removeKey(Object key)
    {
        int bucketIndex = (int) (key.hashCode() & tableHighestIndex);

        if (numberKeys[bucketIndex] > 0)
        {
            int keyIndex;
            Object[] local_keys = keys[bucketIndex];
            Object[] local_values = values[bucketIndex];
            int local_numberKeys = numberKeys[bucketIndex];

            for (keyIndex = 0; keyIndex < local_numberKeys; keyIndex++)
            {
                if (key == local_keys[keyIndex] || key.equals(local_keys[keyIndex]))
                {
                    Object oldValue = local_values[keyIndex];

                    int highest_index = local_values.length - 1;

                    if (keyIndex < highest_index)
                    {
                        System.arraycopy(local_values, keyIndex + 1, local_values, keyIndex, highest_index - keyIndex);
                        System.arraycopy(local_keys,   keyIndex + 1, local_keys,   keyIndex, highest_index - keyIndex);
                    }

                    local_keys[highest_index]   = null;
                    local_values[highest_index] = null;

                    numberKeys[bucketIndex]--;
                    size--;

                    return oldValue;
                }
            }
        }

        return ValueNotFound;
    }

    public Object removeKey(Object key, ObjectObjectKeyValuePolicyIF policy)
    {
        int bucketIndex = (int) (key.hashCode() & tableHighestIndex);

        if (numberKeys[bucketIndex] > 0)
        {
            int keyIndex;
            Object[] local_keys = keys[bucketIndex];
            Object[] local_values = values[bucketIndex];
            int local_numberKeys = numberKeys[bucketIndex];

            for (keyIndex = 0; keyIndex < local_numberKeys; keyIndex++)
            {
                if (key == local_keys[keyIndex] || key.equals(local_keys[keyIndex]))
                {
                    Object oldValue = local_values[keyIndex];

                    if (!policy.canRemove(key, oldValue))
                    {
                        return ValueNotFound;
                    }

                    int highest_index = local_values.length - 1;

                    if (keyIndex < highest_index)
                    {
                        System.arraycopy(local_values, keyIndex + 1, local_values, keyIndex, highest_index - keyIndex);
                        System.arraycopy(local_keys,   keyIndex + 1, local_keys,   keyIndex, highest_index - keyIndex);
                    }

                    local_keys[highest_index]   = null;
                    local_values[highest_index] = null;

                    numberKeys[bucketIndex]--;
                    size--;

                    return oldValue;
                }
            }
        }

        return ValueNotFound;
    }

    public ObjectObjectVisitorIF acceptVisitor(ObjectObjectVisitorIF visitor)
    {
        int             bucketSize;
        int             keyIndex;
        int             rc;
        Object[]   local_keys;
        Object[] local_values;

        for (int bucketIndex = 0; bucketIndex < keys.length; bucketIndex++)
        {
            bucketSize = numberKeys[bucketIndex];

            local_keys = keys[bucketIndex];

            if (bucketSize > 0)
            {
                local_values = values[bucketIndex];

                for (keyIndex = 0; keyIndex < bucketSize; keyIndex++)
                {
                    rc = visitor.visit(local_keys[keyIndex], local_values[keyIndex]);

                    if (rc == ObjectObjectVisitorIF.CONTINUE)
                    {
                        continue;
                    }

                    if (rc == ObjectObjectVisitorIF.ABORT)
                    {
                         return visitor;
                    }
                }
            }
        }

        return visitor;
    }

    public ObjectObjectMap clear()
    {
        for (int bucketIndex = keys.length; --bucketIndex >= 0; )
        {
            keys[bucketIndex]   = null;
            values[bucketIndex] = null;
            numberKeys[bucketIndex]  = 0;
        }

        size = 0;

        return this;
    }

    public ObjectObjectMap clear(ObjectObjectArrayHolderIF arrayHolder)
    {
        getData(arrayHolder);

        for (int bucketIndex = keys.length; --bucketIndex >= 0; )
        {
            keys[bucketIndex]   = null;
            values[bucketIndex] = null;
            numberKeys[bucketIndex]  = 0;
        }

        size = 0;

        return this;
    }
}
