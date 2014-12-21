package com.cboe.util.collections;

/**
 * IntArrayHolderImpl.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM TypedArrayHolder.template (Int/int)
 *
 */

public interface IntArrayHolder
{
    public int size();
    public boolean isEmpty();
    public int capacity();
    public int ensureCapacity(int capacity);
    public int[] keys();
    public int[] toArray();
    public IntArrayHolder add(int key);
    public IntArrayHolder add(int[] keyArray);
    public IntArrayHolder add(int[] keyArray, int offset, int length);
    public int getKey(int index);
    public boolean containsKey(int key);
    public IntVisitor acceptVisitor(IntVisitor visitor);
    public IntArrayHolder clear();
}
