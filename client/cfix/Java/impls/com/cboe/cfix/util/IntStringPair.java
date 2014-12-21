package com.cboe.cfix.util;

/**
 * IntStringPair.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 * A pair of an int and a string
 *
 */

public class IntStringPair implements FirstIntIF
{
    public int    first;
    public String second;

    public IntStringPair()
    {

    }

    public IntStringPair(int first, String second)
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

    public String getSecond()
    {
        return second;
    }

    public void setSecond(String second)
    {
        this.second = second;
    }

    public IntStringPair reset(int first, String second)
    {
        this.first  = first;
        this.second = second;

        return this;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder(second.length()+30);
        sb.append("IntStringPair([").append(first).append("],[").append(second).append("])");
        return sb.toString();
    }
}
