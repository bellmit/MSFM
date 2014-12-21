package com.cboe.util.collections;

/**
 * LongArrayHolderImpl.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM TypedArrayHolder.template (Long/long)
 *
 */

public interface LongArrayHolder
{
    public int size();
    public boolean isEmpty();
    public int capacity();
    public int ensureCapacity(int capacity);
    public long[] keys();
    public long[] toArray();
    public LongArrayHolder add(long key);
    public LongArrayHolder add(long[] keyArray);
    public LongArrayHolder add(long[] keyArray, int offset, int length);
    public long getKey(int index);
    public boolean containsKey(long key);
    public LongVisitor acceptVisitor(LongVisitor visitor);
    public LongArrayHolder clear();
}
