package com.cboe.util.collections;

/**
 * IntLongMapArrayHolder.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueArrayHolder.template (Int/int, Long/long)
 *
 */

public interface IntLongMapArrayHolder
{
    public int size();
    public boolean isEmpty();
    public int capacity();
    public int ensureCapacity(int capacity);
    public int[] keys();
    public int[] toKeyArray();
    public long[] values();
    public long[] toValueArray();
    public IntLongMapArrayHolder add(int key, long value);
    public IntLongMapArrayHolder add(int[] keys, long[] values, int count);
    public int getKey(int index);
    public long getValue(int index);
    public boolean containsKey(int key);
    public boolean containsValue(long value);
    public IntLongMapArrayHolder clear();
    public IntLongMapVisitor acceptVisitor(IntLongMapVisitor visitor);
}

