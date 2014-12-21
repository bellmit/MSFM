package com.cboe.util.collections;

/**
 * LongLongMapArrayHolder.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueArrayHolder.template (Long/long, Long/long)
 *
 */

public interface LongLongMapArrayHolder
{
    public int size();
    public boolean isEmpty();
    public int capacity();
    public int ensureCapacity(int capacity);
    public long[] keys();
    public long[] toKeyArray();
    public long[] values();
    public long[] toValueArray();
    public LongLongMapArrayHolder add(long key, long value);
    public LongLongMapArrayHolder add(long[] keys, long[] values, int count);
    public long getKey(int index);
    public long getValue(int index);
    public boolean containsKey(long key);
    public boolean containsValue(long value);
    public LongLongMapArrayHolder clear();
    public LongLongMapVisitor acceptVisitor(LongLongMapVisitor visitor);
}

