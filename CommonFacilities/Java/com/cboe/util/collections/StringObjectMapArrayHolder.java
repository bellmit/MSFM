package com.cboe.util.collections;

/**
 * StringObjectMapArrayHolder.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueArrayHolder.template (String/String, Object/Object)
 *
 */

public interface StringObjectMapArrayHolder
{
    public int size();
    public boolean isEmpty();
    public int capacity();
    public int ensureCapacity(int capacity);
    public String[] keys();
    public String[] toKeyArray();
    public Object[] values();
    public Object[] toValueArray();
    public StringObjectMapArrayHolder add(String key, Object value);
    public StringObjectMapArrayHolder add(String[] keys, Object[] values, int count);
    public String getKey(int index);
    public Object getValue(int index);
    public boolean containsKey(String key);
    public boolean containsValue(Object value);
    public StringObjectMapArrayHolder clear();
    public StringObjectMapVisitor acceptVisitor(StringObjectMapVisitor visitor);
}

