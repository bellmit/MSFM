package com.cboe.util.collections;

/**
 * IntObjectMapArrayHolder.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueArrayHolder.template (Int/int, Object/Object)
 *
 */

public interface IntObjectMapArrayHolder
{
    public int size();
    public boolean isEmpty();
    public int capacity();
    public int ensureCapacity(int capacity);
    public int[] keys();
    public int[] toKeyArray();
    public Object[] values();
    public Object[] toValueArray();
    public IntObjectMapArrayHolder add(int key, Object value);
    public IntObjectMapArrayHolder add(int[] keys, Object[] values, int count);
    public int getKey(int index);
    public Object getValue(int index);
    public boolean containsKey(int key);
    public boolean containsValue(Object value);
    public IntObjectMapArrayHolder clear();
    public IntObjectMapVisitor acceptVisitor(IntObjectMapVisitor visitor);
}

