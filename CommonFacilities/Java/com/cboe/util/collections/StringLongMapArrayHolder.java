package com.cboe.util.collections;

/**
 * StringLongMapArrayHolder.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueArrayHolder.template (String/String, Long/long)
 *
 */

public interface StringLongMapArrayHolder
{
    public int size();
    public boolean isEmpty();
    public int capacity();
    public int ensureCapacity(int capacity);
    public String[] keys();
    public String[] toKeyArray();
    public long[] values();
    public long[] toValueArray();
    public StringLongMapArrayHolder add(String key, long value);
    public StringLongMapArrayHolder add(String[] keys, long[] values, int count);
    public String getKey(int index);
    public long getValue(int index);
    public boolean containsKey(String key);
    public boolean containsValue(long value);
    public StringLongMapArrayHolder clear();
    public StringLongMapVisitor acceptVisitor(StringLongMapVisitor visitor);
}

