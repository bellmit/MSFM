package com.cboe.util.collections;

/**
 * LongStringMapArrayHolder.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueArrayHolder.template (Long/long, String/String)
 *
 */

public interface LongStringMapArrayHolder
{
    public int size();
    public boolean isEmpty();
    public int capacity();
    public int ensureCapacity(int capacity);
    public long[] keys();
    public long[] toKeyArray();
    public String[] values();
    public String[] toValueArray();
    public LongStringMapArrayHolder add(long key, String value);
    public LongStringMapArrayHolder add(long[] keys, String[] values, int count);
    public long getKey(int index);
    public String getValue(int index);
    public boolean containsKey(long key);
    public boolean containsValue(String value);
    public LongStringMapArrayHolder clear();
    public LongStringMapVisitor acceptVisitor(LongStringMapVisitor visitor);
}

