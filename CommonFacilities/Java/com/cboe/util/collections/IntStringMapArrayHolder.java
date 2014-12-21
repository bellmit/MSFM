package com.cboe.util.collections;

/**
 * IntStringMapArrayHolder.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueArrayHolder.template (Int/int, String/String)
 *
 */

public interface IntStringMapArrayHolder
{
    public int size();
    public boolean isEmpty();
    public int capacity();
    public int ensureCapacity(int capacity);
    public int[] keys();
    public int[] toKeyArray();
    public String[] values();
    public String[] toValueArray();
    public IntStringMapArrayHolder add(int key, String value);
    public IntStringMapArrayHolder add(int[] keys, String[] values, int count);
    public int getKey(int index);
    public String getValue(int index);
    public boolean containsKey(int key);
    public boolean containsValue(String value);
    public IntStringMapArrayHolder clear();
    public IntStringMapVisitor acceptVisitor(IntStringMapVisitor visitor);
}

