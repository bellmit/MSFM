package com.cboe.util.collections;

/**
 * StringIntMapArrayHolder.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueArrayHolder.template (String/String, Int/int)
 *
 */

public interface StringIntMapArrayHolder
{
    public int size();
    public boolean isEmpty();
    public int capacity();
    public int ensureCapacity(int capacity);
    public String[] keys();
    public String[] toKeyArray();
    public int[] values();
    public int[] toValueArray();
    public StringIntMapArrayHolder add(String key, int value);
    public StringIntMapArrayHolder add(String[] keys, int[] values, int count);
    public String getKey(int index);
    public int getValue(int index);
    public boolean containsKey(String key);
    public boolean containsValue(int value);
    public StringIntMapArrayHolder clear();
    public StringIntMapVisitor acceptVisitor(StringIntMapVisitor visitor);
}

