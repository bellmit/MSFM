package com.cboe.client.util;

/**
 * Pair.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 * A pair of two objects
 *
 */

public class Pair
{
    public Object first;
    public Object second;

    public Pair()
    {

    }

    public Pair(Object first, Object second)
    {
        this.first  = first;
        this.second = second;
    }

    public Object getFirst()
    {
        return first;
    }

    public void setFirst(Object first)
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

    public Pair reset(Object first, Object second)
    {
        this.first  = first;
        this.second = second;

        return this;
    }

    public String toString()
    {
        String a = first.toString();
        String b = second.toString();
        StringBuilder result = new StringBuilder(a.length()+b.length()+11);
        result.append("Pair([").append(a).append("],[").append(b).append("])");
        return result.toString();
    }
}
