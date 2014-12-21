package com.cboe.cfix.util;

/**
 * IntIntPair.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 * A pair of two ints
 *
 */

public class IntIntPair implements FirstIntIF
{
    public int first;
    public int second;

    public IntIntPair()
    {

    }

    public IntIntPair(int first, int second)
    {
        this.first  = first;
        this.second = second;
    }

    public int getFirst()
    {
        return first;
    }

    public void setFirst(int first)
    {
        this.first = first;
    }

    public int getSecond()
    {
        return second;
    }

    public void setSecond(int second)
    {
        this.second = second;
    }

    public IntIntPair reset(int first, int second)
    {
        this.first  = first;
        this.second = second;

        return this;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder(40);
        sb.append("IntIntPair([").append(first).append("],[").append(second).append("])");
        return sb.toString();
    }
}
