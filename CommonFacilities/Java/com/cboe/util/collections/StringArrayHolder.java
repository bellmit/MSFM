package com.cboe.util.collections;

/**
 * StringArrayHolderImpl.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM TypedArrayHolder.template (String/String)
 *
 */

public interface StringArrayHolder
{
    public int size();
    public boolean isEmpty();
    public int capacity();
    public int ensureCapacity(int capacity);
    public String[] keys();
    public String[] toArray();
    public StringArrayHolder add(String key);
    public StringArrayHolder add(String[] keyArray);
    public StringArrayHolder add(String[] keyArray, int offset, int length);
    public String getKey(int index);
    public boolean containsKey(String key);
    public StringVisitor acceptVisitor(StringVisitor visitor);
    public StringArrayHolder clear();
}
