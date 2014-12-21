package com.cboe.util.collections;

/**
 * LongIntMapArrayHolder.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueArrayHolder.template (Long/long, Int/int)
 *
 */

public interface LongIntMapArrayHolder
{
    public int size();
    public boolean isEmpty();
    public int capacity();
    public int ensureCapacity(int capacity);
    public long[] keys();
    public long[] toKeyArray();
    public int[] values();
    public int[] toValueArray();
    public LongIntMapArrayHolder add(long key, int value);
    public LongIntMapArrayHolder add(long[] keys, int[] values, int count);
    public long getKey(int index);
    public int getValue(int index);
    public boolean containsKey(long key);
    public boolean containsValue(int value);
    public LongIntMapArrayHolder clear();
    public LongIntMapVisitor acceptVisitor(LongIntMapVisitor visitor);
}

