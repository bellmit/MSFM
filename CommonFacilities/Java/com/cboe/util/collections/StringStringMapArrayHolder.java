package com.cboe.util.collections;

/**
 * StringStringMapArrayHolder.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueArrayHolder.template (String/String, String/String)
 *
 */

public interface StringStringMapArrayHolder
{
    public int size();
    public boolean isEmpty();
    public int capacity();
    public int ensureCapacity(int capacity);
    public String[] keys();
    public String[] toKeyArray();
    public String[] values();
    public String[] toValueArray();
    public StringStringMapArrayHolder add(String key, String value);
    public StringStringMapArrayHolder add(String[] keys, String[] values, int count);
    public String getKey(int index);
    public String getValue(int index);
    public boolean containsKey(String key);
    public boolean containsValue(String value);
    public StringStringMapArrayHolder clear();
    public StringStringMapVisitor acceptVisitor(StringStringMapVisitor visitor);
}

