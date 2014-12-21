package com.cboe.util.collections;

/**
 * LongObjectMapArrayHolder.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueArrayHolder.template (Long/long, Object/Object)
 *
 */

public interface LongObjectMapArrayHolder
{
    public int size();
    public boolean isEmpty();
    public int capacity();
    public int ensureCapacity(int capacity);
    public long[] keys();
    public long[] toKeyArray();
    public Object[] values();
    public Object[] toValueArray();
    public LongObjectMapArrayHolder add(long key, Object value);
    public LongObjectMapArrayHolder add(long[] keys, Object[] values, int count);
    public long getKey(int index);
    public Object getValue(int index);
    public boolean containsKey(long key);
    public boolean containsValue(Object value);
    public LongObjectMapArrayHolder clear();
    public LongObjectMapVisitor acceptVisitor(LongObjectMapVisitor visitor);
}

