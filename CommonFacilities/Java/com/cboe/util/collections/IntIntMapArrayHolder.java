package com.cboe.util.collections;

/**
 * IntIntMapArrayHolder.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueArrayHolder.template (Int/int, Int/int)
 *
 */

public interface IntIntMapArrayHolder
{
    public int size();
    public boolean isEmpty();
    public int capacity();
    public int ensureCapacity(int capacity);
    public int[] keys();
    public int[] toKeyArray();
    public int[] values();
    public int[] toValueArray();
    public IntIntMapArrayHolder add(int key, int value);
    public IntIntMapArrayHolder add(int[] keys, int[] values, int count);
    public int getKey(int index);
    public int getValue(int index);
    public boolean containsKey(int key);
    public boolean containsValue(int value);
    public IntIntMapArrayHolder clear();
    public IntIntMapVisitor acceptVisitor(IntIntMapVisitor visitor);
}

