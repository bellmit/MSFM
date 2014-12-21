package com.cboe.cfix.util;

/**
 * IntObjectPair.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 * A pair of an int and an object
 *
 */

public class IntObjectPair implements FirstIntIF
{
    public int    first;
    public Object second;

    public IntObjectPair()
    {

    }

    public IntObjectPair(int first, Object second)
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

    public Object getSecond()
    {
        return second;
    }

    public void setSecond(Object second)
    {
        this.second = second;
    }

    public IntObjectPair reset(int first, Object second)
    {
        this.first  = first;
        this.second = second;

        return this;
    }

    public String toString()
    {
        String secondStr = second.toString();
        StringBuilder sb = new StringBuilder(secondStr.length()+30);
        sb.append("IntObjectPair([").append(first).append("],[").append(secondStr).append("])");
        return sb.toString();
    }
}
