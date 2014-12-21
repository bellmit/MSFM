package com.cboe.cfix.util;

/**
 * PackedIntArray.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 *
 * Array of <i>ints</i>.
 *
 */

public interface PackedIntArrayIF
{
    public void add(int i);
    public int get(int index);
    public int[] getArray();
    public int[] getArrayClone();
    public int length();
    public boolean isEmpty();
    public void clear();
}
