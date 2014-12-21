package com.cboe.client.util;

/**
 * BitArrayIF.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 * Used to pack boolean values together. Faster than Java's BitSet.
 *
 */

public interface BitArrayIF extends Cloneable
{
    public void    copyFrom(BitArrayIF bitArray);
    public void    clear(int bitIndex);
    public void    set(int bitIndex);
    public boolean isSet(int bitIndex);
    public boolean isClear(int bitIndex);
    public int     timesChanged(int bitIndex);
    public void    clear();
    public int     capacity();
    public int     size();
    public Object  clone();
}
